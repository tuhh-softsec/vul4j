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
package org.apache.commons.digester3.annotations.catalog;

import java.util.Collection;
import java.util.Stack;

import org.apache.commons.digester3.annotations.AbstractAnnotatedPojoTestCase;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.junit.Test;

/**
 * @since 2.1
 */
public final class CatalogTestCase
    extends AbstractAnnotatedPojoTestCase
{

    @Test
    public void testCatalog()
        throws Exception
    {
        Catalog catalog = new Catalog();

        Book book = new Book( "0-596-00184-3" );
        book.setTitle( "Ant, The Definitive Guide" );
        book.setAuthor( "Jesse Tilly & Eric M. Burke" );
        book.setDesc( "Complete build management for Java." );
        catalog.addItem( book );

        book = new Book( "0201310058" );
        book.setTitle( "Effective Java" );
        book.setAuthor( "Joshua Bloch" );
        book.setDesc( "Tips for experienced Java software developers." );
        catalog.addItem( book );

        AudioVisual dvd = new AudioVisual();
        dvd.setName( "Drunken Master" );
        dvd.setCategory( "martial arts" );
        dvd.setDesc( "Hilarious slapstick starring Jackie Chan." );
        dvd.setRuntime( 106 );
        dvd.setYearMade( 1978 );
        catalog.addItem( dvd );

        dvd = new AudioVisual();
        dvd.setName( "The Piano" );
        dvd.setCategory( "drama" );
        dvd.setDesc( "Character drama set in New Zealand during the Victorian era." );
        dvd.setRuntime( 121 );
        dvd.setYearMade( 1993 );
        catalog.addItem( dvd );

        this.verifyExpectedEqualsToParsed( catalog );
    }

    @Override
    protected Collection<RulesModule> getAuxModules()
    {
        Collection<RulesModule> modules = new Stack<RulesModule>();
        modules.add( new AbstractRulesModule()
        {

            @Override
            public void configure()
            {
                forPattern( "catalog/dvd/attr" ).setProperty( "id" ).extractingValueFromAttribute( "value" );
            }

        } );
        return modules;
    }

}
