package org.codehaus.plexus.archiver.tar;

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

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.gzip.GZipUnArchiver;

import java.io.File;
import java.io.IOException;

/**
 * Extract files in tar with gzip compression
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Revision: $
 */
public class TarGZipUnArchiver
    extends TarUnArchiver
{
    public TarGZipUnArchiver()
    {
    }

    public TarGZipUnArchiver( File sourceFile )
    {
        super( sourceFile );
    }

    protected void execute()
        throws ArchiverException
    {
        File tempTarFile;
        try
        {
            tempTarFile = File.createTempFile("tmp",".tar");
        }
        catch ( IOException e )
        {
            throw new ArchiverException("Cannot create temporary file for gzip uncompression", e );
        }

        tempTarFile.delete();

        File originalSourceFile = this.getSourceFile();

        try
        {
            GZipUnArchiver zipUnArchiver = new GZipUnArchiver( getSourceFile() );

            zipUnArchiver.enableLogging( this.getLogger() );

            zipUnArchiver.setDestFile( tempTarFile );

            zipUnArchiver.extract();

            // This is so nasty. You have to make it clear what you're doing. If you're going to take a source file
            // and do something with it like gunzip it and then untar the result then use methods named what they
            // actually do. And setters for this stuff is just so confusing, use a method with a parameter.

            setSourceFile( tempTarFile );

            super.execute();
        }
        finally
        {
            tempTarFile.delete();

            this.setSourceFile( originalSourceFile );
        }

    }
}
