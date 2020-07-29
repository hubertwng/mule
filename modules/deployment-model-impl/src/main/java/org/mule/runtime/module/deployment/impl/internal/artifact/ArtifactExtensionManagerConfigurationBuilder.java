/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.deployment.impl.internal.artifact;

import static org.mule.runtime.app.declaration.internal.utils.Preconditions.checkArgument;

import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.config.ConfigurationBuilder;
import org.mule.runtime.core.api.config.builders.AbstractConfigurationBuilder;
import org.mule.runtime.core.api.extension.ExtensionManager;
import org.mule.runtime.module.extension.api.manager.ExtensionManagerFactory;

/**
 * Implementation of {@link ConfigurationBuilder} that registers a {@link ExtensionManager}
 *
 * @since 4.0
 */
public class ArtifactExtensionManagerConfigurationBuilder extends AbstractConfigurationBuilder {

  private final String[] configurationFiles;
  private final ExtensionManagerFactory extensionManagerFactory;

  /**
   * Creates an instance of the configuration builder.
   *
   * @param configurationFiles the configuration files that make up for the artifact being deployed
   * @param extensionManagerFactory creates the extension manager for this artifact. Non null.
   */
  public ArtifactExtensionManagerConfigurationBuilder(String[] configurationFiles,
                                                      ExtensionManagerFactory extensionManagerFactory) {
    checkArgument(configurationFiles != null, "configurationFiles cannot be null");
    checkArgument(extensionManagerFactory != null, "extensionManagerFactory cannot be null");

    this.configurationFiles = configurationFiles;
    this.extensionManagerFactory = extensionManagerFactory;
  }

  @Override
  protected void doConfigure(MuleContext muleContext) throws Exception {
    ExtensionManager extensionManager = extensionManagerFactory.create(muleContext);

    muleContext.setExtensionManager(extensionManager);
  }
}
