/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

import java.util.LinkedList;
import java.util.Iterator;

/**
 * See Main.java.
 */
public class AddressBook {
    LinkedList people = new LinkedList();
    
    public void addPerson(Person p) {
        people.addLast(p);
    }
    
    public void print() {
        System.out.println("Address book has " + people.size() + " entries");

        for(Iterator i = people.iterator(); i.hasNext(); ) {
            Person p = (Person) i.next();
            p.print();
        }
    }
}
