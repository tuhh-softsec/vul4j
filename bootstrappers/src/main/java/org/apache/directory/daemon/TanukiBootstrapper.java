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

package org.apache.directory.daemon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

/**
 * The bootstrapper used by Tanuki Wrapper.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: $
 */
public class TanukiBootstrapper extends Bootstrapper implements WrapperListener
{
    private static final Logger log = LoggerFactory.getLogger( Bootstrapper.class );


    private TanukiBootstrapper()
    {
    }

    public static void main( String[] args )
    {
        WrapperManager.start( new TanukiBootstrapper(), args );
    }

    public Integer start(String[] args) {
        setInstallationLayout( args[0] );
        setParentLoader( Thread.currentThread().getContextClassLoader() );
        callInit( shift( args, 1 ) );
        callStart();
        return null;
    }

    public int stop(int exitCode) {
        log.info( "Attempting graceful shutdown of this server instance" );

        callStop( EMPTY_STRARRAY );
        callDestroy();

        log.info( "Completed graceful shutdown..." );

        return exitCode;
    }

    public void controlEvent(int event) {
        log.error("Recvd Event: " + event);
        if (WrapperManager.isControlledByNativeWrapper()) {
            // The Wrapper will take care of this event
        } else {
            // We are not being controlled by the Wrapper, so
            //  handle the event ourselves.
            if ((event == WrapperManager.WRAPPER_CTRL_C_EVENT) ||
                    (event == WrapperManager.WRAPPER_CTRL_CLOSE_EVENT) ||
                    (event == WrapperManager.WRAPPER_CTRL_SHUTDOWN_EVENT)){
                WrapperManager.stop(0);
            }
        }
    }

}
