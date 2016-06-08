/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.archiver.jar;

/**
 * Manifest constants
 * Not part of any public API
 */
class ManifestConstants
{

    /**
     * The standard manifest version header
     */
    public static final String ATTRIBUTE_MANIFEST_VERSION = "Manifest-Version";

    /**
     * The standard Signature Version header
     */
    public static final String ATTRIBUTE_SIGNATURE_VERSION = "Signature-Version";

    /**
     * The Name Attribute is the first in a named section
     */
    public static final String ATTRIBUTE_NAME = "Name";

    /**
     * The From Header is disallowed in a Manifest
     */
    public static final String ATTRIBUTE_FROM = "From";

    /**
     * The Class-Path Header is special - it can be duplicated
     */
    public static final String ATTRIBUTE_CLASSPATH = "Class-Path";

    /**
     * Default Manifest version if one is not specified
     */
    public static final String DEFAULT_MANIFEST_VERSION = "1.0";

}
