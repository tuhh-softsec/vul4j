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

/**
 * The Digester EDSL.
 */
public interface RulesBinder {

    /**
     * Records an error message which will be presented to the user at a later
     * time. Unlike throwing an exception, this enable us to continue
     * configuring the Digester and discover more errors. Uses {@link
     * String#format(String, Object[])} to insert the arguments into the
     * message.
     *
     * @param messagePattern The message string pattern
     * @param arguments Arguments referenced by the format specifiers in the format string
     */
    void addError(String messagePattern, Object... arguments);

    /**
     * Records an exception, the full details of which will be logged, and the
     * message of which will be presented to the user at a later
     * time. If your Module calls something that you worry may fail, you should
     * catch the exception and pass it into this.
     *
     * @param t The exception has to be recorded.
     */
    void addError(Throwable t);

}
