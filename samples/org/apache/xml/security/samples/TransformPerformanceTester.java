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
package org.apache.xml.security.samples;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.apache.xml.security.transforms.params.XPath2FilterContainer04;
import org.apache.xml.security.transforms.params.XPathFilterCHGPContainer;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Class TransformPerformanceTester
 *
 * 300 * simple_gif_detached       took 136,376 seconds
 * 300 * pureedge_xfilter2         took 574,797 seconds
 * 300 * pureedge_apachefilter     took 595,617 seconds
 * 300 * xfilter2spec_xfilter2     took  75,408 seconds
 * 300 * xfilter2spec_apachefilter took  58,624 seconds
 *
 * 400 * simple_gif_detached       took 196,733 seconds
 * 400 * pureedge_xfilter2         took 767,404 seconds
 * 400 * pureedge_apachefilter     took 805,648 seconds
 * 400 * xfilter2spec_xfilter2     took  81,367 seconds
 * 400 * xfilter2spec_apachefilter took  72,013 seconds
 *
 * 500 * simple_gif_detached took 246,054 seconds
 * 500 * xfilter2spec_xfilter2_1 took  98,842 seconds
 * 500 * xfilter2spec_xfilter2_2 took 122,236 seconds
 * 500 * xfilter2spec_xfilter2_3 took 122,917 seconds
 * 500 * xfilter2spec_apachefilter_1 took 120,563 seconds
 * 500 * xfilter2spec_apachefilter_2 took 109,898 seconds
 * 500 * xfilter2spec_apachefilter_3 took 113,383 seconds
 *
 * 600 * simple_gif_detached took 294,503 seconds
 * 600 * pureedge_xfilter2 took 1.144,616 seconds
 * 600 * pureedge_apachefilter took 1.205,243 seconds
 * 600 * xfilter2spec_xfilter2_1 took 109,337 seconds
 * 600 * xfilter2spec_xfilter2_2 took 122,206 seconds
 * 600 * xfilter2spec_xfilter2_3 took 138,91 seconds
 * 600 * xfilter2spec_apachefilter_1 took 130,297 seconds
 * 600 * xfilter2spec_apachefilter_2 took 123,268 seconds
 * 600 * xfilter2spec_apachefilter_3 took 131,178 seconds
 *
 * @author $Author$
 * @version $Revision$
 */
public class TransformPerformanceTester {

