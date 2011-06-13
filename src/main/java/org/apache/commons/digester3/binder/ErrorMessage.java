package org.apache.commons.digester3.binder;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static java.lang.String.format;

/**
 * An error message and the context in which it occurred. Messages are usually created internally by
 * {@code Digester} and its extensions. Messages can be created explicitly in a module using
 * {@link org.apache.commons.digester3.binder.RulesBinder#addError(Throwable) addError()} statement:
 *
 * <pre>try {
 *   bindRulesFromFile();
 * } catch (IOException e) {
 *   addError(e);
 * }</pre>
 */
final class ErrorMessage
{

    /**
     * The error message text.
     */
    private final String message;

    /**
     * The throwable that caused this message.
     */
    /* @Nullable */private final Throwable cause;

    /**
     * Create a new {@link ErrorMessage} instance from the error message text.
     *
     * @param messagePattern The error message text pattern
     * @param arguments Arguments referenced by the format specifiers in the format string
     */
    public ErrorMessage( String messagePattern, Object... arguments )
    {
        this( format( messagePattern, arguments ), (Throwable) null );
    }

    /**
     * Create a new {@link ErrorMessage} instance from the error message text and the related cause.
     *
     * @param message The error message text
     * @param cause The throwable that caused this message
     */
    public ErrorMessage( String message, Throwable cause )
    {
        this.message = message;
        this.cause = cause;
    }

    /**
     * Gets the error message text.
     * 
     * @return The error message text
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Returns the Throwable that caused this message, or {@code null} if this message was not caused by a Throwable.
     *
     * @return The Throwable that caused this message, or {@code null} if this message was not caused by a Throwable
     */
    public Throwable getCause()
    {
        return cause;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return message;
    }

}
