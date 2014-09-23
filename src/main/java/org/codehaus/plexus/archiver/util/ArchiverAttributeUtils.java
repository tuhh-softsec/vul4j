package org.codehaus.plexus.archiver.util;

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
