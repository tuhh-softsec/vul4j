package org.codehaus.plexus.archiver.jar;

/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.EnumeratedAttribute;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.archiver.zip.ZipEntry;
import org.codehaus.plexus.archiver.zip.ZipFile;
import org.codehaus.plexus.archiver.zip.ZipOutputStream;

/**
 * Base class for tasks that build archives in JAR file format.
 *
 * @version $Revision$ $Date$
 */
public class JarArchiver
    extends ZipArchiver
{
    /**
     * the name of the meta-inf dir
     */
    private static final String META_INF_NAME = "META-INF";
    
    /**
     * The index file name.
     */
    private static final String INDEX_NAME = "META-INF/INDEX.LIST";

    /**
     * The manifest file name.
     */
    private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";

    /**
     * merged manifests added through addConfiguredManifest
     */
    private Manifest configuredManifest;

    /**
     * shadow of the above if upToDate check alters the value
     */
    private Manifest savedConfiguredManifest;

    /**
     * merged manifests added through filesets
     */
    private Manifest filesetManifest;

    /**
     * Manifest of original archive, will be set to null if not in
     * update mode.
     */
    private Manifest originalManifest;

    /**
     * whether to merge fileset manifests;
     * value is true if filesetmanifest is 'merge' or 'mergewithoutmain'
     */
    private FilesetManifestConfig filesetManifestConfig;

    /**
     * whether to merge the main section of fileset manifests;
     * value is true if filesetmanifest is 'merge'
     */
    private boolean mergeManifestsMain = true;

    /**
     * the manifest specified by the 'manifest' attribute *
     */
    private Manifest manifest;

    /**
     * The encoding to use when reading in a manifest file
     */
    private String manifestEncoding;

    /**
     * The file found from the 'manifest' attribute.  This can be
     * either the location of a manifest, or the name of a jar added
     * through a fileset.  If its the name of an added jar, the
     * manifest is looked for in META-INF/MANIFEST.MF
     */
    private File manifestFile;

    /**
     * jar index is JDK 1.3+ only
     */
    private boolean index = false;

    /**
     * whether to really create the archive in createEmptyZip, will
     * get set in getResourcesToAdd.
     */
    private boolean createEmpty = false;

    /**
     * Stores all files that are in the root of the archive (i.e. that
     * have a name that doesn't contain a slash) so they can get
     * listed in the index.
     * <p/>
     * Will not be filled unless the user has asked for an index.
     */
    private Vector rootEntries;

    /**
     * Path containing jars that shall be indexed in addition to this archive.
     */
    private ArrayList indexJars;
    
    /**
     * constructor
     */
    public JarArchiver()
    {
        super();
        archiveType = "jar";
        setEncoding( "UTF8" );
        rootEntries = new Vector();
    }

    /**
     * Set whether or not to create an index list for classes.
     * This may speed up classloading in some cases.
     */
    public void setIndex( boolean flag )
    {
        index = flag;
    }

    /**
     * Set whether or not to create an index list for classes.
     * This may speed up classloading in some cases.
     */
    public void setManifestEncoding( String manifestEncoding )
    {
        this.manifestEncoding = manifestEncoding;
    }

    /**
     * Allows the manifest for the archive file to be provided inline
     * in the build file rather than in an external file.
     *
     * @param newManifest
     * @throws ManifestException
     */
    public void addConfiguredManifest( Manifest newManifest )
        throws ManifestException
    {
        if ( configuredManifest == null )
        {
            configuredManifest = newManifest;
        }
        else
        {
            configuredManifest.merge( newManifest );
        }
        savedConfiguredManifest = configuredManifest;
    }

    /**
     * The manifest file to use. This can be either the location of a manifest,
     * or the name of a jar added through a fileset. If its the name of an added
     * jar, the task expects the manifest to be in the jar at META-INF/MANIFEST.MF.
     *
     * @param manifestFile the manifest file to use.
     */
    public void setManifest( File manifestFile )
        throws ArchiverException
    {
        if ( !manifestFile.exists() )
        {
            throw new ArchiverException( "Manifest file: " + manifestFile
                                         + " does not exist." );
        }

        this.manifestFile = manifestFile;
    }

    private Manifest getManifest( File manifestFile )
        throws ArchiverException
    {
        Manifest newManifest = null;
        FileInputStream fis;
        InputStreamReader isr = null;
        try
        {
            fis = new FileInputStream( manifestFile );
            if ( manifestEncoding == null )
            {
                isr = new InputStreamReader( fis );
            }
            else
            {
                isr = new InputStreamReader( fis, manifestEncoding );
            }
            newManifest = getManifest( isr );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new ArchiverException( "Unsupported encoding while reading manifest: "
                                         + e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new ArchiverException( "Unable to read manifest file: "
                                         + manifestFile
                                         + " (" + e.getMessage() + ")", e );
        }
        finally
        {
            if ( isr != null )
            {
                try
                {
                    isr.close();
                }
                catch ( IOException e )
                {
                    // do nothing
                }
            }
        }
        return newManifest;
    }

    private Manifest getManifest( Reader r )
        throws ArchiverException
    {
        Manifest newManifest;
        try
        {
            newManifest = new Manifest( r );
        }
        catch ( ManifestException e )
        {
            getLogger().error( "Manifest is invalid: " + e.getMessage() );
            throw new ArchiverException( "Invalid Manifest: " + manifestFile,
                                         e );
        }
        catch ( IOException e )
        {
            throw new ArchiverException( "Unable to read manifest file"
                                         + " (" + e.getMessage() + ")", e );
        }
        return newManifest;
    }

    /**
     * Behavior when a Manifest is found in a zipfileset or zipgroupfileset file.
     * Valid values are "skip", "merge", and "mergewithoutmain".
     * "merge" will merge all of manifests together, and merge this into any
     * other specified manifests.
     * "mergewithoutmain" merges everything but the Main section of the manifests.
     * Default value is "skip".
     * <p/>
     * Note: if this attribute's value is not "skip", the created jar will not
     * be readable by using java.util.jar.JarInputStream
     *
     * @param config setting for found manifest behavior.
     */
    public void setFilesetmanifest( FilesetManifestConfig config )
    {
        filesetManifestConfig = config;
        mergeManifestsMain = "merge".equals( config.getValue() );

        if ( filesetManifestConfig != null
             && !filesetManifestConfig.getValue().equals( "skip" ) )
        {

            doubleFilePass = true;
        }
    }

    /**
     * Adds a zipfileset to include in the META-INF directory.
     *
     * @param fs zipfileset to add
     */
/*    public void addMetainf(ZipFileSet fs) {
        // We just set the prefix for this fileset, and pass it up.
        fs.setPrefix("META-INF/");
        super.addFileset(fs);
    }
*/

    /**
     *
     */
    public void addConfiguredIndexJars( File indexJar )
    {
        if ( indexJars == null )
        {
            indexJars = new ArrayList();
        }
        indexJars.add( indexJar.getAbsolutePath() );
    }

    protected void initZipOutputStream( ZipOutputStream zOut )
        throws IOException, ArchiverException
    {

        if ( !skipWriting )
        {
            Manifest jarManifest = createManifest();
            writeManifest( zOut, jarManifest );
        }
    }

    private Manifest createManifest()
        throws ArchiverException
    {
        try
        {
            Manifest finalManifest = Manifest.getDefaultManifest();

            if ( manifest == null )
            {
                if ( manifestFile != null )
                {
                    // if we haven't got the manifest yet, attempt to
                    // get it now and have manifest be the final merge
                    manifest = getManifest( manifestFile );
                }
            }

            /*
             * Precedence: manifestFile wins over inline manifest,
             * over manifests read from the filesets over the original
             * manifest.
             *
             * merge with null argument is a no-op
             */

            if ( isInUpdateMode() )
            {
                finalManifest.merge( originalManifest );
            }
            finalManifest.merge( filesetManifest );
            finalManifest.merge( configuredManifest );
            finalManifest.merge( manifest, !mergeManifestsMain );

            return finalManifest;

        }
        catch ( ManifestException e )
        {
            getLogger().error( "Manifest is invalid: " + e.getMessage() );
            throw new ArchiverException( "Invalid Manifest", e );
        }
    }

    private void writeManifest( ZipOutputStream zOut, Manifest manifest )
        throws IOException, ArchiverException
    {
        for ( Enumeration e = manifest.getWarnings();
              e.hasMoreElements(); )
        {
            getLogger().warn( "Manifest warning: " + e.nextElement() );
        }

        zipDir( null, zOut, "META-INF/", DEFAULT_DIR_MODE );
        // time to write the manifest
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter( baos, "UTF-8" );
        PrintWriter writer = new PrintWriter( osw );
        manifest.write( writer );
        writer.flush();

        ByteArrayInputStream bais =
            new ByteArrayInputStream( baos.toByteArray() );
        super.zipFile( bais, zOut, MANIFEST_NAME,
                       System.currentTimeMillis(), null,
                       DEFAULT_FILE_MODE );
        super.initZipOutputStream( zOut );
    }

    protected void finalizeZipOutputStream( ZipOutputStream zOut )
        throws IOException, ArchiverException
    {

        if ( index )
        {
            createIndexList( zOut );
        }
    }

    /**
     * Create the index list to speed up classloading.
     * This is a JDK 1.3+ specific feature and is enabled by default. See
     * <a href="http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html#JAR%20Index">
     * the JAR index specification</a> for more details.
     *
     * @param zOut the zip stream representing the jar being built.
     * @throws IOException thrown if there is an error while creating the
     *                     index and adding it to the zip stream.
     */
    private void createIndexList( ZipOutputStream zOut )
        throws IOException, ArchiverException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // encoding must be UTF8 as specified in the specs.
        PrintWriter writer = new PrintWriter( new OutputStreamWriter( baos,
                                                                      "UTF8" ) );

        // version-info blankline
        writer.println( "JarIndex-Version: 1.0" );
        writer.println();

        // header newline
        writer.println( getDestFile().getName() );

        // filter out META-INF if it doesn't contain anything other than the index and manifest.
        // this is what sun.misc.JarIndex does, guess we ought to be consistent.
        HashSet filteredDirs = new HashSet(addedDirs.keySet());
        // our added dirs always have a trailing slash
        if(filteredDirs.contains(META_INF_NAME+"/")) {
            boolean add = false;
            Iterator i = entries.keySet().iterator();
            while(i.hasNext()) {
                String entry = (String)i.next();
                if(entry.startsWith(META_INF_NAME+"/") &&
                        !entry.equals(INDEX_NAME) && !entry.equals(MANIFEST_NAME)) {
                    add = true;
                    break;
                }
            }
            if(!add)
                filteredDirs.remove(META_INF_NAME+"/");
        }
        writeIndexLikeList( new ArrayList( filteredDirs ),
                            rootEntries, writer );
        writer.println();

        if ( indexJars != null )
        {
            Manifest mf = createManifest();
            Manifest.Attribute classpath =
                mf.getMainSection().getAttribute( Manifest.ATTRIBUTE_CLASSPATH );
            String[] cpEntries = null;
            if ( classpath != null )
            {
                StringTokenizer tok = new StringTokenizer( classpath.getValue(),
                                                           " " );
                cpEntries = new String[tok.countTokens()];
                int c = 0;
                while ( tok.hasMoreTokens() )
                {
                    cpEntries[ c++ ] = tok.nextToken();
                }
            }

            for ( Iterator i = indexJars.iterator(); i.hasNext(); )
            {
                String indexJar = (String)i.next();
                String name = findJarName( indexJar, cpEntries );
                if ( name != null )
                {
                    ArrayList dirs = new ArrayList();
                    ArrayList files = new ArrayList();
                    grabFilesAndDirs( indexJar, dirs, files );
                    if ( dirs.size() + files.size() > 0 )
                    {
                        writer.println( name );
                        writeIndexLikeList( dirs, files, writer );
                        writer.println();
                    }
                }
            }
        }

        writer.flush();
        ByteArrayInputStream bais =
            new ByteArrayInputStream( baos.toByteArray() );
        super.zipFile( bais, zOut, INDEX_NAME, System.currentTimeMillis(), null,
                       DEFAULT_FILE_MODE );
    }

    /**
     * Overridden from Zip class to deal with manifests and index lists.
     */
    protected void zipFile( InputStream is, ZipOutputStream zOut, String vPath,
                            long lastModified, File fromArchive, int mode )
        throws IOException, ArchiverException
    {
        if ( MANIFEST_NAME.equalsIgnoreCase( vPath ) )
        {
            if ( !doubleFilePass || skipWriting )
            {
                filesetManifest( fromArchive, is );
            }
        }
        else if ( INDEX_NAME.equalsIgnoreCase( vPath ) && index )
        {
            getLogger().warn( "Warning: selected " + archiveType
                              + " files include a META-INF/INDEX.LIST which will"
                              + " be replaced by a newly generated one." );
        }
        else
        {
            if ( index && vPath.indexOf( "/" ) == -1 )
            {
                rootEntries.addElement( vPath );
            }
            super.zipFile( is, zOut, vPath, lastModified, fromArchive, mode );
        }
    }

    private void filesetManifest( File file, InputStream is )
        throws ArchiverException
    {
        if ( manifestFile != null && manifestFile.equals( file ) )
        {
            // If this is the same name specified in 'manifest', this
            // is the manifest to use
            getLogger().debug( "Found manifest " + file );
            try
            {
                if ( is != null )
                {
                    InputStreamReader isr;
                    if ( manifestEncoding == null )
                    {
                        isr = new InputStreamReader( is );
                    }
                    else
                    {
                        isr = new InputStreamReader( is, manifestEncoding );
                    }
                    manifest = getManifest( isr );
                }
                else
                {
                    manifest = getManifest( file );
                }
            }
            catch ( UnsupportedEncodingException e )
            {
                throw new ArchiverException( "Unsupported encoding while reading "
                                             + "manifest: " + e.getMessage(), e );
            }
        }
        else if ( filesetManifestConfig != null
                  && !filesetManifestConfig.getValue().equals( "skip" ) )
        {
            // we add this to our group of fileset manifests
            getLogger().debug( "Found manifest to merge in file " + file );

            try
            {
                Manifest newManifest;
                if ( is != null )
                {
                    InputStreamReader isr;
                    if ( manifestEncoding == null )
                    {
                        isr = new InputStreamReader( is );
                    }
                    else
                    {
                        isr = new InputStreamReader( is, manifestEncoding );
                    }
                    newManifest = getManifest( isr );
                }
                else
                {
                    newManifest = getManifest( file );
                }

                if ( filesetManifest == null )
                {
                    filesetManifest = newManifest;
                }
                else
                {
                    filesetManifest.merge( newManifest );
                }
            }
            catch ( UnsupportedEncodingException e )
            {
                throw new ArchiverException( "Unsupported encoding while reading "
                                             + "manifest: " + e.getMessage(), e );
            }
            catch ( ManifestException e )
            {
                getLogger().error( "Manifest in file " + file + " is invalid: "
                                   + e.getMessage() );
                throw new ArchiverException( "Invalid Manifest", e );
            }
        }
        else
        {
            // assuming 'skip' otherwise
            // don't warn if skip has been requested explicitly, warn if user
            // didn't set the attribute

            // Hide warning also as it makes no sense since
            // the filesetmanifest attribute itself has been
            // hidden

            //int logLevel = filesetManifestConfig == null ?
            //    Project.MSG_WARN : Project.MSG_VERBOSE;
            //log("File " + file
            //    + " includes a META-INF/MANIFEST.MF which will be ignored. "
            //    + "To include this file, set filesetManifest to a value other "
            //    + "than 'skip'.", logLevel);
        }
    }

    /**
     * Collect the resources that are newer than the corresponding
     * entries (or missing) in the original archive.
     * <p/>
     * <p>If we are going to recreate the archive instead of updating
     * it, all resources should be considered as new, if a single one
     * is.  Because of this, subclasses overriding this method must
     * call <code>super.getResourcesToAdd</code> and indicate with the
     * third arg if they already know that the archive is
     * out-of-date.</p>
     *
     * @param filesets    The filesets to grab resources from
     * @param zipFile     intended archive file (may or may not exist)
     * @param needsUpdate whether we already know that the archive is
     *                    out-of-date.  Subclasses overriding this method are supposed to
     *                    set this value correctly in their call to
     *                    super.getResourcesToAdd.
     * @return an map of resources to add for each fileset passed in as well
     *         as a flag that indicates whether the archive is uptodate.
     * @throws ArchiverException if it likes
     */
/*    protected Map getResourcesToAdd(FileSet[] filesets,
                                             File zipFile,
                                             boolean needsUpdate)
        throws ArchiverException {

        // need to handle manifest as a special check
        if (zipFile.exists()) {
            // if it doesn't exist, it will get created anyway, don't
            // bother with any up-to-date checks.

            try {
                originalManifest = getManifestFromJar(zipFile);
                if (originalManifest == null) {
                    getLogger().debug("Updating jar since the current jar has no manifest");
                    needsUpdate = true;
                } else {
                    Manifest mf = createManifest();
                    if (!mf.equals(originalManifest)) {
                        getLogger().debug("Updating jar since jar manifest has changed");
                        needsUpdate = true;
                    }
                }
            } catch (Throwable t) {
                getLogger().warn("error while reading original manifest: " + t.getMessage());
                needsUpdate = true;
            }

        } else {
            // no existing archive
            needsUpdate = true;
        }

        createEmpty = needsUpdate;
        return super.getResourcesToAdd(filesets, zipFile, needsUpdate);
    }
*/

    /**
     */
    protected boolean createEmptyZip( File zipFile )
        throws ArchiverException
    {
        if ( !createEmpty )
        {
            return true;
        }

        ZipOutputStream zOut = null;
        try
        {
            getLogger().debug( "Building MANIFEST-only jar: "
                               + getDestFile().getAbsolutePath() );
            zOut = new ZipOutputStream( new FileOutputStream( getDestFile() ) );

            zOut.setEncoding( getEncoding() );
            if ( isCompress() )
            {
                zOut.setMethod( ZipOutputStream.DEFLATED );
            }
            else
            {
                zOut.setMethod( ZipOutputStream.STORED );
            }
            initZipOutputStream( zOut );
            finalizeZipOutputStream( zOut );
        }
        catch ( IOException ioe )
        {
            throw new ArchiverException( "Could not create almost empty JAR archive"
                                         + " (" + ioe.getMessage() + ")", ioe );
        }
        finally
        {
            // Close the output stream.
            try
            {
                if ( zOut != null )
                {
                    zOut.close();
                }
            }
            catch ( IOException ex )
            {
            }
            createEmpty = false;
        }
        return true;
    }

    /**
     * Make sure we don't think we already have a MANIFEST next time this task
     * gets executed.
     *
     * @see ZipArchiver#cleanUp
     */
    protected void cleanUp()
    {
        super.cleanUp();

        // we want to save this info if we are going to make another pass
        if ( !doubleFilePass || !skipWriting )
        {
            manifest = null;
            configuredManifest = savedConfiguredManifest;
            filesetManifest = null;
            originalManifest = null;
        }
        rootEntries.removeAllElements();
    }

    /**
     * reset to default values.
     *
     * @see ZipArchiver#reset
     */
    public void reset()
    {
        super.reset();
        configuredManifest = null;
        filesetManifestConfig = null;
        mergeManifestsMain = false;
        manifestFile = null;
        index = false;
    }

    public static class FilesetManifestConfig
        extends EnumeratedAttribute
    {
        public String[] getValues()
        {
            return new String[]{"skip", "merge", "mergewithoutmain"};
        }
    }

    /**
     * Writes the directory entries from the first and the filenames
     * from the second list to the given writer, one entry per line.
     */
    protected final void writeIndexLikeList( List dirs, List files,
                                             PrintWriter writer )
    {
        // JarIndex is sorting the directories by ascending order.
        // it has no value but cosmetic since it will be read into a
        // hashtable by the classloader, but we'll do so anyway.
        Collections.sort( dirs );
        Collections.sort( files );
        Iterator iter = dirs.iterator();
        while ( iter.hasNext() )
        {
            String dir = (String) iter.next();

            // try to be smart, not to be fooled by a weird directory name
            dir = dir.replace( '\\', '/' );
            if ( dir.startsWith( "./" ) )
            {
                dir = dir.substring( 2 );
            }
            while ( dir.startsWith( "/" ) )
            {
                dir = dir.substring( 1 );
            }
            int pos = dir.lastIndexOf( '/' );
            if ( pos != -1 )
            {
                dir = dir.substring( 0, pos );
            }
            
            // name newline
            writer.println( dir );
        }

        iter = files.iterator();
        while ( iter.hasNext() )
        {
            writer.println( iter.next() );
        }
    }

    /**
     * try to guess the name of the given file.
     * <p/>
     * <p>If this jar has a classpath attribute in its manifest, we
     * can assume that it will only require an index of jars listed
     * there.  try to find which classpath entry is most likely the
     * one the given file name points to.</p>
     * <p/>
     * <p>In the absence of a classpath attribute, assume the other
     * files will be placed inside the same directory as this jar and
     * use their basename.</p>
     * <p/>
     * <p>if there is a classpath and the given file doesn't match any
     * of its entries, return null.</p>
     */
    protected static final String findJarName( String fileName,
                                               String[] classpath )
    {
        if ( classpath == null )
        {
            return ( new File( fileName ) ).getName();
        }
        fileName = fileName.replace( File.separatorChar, '/' );
        TreeMap matches = new TreeMap( new Comparator()
        {
            // longest match comes first
            public int compare( Object o1, Object o2 )
            {
                if ( o1 instanceof String && o2 instanceof String )
                {
                    return ( (String) o2 ).length()
                           - ( (String) o1 ).length();
                }
                return 0;
            }
        } );

        for ( int i = 0; i < classpath.length; i++ )
        {
            if ( fileName.endsWith( classpath[ i ] ) )
            {
                matches.put( classpath[ i ], classpath[ i ] );
            }
            else
            {
                int slash = classpath[ i ].indexOf( "/" );
                String candidate = classpath[ i ];
                while ( slash > -1 )
                {
                    candidate = candidate.substring( slash + 1 );
                    if ( fileName.endsWith( candidate ) )
                    {
                        matches.put( candidate, classpath[ i ] );
                        break;
                    }
                    slash = candidate.indexOf( "/" );
                }
            }
        }

        return matches.size() == 0
               ? null : (String) matches.get( matches.firstKey() );
    }

    /**
     * Grab lists of all root-level files and all directories
     * contained in the given archive.
     */
    protected static final void grabFilesAndDirs( String file, List dirs,
                                                  List files )
        throws IOException
    {
        ZipFile zf = null;
        try
        {
            zf = new ZipFile( file, "utf-8" );
            Enumeration entries = zf.getEntries();
            HashSet dirSet = new HashSet();
            while ( entries.hasMoreElements() )
            {
                ZipEntry ze =
                    (ZipEntry) entries.nextElement();
                String name = ze.getName();
                // avoid index for manifest-only jars.
                if (!name.equals(META_INF_NAME) && !name.equals(META_INF_NAME+"/") && 
                        !name.equals(INDEX_NAME) && !name.equals(MANIFEST_NAME))
                {
                    if ( ze.isDirectory() )
                    {
                        dirSet.add( name );
                    }
                    else if ( name.indexOf( "/" ) == -1 )
                    {
                        files.add( name );
                    }
                    else
                    {
                        // a file, not in the root
                        // since the jar may be one without directory
                        // entries, add the parent dir of this file as
                        // well.
                        dirSet.add( name.substring( 0,
                                                    name.lastIndexOf( "/" ) + 1 ) );
                    }
                }
            }
            dirs.addAll( dirSet );
        }
        finally
        {
            if ( zf != null )
            {
                zf.close();
            }
        }
    }
}
