/*
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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


package org.apache.commons.digester.parser;

import java.lang.reflect.Method;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Create a <code>SAXParser</code> based on the underlying Xerces version.
 * Currently, Xerces 2.3 and up doesn't implement schema validation the same way
 * 2.1 was. In other to support schema validation in a portable way between 
 * parser, some features/properties need to be set.
 *
 * @author Jean-Francois Arcand
 */

public class XercesParser{

    /**
     * The Log to which all SAX event related logging calls will be made.
     */
    protected static Log log =
        LogFactory.getLog("org.apache.commons.digester.Digester.sax");


    /**
     * The JAXP 1.2 property required to set up the schema location.
     */
    private static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";


    /**
     * The JAXP 1.2 property to set up the schemaLanguage used.
     */
    protected static String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";


    /**
     * Xerces dynamic validation property
     */
    protected static String XERCES_DYNAMIC = 
        "http://apache.org/xml/features/validation/dynamic";


    /**
     * Xerces schema validation property
     */
    protected static String XERCES_SCHEMA =
        "http://apache.org/xml/features/validation/schema";


    /**
     * A <code>float</code> representing the underlying Xerces version
     */
    protected static float version;


    /**
     * The current Xerces version.
     */
    protected static String versionNumber = null;


    /**
     * Return the current Xerces version.
     * @return the current Xerces version.
     */
    private static String getXercesVersion() {
        // If for some reason we can't get the version, set it to 1.0.
        String versionNumber = "1.0";
        try{
            // Use reflection to avoid a build dependency with Xerces.
            Class versionClass = 
                            Class.forName("org.apache.xerces.impl.Version");
            // Will return Xerces-J 2.x.0
            Method method = 
                versionClass.getMethod("getVersion", null); 
            String version = (String)method.invoke(null,null);
            versionNumber = version.substring( "Xerces-J".length() , 
                                               version.lastIndexOf(".") ); 
        } catch (Exception ex){
            // Do nothing.
        }
        return versionNumber;
    }


    /**
     * Create a <code>SAXParser</code> based on the underlying
     * <code>Xerces</code> version.
     * @param properties parser specific properties/features
     * @return an XML Schema/DTD enabled <code>SAXParser</code>
     */
    public static SAXParser newSAXParser(Properties properties) 
            throws ParserConfigurationException, 
                   SAXException,
                   SAXNotSupportedException {

        SAXParserFactory factory =  
                        (SAXParserFactory)properties.get("SAXParserFactory");

        if (versionNumber == null){
            versionNumber = getXercesVersion();
            version = new Float( versionNumber ).floatValue();
        }

        // Note: 2.2 is completely broken (with XML Schema). 
        if (version > 2.1) {

            configureXerces(factory);
            return factory.newSAXParser();
        } else {
            SAXParser parser = factory.newSAXParser();
            configureOldXerces(parser,properties);
            return parser;
        }
    }


    /**
     * Configure schema validation as recommended by the JAXP 1.2 spec.
     * The <code>properties</code> object may contains information about
     * the schema local and language. 
     * @param properties parser optional info
     */
    private static void configureOldXerces(SAXParser parser, 
                                           Properties properties) 
            throws ParserConfigurationException, 
                   SAXNotSupportedException {

        String schemaLocation = (String)properties.get("schemaLocation");
        String schemaLanguage = (String)properties.get("schemaLanguage");

        try{
            if (schemaLocation != null) {
                parser.setProperty(JAXP_SCHEMA_LANGUAGE, schemaLanguage);
                parser.setProperty(JAXP_SCHEMA_SOURCE, schemaLocation);
            }
        } catch (SAXNotRecognizedException e){
            log.info(parser.getClass().getName() + ": " 
                                        + e.getMessage() + " not supported."); 
        }

    }


    /**
     * Configure schema validation as recommended by the Xerces spec. 
     * Both DTD and Schema validation will be enabled simultaneously.
     * @param factory SAXParserFactory to be configured
     */
    private static void configureXerces(SAXParserFactory factory)
            throws ParserConfigurationException, 
                   SAXNotRecognizedException, 
                   SAXNotSupportedException {

        factory.setFeature(XERCES_DYNAMIC, true);
        factory.setFeature(XERCES_SCHEMA, true);

    }
}
