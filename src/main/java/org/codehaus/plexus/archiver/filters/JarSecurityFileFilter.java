package org.codehaus.plexus.archiver.filters;

import java.io.InputStream;

import org.codehaus.plexus.archiver.ArchiveFileFilter;
import org.codehaus.plexus.archiver.ArchiveFilterException;
import org.codehaus.plexus.util.SelectorUtils;

public class JarSecurityFileFilter
    implements ArchiveFileFilter
{
    
    public static final String[] SECURITY_FILE_PATTERNS = { 
        "/META-INF/*.RSA", 
        "/META-INF/*.DSA", 
        "/META-INF/*.SF", 
        "/META-INF/*.rsa", 
        "/META-INF/*.dsa", 
        "/META-INF/*.sf" 
    };
    
    public boolean include( InputStream dataStream, String entryName )
        throws ArchiveFilterException
    {
        for ( int i = 0; i < SECURITY_FILE_PATTERNS.length; i++ )
        {
            String pattern = SECURITY_FILE_PATTERNS[i];
            
            if ( SelectorUtils.match( pattern, entryName ) )
            {
                return false;
            }
        }
        
        return true;
    }
}
