package org.apache.commons.digester3.examples.plugins.pipeline;

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
 * An implementation of the Transform interface which converts all
 * input text to either upper or lower case.
 * <p>
 * Note that because it doesn't use any nested tags for configuration,
 * just xml attributes which map 1:1 onto bean property-setter methods,
 * there is no need to define any custom addRules method to use this
 * as a Digester plugin class.
 */
public class CaseTransform
    implements Transform
{

    private boolean toLower = true;

    public void setCase( String caseType )
    {
        if ( caseType.equalsIgnoreCase( "upper" ) )
        {
            toLower = false;
        }
        else
        {
            toLower = true;
        }
    }

    public String transform( String s )
    {
        if ( toLower )
        {
            return s.toLowerCase();
        }
        return s.toUpperCase();
    }

}
