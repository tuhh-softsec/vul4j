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
package org.apache.commons.digester3.annotations.rss;

import org.apache.commons.digester3.annotations.AbstractAnnotatedPojoTestCase;
import org.junit.Test;

/**
 * @since 2.1
 */
public final class RssTestCase
    extends AbstractAnnotatedPojoTestCase
{

    @Test
    public void testRss()
        throws Exception
    {
        Channel channel = new Channel();
        channel.setTitle( "Apache" );
        channel.setLink( "http://www.apache.org" );
        channel.setDescription( "The Apache Software Foundation" );
        channel.setLanguage( "en-US" );

        Image image = new Image();
        image.setTitle( "Apache" );
        image.setUrl( "http://jakarta.apache.org/images/jakarta-logo.gif" );
        image.setLink( "http://jakarta.apache.org" );
        image.setDescription( "The Jakarta project. Open source, serverside java." );
        image.setWidth( 505 );
        image.setHeight( 480 );
        channel.setImage( image );

        Item item = new Item();
        item.setTitle( "Commons Attributes 2.1 Released" );
        item.setLink( "http://jakarta.apache.org/site/news/news-2004-2ndHalf.html#20040815.1" );
        item.setDescription( "The Apache Commons team is happy to announce the release of Commons Attributes 2.1. This is the first release of the new Commons-Attributes code." );
        channel.addItem( item );

        item = new Item();
        item.setTitle( "Cloudscape Becomes Apache Derby" );
        item.setLink( "http://jakarta.apache.org/site/news/elsewhere-2004-2ndHalf.html#20040803.1" );
        item.setDescription( "IBM has submitted a proposal to the Apache DB project for a Java-based package to be called 'Derby'." );
        channel.addItem( item );

        item = new Item();
        item.setTitle( "Commons BeanUtils 1.7 Released" );
        item.setLink( "http://jakarta.apache.org/site/news/news-2004-2ndHalf.html#20040802.1" );
        item.setDescription( "" );
        channel.addItem( item );

        item = new Item();
        item.setTitle( "Commons JXPath 1.2 Released" );
        item.setLink( "http://jakarta.apache.org/site/news/news-2004-2ndHalf.html#20040801.2" );
        item.setDescription( "" );
        channel.addItem( item );

        this.verifyExpectedEqualsToParsed( channel );
    }

}
