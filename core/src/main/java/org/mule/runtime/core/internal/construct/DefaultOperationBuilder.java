/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.core.internal.construct;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.mule.runtime.api.util.Preconditions.checkArgument;
import static org.mule.runtime.core.api.construct.Flow.INITIAL_STATE_STARTED;
import static org.mule.runtime.core.internal.construct.AbstractFlowConstruct.createFlowStatistics;
import static org.mule.runtime.core.privileged.processor.MessageProcessors.processToApply;
import static reactor.core.publisher.Flux.from;

import org.mule.runtime.api.deployment.management.ComponentInitialStateManager;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.construct.Operation;
import org.mule.runtime.core.api.construct.Operation.Builder;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.management.stats.FlowConstructStatistics;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.api.processor.strategy.ProcessingStrategyFactory;
import org.mule.runtime.core.internal.lifecycle.OperationComponentInitialStateManager;
import org.mule.runtime.core.privileged.processor.MessageProcessors;

import java.util.List;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * Creates instances of {@link Operation} with a default implementation
 * <p>
 * Builder instance can be configured using the methods that follow the builder pattern until the flow is built. After that point,
 * builder methods will fail to update the builder state.
 */
public class DefaultOperationBuilder implements Operation.Builder {

  private final String name;
  private final MuleContext muleContext;
  private List<Processor> processors = emptyList();
  private Integer maxConcurrency;

  private DefaultOperation operation;

  /**
   * Creates a new builder
   *
   * @param name        name of the operation to be created. Non empty.
   * @param muleContext context where the operation will be associated with. Non null.
   */
  public DefaultOperationBuilder(String name, MuleContext muleContext) {
    checkArgument(isNotEmpty(name), "name cannot be empty");
    checkArgument(muleContext != null, "muleContext cannot be null");

    this.name = name;
    this.muleContext = muleContext;
  }

  /**
   * Configures the message processors to execute as part of flow.
   *
   * @param processors processors to execute on a {@link Message}. Non null.
   * @return same builder instance.
   */
  @Override
  public Builder processors(List<Processor> processors) {
    checkImmutable();
    checkArgument(processors != null, "processors cannot be null");
    this.processors = processors;

    return this;
  }

  /**
   * Configures the message processors to execute as part of flow.
   *
   * @param processors processors to execute on a {@link Message}.
   * @return same builder instance.
   */
  @Override
  public Builder processors(Processor... processors) {
    checkImmutable();
    this.processors = asList(processors);

    return this;
  }

  @Override
  public Builder maxConcurrency(int maxConcurrency) {
    checkImmutable();
    checkArgument(maxConcurrency > 0, "maxConcurrency cannot be less than 1");
    this.maxConcurrency = maxConcurrency;
    return this;
  }

  /**
   * Builds a flow with the provided configuration.
   *
   * @return a new flow instance.
   */
  @Override
  public Operation build() {
    checkImmutable();

    operation = new DefaultOperation(name,
            muleContext,
            processors,
            maxConcurrency,
            createFlowStatistics(name, muleContext),
            OperationComponentInitialStateManager.INSTANCE);

    return operation;
  }

  protected final void checkImmutable() {
    if (operation != null) {
      throw new IllegalStateException("Cannot change attributes once the operation was built");
    }
  }

  /**
   * Default implementation of {@link Operation}
   */
  public static class DefaultOperation extends AbstractPipeline implements Operation {

    protected DefaultOperation(String name, MuleContext muleContext, List<Processor> processors,
                               Integer maxConcurrency, FlowConstructStatistics flowConstructStatistics,
                               ComponentInitialStateManager componentInitialStateManager) {
      super(name, muleContext, null, processors, empty(),
              of(MessageProcessors.createDefaultProcessingStrategyFactory()),
              INITIAL_STATE_STARTED, maxConcurrency, flowConstructStatistics, componentInitialStateManager);
    }

    @Override
    public CoreEvent process(final CoreEvent event) throws MuleException {
      return processToApply(event, this);
    }

    /**
     * This implementation does not support {@link Flux}es, but because of backwards compatibility we cannot "improve" it.
     */
    @Override
    public Publisher<CoreEvent> apply(Publisher<CoreEvent> publisher) {
      return from(publisher)
              .doOnNext(assertStarted())
              // Insert the incoming event into the flow, routing it through the processing strategy
              .compose(routeThroughProcessingStrategyTransformer())
              // Don't handle errors, these will be handled by parent flow
              .onErrorStop();
    }

    /**
     * {@inheritDoc}
     *
     * @return a {@link ProcessingStrategyFactory}
     */
    @Override
    protected ProcessingStrategyFactory createDefaultProcessingStrategyFactory() {
      return MessageProcessors.createDefaultProcessingStrategyFactory();
    }

    @Override
    public boolean isSynchronous() {
      return getProcessingStrategy() != null ? getProcessingStrategy().isSynchronous() : true;
    }
  }
}
