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
    protected void execute()
        throws ArchiverException, IOException
    {
        File tempTarFile = File.createTempFile("tmp",".tar");
        //remove the temp file so that GZipUnArchiver does not do date comparision
        tempTarFile.delete();
        
        File originalSourceFile = this.getSourceFile();
        
        try
        {
            GZipUnArchiver zipUnArchiver = new GZipUnArchiver();
            
            zipUnArchiver.enableLogging( this.getLogger() );
            
            zipUnArchiver.setDestFile( tempTarFile );
            
            zipUnArchiver.setSourceFile( this.getSourceFile() ); 
                       
            zipUnArchiver.extract();
            
            this.setSourceFile( tempTarFile );
            
            super.execute();
        }
        finally
        {
            tempTarFile.delete();
            
            this.setSourceFile( originalSourceFile );
        }        

    }
}
