/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.api.construct;

import org.mule.runtime.api.component.execution.ExecutableComponent;
import org.mule.runtime.api.lifecycle.Lifecycle;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.internal.construct.DefaultOperationBuilder;

import java.util.List;

public interface Operation extends ExecutableComponent, Lifecycle, Pipeline, Processor {

    /**
     * Creates a new flow builder
     *
     * @param name        name of the flow to be created. Non empty.
     * @param muleContext context where the flow will be registered. Non null.
     */
    static Builder builder(String name, MuleContext muleContext) {
        return new DefaultOperationBuilder(name, muleContext);
    }

    interface Builder {

        /**
         * Configures the message processors to execute as part of the operation.
         *
         * @param processors message processors to execute. Non null.
         * @return same builder instance.
         */
        Builder processors(List<Processor> processors);

        /**
         * Configures the message processors to execute as part of the operation.
         *
         * @param processors message processors to execute.
         * @return same builder instance.
         */
        Builder processors(Processor... processors);

        /**
         * Configures the maximum permitted concurrency of the {@link Operation}. This value determines the maximum level of parallelism
         * that the operation can use to optimize for performance when processing messages. Note that this does not impact in any way the
         * number of threads that the invoker may use.
         *
         * @param maxConcurrency
         * @return same builder instance.
         */
        Builder maxConcurrency(int maxConcurrency);

        /**
         * Builds a flow with the provided configuration.
         *
         * @return a new flow instance.
         */
        Operation build();
    }
}
