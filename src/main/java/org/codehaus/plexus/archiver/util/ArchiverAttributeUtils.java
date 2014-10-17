package org.codehaus.plexus.archiver.util;

/*
 * Copyright 2014 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributeUtils;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;

public class ArchiverAttributeUtils {
	public static PlexusIoResourceAttributes getFileAttributes(File file) throws ArchiverException {
		try {
			return PlexusIoResourceAttributeUtils.getFileAttributes(file);
		} catch (IOException e) {
			throw new ArchiverException("Failed to read filesystem attributes for: " + file, e);
		}
	}
}
