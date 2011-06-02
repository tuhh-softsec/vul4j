package org.apache.commons.digester3.examples.api.catalog;

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

/**
 * See Main.java.
 */
public class Book
    implements Item
{

    private String isbn;

    private String title;

    private String author;

    private String desc;

    public Book( String isbn )
    {
        this.isbn = isbn;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public void setAuthor( String author )
    {
        this.author = author;
    }

    public void setDesc( String desc )
    {
        this.desc = desc;
    }

    public void print()
    {
        System.out.println( "Book:" );
        System.out.println( "  isbn=" + isbn );
        System.out.println( "  title=" + title );
        System.out.println( "  author=" + author );
        System.out.println( "  desc=" + desc );
    }

}
