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

package org.apache.commons.digester3.plugins;

import org.apache.commons.digester3.Digester;

public class Slider
    implements Widget
{
    private String label = "nolabel";

    private int min = 0;

    private int max = 0;

    // define rules on this class
    public static void addRules( Digester digester, String pattern )
    {
        digester.addSetProperties( pattern );

        Class<?>[] paramtypes = { Integer.class };
        digester.addCallMethod( pattern + "/min", "setMin", 0, paramtypes );
        digester.addCallMethod( pattern + "/max", "setMax", 0, paramtypes );
    }

    // define different rules on this class
    public static void addRangeRules( Digester digester, String pattern )
    {
        // note: deliberately no addSetProperties rule
        Class<?>[] paramtypes = { Integer.class, Integer.class };
        digester.addCallMethod( pattern + "/range", "setRange", 2, paramtypes );
        digester.addCallParam( pattern + "/range", 0, "min" );
        digester.addCallParam( pattern + "/range", 1, "max" );
    }

    public Slider()
    {
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    public void setMin( int min )
    {
        this.min = min;
    }

    public int getMin()
    {
        return min;
    }

    public void setMax( int max )
    {
        this.max = max;
    }

    public int getMax()
    {
        return max;
    }

    public void setRange( int min, int max )
    {
        this.min = min;
        this.max = max;
    }
}
