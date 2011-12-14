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
package org.apache.xml.security.samples.utils;

import java.math.BigInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.utils.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Sample usage for Base64 class
 *
 * @author $Author$
 */
public class Base64Sample {

    /**
     * Method main
     *
     * @param unused
     * @throws Exception
     */
    public static void main(String[] unused) throws Exception {
        
        org.apache.xml.security.Init.init();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        BigInteger bi =
            new BigInteger("43268743267463264169236328648732694167862"
                           + "349613278648732164986132849761329543543"
                           + "874618327964897164823698416236345435435"
                           + "491823648913268496218974698126498712698"
                           + "426861432892343242343243242342342354354"
                           + "349613278648732164986132849761329543543"
                           + "874618327964897164823698416236345435435"
                           + "491823648913268496218974698126498712698"
                           + "426861432892343242343243242342342354354"
                           + "349613278648732164986132849761329543543"
                           + "874618327964897164823698416236345435435"
                           + "491823648913268496218974698126498712698"
                           + "426861432892343242343243242342342354354"
                           + "349613278648732164986132849761329543543"
                           + "874618327964897164823698416236345435435"
                           + "491823648913268496218974698126498712698"
                           + "426861432892343242343243242342342354354"
                           + "349613278648732164986132849761329543543"
                           + "874618327964897164823698416236345435435"
                           + "491823648913268496218974698126498712698"
                           + "426861432892343242343243242342342354354"
                           + "3246874621496829136");

        Text base64text = doc.createTextNode(Base64.encode(bi));
        Element root = doc.createElementNS(null, "Base64");

        doc.appendChild(root);
        root.appendChild(base64text);

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);

        System.out.println(new String(c14n.canonicalizeSubtree(doc)));
    }
}
