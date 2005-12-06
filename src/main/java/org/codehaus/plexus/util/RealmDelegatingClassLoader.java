package org.codehaus.plexus.util;

import org.codehaus.classworlds.ClassRealm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author jdcasey
 */
public class RealmDelegatingClassLoader
    extends ClassLoader
{
    
    private final ClassRealm realm;

    public RealmDelegatingClassLoader(ClassRealm realm)
    {
        this.realm = realm;
    }

    protected Enumeration findResources( String name ) throws IOException
    {
        return realm.findResources( name );
    }
    
    public URL getResource( String name )
    {
        return realm.getResource( name );
    }
    
    public InputStream getResourceAsStream( String name )
    {
        return realm.getResourceAsStream( name );
    }
    
    public Class loadClass( String name ) throws ClassNotFoundException
    {
        return realm.loadClass( name );
    }
    
}
