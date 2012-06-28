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

import org.apache.xml.security.stax.config.TransformerAlgorithmMapper;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityUtils {

    protected XMLSecurityUtils() {
    }

    /**
     * Returns the Id reference without the leading #
     *
     * @param reference The reference on which to drop the #
     * @return The reference without a leading #
     */
    public static String dropReferenceMarker(String reference) {
        if (reference.startsWith("#")) {
            return reference.substring(1);
        }
        return reference;
    }

    /**
     * Returns the XMLEvent type in String form
     *
     * @param xmlSecEvent
     * @return The XMLEvent type as string representation
     */
    public static String getXMLEventAsString(XMLSecEvent xmlSecEvent) {
        int eventType = xmlSecEvent.getEventType();

        switch (eventType) {
            case XMLSecEvent.START_ELEMENT:
                return "START_ELEMENT";
            case XMLSecEvent.END_ELEMENT:
                return "END_ELEMENT";
            case XMLSecEvent.PROCESSING_INSTRUCTION:
                return "PROCESSING_INSTRUCTION";
            case XMLSecEvent.CHARACTERS:
                return "CHARACTERS";
            case XMLSecEvent.COMMENT:
                return "COMMENT";
            case XMLSecEvent.START_DOCUMENT:
                return "START_DOCUMENT";
            case XMLSecEvent.END_DOCUMENT:
                return "END_DOCUMENT";
            case XMLSecEvent.ATTRIBUTE:
                return "ATTRIBUTE";
            case XMLSecEvent.DTD:
                return "DTD";
            case XMLSecEvent.NAMESPACE:
                return "NAMESPACE";
            default:
                throw new IllegalArgumentException("Illegal XMLSecEvent received: " + eventType);
        }
    }

    /**
     * Executes the Callback handling. Typically used to fetch passwords
     *
     * @param callbackHandler
     * @param callback
     * @throws XMLSecurityException if the callback couldn't be executed
     */
    public static void doPasswordCallback(CallbackHandler callbackHandler, Callback callback) throws XMLSecurityException {
        if (callbackHandler == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "noCallback");
        }
        try {
            callbackHandler.handle(new Callback[]{callback});
        } catch (IOException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "noPassword", e);
        } catch (UnsupportedCallbackException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "noPassword", e);
        }
    }

    /**
     * Try to get the secret key from a CallbackHandler implementation
     *
     * @param callbackHandler a CallbackHandler implementation
     * @return An array of bytes corresponding to the secret key (can be null)
     * @throws XMLSecurityException
     */
    public static void doSecretKeyCallback(CallbackHandler callbackHandler, Callback callback, String id) throws XMLSecurityException {
        if (callbackHandler != null) {
            try {
                callbackHandler.handle(new Callback[]{callback});
            } catch (IOException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "noPassword", e);
            } catch (UnsupportedCallbackException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "noPassword", e);
            }
        }
    }

    public static Class loadClass(String className) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    //todo transformer factory?
    public static Transformer getTransformer(Object methodParameter1, Object methodParameter2, String algorithm)
            throws XMLSecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        @SuppressWarnings("unchecked")
        Class<Transformer> transformerClass = (Class<Transformer>) TransformerAlgorithmMapper.getTransformerClass(algorithm, null);
        if (transformerClass == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.UNSUPPORTED_ALGORITHM);
        }
        Transformer childTransformer = transformerClass.newInstance();
        if (methodParameter2 != null) {
            childTransformer.setList((List) methodParameter1);
            childTransformer.setOutputStream((OutputStream) methodParameter2);
        } else {
            childTransformer.setTransformer((Transformer) methodParameter1);
        }
        return childTransformer;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getType(List<Object> objects, Class<T> clazz) {
        for (int i = 0; i < objects.size(); i++) {
            Object o = objects.get(i);
            if (o instanceof JAXBElement) {
                o = ((JAXBElement) o).getValue();
            }
            if (clazz.isAssignableFrom(o.getClass())) {
                return (T) o;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getQNameType(List<Object> objects, QName qName) {
        for (int i = 0; i < objects.size(); i++) {
            Object o = objects.get(i);
            if (o instanceof JAXBElement) {
                JAXBElement jaxbElement = (JAXBElement) o;
                if (jaxbElement.getName().equals(qName)) {
                    return (T) jaxbElement.getValue();
                }
            }
        }
        return null;
    }

    public static String getQNameAttribute(Map<QName, String> attributes, QName qName) {
        return attributes.get(qName);
    }
}
