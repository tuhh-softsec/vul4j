package org.apache.commons.digester3;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.junit.Test;
import org.xml.sax.SAXException;

public final class Digester133TestCase
{

    public static class MyClass
        extends HashMap<String, String>
    {
        private static final long serialVersionUID = 723339335374093719L;

        private boolean flag = false;

        public boolean isFlag()

        {
            return flag;
        }

        public void setFlag( boolean flag )

        {
            this.flag = flag;
        }
    }

    public static class MyPropertyUtilsBean extends PropertyUtilsBean
    {

        @Override
        protected Object getPropertyOfMapBean( @SuppressWarnings( "rawtypes" ) Map bean, String propertyName )
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
        {
            if ( isReadable( bean, propertyName ) )
            {
                return getSimpleProperty( bean, propertyName );
            }
            return super.getPropertyOfMapBean( bean, propertyName );
        }

        @Override
        protected void setPropertyOfMapBean( @SuppressWarnings( "rawtypes" ) Map bean, String propertyName, Object value )
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
        {
            if ( isWriteable( bean, propertyName ) )
            {
                Class<?> propertyType = getPropertyType( bean, propertyName );
                setSimpleProperty( bean, propertyName, ConvertUtils.convert( value, propertyType ) );
            }
            else
            {
                super.setPropertyOfMapBean( bean, propertyName, value );
            }
        }

    }

    @Test
    public void testDigester()
        throws IOException, SAXException
    {
        PropertyUtilsBean propertyUtils = new MyPropertyUtilsBean();
        ConvertUtilsBean convertUtils = new ConvertUtilsBean();
        BeanUtilsBean beanUtils = new BeanUtilsBean( convertUtils, propertyUtils );
        BeanUtilsBean.setInstance( beanUtils );

        final String xml = "<myclass flag='true' />";
        final Digester digester = new Digester();
        digester.addObjectCreate( "myclass", MyClass.class );
        digester.addSetProperties( "myclass" );
        final MyClass res = digester.parse( new ByteArrayInputStream( xml.getBytes() ) );
        assertTrue( res.isFlag() );
    }

}
