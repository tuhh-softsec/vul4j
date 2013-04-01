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
package org.apache.xml.security.test.stax.utils;

import org.apache.xml.security.stax.impl.InboundSecurityContextImpl;
import org.apache.xml.security.stax.impl.processor.input.AbstractSignatureReferenceVerifyInputProcessor;
import org.apache.xml.security.stax.impl.processor.input.XMLEventReaderInputProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public class TestUtils {

    //sometimes I really like reflection. We can fix jdk bugs which will never be fixed, we can do other funny things and
    //we can also change "private static final" fields for testing:-)
    //But keep in mind that this only works for Objects and not primitive types. Primitive types will be inlined...

    public static void switchAllowNotSameDocumentReferences(Boolean value) throws NoSuchFieldException, IllegalAccessException {

        Field field = AbstractSignatureReferenceVerifyInputProcessor.class.getDeclaredField("allowNotSameDocumentReferences");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, value);
    }

    public static void switchDoNotThrowExceptionForManifests(Boolean value) throws NoSuchFieldException, IllegalAccessException {
        Field field = AbstractSignatureReferenceVerifyInputProcessor.class.getDeclaredField("doNotThrowExceptionForManifests");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, value);
    }

    public static int changeValueOfMaximumAllowedReferencesPerManifest(Integer value) throws NoSuchFieldException, IllegalAccessException {
        Field field = AbstractSignatureReferenceVerifyInputProcessor.class.getDeclaredField("maximumAllowedReferencesPerManifest");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        Integer oldval = (Integer) field.get(null);
        field.set(null, value);
        return oldval;
    }

    public static int changeValueOfMaximumAllowedTransformsPerReference(Integer value) throws NoSuchFieldException, IllegalAccessException {
        Field field = AbstractSignatureReferenceVerifyInputProcessor.class.getDeclaredField("maximumAllowedTransformsPerReference");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        Integer oldval = (Integer) field.get(null);
        field.set(null, value);
        return oldval;
    }

    public static void switchAllowMD5Algorithm(Boolean value) throws NoSuchFieldException, IllegalAccessException {
        Field field = InboundSecurityContextImpl.class.getDeclaredField("allowMD5Algorithm");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, value);
    }

    public static int changeValueOfMaximumAllowedXMLStructureDepth(Integer value) throws NoSuchFieldException, IllegalAccessException {
        Field field = XMLEventReaderInputProcessor.class.getDeclaredField("maximumAllowedXMLStructureDepth");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        Integer oldval = (Integer) field.get(null);
        field.set(null, value);
        return oldval;
    }
}
