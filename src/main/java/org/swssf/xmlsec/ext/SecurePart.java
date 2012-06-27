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

import javax.xml.namespace.QName;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to describe which and how an element must be secured
 *
 * @author $Author: giger $
 * @version $Revision: 1181995 $ $Date: 2011-10-11 20:03:00 +0200 (Tue, 11 Oct 2011) $
 */
public class SecurePart {

    public enum Modifier {
        Element("http://www.w3.org/2001/04/xmlenc#Element"),
        Content("http://www.w3.org/2001/04/xmlenc#Content");

        private final String modifier;

        Modifier(String modifier) {
            this.modifier = modifier;
        }

        public String getModifier() {
            return this.modifier;
        }

        private static final Map<String, Modifier> modifierMap = new HashMap<String, Modifier>();

        static {
            for (Modifier modifier : EnumSet.allOf(Modifier.class)) {
                modifierMap.put(modifier.getModifier(), modifier);
            }
        }

        public static Modifier getModifier(String modifier) {
            return modifierMap.get(modifier);
        }
    }

    private QName name;
    private Modifier modifier;
    private String idToSign;
    private String idToReference;

    public SecurePart(QName name, Modifier modifier) {
        this.name = name;
        this.modifier = modifier;
    }

    public SecurePart(QName name, String idToSign, String idToReference, Modifier modifier) {
        this.name = name;
        this.idToSign = idToSign;
        this.idToReference = idToReference;
        this.modifier = modifier;
    }

    /**
     * The name of the element to be secured
     *
     * @return The Element-Local-Name
     */
    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    /**
     * The id of the Element
     *
     * @return The id
     */
    public String getIdToSign() {
        return idToSign;
    }

    public void setIdToSign(String idToSign) {
        this.idToSign = idToSign;
    }

    public String getIdToReference() {
        return idToReference;
    }

    public void setIdToReference(String idToReference) {
        this.idToReference = idToReference;
    }
}
