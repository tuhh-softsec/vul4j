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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * See Main.java.
 */
public class Person {

    private final Map<String, String> emails = new HashMap<String, String>();

    private final List<Address> addresses = new ArrayList<Address>();

    private int id;

    private String category;

    private String name;

    /**
     * A unique id for this person. Note that the Digester automatically
     * converts the id to an integer.
     */
    public void setId(int id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** we assume only one email of each type... */
    public void addEmail(String type, String address) {
        emails.put(type, address);
    }

    public void addAddress(Address addr) {
        addresses.add(addr);
    }

    public void print() {
        System.out.println("Person #" + id);
        System.out.println("  category=" + category);
        System.out.println("  name=" + name);

        for (Entry<String, String> email : this.emails.entrySet()) {
            String type = email.getKey();
            String address = email.getValue();

            System.out.println("  email (type " + type + ") : " + address);
        }

        for (Address addr : this.addresses) {
            addr.print(System.out, 2);
        }
    }

}
