/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.ldap.client.api;


import java.util.HashMap;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Krb5LoginConfiguration extends Configuration
{

    /** The list with configuration entries. */
    private static AppConfigurationEntry[] configList = new AppConfigurationEntry[1];


    /**
     * Creates a new instance of Krb5LoginConfiguration.
     */
    public Krb5LoginConfiguration()
    {
        String loginModule = "com.sun.security.auth.module.Krb5LoginModule";

        HashMap<String, Object> options = new HashMap<String, Object>();

        // TODO: this only works for Sun JVM
        options.put( "refreshKrb5Config", "true" );
        
        LoginModuleControlFlag flag = LoginModuleControlFlag.REQUIRED;
        configList[0] = new AppConfigurationEntry( loginModule, flag, options );
    }


    /**
     * Interface method requiring us to return all the LoginModules we know about.
     *
     * @param applicationName the application name
     * @return the configuration entry
     */
    public AppConfigurationEntry[] getAppConfigurationEntry( String applicationName )
    {
        // We will ignore the applicationName, since we want all apps to use Kerberos V5
        return configList;
    }


    /**
     * Interface method for reloading the configuration.  We don't need this.
     */
    public void refresh()
    {
        // Right now this is a load once scheme and we will not implement the refresh method
    }
}
