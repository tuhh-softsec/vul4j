/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester3;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

/**
 * Thrown when errors occur while creating a {@link Digester}. Includes a list of encountered
 * errors. Clients should catch this exception, log it, and stop execution.
 */
public final class DigesterLoadingException extends RuntimeException {

    /**
     * The default head when reporting an errors list.
     */
    private static final String HEADING = "Digester creation errors:%n%n";

    /**
     * The typical serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The errors list encountered while creating a new {@code Digester} instance.
     */
    private final List<ErrorMessage> errors;

    /**
     * Create a new {@link DigesterLoadingException} with the given specified message.
     *
     * @param messagePattern The message string pattern
     * @param arguments Arguments referenced by the format specifiers in the format string
     */
    protected DigesterLoadingException(String messagePattern, Object... arguments) {
        this(new ErrorMessage(String.format(messagePattern, arguments)));
    }

    /**
     * Create a new {@link DigesterLoadingException} with the given cause.
     *
     * @param cause The throwable that caused this message
     */
    protected DigesterLoadingException(Throwable cause) {
        this(new ErrorMessage("An exception was caught and reported. Message: " + cause.getMessage(), cause));
    }

    /**
     * Create a new {@link DigesterLoadingException} with the given errors.
     *
     * @param errors the errors occurred while creating a new {@code Digester} instance
     */
    protected DigesterLoadingException(ErrorMessage...errors) {
        this(Arrays.asList(errors));
    }

    /**
     * Create a new {@link DigesterLoadingException} with the given errors.
     *
     * @param errors the errors occurred while creating a new {@code Digester} instance
     */
    protected DigesterLoadingException(List<ErrorMessage> errors) {
        this.errors = errors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        Formatter fmt = new Formatter().format(HEADING);
        int index = 1;

        for (ErrorMessage errorMessage : this.errors) {
            fmt.format("%s) %s%n", index++, errorMessage.getMessage());

            Throwable cause = errorMessage.getCause();
            if (cause != null) {
                StringWriter writer = new StringWriter();
                cause.printStackTrace(new PrintWriter(writer));
                fmt.format("Caused by: %s", writer.getBuffer());
            }

            fmt.format("%n");
        }

        if (this.errors.size() == 1) {
            fmt.format("1 error");
        } else {
            fmt.format("%s errors", this.errors.size());
        }

        return fmt.toString();
    }

}
