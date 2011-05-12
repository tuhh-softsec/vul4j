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

import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetProperty;

/**
 * @since 2.1
 */
@ObjectCreate.List( @ObjectCreate( pattern = "catalog/dvd" ) )
public final class AudioVisual
    implements Item
{

    @SetProperty( pattern = "catalog/dvd", attributeName = "year-made" )
    private int yearMade;

    private String category;

    private String name;

    private String desc;

    private int runtime;

    public int getYearMade()
    {
        return yearMade;
    }

    public void setYearMade( int yearMade )
    {
        this.yearMade = yearMade;
    }

    public String getCategory()
    {
        return this.category;
    }

    public void setCategory( String category )
    {
        this.category = category;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDesc()
    {
        return this.desc;
    }

    public void setDesc( String desc )
    {
        this.desc = desc;
    }

    public int getRuntime()
    {
        return this.runtime;
    }

    public void setRuntime( int runtime )
    {
        this.runtime = runtime;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        AudioVisual other = (AudioVisual) obj;
        if ( this.category == null )
        {
            if ( other.getCategory() != null )
                return false;
        }
        else if ( !this.category.equals( other.getCategory() ) )
            return false;
        if ( this.desc == null )
        {
            if ( other.getDesc() != null )
                return false;
        }
        else if ( !this.desc.equals( other.getDesc() ) )
            return false;
        if ( this.name == null )
        {
            if ( other.getName() != null )
                return false;
        }
        else if ( !this.name.equals( other.getName() ) )
            return false;
        if ( this.runtime != other.getRuntime() )
            return false;
        if ( this.yearMade != other.getYearMade() )
            return false;
        return true;
    }

    public void print()
    {
        System.out.println( this.toString() );
    }

}
