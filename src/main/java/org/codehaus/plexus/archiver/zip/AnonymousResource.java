package org.codehaus.plexus.archiver.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResource;

import javax.annotation.Nonnull;

public class AnonymousResource extends AbstractPlexusIoResource {

	private final File file;

	public AnonymousResource( File file )
	{
		this( file, getName( file ));
	}

	public AnonymousResource(File file, String name)
	{
		super( name, file.lastModified(), file.length(), file.isFile(), file.isDirectory(), file.exists() );
		this.file = file;
	}

	@Nonnull
	public InputStream getContents()
			throws IOException
	{
		throw new UnsupportedOperationException("not supp");
		// Does this really have an input stream ?
		//return new FileInputStream( file );
	}

	public URL getURL()
			throws IOException
	{
		return file.toURI().toURL();
	}


	private static String getName( File file )
	{
		return file.getPath().replace( '\\', '/' );
	}


}

