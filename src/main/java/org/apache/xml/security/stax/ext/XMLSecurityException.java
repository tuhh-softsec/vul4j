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

package org.apache.xml.security.stax.ext;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * class lent from apache wss4j
 */

/**
 * Exception class for XML-Security.
 * <p/>
 *
 * @author Davanum Srinivas (dims@yahoo.com).
 */
public class XMLSecurityException extends RemoteException {

    public enum ErrorCode {
        FAILURE,
        UNSUPPORTED_SECURITY_TOKEN,
        UNSUPPORTED_ALGORITHM,
        INVALID_SECURITY,
        INVALID_SECURITY_TOKEN,
        FAILED_AUTHENTICATION,
        FAILED_CHECK,
        SECURITY_TOKEN_UNAVAILABLE,
        MESSAGE_EXPIRED,
        FAILED_ENCRYPTION,
        FAILED_SIGNATURE,
    }

    private static final ResourceBundle resources;

    static {
        try {
            resources = ResourceBundle.getBundle("messages.errors");
        } catch (MissingResourceException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Constructor.
     * <p/>
     *
     * @param errorCode
     * @param msgId
     * @param exception
     * @param arguments
     */
    public XMLSecurityException(ErrorCode errorCode, String msgId, Throwable exception, Object... arguments) {
        super(getMessage(errorCode, msgId, arguments), exception);
    }

    /**
     * Constructor.
     * <p/>
     *
     * @param errorCode
     * @param msgId
     * @param exception
     */
    public XMLSecurityException(ErrorCode errorCode, String msgId, Throwable exception) {
        super(getMessage(errorCode, msgId), exception);
    }

    public XMLSecurityException(ErrorCode errorCode, Throwable exception) {
        super(getMessage(errorCode, null), exception);
    }

    /**
     * Constructor.
     * <p/>
     *
     * @param errorCode
     * @param msgId
     * @param arguments
     */
    public XMLSecurityException(ErrorCode errorCode, String msgId, Object... arguments) {
        super(getMessage(errorCode, msgId, arguments));
    }

    /**
     * Constructor.
     * <p/>
     *
     * @param errorCode
     * @param msgId
     */
    public XMLSecurityException(ErrorCode errorCode, String msgId) {
        this(errorCode, msgId, (Object[]) null);
    }

    /**
     * Constructor.
     * <p/>
     *
     * @param errorCode
     */
    public XMLSecurityException(ErrorCode errorCode) {
        this(errorCode, null, (Object[]) null);
    }

    /**
     * Constructor.
     * <p/>
     *
     * @param errorMessage
     */
    public XMLSecurityException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructor.
     * <p/>
     *
     * @param errorMessage
     */
    public XMLSecurityException(String errorMessage, Throwable t) {
        super(errorMessage, t);
    }

    /**
     * get the message from resource bundle.
     * <p/>
     *
     * @param errorCode
     * @param msgId
     * @param arguments
     * @return the message translated from the property (message) file.
     */
    private static String getMessage(ErrorCode errorCode, String msgId, Object... arguments) {
        String msg = null;
        try {
            msg = resources.getString(String.valueOf(errorCode.ordinal()));
            if (msgId != null) {
                return msg += (" (" + MessageFormat.format(resources.getString(msgId), arguments) + ")");
            }
        } catch (MissingResourceException e) {
            throw new RuntimeException("Undefined '" + msgId + "' resource property", e);
        }
        return msg;
    }
}