/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/Address.java,v 1.1 2001/08/20 21:59:43 craigmcc Exp $
 * $Revision: 1.1 $
 * $Date: 2001/08/20 21:59:43 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 

package org.apache.commons.digester;


/**
 * Bean for Digester testing.
 */


public class Address {

    public Address() {
        this("My Street", "My City", "US", "MyZip");
    }

    public Address(String street, String city, String state, String zipCode) {
        super();
        setStreet(street);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
    }

    private String city = null;
    public String getCity() {
        return (this.city);
    }
    public void setCity(String city) {
        this.city = city;
    }

    private String state = null;
    public String getState() {
        return (this.state);
    }
    public void setState(String state) {
        this.state = state;
    }

    private String street = null;
    public String getStreet() {
        return (this.street);
    }
    public void setStreet(String street) {
        this.street = street;
    }

    private String type = null;
    public String getType() {
        return (this.type);
    }
    public void setType(String type) {
        this.type = type;
    }

    private String zipCode = null;
    public String getZipCode() {
        return (this.zipCode);
    }
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Address[");
        sb.append("street=");
        sb.append(street);
        sb.append(", city=");
        sb.append(city);
        sb.append(", state=");
        sb.append(state);
        sb.append(", zipCode=");
        sb.append(zipCode);
        sb.append("]");
        return (sb.toString());
    }

}
