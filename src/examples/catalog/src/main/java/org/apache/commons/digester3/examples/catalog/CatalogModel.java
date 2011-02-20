/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package org.apache.commons.digester3.examples.catalog;

import org.apache.commons.digester3.AbstractRulesModule;

public final class CatalogModel extends AbstractRulesModule {

    @Override
    protected void configure() {
        // when we encounter the root "catalog" tag, create an
        // instance of the Catalog class. 
        //
        // Note that this approach is different from the approach taken in 
        // the AddressBook example, where an initial "root" object was 
        // explicitly created and pushed onto the digester stack before 
        // parsing started instead
        //
        // Either approach is fine.
        forPattern("catalog").createObject().ofType(Catalog.class);

        forPattern("catalog/book")
            // when we encounter a book tag, we want to create a Book
            // instance. However the Book class doesn't have a default
            // constructor (one with no arguments), so we can't use
            // the ObjectCreateRule. Instead, we use the FactoryCreateRule.
            .factoryCreate().usingFactory(new BookFactory())
            .then()
            // we want each subtag of book to map the text contents of
            // the tag into a bean property with the same name as the tag.
            // eg <title>foo</title> --> setTitle("foo")
            .setNestedProperties()
            .then()
            // and add the book to the parent catalog object (which is
            // the next-to-top object on the digester object stack).
            .setNext("addItem");

        forPattern("catalog/dvd")
           // We are using the "AudioVisual" class to represent both
            // dvds and videos, so when the "dvd" tag is encountered,
            // create an AudioVisual object.
            .createObject().ofType(AudioVisual.class)
            .then()
            // We want to map every xml attribute onto a corresponding
            // property-setter method on the Dvd class instance. However
            // this doesn't work with the xml attribute "year-made", because
            // of the internal hyphen. We could use explicit CallMethodRule
            // rules instead, or use a version of the SetPropertiesRule that
            // allows us to override any troublesome mappings...
            //
            // If there was more than one troublesome mapping, we could
            // use the method variant that takes arrays of xml-attribute-names
            // and bean-property-names to override multiple mappings.
            //
            // For any attributes not explicitly mapped here, the default
            // processing is applied, so xml attribute "category" --> setCategory.
            .setProperties().addAlias("year-made", "yearMade")
            .then()
            // We also need to tell this AudioVisual object that it is actually
            // a dvd; we can use the ObjectParamRule to pass a string to any
            // method. This usage is a little artificial - normally in this
            // situation there would be separate Dvd and Video classes.
            // Note also that equivalent behaviour could be implemented by
            // using factory objects to create & initialise the AudioVisual
            // objects with their type rather than using ObjectCreateRule.
            .callMethod("setType").withParamCount(1)
            .then()
            // pass literal "dvd" string
            .callParam()
            .then()
            // add this dvd to the parent catalog object
            .setNext("addItem");

        // Each tag of form "<attr id="foo" value="bar"/> needs to map
        // to a call to setFoo("bar").
        //
        // This is an alternative to the syntax used for books above (see
        // method addSetNestedProperties), where the name of the subtag 
        // indicated which property to set. Using this syntax in the xml has 
        // advantages and disadvantages both for the user and the application 
        // developer. It is commonly used with the FactoryCreateRule variant 
        // which allows the target class to be created to be specified in an 
        // xml attribute; this feature of FactoryCreateRule is not demonstrated
        // in this example, but see the Apache Tomcat configuration files for 
        // an example of this usage.
        //
        // Note that despite the name similarity, there is no link
        // between SetPropertyRule and SetPropertiesRule.
        forPattern("catalog/dvd/attr").setProperty("id").extractingValueFromAttribute("value");

        // and here we repeat the dvd rules, but for the video tag.
        forPattern("catalog/video")
            .createObject().ofType(AudioVisual.class)
            .then()
            .setProperties().addAlias("year-made", "yearMade")
            .then()
            .callMethod("setType").withParamCount(1)
            .then()
            .objectParam("video")
            .then()
            .setNext("addItem");
        forPattern("catalog/video/attr").setProperty("id").extractingValueFromAttribute("value");
    }

}
