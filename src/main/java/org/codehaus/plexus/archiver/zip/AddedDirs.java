package org.codehaus.plexus.archiver.zip;

/**
 *
 * Copyright 2015 The Apache Software Foundation
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


import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Stack;

/**
 * A list of directories that have been added to an archive.
 */
public class AddedDirs {

	private final Hashtable<String, String> addedDirs = new Hashtable<String, String>();

	public Stack<String> asStringStack(String entry) {
		Stack<String> directories = new Stack<String>();

		// Don't include the last entry itself if it's
		// a dir; it will be added on its own.
		int slashPos = entry.length() - ( entry.endsWith( "/" ) ? 1 : 0 );

		while ( ( slashPos = entry.lastIndexOf( '/', slashPos - 1 ) ) != -1 )
		{
			String dir = entry.substring( 0, slashPos + 1 );

			if ( addedDirs.contains( dir ) )
			{
				break;
			}

			directories.push( dir );
		}
		return directories;
	}

	public void clear() {
		addedDirs.clear();
	}


	/**
	 * Adds the path to this list.
	 * @param vPath The path to add.
	 * @return true if the path was not present, false if it already existed.
	 */
	public boolean update(String vPath){
		if ( addedDirs.get( vPath ) != null )
		{
			// don't add directories we've already added.
			// no warning if we try, it is harmless in and of itself
			return true;
		}
		addedDirs.put( vPath, vPath );
		return false;
	}

	public Set<String> allAddedDirs(){
		return new HashSet<String>( addedDirs.keySet() );
	}
}
