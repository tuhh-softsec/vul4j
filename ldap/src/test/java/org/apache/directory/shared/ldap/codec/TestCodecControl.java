package org.apache.directory.shared.ldap.codec;


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;


public class TestCodecControl implements ITestCodecControl
{
    TestControl decorated = new TestControl();
    
    public String getOid()
    {
        return decorated.getOid();
    }

    public boolean isCritical()
    {
        return decorated.isCritical();
    }

    public void setCritical( boolean isCritical )
    {
        decorated.setCritical( isCritical );
    }

    public ITestControl getDecorated()
    {
        return decorated;
    }

    public int computeLength()
    {
        return 0;
    }

    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        return null;
    }

    public int getFoo()
    {
        return decorated.getFoo();
    }

    public void setFoo( int foo )
    {
        decorated.setFoo( foo );
    }

    public Asn1Object decode( byte[] controlBytes ) throws DecoderException
    {
        return null;
    }

    public boolean hasValue()
    {
        return false;
    }

    public byte[] getValue()
    {
        return null;
    }

    public void setValue( byte[] value )
    {
    }
}