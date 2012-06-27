/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.swssf.xmlsec.ext;

/**
 * Exception when configuration errors are detected
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityConfigurationException extends XMLSecurityException {

    public XMLSecurityConfigurationException(ErrorCode errorCode, String msgId, Object[] args, Throwable exception) {
        super(errorCode, msgId, exception, args);
    }

    public XMLSecurityConfigurationException(ErrorCode errorCode, String msgId, Throwable exception) {
        super(errorCode, msgId, exception);
    }

    public XMLSecurityConfigurationException(ErrorCode errorCode, String msgId, Object[] args) {
        super(errorCode, msgId, args);
    }

    public XMLSecurityConfigurationException(ErrorCode errorCode, String msgId) {
        super(errorCode, msgId);
    }

    public XMLSecurityConfigurationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public XMLSecurityConfigurationException(String errorMessage) {
        super(errorMessage);
    }

    public XMLSecurityConfigurationException(String errorMessage, Throwable t) {
        super(errorMessage, t);
    }
}
