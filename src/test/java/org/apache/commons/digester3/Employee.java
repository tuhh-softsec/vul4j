/* $Id$
 *
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
package org.apache.commons.digester3;

import java.util.ArrayList;

/**
 * Bean for Digester testing.
 */
public class Employee {

    private final ArrayList<Address> addresses = new ArrayList<Address>();

    private String firstName = null;

    private String lastName = null;

    // this is to allow testing of primitive convertion 
    private int age;

    private boolean active;  

    private float salary;

    public Employee() {
        this("My First Name", "My Last Name");
    }

    public Employee(String firstName, String lastName) {
        setFirstName(firstName);
        setLastName(lastName);
    }

    public void addAddress(Address address) {
        addresses.add(address);
    }

    public Address getAddress(String type) {
        for (Address address : addresses) {
            if (type.equals(address.getType()))
                return (address);
        }
        return (null);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
    }

    public String getFirstName() {
        return (this.firstName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return (this.lastName);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Employee[");
        sb.append("firstName=");
        sb.append(firstName);
        sb.append(", lastName=");
        sb.append(lastName);
        sb.append("]");
        return (sb.toString());
    }

}
