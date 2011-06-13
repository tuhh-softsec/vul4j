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
package org.apache.commons.digester3.annotations.servletbean;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.CallMethod;
import org.apache.commons.digester3.annotations.rules.CallParam;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;

/**
 * @since 2.1
 */
@ObjectCreate( pattern = "web-app/servlet" )
public final class ServletBean
{

    private final Map<String, String> initParams = new HashMap<String, String>();

    @BeanPropertySetter( pattern = "web-app/servlet/servlet-name" )
    private String servletName;

    @BeanPropertySetter( pattern = "web-app/servlet/servlet-class" )
    private String servletClass;

    @CallMethod( pattern = "web-app/servlet/init-param" )
    public void addInitParam( @CallParam( pattern = "web-app/servlet/init-param/param-name" ) String name,
                              @CallParam( pattern = "web-app/servlet/init-param/param-value" ) String value )
    {
        this.initParams.put( name, value );
    }

    public String getServletName()
    {
        return servletName;
    }

    public void setServletName( String servletName )
    {
        this.servletName = servletName;
    }

    public String getServletClass()
    {
        return servletClass;
    }

    public void setServletClass( String servletClass )
    {
        this.servletClass = servletClass;
    }

    public Map<String, String> getInitParams()
    {
        return initParams;
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
        ServletBean other = (ServletBean) obj;
        if ( initParams == null )
        {
            if ( other.initParams != null )
                return false;
        }
        else if ( !initParams.equals( other.initParams ) )
            return false;
        if ( servletClass == null )
        {
            if ( other.servletClass != null )
                return false;
        }
        else if ( !servletClass.equals( other.servletClass ) )
            return false;
        if ( servletName == null )
        {
            if ( other.servletName != null )
                return false;
        }
        else if ( !servletName.equals( other.servletName ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ServletBean [initParams=" + initParams + ", servletClass=" + servletClass + ", servletName="
            + servletName + "]";
    }

}
