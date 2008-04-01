/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.functor.generator;

import org.apache.commons.functor.UnaryFunction;

/**
 * Transformers are used to change a {@link Generator} into something else,
 * such as a {@link java.util.Collection}.
 *
 * @since 1.0
 * @version $Revision$ $Date$
 * @author  Jason Horman (jason@jhorman.org)
 * @deprecated Simply use UnaryFunction.
 */

public interface Transformer extends UnaryFunction {
    Object transform(Generator generator);
}