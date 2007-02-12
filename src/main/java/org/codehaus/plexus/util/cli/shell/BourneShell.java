package org.codehaus.plexus.util.cli.shell;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason van Zyl
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
        
        existingShellArgs.add( "-c" );
        
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
    
}