    /**
     * Method main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {

        org.apache.xml.security.Init.init();

        // checkMerlinsSample();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        DocumentBuilder db = dbf.newDocumentBuilder();

        int counter = 50;
        boolean simple_gif_detached = false;

        boolean pureedge_xfilter2 = true;
        boolean pureedge_xfilter2_new = true;
        boolean pureedge_apachefilter = true;

        boolean xfilter2spec_xfilter2_1 = true;
        boolean xfilter2spec_xfilter2_2 = true;
        boolean xfilter2spec_xfilter2_3 = true;

        boolean xfilter2spec_apachefilter_1 = true;
        boolean xfilter2spec_apachefilter_2 = true;
        boolean xfilter2spec_apachefilter_3 = true;

        boolean apachesample_xfilter2_1 = false;
        boolean apachesample_xfilter2_2 = false;
        boolean apachesample_xfilter2_3 = false;
        boolean apachesample_xfilter2_4 = false;
        boolean apachesample_xfilter2_5 = false;
        boolean apachesample_xfilter2_6 = false;
        boolean apachesample_xfilter2_7 = false;

        boolean apachesample_apachefilter_1 = false;
        boolean apachesample_apachefilter_2 = false;
        boolean apachesample_apachefilter_3 = false;
        boolean apachesample_apachefilter_4 = false;
        boolean apachesample_apachefilter_5 = false;
        boolean apachesample_apachefilter_6 = false;
        boolean apachesample_apachefilter_7 = false;

        boolean apachesample_apachefilter_7_optimal = false;


        boolean xfilter2spec_xfilter2_3_new = true;

        if (simple_gif_detached) {
            Document doc = db.newDocument();
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.simple_gif_detached(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * simple_gif_detached took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("simple_gif_detached.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("simple_gif_detached.gif");

            fos.write(result[1]);
            fos.close();
        }

        if (pureedge_xfilter2) {
            Document doc =
                db.parse(new FileInputStream("samples/data/com/pureedge/LeaveRequest.xfd"));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.pureedge_xfilter2(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * pureedge_xfilter2 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("pureedge_xfilter2_doc.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("pureedge_xfilter2_ref.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (pureedge_xfilter2_new) {
            Document doc =
                db.parse(new FileInputStream("samples/data/com/pureedge/LeaveRequest.xfd"));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.pureedge_xfilter2_new(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * pureedge_xfilter2_new took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("pureedge_xfilter2_new_doc.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("pureedge_xfilter2_new_ref.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (pureedge_apachefilter) {
            Document doc =
                db.parse(new FileInputStream("data/com/pureedge/LeaveRequest.xfd"));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.pureedge_apachefilter(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * pureedge_apachefilter took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("pureedge_apachefilter_doc.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("pureedge_apachefilter_ref.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (xfilter2spec_xfilter2_1) {
            String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
            + "       <!-- comment -->\n" + "       <Data />\n"
            + "       <NotToBeSigned>\n"
            + "         <ReallyToBeSigned>\n"
            + "           <!-- comment -->\n"
            + "           <Data />\n"
            + "         </ReallyToBeSigned>\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
            + "       <Data />\n" + "       <NotToBeSigned>\n"
            + "         <Data />\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "</Document>";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.xfilter2spec_xfilter2_1(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * xfilter2spec_xfilter2_1 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("xfilter2spec_xfilter2_doc_1.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("xfilter2spec_xfilter2_ref_1.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (xfilter2spec_xfilter2_2) {
            String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
            + "       <!-- comment -->\n" + "       <Data />\n"
            + "       <NotToBeSigned>\n"
            + "         <ReallyToBeSigned>\n"
            + "           <!-- comment -->\n"
            + "           <Data />\n"
            + "         </ReallyToBeSigned>\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
            + "       <Data />\n" + "       <NotToBeSigned>\n"
            + "         <Data />\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "</Document>";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.xfilter2spec_xfilter2_2(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * xfilter2spec_xfilter2_2 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("xfilter2spec_xfilter2_doc_2.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("xfilter2spec_xfilter2_ref_2.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (xfilter2spec_xfilter2_3) {
            String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
            + "       <!-- comment -->\n" + "       <Data />\n"
            + "       <NotToBeSigned>\n"
            + "         <ReallyToBeSigned>\n"
            + "           <!-- comment -->\n"
            + "           <Data />\n"
            + "         </ReallyToBeSigned>\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
            + "       <Data />\n" + "       <NotToBeSigned>\n"
            + "         <Data />\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "</Document>";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.xfilter2spec_xfilter2_3(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * xfilter2spec_xfilter2_3 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("xfilter2spec_xfilter2_doc_3.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("xfilter2spec_xfilter2_ref_3.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (xfilter2spec_apachefilter_1) {
            String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
            + "       <!-- comment -->\n" + "       <Data />\n"
            + "       <NotToBeSigned>\n"
            + "         <ReallyToBeSigned>\n"
            + "           <!-- comment -->\n"
            + "           <Data />\n"
            + "         </ReallyToBeSigned>\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
            + "       <Data />\n" + "       <NotToBeSigned>\n"
            + "         <Data />\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "</Document>";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester.xfilter2spec_apachefilter_1(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * xfilter2spec_apachefilter_1 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("xfilter2spec_apachefilter_doc_1.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("xfilter2spec_apachefilter_ref_1.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (xfilter2spec_apachefilter_2) {
            String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
            + "       <!-- comment -->\n" + "       <Data />\n"
            + "       <NotToBeSigned>\n"
            + "         <ReallyToBeSigned>\n"
            + "           <!-- comment -->\n"
            + "           <Data />\n"
            + "         </ReallyToBeSigned>\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
            + "       <Data />\n" + "       <NotToBeSigned>\n"
            + "         <Data />\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "</Document>";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester.xfilter2spec_apachefilter_2(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * xfilter2spec_apachefilter_2 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("xfilter2spec_apachefilter_doc_2.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("xfilter2spec_apachefilter_ref_2.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (xfilter2spec_apachefilter_3) {
            String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
            + "       <!-- comment -->\n" + "       <Data />\n"
            + "       <NotToBeSigned>\n"
            + "         <ReallyToBeSigned>\n"
            + "           <!-- comment -->\n"
            + "           <Data />\n"
            + "         </ReallyToBeSigned>\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
            + "       <Data />\n" + "       <NotToBeSigned>\n"
            + "         <Data />\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "</Document>";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester.xfilter2spec_apachefilter_3(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * xfilter2spec_apachefilter_3 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("xfilter2spec_apachefilter_doc_3.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("xfilter2spec_apachefilter_ref_3.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_apachefilter_7_optimal) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            //J+
            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester
                    .apachesample_apachefilter_7_optimal(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_apachefilter_7_optimal took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream(
            "apachesample_apachefilter_doc_7_optimal.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream(
            "apachesample_apachefilter_ref_7_optimal.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_apachefilter_1) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            //J+
            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester.apachesample_apachefilter_1(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_apachefilter_1 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_apachefilter_doc_1.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_apachefilter_ref_1.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_apachefilter_2) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester.apachesample_apachefilter_2(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_apachefilter_2 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_apachefilter_doc_2.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_apachefilter_ref_2.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_apachefilter_3) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester.apachesample_apachefilter_3(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_apachefilter_3 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_apachefilter_doc_3.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_apachefilter_ref_3.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_apachefilter_4) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester.apachesample_apachefilter_4(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_apachefilter_4 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_apachefilter_doc_4.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_apachefilter_ref_4.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_apachefilter_5) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester.apachesample_apachefilter_5(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_apachefilter_5 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_apachefilter_doc_5.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_apachefilter_ref_5.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_apachefilter_6) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester.apachesample_apachefilter_6(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_apachefilter_6 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_apachefilter_doc_6.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_apachefilter_ref_6.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_apachefilter_7) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester.apachesample_apachefilter_7(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_apachefilter_7 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_apachefilter_doc_7.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_apachefilter_ref_7.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_xfilter2_1) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.apachesample_xfilter2_1(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_xfilter2_1 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_xfilter2_doc_1.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_xfilter2_ref_1.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_xfilter2_2) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.apachesample_xfilter2_2(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_xfilter2_2 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_xfilter2_doc_2.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_xfilter2_ref_2.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_xfilter2_3) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.apachesample_xfilter2_3(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_xfilter2_3 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_xfilter2_doc_3.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_xfilter2_ref_3.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_xfilter2_4) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.apachesample_xfilter2_4(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_xfilter2_4 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_xfilter2_doc_4.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_xfilter2_ref_4.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_xfilter2_5) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.apachesample_xfilter2_5(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_xfilter2_5 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_xfilter2_doc_5.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_xfilter2_ref_5.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_xfilter2_6) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.apachesample_xfilter2_6(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_xfilter2_6 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_xfilter2_doc_6.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_xfilter2_ref_6.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (apachesample_xfilter2_7) {
            String inputDoc =
                "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result = TransformPerformanceTester.apachesample_xfilter2_7(doc);

                if (i % 10 == 0) {

                    // System.out.print(".");
                }
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * apachesample_xfilter2_7 took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("apachesample_xfilter2_doc_7.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("apachesample_xfilter2_ref_7.xml");

            fos.write(result[1]);
            fos.close();
        }

        if (xfilter2spec_xfilter2_3_new) {
            String inputDoc = "<Document>\n" + "     <ToBeSigned>\n"
            + "       <!-- comment -->\n" + "       <Data />\n"
            + "       <NotToBeSigned>\n"
            + "         <ReallyToBeSigned>\n"
            + "           <!-- comment -->\n"
            + "           <Data />\n"
            + "         </ReallyToBeSigned>\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "     <ToBeSigned>\n"
            + "       <Data />\n" + "       <NotToBeSigned>\n"
            + "         <Data />\n"
            + "       </NotToBeSigned>\n"
            + "     </ToBeSigned>\n" + "</Document>";

            Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
            long start = System.currentTimeMillis();
            byte[][] result = null;

            for (int i = 0; i < counter; i++) {
                result =
                    TransformPerformanceTester.xfilter2spec_xfilter2_3_new(doc);
            }

            // System.out.println("");
            long end = System.currentTimeMillis();
            double delta = end - start;

            System.out.println(
                               counter + " * xfilter2spec_xfilter2_3_new took "
                               + java.text.DecimalFormat.getInstance().format(delta / 1000.)
                               + " seconds");

            FileOutputStream fos;

            fos = new FileOutputStream("xfilter2spec_xfilter2_3_new_doc.xml");

            fos.write(result[0]);
            fos.close();

            fos = new FileOutputStream("xfilter2spec_xfilter2_3_new_ref.xml");

            fos.write(result[1]);
            fos.close();
        }
    }

    /**
     * Method pureedge_xfilter2
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] pureedge_xfilter2(Document doc) throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            XPath2FilterContainer04 xpathContainer =
                XPath2FilterContainer04.newInstanceSubtract(doc,
                                                            "\n" +
                                                            "/XFDL/page[@sid='PAGE1']/*[@sid='CHECK16' or \n" +
                                                            "                           @sid='CHECK17' or \n" +
                                                            "                           @sid='FIELD47' or \n" +
                                                            "                           @sid='BUTTON2' or \n" +
                                                            "                           @sid='FIELD48']\n" +
                                                            " | \n" +
                                                            "/XFDL/page/triggeritem[not(attribute::sid) | \n"  +
                                                            "                       /XFDL/page/*/triggeritem]\n" +
                                                            " | \n" +
                                                            "here()/ancestor::ds:Signature[1]" +
                "");
            xpathContainer.setXPathNamespaceContext("ds",
                                                    Constants.SignatureSpecNS);
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    xpathContainer.getElement());
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method pureedge_apachefilter
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] pureedge_apachefilter(Document doc) throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            String includeButSearch = null;
            String excludeButSearch = null;
            String exclude =
                "\n" +
                "/XFDL/page[@sid='PAGE1']/*[@sid='CHECK16' or \n" +
                "                           @sid='CHECK17' or \n" +
                "                           @sid='FIELD47' or \n" +
                "                           @sid='BUTTON2' or \n" +
                "                           @sid='FIELD48']\n" +
                " | \n" +
                "/XFDL/page/triggeritem[not(attribute::sid) | \n"  +
                "                       /XFDL/page/*/triggeritem]\n" +
                " | \n" +
                "here()/ancestor::ds:Signature[1]";
            XPathFilterCHGPContainer xpathContainer =
                XPathFilterCHGPContainer
                .getInstance(doc, XPathFilterCHGPContainer
                             .IncludeSlash, includeButSearch, excludeButSearch, exclude);

            xpathContainer.setXPathNamespaceContext("ds",
                                                    Constants.SignatureSpecNS);
            transforms.addTransform(XPathFilterCHGPContainer.TRANSFORM_XPATHFILTERCHGP,
                                    xpathContainer.getElement());
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method simple_gif_detached
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] simple_gif_detached(Document doc) throws Exception {

        XMLSignature sig = new XMLSignature(doc,
                                            new File(".").toURL().toString(),
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.appendChild(sig.getElement());
        sig.addDocument("./image.gif");

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method xfilter2spec_xfilter2_1
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] xfilter2spec_xfilter2_1(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                XPath2FilterContainer04.newInstanceIntersect(doc,
                                "//ToBeSigned").getElement());
        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method xfilter2spec_xfilter2_2
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] xfilter2spec_xfilter2_2(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                XPath2FilterContainer04.newInstanceIntersect(doc,
                                "//ToBeSigned").getElement());
        transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                XPath2FilterContainer04.newInstanceSubtract(doc,
                                "//NotToBeSigned").getElement());
        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method xfilter2spec_xfilter2
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] xfilter2spec_xfilter2_3(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                XPath2FilterContainer04.newInstanceIntersect(doc,
                                "//ToBeSigned").getElement());
        transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                XPath2FilterContainer04.newInstanceSubtract(doc,
                                "//NotToBeSigned").getElement());
        transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                XPath2FilterContainer04.newInstanceUnion(doc,
                                "//ReallyToBeSigned").getElement());
        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method xfilter2spec_apachefilter
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] xfilter2spec_apachefilter_1(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            String includeButSearch = "//ToBeSigned";
            String excludeButSearch = null;
            String exclude = "here()/ancestor::ds:Signature[1]";
            XPathFilterCHGPContainer xpathContainer =
                XPathFilterCHGPContainer
                .getInstance(doc, XPathFilterCHGPContainer
                             .ExcludeSlash, includeButSearch, excludeButSearch, exclude);

            xpathContainer.setXPathNamespaceContext("ds",
                                                    Constants.SignatureSpecNS);
            transforms.addTransform(XPathFilterCHGPContainer.TRANSFORM_XPATHFILTERCHGP,
                                    xpathContainer.getElement());
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method xfilter2spec_apachefilter
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] xfilter2spec_apachefilter_2(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            String includeButSearch = "//ToBeSigned";
            String excludeButSearch = "//NotToBeSigned";
            String exclude = "here()/ancestor::ds:Signature[1]";
            XPathFilterCHGPContainer xpathContainer =
                XPathFilterCHGPContainer
                .getInstance(doc, XPathFilterCHGPContainer
                             .ExcludeSlash, includeButSearch, excludeButSearch, exclude);

            xpathContainer.setXPathNamespaceContext("ds",
                                                    Constants.SignatureSpecNS);
            transforms.addTransform(XPathFilterCHGPContainer.TRANSFORM_XPATHFILTERCHGP,
                                    xpathContainer.getElement());
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method xfilter2spec_apachefilter
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] xfilter2spec_apachefilter_3(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            String includeButSearch = "//ToBeSigned | //ReallyToBeSigned";
            String excludeButSearch = "//NotToBeSigned";
            String exclude = "here()/ancestor::ds:Signature[1]";
            XPathFilterCHGPContainer xpathContainer =
                XPathFilterCHGPContainer
                .getInstance(doc, XPathFilterCHGPContainer
                             .ExcludeSlash, includeButSearch, excludeButSearch, exclude);

            xpathContainer.setXPathNamespaceContext("ds",
                                                    Constants.SignatureSpecNS);
            transforms.addTransform(XPathFilterCHGPContainer.TRANSFORM_XPATHFILTERCHGP,
                                    xpathContainer.getElement());
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method checkMerlinsSample
     *
     * @throws Exception
     */
    public static void checkMerlinsSample() throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        File f = new File(
        "data/ie/baltimore/merlin-examples/merlin-xmldsig-filter2-one/sign-xfdl.xml");
        Document doc = db.parse(new FileInputStream(f));
        XMLSignature sig =
            new XMLSignature((Element) doc
                             .getElementsByTagNameNS(Constants.SignatureSpecNS, "Signature")
                             .item(0), f.toURL().toString());

        System.out.println("Signature erzeugt");

        boolean v = sig.checkSignatureValue(sig.getKeyInfo().getPublicKey());

        System.out.println("Merlin: " + v);
    }

    /**
     * Method apachesample_apachefilter_1
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_apachefilter_1(Document doc)
    throws Exception {

        String includeButSearchStr = "//E";
        String excludeButSearchStr = null;
        String excludeStr = null;
        boolean includeSlashPolicy = XPathFilterCHGPContainer.ExcludeSlash;

        return TransformPerformanceTester.apachesample_apachefilter_x(doc,
                                                                      includeSlashPolicy, includeButSearchStr, excludeButSearchStr,
                                                                      excludeStr);
    }

    /**
     * Method apachesample_apachefilter_2
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_apachefilter_2(Document doc)
    throws Exception {

        String includeButSearchStr = "//B | //E";
        String excludeButSearchStr = null;
        String excludeStr = null;
        boolean includeSlashPolicy = XPathFilterCHGPContainer.ExcludeSlash;

        return TransformPerformanceTester.apachesample_apachefilter_x(doc,
                                                                      includeSlashPolicy, includeButSearchStr, excludeButSearchStr,
                                                                      excludeStr);
    }

    /**
     * Method apachesample_apachefilter_3
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_apachefilter_3(Document doc)
    throws Exception {

        String includeButSearchStr = "//B | //E";
        String excludeButSearchStr = "//C";
        String excludeStr = null;
        boolean includeSlashPolicy = XPathFilterCHGPContainer.ExcludeSlash;

        return TransformPerformanceTester.apachesample_apachefilter_x(doc,
                                                                      includeSlashPolicy, includeButSearchStr, excludeButSearchStr,
                                                                      excludeStr);
    }

    /**
     * Method apachesample_apachefilter_4
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_apachefilter_4(Document doc)
    throws Exception {

        String includeButSearchStr = "//B | //E | //F";
        String excludeButSearchStr = "//C";
        String excludeStr = null;
        boolean includeSlashPolicy = XPathFilterCHGPContainer.ExcludeSlash;

        return TransformPerformanceTester.apachesample_apachefilter_x(doc,
                                                                      includeSlashPolicy, includeButSearchStr, excludeButSearchStr,
                                                                      excludeStr);
    }

    /**
     * Method apachesample_apachefilter_5
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_apachefilter_5(Document doc)
    throws Exception {

        String includeButSearchStr = "//B | //E | //F";
        String excludeButSearchStr = "//C | //G";
        String excludeStr = null;
        boolean includeSlashPolicy = XPathFilterCHGPContainer.ExcludeSlash;

        return TransformPerformanceTester.apachesample_apachefilter_x(doc,
                                                                      includeSlashPolicy, includeButSearchStr, excludeButSearchStr,
                                                                      excludeStr);
    }

    /**
     * Method apachesample_apachefilter_6
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_apachefilter_6(Document doc)
    throws Exception {

        String includeButSearchStr = "//B | //E | //F | //H ";
        String excludeButSearchStr = "//C | //G";
        String excludeStr = null;
        boolean includeSlashPolicy = XPathFilterCHGPContainer.ExcludeSlash;

        return TransformPerformanceTester.apachesample_apachefilter_x(doc,
                                                                      includeSlashPolicy, includeButSearchStr, excludeButSearchStr,
                                                                      excludeStr);
    }

    /**
     * Method apachesample_apachefilter_7
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_apachefilter_7(Document doc)
    throws Exception {

        String includeButSearchStr = "//B | //E | //F | //H";
        String excludeButSearchStr = "//C | //G | //@x:attr";
        String excludeStr = null;
        boolean includeSlashPolicy = XPathFilterCHGPContainer.ExcludeSlash;

        return TransformPerformanceTester.apachesample_apachefilter_x(doc,
                                                                      includeSlashPolicy, includeButSearchStr, excludeButSearchStr,
                                                                      excludeStr);
    }

    /**
     * Method apachesample_apachefilter_7_optimal
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_apachefilter_7_optimal(Document doc)
    throws Exception {

        String includeButSearchStr = "//B | //E | //F | //H";
        String excludeButSearchStr = "//G";
        String excludeStr = "//C | //D | //@x:attr ";
        boolean includeSlashPolicy = XPathFilterCHGPContainer.ExcludeSlash;

        return TransformPerformanceTester.apachesample_apachefilter_x(doc,
                                                                      includeSlashPolicy, includeButSearchStr, excludeButSearchStr,
                                                                      excludeStr);
    }

    /**
     * Method apachesample_apachefilter_x
     *
     * @param doc
     * @param includeSlashPolicy
     * @param includeButSearchStr
     * @param excludeButSearchStr
     * @param excludeStr
     *
     * @throws Exception
     */
    public static byte[][] apachesample_apachefilter_x(
                                                       Document doc, boolean includeSlashPolicy, String includeButSearchStr, String excludeButSearchStr, String excludeStr)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            XPathFilterCHGPContainer xpathContainer =
                XPathFilterCHGPContainer.getInstance(doc, includeSlashPolicy,
                                                     includeButSearchStr,
                                                     excludeButSearchStr,
                                                     excludeStr);

            xpathContainer.setXPathNamespaceContext("ds",
                                                    Constants.SignatureSpecNS);
            xpathContainer.setXPathNamespaceContext("x", "http://foo.bar/");
            transforms.addTransform(XPathFilterCHGPContainer.TRANSFORM_XPATHFILTERCHGP,
                                    xpathContainer.getElement());
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method apachesample_xfilter2_1
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_xfilter2_1(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceIntersect(doc, "//E")
                                    .getElement());

            /*
         transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                 XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//B").getElement());
         transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                 XPath2FilterContainer04.newInstanceSubtract(doc,
                                    "//C").getElement());
         transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                 XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//F").getElement());
         transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                 XPath2FilterContainer04.newInstanceSubtract(doc,
                                    "//G").getElement());
         transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                 XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//H").getElement());
         transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                 XPath2FilterContainer04.newInstanceSubtract(doc,
                                    "//@x:attr").getElement());
             */
            transforms.setXPathNamespaceContext("xmlns:x", "http://foo.bar/");
            transforms
            .setXPathNamespaceContext(Transforms
                                      .getDefaultPrefix(XPath2FilterContainer04.XPathFilter2NS), XPath2FilterContainer04.XPathFilter2NS);
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method apachesample_xfilter2_2
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_xfilter2_2(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceIntersect(doc, "//E")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//B").getElement());

            /*
         transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                 XPath2FilterContainer04.newInstanceSubtract(doc,
                                    "//C").getElement());
         transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                 XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//F").getElement());
         transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                 XPath2FilterContainer04.newInstanceSubtract(doc,
                                    "//G").getElement());
         transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                 XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//H").getElement());
         transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                 XPath2FilterContainer04.newInstanceSubtract(doc,
                                    "//@x:attr").getElement());
             */
            transforms.setXPathNamespaceContext("xmlns:x", "http://foo.bar/");
            transforms
            .setXPathNamespaceContext(Transforms
                                      .getDefaultPrefix(XPath2FilterContainer04.XPathFilter2NS), 
                                      XPath2FilterContainer04.XPathFilter2NS);
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method apachesample_xfilter2_3
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_xfilter2_3(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceIntersect(doc, "//E")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//B").getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceSubtract(doc, "//C")
                                    .getElement());
            transforms.setXPathNamespaceContext("xmlns:x", "http://foo.bar/");
            transforms
            .setXPathNamespaceContext(Transforms
                                      .getDefaultPrefix(XPath2FilterContainer04.XPathFilter2NS), 
                                      XPath2FilterContainer04.XPathFilter2NS);
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method apachesample_xfilter2_4
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_xfilter2_4(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceIntersect(doc, "//E")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//B").getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceSubtract(doc, "//C")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//F").getElement());
            transforms.setXPathNamespaceContext("xmlns:x", "http://foo.bar/");
            transforms
            .setXPathNamespaceContext(Transforms
                                      .getDefaultPrefix(XPath2FilterContainer04.XPathFilter2NS), 
                                      XPath2FilterContainer04.XPathFilter2NS);
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method apachesample_xfilter2_5
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_xfilter2_5(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceIntersect(doc, "//E")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//B").getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceSubtract(doc, "//C")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//F").getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceSubtract(doc, "//G")
                                    .getElement());
            transforms.setXPathNamespaceContext("xmlns:x", "http://foo.bar/");
            transforms
            .setXPathNamespaceContext(Transforms
                                      .getDefaultPrefix(XPath2FilterContainer04.XPathFilter2NS), 
                                      XPath2FilterContainer04.XPathFilter2NS);
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method apachesample_xfilter2_6
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_xfilter2_6(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceIntersect(doc, "//E")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//B").getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceSubtract(doc, "//C")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//F").getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceSubtract(doc, "//G")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//H").getElement());
            transforms.setXPathNamespaceContext("xmlns:x", "http://foo.bar/");
            transforms
            .setXPathNamespaceContext(Transforms
                                      .getDefaultPrefix(XPath2FilterContainer04.XPathFilter2NS), 
                                      XPath2FilterContainer04.XPathFilter2NS);
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method apachesample_xfilter2_7
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] apachesample_xfilter2_7(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        {
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceIntersect(doc, "//E")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//B").getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceSubtract(doc, "//C")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//F").getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceSubtract(doc, "//G")
                                    .getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04.newInstanceUnion(doc,
                                    "//H").getElement());
            transforms.addTransform(XPath2FilterContainer04.XPathFilter2NS,
                                    XPath2FilterContainer04
                                    .newInstanceSubtract(doc, "//@x:attr")
                                    .getElement());
            transforms.setXPathNamespaceContext("xmlns:x", "http://foo.bar/");
            transforms
            .setXPathNamespaceContext(Transforms
                                      .getDefaultPrefix(XPath2FilterContainer04.XPathFilter2NS), XPath2FilterContainer04.XPathFilter2NS);
        }

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }

    /**
     * Method xfilter2spec_xfilter2_3_new
     *
     * @param doc
     *
     * @throws Exception
     */
    public static byte[][] xfilter2spec_xfilter2_3_new(Document doc)
    throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);
        String params[][] = {
                             { XPath2FilterContainer.INTERSECT, "//ToBeSigned" },
                             { XPath2FilterContainer.SUBTRACT, "//NotToBeSigned" },
                             { XPath2FilterContainer.UNION, "//ReallyToBeSigned" }
        };
        NodeList nodeList = XPath2FilterContainer.newInstances(doc, params);

        transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER, nodeList);
        transforms.setXPathNamespaceContext("xfilter2b", Transforms.TRANSFORM_XPATH2FILTER);
        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }
    public static byte[][] pureedge_xfilter2_new(Document doc) throws Exception {

        XMLSignature sig = new XMLSignature(doc, null,
                                            XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);
        String params[][] = {
                             { XPath2FilterContainer.SUBTRACT,
                                 "\n" +
                                 "/XFDL/page[@sid='PAGE1']/*[@sid='CHECK16' or \n" +
                                 "                           @sid='CHECK17' or \n" +
                                 "                           @sid='FIELD47' or \n" +
                                 "                           @sid='BUTTON2' or \n" +
                                 "                           @sid='FIELD48']\n" +
                                 " | \n" +
                                 "/XFDL/page/triggeritem[not(attribute::sid) | \n"  +
                                 "                       /XFDL/page/*/triggeritem]\n" +
                                 " | \n" +
                                 "here()/ancestor::ds:Signature[1]" +
                                 ""
                             }
        };
        NodeList nodeList = XPath2FilterContainer.newInstances(doc, params);
        transforms.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
        transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER, nodeList);

        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        byte[] ref = sig.getSignedInfo().item(0).getTransformsOutput().getBytes();
        byte[][] result = {
                           full, ref
        };

        // we remove the signature now
        sig.getElement().getParentNode().removeChild(sig.getElement());

        return result;
    }
}

