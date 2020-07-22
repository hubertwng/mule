/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.foo.oracle;

import static org.mule.runtime.extension.api.annotation.param.MediaType.TEXT_PLAIN;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.foo.oracle.OracleExtension;

public class OracleOperation {

    public OracleOperation() {}

    @MediaType(value = TEXT_PLAIN, strict = false)
    public String printMessage(@Config OracleExtension config) {
        System.out.println("OracleTestExtension extension says: " + config.getMessage());
        return config.getMessage();
    }
}