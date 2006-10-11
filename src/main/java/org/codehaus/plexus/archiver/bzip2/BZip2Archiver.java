package org.codehaus.plexus.archiver.bzip2;

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

import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;

import java.io.IOException;

/**
 * @version $Revision$ $Date$
 */
public class BZip2Archiver
    extends AbstractArchiver
{
    private BZip2Compressor compressor = new BZip2Compressor();
    
    public void execute()
        throws ArchiverException, IOException
    {
    	if ( ! checkForced() )
    	{
    		return;
    	}
        
        if ( getFiles().size() > 1 )
        {
            throw new ArchiverException( "There is more than one file in input." );
        }
        ArchiveEntry entry = (ArchiveEntry) getFiles().values().toArray()[ 0 ];
        compressor.setSourceFile( entry.getFile() );
        compressor.setDestFile( getDestFile() );
        compressor.compress();
    }

	public boolean isSupportingForced() {
		return true;
	}

    protected void cleanUp()
    {
    }

    protected void close()
    {
        compressor.close();
    }

    protected String getArchiveType()
    {
        return "bzip2";
    }
}
