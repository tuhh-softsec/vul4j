/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.ldap.aci.protectedItem;

import org.apache.directory.shared.ldap.aci.ProtectedItem;

/**
 * The entry contents as a whole. In case of a family member, it also means
 * the entry content of each subordinate family member within the same
 * compound attribute. It does not necessarily include the information in
 * these entries. This element shall be ignored if the classes element is
 * present, since this latter element selects protected entries (and
 * subordinate family members) on the basis of their object class.
 */
public class EntryItem extends ProtectedItem
{
    public EntryItem()
    {
    }


    public String toString()
    {
        return "entry";
    }
}
