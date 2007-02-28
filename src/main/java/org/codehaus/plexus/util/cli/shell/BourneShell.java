package org.codehaus.plexus.util.cli.shell;

/*
 * Copyright 2007 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason van Zyl
 *  @version $Id$
 */
public class BourneShell
    extends Shell
{
    public BourneShell()
    {
        this( false );
    }
    
    public BourneShell( boolean isLoginShell )
    {
        setShellCommand( "/bin/bash" );
        setSingleQuotedArgumentEscaped( true );
        setSingleQuotedExecutableEscaped( true );
        setQuotedExecutableEnabled( false );
        
        if ( isLoginShell )
        {
            addShellArg( "-l" );
        }
    }
    
    public List getShellArgsList()
    {
        List shellArgs = new ArrayList();
        List existingShellArgs = super.getShellArgsList();
        
        if ( existingShellArgs != null && !existingShellArgs.isEmpty() )
        {
            shellArgs.addAll( existingShellArgs );
        }
        
        shellArgs.add( "-c" );
        
        return shellArgs;
    }
    
    public String[] getShellArgs()
    {
        String[] shellArgs = super.getShellArgs();
        if ( shellArgs == null )
        {
           shellArgs = new String[0];
        }
        
        if ( shellArgs.length > 0 && !shellArgs[shellArgs.length-1].equals( "-c" ) )
        {
            String[] newArgs = new String[shellArgs.length + 1];
            
            System.arraycopy( shellArgs, 0, newArgs, 0, shellArgs.length );
            newArgs[shellArgs.length] = "-c";
            
            shellArgs = newArgs;
        }
        
        return shellArgs;
    }
    
    public String getExecutable()
    {
        File wd = getWorkingDirectory();
        
        if ( wd != null )
        {
            return "cd " + getWorkingDirectory().getAbsolutePath() + " && " + super.getExecutable();
        }
        else
        {
            return super.getExecutable();
        }
    }
    
}
