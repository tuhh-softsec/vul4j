package org.codehaus.plexus.util;

/* ====================================================================
 *   Copyright 2001-2004 The Apache Software Foundation.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */

/**
 * Path tool contains static methods to assist in determining path-related
 * information such as relative paths.
 *
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 * @version $Id$
 */
public class PathTool
{
    /**
     * Determines the relative path of a filename from a base directory.
     * This method is useful in building relative links within pages of
     * a web site.  It provides similar functionality to Anakia's
     * <code>$relativePath</code> context variable.  The arguments to
     * this method may contain either forward or backward slashes as
     * file separators.  The relative path returned is formed using
     * forward slashes as it is expected this path is to be used as a
     * link in a web page (again mimicking Anakia's behavior).
     * <p/>
     * This method is thread-safe.
     *
     * @param basedir The base directory.
     * @param filename The filename that is relative to the base
     * directory.
     * @return The relative path of the filename from the base
     * directory.  This value is not terminated with a forward slash.
     * A zero-length string is returned if: the filename is not relative to
     * the base directory, <code>basedir</code> is null or zero-length,
     * or <code>filename</code> is null or zero-length.
     */
    public static final String getRelativePath( String basedir, String filename )
    {
        basedir = uppercaseDrive(basedir);
        filename = uppercaseDrive(filename);

        /*
         * Verify the arguments and make sure the filename is relative
         * to the base directory.
         */
        if ( basedir == null || basedir.length() == 0 || filename == null
            || filename.length() == 0 || !filename.startsWith( basedir ) )
        {
            return "";
        }

        /*
         * Normalize the arguments.  First, determine the file separator
         * that is being used, then strip that off the end of both the
         * base directory and filename.
         */
        String separator = determineSeparator( filename );
        basedir = StringUtils.chompLast( basedir, separator );
        filename = StringUtils.chompLast( filename, separator );

        /*
         * Remove the base directory from the filename to end up with a
         * relative filename (relative to the base directory).  This
         * filename is then used to determine the relative path.
         */
        String relativeFilename = filename.substring( basedir.length() );

        return determineRelativePath( relativeFilename, separator );
    }

    /**
     * Determines the relative path of a filename.  This method is
     * useful in building relative links within pages of a web site.  It
     * provides similar functionality to Anakia's
     * <code>$relativePath</code> context variable.  The argument to
     * this method may contain either forward or backward slashes as
     * file separators.  The relative path returned is formed using
     * forward slashes as it is expected this path is to be used as a
     * link in a web page (again mimicking Anakia's behavior).
     * <p/>
     * This method is thread-safe.
     *
     * @param filename The filename to be parsed.
     * @return The relative path of the filename. This value is not
     * terminated with a forward slash.  A zero-length string is
     * returned if: <code>filename</code> is null or zero-length.
     */
    public static final String getRelativePath( String filename )
    {
        filename = uppercaseDrive(filename);

        if ( filename == null || filename.length() == 0 )
        {
            return "";
        }

        /*
         * Normalize the argument.  First, determine the file separator
         * that is being used, then strip that off the end of the
         * filename.  Then, if the filename doesn't begin with a
         * separator, add one.
         */

        String separator = determineSeparator( filename );
        filename = StringUtils.chompLast( filename, separator );
        if ( !filename.startsWith( separator ) )
        {
            filename = separator + filename;
        }

        return determineRelativePath( filename, separator );
    }

    /**
     * Determines the directory component of a filename.  This is useful
     * within DVSL templates when used in conjunction with the DVSL's
     * <code>$context.getAppValue("infilename")</code> to get the
     * current directory that is currently being processed.
     * <p/>
     * This method is thread-safe.
     *
     * @param filename The filename to be parsed.
     * @return The directory portion of the <code>filename</code>.  If
     * the filename does not contain a directory component, "." is
     * returned.
     */
    public static final String getDirectoryComponent( String filename )
    {
        if ( filename == null || filename.length() == 0 )
        {
            return "";
        }

        String separator = determineSeparator( filename );
        String directory = StringUtils.chomp( filename, separator );

        if ( filename.equals( directory ) )
        {
            return ".";
        }
        else
        {
            return directory;
        }
    }

    /**
     * Determines the relative path of a filename.  For each separator
     * within the filename (except the leading if present), append the
     * "../" string to the return value.
     *
     * @param filename The filename to parse.
     * @param separator The separator used within the filename.
     * @return The relative path of the filename.  This value is not
     * terminated with a forward slash.  A zero-length string is
     * returned if: the filename is zero-length.
     */
    private static final String determineRelativePath( String filename,
                                                       String separator )
    {
        if ( filename.length() == 0 )
        {
            return "";
        }

        
        /*
         * Count the slashes in the relative filename, but exclude the
         * leading slash.  If the path has no slashes, then the filename
         * is relative to the current directory.
         */
        int slashCount = StringUtils.countMatches( filename, separator ) - 1;
        if ( slashCount <= 0 )
        {
            return ".";
        }

        /*
         * The relative filename contains one or more slashes indicating
         * that the file is within one or more directories.  Thus, each
         * slash represents a "../" in the relative path.
         */
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < slashCount; i++ )
        {
            sb.append( "../" );
        }

        /*
         * Finally, return the relative path but strip the trailing
         * slash to mimic Anakia's behavior.
         */
        return StringUtils.chop( sb.toString() );
    }

    /**
     * Helper method to determine the file separator (forward or
     * backward slash) used in a filename.  The slash that occurs more
     * often is returned as the separator.
     *
     * @param filename The filename parsed to determine the file
     * separator.
     * @return The file separator used within <code>filename</code>.
     * This value is either a forward or backward slash.
     */
    private static final String determineSeparator( String filename )
    {
        int forwardCount = StringUtils.countMatches( filename, "/" );
        int backwardCount = StringUtils.countMatches( filename, "\\" );

        return forwardCount >= backwardCount ? "/" : "\\";
    }

    /**
     * Cygwin prefers lowercase drive letters, but other parts of maven use uppercase
     * @param path
     * @return String
     */
    static final String uppercaseDrive(String path)
    {
        if (path == null)
        {
            return null;
        }
        if (path.length() >= 2 && path.charAt(1) == ':')
        {
            path = path.substring(0, 1).toUpperCase() + path.substring(1);
        }
        return path;
    }

    /**
     * Calculates the appropriate link given the preferred link and the relativePath of the document
     * @param link
     * @param relativePath
     * @return String
     */
    public static final String calculateLink(String link, String relativePath)
    {
        //This must be some historical feature
        if (link.startsWith("/site/"))
        {
            return link.substring(5);
        }

        //Allows absolute links in nav-bars etc
        if (link.startsWith("/absolute/"))
        {
            return link.substring(10);
        }

        // This traps urls like http://
        if (link.indexOf(":") >= 0)
        {
            return link;
        }

        //If relativepath is current directory, just pass the link through
        if (relativePath.equals("."))
        {
            if (link.startsWith("/"))
            {
                return link.substring(1);
            }
            else
            {
                return link;
            }
        }

        //If we don't do this, you can end up with ..//bob.html rather than ../bob.html
        if (relativePath.endsWith("/") && link.startsWith("/"))
        {
            return relativePath + "." + link.substring(1);
        }

        if (relativePath.endsWith("/") || link.startsWith("/"))
        {
            return relativePath + link;
        }

        return relativePath + "/" + link;
    }

}
