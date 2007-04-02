package org.codehaus.plexus.archiver.filters;

import java.io.IOException;

import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.util.SelectorUtils;


/**
 * @since 1.0-alpha-9
 */
public class JarSecurityFileSelector implements FileSelector
{
    public static final String ROLE_HINT = "jar-security";
    
    public static final String[] SECURITY_FILE_PATTERNS = { 
        "/META-INF/*.RSA", 
        "/META-INF/*.DSA", 
        "/META-INF/*.SF", 
        "/META-INF/*.rsa", 
        "/META-INF/*.dsa", 
        "/META-INF/*.sf" 
    };
    
    public boolean isSelected( FileInfo fileInfo ) throws IOException
    {
        String name = fileInfo.getName();
        for ( int i = 0; i < SECURITY_FILE_PATTERNS.length; i++ )
        {
            String pattern = SECURITY_FILE_PATTERNS[i];
            
            if ( SelectorUtils.match( pattern, name ) )
            {
                return false;
            }
        }
        
        return true;
     }
}
