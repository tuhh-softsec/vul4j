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
import org.codehaus.plexus.archiver.bzip2.BZip2UnArchiver;

import java.io.File;
import java.io.IOException;

/**
 * Extract files in tar with bzip2 compression
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @version $Revision: $
 */
public class TarBZip2UnArchiver
    extends TarUnArchiver
{
    public TarBZip2UnArchiver()
    {
    }

    public TarBZip2UnArchiver( File sourceFile )
    {
        super( sourceFile );
    }

    protected void execute()
        throws ArchiverException
    {
        File tempTarFile;
        try
        {
            tempTarFile = File.createTempFile( "tmp", ".tar" );
        }
        catch ( IOException e )
        {
            throw new ArchiverException( "Cannot create temporary file for bzip2 uncompression", e );
        }

        tempTarFile.delete();

        File originalSourceFile = this.getSourceFile();

        try
        {
            BZip2UnArchiver zipUnArchiver = new BZip2UnArchiver( getSourceFile() );

            zipUnArchiver.enableLogging( this.getLogger() );

            zipUnArchiver.setDestFile( tempTarFile );

            zipUnArchiver.extract();

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
