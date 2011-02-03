/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.codec;


import org.apache.directory.shared.ldap.model.message.Control;


/**
 * Implementors of new codec control extensions must implement a factory using
 * this factory interface, Factory implementations for specific controls are
 * then registered with the codec and used by the codec to encode and decode
 * those controls.
 *
 * @TODO must review this interface - too many methods - implementors should not
 * have to implement so many methods.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface IControlFactory<C extends Control, D extends ICodecControl<C>>
{
    /**
     * @return The OID of the Control this factory creates.
     */
    String getOid();


    /**
     * Creates and returns a decorated version of the Control.
     *
     * @return The {@link ICodecControl} decorated version of the Control.
     */
    D newCodecControl();


    /**
     * Decorates an existing control. Implementors should check to make sure
     * the supplied Control has not already been decorated to prevent needless
     * decorator nesting.
     *
     * @param control The {@link Control} to be decorated.
     * @return The decorator wrapping the Control.
     */
    D decorate( C control );


    /**
     * Same as the {@link #newCodecControl()} but returns the decorated object using
     * the Control interface subtype. Or do we really want that?
     *
     * @TODO isn't this totally superfluous? If the codec needs to get a handle on
     * the original control object it can get that when it likes. If it needs to
     * decorate it can do so. Must investigte why this is here and if we can remove
     * it. This might be for efficiency - not to have to unnecesarily create a new
     * decorator when all the codec wants is the control object.
     *
     * @return
     */
    C newControl();
}
