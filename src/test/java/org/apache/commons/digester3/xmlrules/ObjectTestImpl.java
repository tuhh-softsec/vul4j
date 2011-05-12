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

package org.apache.commons.digester3.xmlrules;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Test harness object for holding results of digestion.
 * 
 * @author David H. Martin - Initial Contribution
 * @author Scott Sanders - Added ASL, removed external dependencies
 * @author Tim O'Brien - Added bean property to test bean property setter rule
 */
public class ObjectTestImpl
{

    private ArrayList<Object> children = new ArrayList<Object>();

    private String value = "";

    private Long longValue = new Long( -1L );

    private String property = "";

    private HashMap<String, String> mapValue = new HashMap<String, String>();

    private boolean pushed = false;

    public ObjectTestImpl()
    {
    }

    @Override
    public String toString()
    {
        String str = value;
        for ( Object o : children )
        {
            str += " " + o;
        }
        return str;
    }

    public void add( Object o )
    {
        children.add( o );
    }

    public void setValue( String val )
    {
        value = val;
    }

    public void setLongValue( Long val )
    {
        longValue = val;
    }

    public Long getLongValue()
    {
        return longValue;
    }

    public void setStringValue( String val )
    {
    }

    public boolean isPushed()
    {
        return pushed;
    }

    public void push()
    {
        pushed = true;
    }

    public void setMapValue( String name, String value )
    {
        this.mapValue.put( name, value );
    }

    public String getMapValue( String name )
    {
        return this.mapValue.get( name );
    }

    public String getProperty()
    {
        return property;
    }

    public void setProperty( String pProperty )
    {
        property = pProperty;
    }
}
