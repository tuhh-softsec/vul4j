package org.codehaus.plexus.archiver.filters;

import java.io.InputStream;

import org.codehaus.plexus.archiver.ArchiveFileFilter;
import org.codehaus.plexus.util.SelectorUtils;


/**
 * @deprecated Use {@link JarSecurityFileSelector}
 */
public class JarSecurityFileFilter
    implements ArchiveFileFilter
{
    public static final String[] SECURITY_FILE_PATTERNS = JarSecurityFileSelector.SECURITY_FILE_PATTERNS;

    public boolean include( InputStream dataStream, String entryName )
    {
		for (String pattern : SECURITY_FILE_PATTERNS) {
			if (SelectorUtils.match(pattern, entryName)) {
				return false;
			}
		}

        return true;
    }
}
