/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.archiver;

import java.io.File;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;

public interface UnArchiver
{

    String ROLE = UnArchiver.class.getName();

    /**
     * Extract the archive.
     *
     * @throws ArchiverException
     */
    void extract()
        throws ArchiverException;

    /**
     * Take a patch into the archive and extract it to the specified directory.
     *
     * @param path Path inside the archive to be extracted.
     * @param outputDirectory Directory to extract to.
     *
     * @throws ArchiverException
     */
    void extract( String path, File outputDirectory )
        throws ArchiverException;

    File getDestDirectory();

    void setDestDirectory( File destDirectory );

    // todo What is this? If you're extracting isn't it always to a directory. I think it would be cool to extract an
    // archive to another archive but I don't think we support this right now.
    File getDestFile();

    void setDestFile( File destFile );

    File getSourceFile();

    void setSourceFile( File sourceFile );

    /**
     * Gets a flag indicating destination files are always overwritten.
     *
     * @return {@code true}, if destination files are overwritten, even if they are newer than the corresponding entry
     * in the archive.
     *
     * @since 3.4
     */
    boolean isOverwrite();

    /**
     * Should we overwrite files in dest, even if they are newer than the corresponding entries in the archive?
     */
    void setOverwrite( boolean b );

    /**
     * Sets a set of {@link FileSelector} instances, which may be used to select the files to extract from the archive.
     * If file selectors are present, then a file is only extracted, if it is confirmed by all file selectors.
     */
    void setFileSelectors( FileSelector[] selectors );

    /**
     * Returns a set of {@link FileSelector} instances, which may be used to select the files to extract from the
     * archive. If file selectors are present, then a file is only extracted, if it is confirmed by all file selectors.
     */
    FileSelector[] getFileSelectors();

    /**
     * to use or not the jvm method for file permissions : user all <b>not active for group permissions</b>
     *
     * @since 1.1
     * @param useJvmChmod
     * @deprecated this setting is now ignored. The jvm is always used.
     */
    @Deprecated
    void setUseJvmChmod( boolean useJvmChmod );

    /**
     * @since 1.1
     * @return
     * @deprecated this setting is now ignored. The jvm is always used.
     */
    @Deprecated
    boolean isUseJvmChmod();

    /**
     * @since 1.1
     */
    boolean isIgnorePermissions();

    /**
     * @since 1.1
     */
    void setIgnorePermissions( final boolean ignorePermissions );

}
