package org.apache.directory.shared.ldap.codec;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.mina.filter.codec.ProtocolCodecFactory;


public class TestLdapCodecService implements ILdapCodecService 
{
    Map<String,IControlFactory<?,?>> factories = new HashMap<String, IControlFactory<?,?>>();
    
    public void registerControl( IControlFactory<?,?> factory )
    {
        factories.put( factory.getOid(), factory );
    }

    
    public void registerExtendedOp( IExtendedOpFactory<?, ?> factory )
    {
    }


    public <E> E newCodecControl( Class<? extends ICodecControl<? extends Control>> clazz )
    {
        try
        {
            Field f = clazz.getField( "OID" );
            String oid = ( String ) f.get( null );
            IControlFactory<?,?> factory = factories.get( oid );
            return extracted2( factory );
        }
        catch ( IllegalAccessException e )
        {
            e.printStackTrace();
        }
        catch ( SecurityException e )
        {
            e.printStackTrace();
        }
        catch ( NoSuchFieldException e )
        {
            e.printStackTrace();
        }
        
        return null;
    }

    
    public <E> E newControl( Class<? extends Control> clazz )
    {
        try
        {
            Field f = clazz.getField( "OID" );
            String oid = ( String ) f.get( null );
            IControlFactory<?,?> factory = factories.get( oid );
            return extracted( factory );
        }
        catch ( IllegalAccessException e )
        {
            e.printStackTrace();
        }
        catch ( SecurityException e )
        {
            e.printStackTrace();
        }
        catch ( NoSuchFieldException e )
        {
            e.printStackTrace();
        }
        
        return null;
    }

    
    @SuppressWarnings("unchecked")
    private <E> E extracted( IControlFactory<?,?> factory )
    {
        return ( E ) factory.newControl();
    }

    @SuppressWarnings("unchecked")
    private <E> E extracted2( IControlFactory<?,?> factory )
    {
        return ( E ) factory.newCodecControl();
    }

    public Iterator<String> controlOids()
    {
        return null;
    }

    public Iterator<String> extendedRequestOids()
    {
        return null;
    }

    public Iterator<String> extendedResponseOids()
    {
        return null;
    }


    public ProtocolCodecFactory newProtocolCodecFactory( boolean client )
    {
        return null;
    }
}