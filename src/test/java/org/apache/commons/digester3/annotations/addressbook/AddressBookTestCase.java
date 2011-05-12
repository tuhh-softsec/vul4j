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
package org.apache.commons.digester3.annotations.addressbook;

import org.apache.commons.digester3.annotations.AbstractAnnotatedPojoTestCase;
import org.junit.Test;

/**
 * @since 2.1
 */
public final class AddressBookTestCase
    extends AbstractAnnotatedPojoTestCase
{

    @Test
    public void testAddressBook()
        throws Exception
    {
        AddressBook addressBook = new AddressBook();

        Person person = new Person();
        person.setId( 1 );
        person.setCategory( "acquaintance" );
        person.setName( "Gonzo" );
        person.addEmail( "business", "gonzo@muppets.com" );

        Address address = new Address();
        address.setType( "home" );
        address.setStreet( "123 Maine Ave." );
        address.setCity( "Las Vegas" );
        address.setState( "NV" );
        address.setZip( "01234" );
        address.setCountry( "USA" );
        person.addAddress( address );

        address = new Address();
        address.setType( "business" );
        address.setStreet( "234 Maple Dr." );
        address.setCity( "Los Angeles" );
        address.setState( "CA" );
        address.setZip( "98765" );
        address.setCountry( "USA" );
        person.addAddress( address );

        addressBook.addPerson( person );

        person = new Person();
        person.setId( 2 );
        person.setCategory( "rolemodel" );
        person.setName( "Kermit" );
        person.addEmail( "business", "kermit@muppets.com" );
        person.addEmail( "home", "kermie@acme.com" );

        address = new Address();
        address.setType( "business" );
        address.setStreet( "987 Brown Rd" );
        address.setCity( "Las Cruces" );
        address.setState( "NM" );
        address.setZip( "75321" );
        address.setCountry( "USA" );
        person.addAddress( address );

        addressBook.addPerson( person );

        this.verifyExpectedEqualsToParsed( addressBook );
    }

}
