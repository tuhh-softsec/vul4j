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
package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.securityToken.SecurityToken;

import java.util.List;

/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public abstract class AbstractSecuredElementSecurityEvent extends AbstractElementSecurityEvent {

    private boolean attachment;
    private boolean signed;
    private boolean encrypted;
    private SecurityToken securityToken;
    private List<XMLSecurityConstants.ContentType> protectionOrder;

    public AbstractSecuredElementSecurityEvent(
            SecurityEventConstants.Event securityEventType, SecurityToken securityToken,
            List<XMLSecurityConstants.ContentType> protectionOrder) {
        this(securityEventType, securityToken, protectionOrder, false, false);
    }

    public AbstractSecuredElementSecurityEvent(
            SecurityEventConstants.Event securityEventType, SecurityToken securityToken,
            List<XMLSecurityConstants.ContentType> protectionOrder, boolean signed, boolean encrypted) {
        super(securityEventType);
        this.securityToken = securityToken;
        this.protectionOrder = protectionOrder;
        this.signed = signed;
        this.encrypted = encrypted;
    }

    public SecurityToken getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(InboundSecurityToken securityToken) {
        this.securityToken = securityToken;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public List<XMLSecurityConstants.ContentType> getProtectionOrder() {
        return protectionOrder;
    }

    public void setProtectionOrder(List<XMLSecurityConstants.ContentType> protectionOrder) {
        this.protectionOrder = protectionOrder;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public boolean isAttachment() {
        return attachment;
    }

    public void setAttachment(boolean attachment) {
        this.attachment = attachment;
    }
}
