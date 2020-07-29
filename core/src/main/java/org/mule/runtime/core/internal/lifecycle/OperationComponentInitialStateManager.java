/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.internal.lifecycle;

import org.mule.runtime.api.component.Component;
import org.mule.runtime.api.deployment.management.ComponentInitialStateManager;

public class OperationComponentInitialStateManager implements ComponentInitialStateManager {

  public static final OperationComponentInitialStateManager INSTANCE = new OperationComponentInitialStateManager();

  private OperationComponentInitialStateManager() {
  }

  @Override
  public boolean mustStartMessageSource(Component component) {
    return false;
  }
}
