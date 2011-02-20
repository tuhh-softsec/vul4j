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
package org.apache.commons.digester3.examples.addressbook;

import org.apache.commons.digester3.AbstractRulesModule;

/**
 * See Main.java.
 */
public final class AddressBookModule extends AbstractRulesModule {

    @Override
    protected void configure() {
        forPattern("address-book/person")
            // create a new instance of class Person, and push that
            // object onto the digester stack of objects
            .createObject().ofType(Person.class)
            .then()
            // map *any* attributes on the tag to appropriate
            // setter-methods on the top object on the stack (the Person
            // instance created by the preceeding rule). 
            //
            // For example:
            // if attribute "id" exists on the xml tag, and method setId 
            // with one parameter exists on the object that is on top of
            // the digester object stack, then a call will be made to that
            // method. The value will be type-converted from string to
            // whatever type the target method declares (where possible), 
            // using the commons ConvertUtils functionality.
            //
            // Attributes on the xml tag for which no setter methods exist
            // on the top object on the stack are just ignored.
            .setProperties()
            .then()
            // call the addPerson method on the second-to-top object on
            // the stack (the AddressBook object), passing the top object
            // on the stack (the recently created Person object).
            .setNext("addPerson");
        //--------------------------------------------------        
        // when we encounter a "name" tag, call setName on the top
        // object on the stack, passing the text contained within the
        // body of that name element [specifying a zero parameter count
        // implies one actual parameter, being the body text]. 
        // The top object on the stack will be a person object, because 
        // the pattern address-book/person always triggers the 
        // ObjectCreateRule we added previously.
        forPattern("address-book/person/name")
            .callMethod("setName").usingElementBodyAsArgument();
        //--------------------------------------------------        
        // when we encounter an "email" tag, call addEmail on the top
        // object on the stack, passing two parameters: the "type"
        // attribute, and the text within the tag body.
        forPattern("address-book/person/email")
            .callMethod("addEmail").withParamTypes(String.class, String.class)
            .then()
            .callParam().ofIndex(0).fromAttribute("type")
            .then()
            .callParam().ofIndex(1);
        //--------------------------------------------------        
        // When we encounter an "address" tag, create an instance of class
        // Address and push it on the digester stack of objects. After
        // doing that, call addAddress on the second-to-top object on the
        // digester stack (a "Person" object), passing the top object on
        // the digester stack (the "Address" object). And also set things
        // up so that for each child xml element encountered between the start
        // of the address tag and the end of the address tag, the text 
        // contained in that element is passed to a setXXX method on the 
        // Address object where XXX is the name of the xml element found.
        forPattern("address-book/person/address")
            .createObject().ofType(Address.class)
            .then()
            .setNestedProperties()
            .then()
            .setNext("addAddress");
    }

}
