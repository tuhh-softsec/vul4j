package org.codehaus.plexus.archiver;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;


/**
 * An @{link ArchiveFinalizer} that process dot files with archiver directives
 * contained within. This basically means you can communicate archive creation
 * instructions between processes using dot files.
 *
 * @author Jason van Zyl
 */
public class DotDirectiveArchiveFinalizer
    extends AbstractArchiveFinalizer
{
    private static final String DEFAULT_DOT_FILE_PREFIX = ".plxarc";

    private final File dotFileDirectory;

    private final String dotFilePrefix;

    public DotDirectiveArchiveFinalizer( File dotFileDirectory )
    {
        this( dotFileDirectory, DEFAULT_DOT_FILE_PREFIX );
    }

    public DotDirectiveArchiveFinalizer( File dotFileDirectory, String dotFilePrefix )
    {
        this.dotFileDirectory = dotFileDirectory;

        this.dotFilePrefix = dotFilePrefix;
    }

    public void finalizeArchiveCreation( Archiver archiver )
        throws ArchiverException
    {
        try
        {
            List dotFiles = FileUtils.getFiles( dotFileDirectory, dotFilePrefix + "*", null );

			for (Object dotFile1 : dotFiles) {
				File dotFile = (File) dotFile1;

				BufferedReader in = new BufferedReader(new FileReader(dotFile));

				String line;

				while ((line = in.readLine()) != null) {
					String[] s = StringUtils.split(line, ":");

					if (s.length == 1) {
						File directory = new File(dotFileDirectory, s[0]);

						System.out.println("adding directory = " + directory);

						archiver.addDirectory(directory);
					} else {
						File directory = new File(dotFileDirectory, s[0]);

						System.out.println("adding directory = " + directory + " to: " + s[1]);

						if (s[1].endsWith("/")) {
							archiver.addDirectory(directory, s[1]);
						} else {
							archiver.addDirectory(directory, s[1] + "/");
						}
					}
				}

				in.close();
			}

        }
        catch ( IOException e )
        {
            throw new ArchiverException( "Error processing dot files.", e );
        }
    }

    public List getVirtualFiles()
    {
        return Collections.EMPTY_LIST;
    }
}
