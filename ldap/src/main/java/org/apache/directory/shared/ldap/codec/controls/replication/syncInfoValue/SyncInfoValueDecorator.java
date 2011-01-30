/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.ldap.codec.controls.replication.syncInfoValue;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.controls.ControlDecorator;
import org.apache.directory.shared.ldap.message.control.replication.SynchronizationInfoEnum;
import org.apache.directory.shared.util.Strings;


/**
 * A syncInfoValue object, as defined in RFC 4533
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncInfoValueDecorator extends ControlDecorator<ISyncInfoValue> implements ISyncInfoValue
{
    /** The syncUUIDs cumulative lentgh */
    private int syncUUIDsLength;
    
    private byte[] value;
    
    /** An instance of this decoder */
    private static final Asn1Decoder decoder = new Asn1Decoder();

    
    /**
     * The constructor for this codec. Dont't forget to set the type.
     */
    public SyncInfoValueDecorator()
    {
        super( new SyncInfoValue() );
    }
    
    
    /**
     * The constructor for this codec.
     * @param type The kind of syncInfo we will store. Can be newCookie, 
     * refreshPresent, refreshDelete or syncIdSet
     */
    public SyncInfoValueDecorator( SynchronizationInfoEnum type )
    {
        this();

        setType( type);
    }
    
    
    /** The global length for this control */
    private int syncInfoValueLength;

    /**
     * {@inheritDoc}
     */
    public SynchronizationInfoEnum getType()
    {
        return getDecorated().getType();
    }

    
    /**
     * {@inheritDoc}
     */
    public void setType( SynchronizationInfoEnum type )
    {
        this.getDecorated().setType( type );

        // Initialize the arrayList if needed
        if ( ( type == SynchronizationInfoEnum.SYNC_ID_SET ) && ( getDecorated().getSyncUUIDs() == null ) )
        {
            getDecorated().setSyncUUIDs( new ArrayList<byte[]>() );
        }
    }

    
    /**
     * {@inheritDoc}
     */
    public byte[] getCookie()
    {
        return getDecorated().getCookie();
    }

    
    /**
     * {@inheritDoc}
     */
    public void setCookie( byte[] cookie )
    {
        // Copy the bytes
        if ( !Strings.isEmpty( cookie ) )
        {
            byte[] copy = new byte[cookie.length];
            System.arraycopy( cookie, 0, copy, 0, cookie.length );
            getDecorated().setCookie( copy );
        }
        else
        {
            getDecorated().setCookie( null );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isRefreshDone()
    {
        return getDecorated().isRefreshDone();
    }


    /**
     * {@inheritDoc}
     */
    public void setRefreshDone( boolean refreshDone )
    {
        getDecorated().setRefreshDone( refreshDone );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isRefreshDeletes()
    {
        return getDecorated().isRefreshDeletes();
    }


    /**
     * {@inheritDoc}
     */
    public void setRefreshDeletes( boolean refreshDeletes )
    {
        getDecorated().setRefreshDeletes( refreshDeletes );
    }


    /**
     * {@inheritDoc}
     */
    public List<byte[]> getSyncUUIDs()
    {
        return getDecorated().getSyncUUIDs();
    }


    /**
     * {@inheritDoc}
     */
    public void setSyncUUIDs( List<byte[]> syncUUIDs )
    {
        getDecorated().setSyncUUIDs( syncUUIDs );
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void addSyncUUID( byte[] syncUUID )
    {
        getDecorated().addSyncUUID( syncUUID );
    }

    

    
    /**
     * Compute the SyncInfoValue length.
     * 
     * SyncInfoValue :
     * 
     * 0xA0 L1 abcd                   // newCookie
     * 0xA1 L2                        // refreshDelete
     *   |
     *  [+--> 0x04 L3 abcd]           // cookie
     *  [+--> 0x01 0x01 (0x00|0xFF)   // refreshDone
     * 0xA2 L4                        // refreshPresent
     *   |
     *  [+--> 0x04 L5 abcd]           // cookie
     *  [+--> 0x01 0x01 (0x00|0xFF)   // refreshDone
     * 0xA3 L6                        // syncIdSet
     *   |
     *  [+--> 0x04 L7 abcd]           // cookie
     *  [+--> 0x01 0x01 (0x00|0xFF)   // refreshDeletes
     *   +--> 0x31 L8                 // SET OF syncUUIDs
     *          |
     *         [+--> 0x04 L9 abcd]    // syncUUID    public static final int AND_FILTER_TAG = 0xA0;

    public static final int OR_FILTER_TAG = 0xA1;

    public static final int NOT_FILTER_TAG = 0xA2;

    public static final int BIND_REQUEST_SASL_TAG = 0xA3;

     */
    public int computeLength()
    {
        // The mode length
        syncInfoValueLength = 0;
        
        switch ( getType() )
        {
            case NEW_COOKIE :
                if ( getCookie() != null )
                {
                    syncInfoValueLength = 1 + TLV.getNbBytes( getCookie().length ) + getCookie().length;
                }
                else
                {
                    syncInfoValueLength = 1 + 1;
                }
                
                valueLength = syncInfoValueLength;

                // Call the super class to compute the global control length
                return super.computeLength( valueLength );
                
            case REFRESH_DELETE :
            case REFRESH_PRESENT :
                if ( getCookie() != null )
                {
                    syncInfoValueLength = 1 + TLV.getNbBytes( getCookie().length ) + getCookie().length;
                }
                
                // The refreshDone flag, only if not true, as it default to true
                if ( ! isRefreshDone() )
                {
                    syncInfoValueLength += 1 + 1 + 1;
                }
                
                valueLength = 1 + TLV.getNbBytes( syncInfoValueLength ) + syncInfoValueLength;
                
                // Call the super class to compute the global control length
                return super.computeLength( valueLength );
                
            case SYNC_ID_SET :
                if ( getCookie() != null )
                {
                    syncInfoValueLength = 1 + TLV.getNbBytes( getCookie().length ) + getCookie().length;
                }
                
                // The refreshDeletes flag, default to false
                if ( isRefreshDeletes() )
                {
                    syncInfoValueLength += 1 + 1 + 1;
                }

                // The syncUUIDs if any
                syncUUIDsLength = 0;

                if ( getSyncUUIDs().size() != 0 )
                {
                    for ( byte[] syncUUID: getSyncUUIDs() )
                    {
                        int uuidLength = 1 + TLV.getNbBytes( syncUUID.length ) + syncUUID.length;
                        
                        syncUUIDsLength += uuidLength;
                    }
                }
                
                syncInfoValueLength += 1 + TLV.getNbBytes( syncUUIDsLength ) + syncUUIDsLength;
                valueLength = 1 + TLV.getNbBytes( syncInfoValueLength ) + syncInfoValueLength;

                // Call the super class to compute the global control length
                return super.computeLength( valueLength );
        }
        
        return 1 + TLV.getNbBytes( syncInfoValueLength ) + syncInfoValueLength;
    }
    
    
    /**
     * Encode the SyncInfoValue control
     * 
     * @param buffer The encoded sink
     * @return A ByteBuffer that contains the encoded PDU
     * @throws EncoderException If anything goes wrong.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        if ( buffer == null )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04023 ) );
        }

        // Encode the Control envelop
        super.encode( buffer );
        
        // Encode the OCTET_STRING tag
        buffer.put( UniversalTag.OCTET_STRING.getValue() );
        buffer.put( TLV.getBytes( valueLength ) );

        switch ( getType() )
        {
            case NEW_COOKIE :
                // The first case : newCookie
                buffer.put( (byte)SyncInfoValueTags.NEW_COOKIE_TAG.getValue() );

                // As the OCTET_STRING is absorbed by the Application tag,
                // we have to store the L and V separately
                if ( ( getCookie() == null ) || ( getCookie().length == 0 ) )
                {
                    buffer.put( ( byte ) 0 );
                }
                else
                {
                    buffer.put( TLV.getBytes( getCookie().length ) );
                    buffer.put( getCookie() );
                }

                break;
                
            case REFRESH_DELETE :
                // The second case : refreshDelete
                buffer.put( (byte)SyncInfoValueTags.REFRESH_DELETE_TAG.getValue() );
                buffer.put( TLV.getBytes( syncInfoValueLength ) );

                // The cookie, if any
                if ( getCookie() != null )
                {
                    Value.encode( buffer, getCookie() );
                }
                
                // The refreshDone flag
                if ( ! isRefreshDone() )
                {
                    Value.encode( buffer, isRefreshDone() );
                }
                
                break;
                
            case REFRESH_PRESENT :
                // The third case : refreshPresent
                buffer.put( (byte)SyncInfoValueTags.REFRESH_PRESENT_TAG.getValue() );
                buffer.put( TLV.getBytes( syncInfoValueLength ) );

                // The cookie, if any
                if ( getCookie() != null )
                {
                    Value.encode( buffer, getCookie() );
                }
                
                // The refreshDone flag
                if ( ! isRefreshDone() )
                {
                    Value.encode( buffer, isRefreshDone() );
                }

                break;
                
            case SYNC_ID_SET :
                // The last case : syncIdSet
                buffer.put( (byte)SyncInfoValueTags.SYNC_ID_SET_TAG.getValue() );
                buffer.put( TLV.getBytes( syncInfoValueLength ) );

                // The cookie, if any
                if ( getCookie() != null )
                {
                    Value.encode( buffer, getCookie() );
                }
                
                // The refreshDeletes flag if not false
                if ( isRefreshDeletes() )
                {
                    Value.encode( buffer, isRefreshDeletes() );
                }
                
                // The syncUUIDs
                buffer.put( UniversalTag.SET.getValue() );
                buffer.put( TLV.getBytes( syncUUIDsLength ) );
                
                // Loop on the UUIDs if any
                if ( getSyncUUIDs().size() != 0 )
                {
                    for ( byte[] syncUUID: getSyncUUIDs() )
                    {
                        Value.encode( buffer , syncUUID );
                    }
                }
        }

        return buffer;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public byte[] getValue()
    {
        if ( value == null )
        {
            try
            { 
                computeLength();
                ByteBuffer buffer = ByteBuffer.allocate( valueLength );
                
                switch ( getType() )
                {
                    case NEW_COOKIE :
                        // The first case : newCookie
                        buffer.put( ( byte ) SyncInfoValueTags.NEW_COOKIE_TAG.getValue() );

                        // As the OCTET_STRING is absorbed by the Application tag,
                        // we have to store the L and V separately
                        if ( ( getCookie() == null ) || ( getCookie().length == 0 ) )
                        {
                            buffer.put( ( byte ) 0 );
                        }
                        else
                        {
                            buffer.put( TLV.getBytes( getCookie().length ) );
                            buffer.put( getCookie() );
                        }

                        break;
                        
                    case REFRESH_DELETE :
                        // The second case : refreshDelete
                        buffer.put( (byte)SyncInfoValueTags.REFRESH_DELETE_TAG.getValue() );
                        buffer.put( TLV.getBytes( syncInfoValueLength ) );

                        // The cookie, if any
                        if ( getCookie() != null )
                        {
                            Value.encode( buffer, getCookie() );
                        }
                        
                        // The refreshDone flag
                        if ( ! isRefreshDone() )
                        {
                            Value.encode( buffer, isRefreshDone() );
                        }
                        
                        break;
                        
                    case REFRESH_PRESENT :
                        // The third case : refreshPresent
                        buffer.put( (byte)SyncInfoValueTags.REFRESH_PRESENT_TAG.getValue() );
                        buffer.put( TLV.getBytes( syncInfoValueLength ) );

                        // The cookie, if any
                        if ( getCookie() != null )
                        {
                            Value.encode( buffer, getCookie() );
                        }
                        
                        // The refreshDone flag
                        if ( ! isRefreshDone() )
                        {
                            Value.encode( buffer, isRefreshDone() );
                        }

                        break;
                        
                    case SYNC_ID_SET :
                        // The last case : syncIdSet
                        buffer.put( (byte)SyncInfoValueTags.SYNC_ID_SET_TAG.getValue() );
                        buffer.put( TLV.getBytes( syncInfoValueLength ) );

                        // The cookie, if any
                        if ( getCookie() != null )
                        {
                            Value.encode( buffer, getCookie() );
                        }
                        
                        // The refreshDeletes flag if not false
                        if ( isRefreshDeletes() )
                        {
                            Value.encode( buffer, isRefreshDeletes() );
                        }
                        
                        // The syncUUIDs
                        buffer.put( UniversalTag.SET.getValue() );
                        buffer.put( TLV.getBytes( syncUUIDsLength ) );
                        
                        // Loop on the UUIDs if any
                        if ( getSyncUUIDs().size() != 0 )
                        {
                            for ( byte[] syncUUID: getSyncUUIDs() )
                            {
                                Value.encode( buffer , syncUUID );
                            }
                        }
                }
                
                value = buffer.array();
            }
            catch ( Exception e )
            {
                return null;
            }
        }
        
        return value;
    }

    
    /**
     * @see Object#equals(Object)
     */
    public boolean equals( Object o )
    {
        if ( !super.equals( o ) )
        {
            return false;
        }

        SyncInfoValueDecorator otherControl = ( SyncInfoValueDecorator ) o;

        if ( getSyncUUIDs() != null )
        {
            if ( otherControl.getSyncUUIDs() == null )
            {
                return false;
            }
            
            // @TODO : check the UUIDs
            for ( @SuppressWarnings("unused") byte[] syncUuid : getSyncUUIDs() )
            {
            }
        }
        else
        {
            if ( otherControl.getSyncUUIDs() != null )
            {
                return false;
            }
        }
        
        return ( isRefreshDeletes() == otherControl.isRefreshDeletes() ) &&
            ( isRefreshDone() == otherControl.isRefreshDone() ) &&
            ( getType() == otherControl.getType() ) &&
            ( Arrays.equals( getCookie(), otherControl.getCookie() ) );
    }



    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append( "    SyncInfoValue control :\n" );
        sb.append( "        oid : " ).append( getOid() ).append( '\n' );
        sb.append( "        critical : " ).append( isCritical() ).append( '\n' );

        switch ( getType() )
        {
            case NEW_COOKIE :
                sb.append( "        newCookie : '" ).
                    append( Strings.dumpBytes( getCookie() ) ).append( "'\n" );
                break;
                
            case REFRESH_DELETE :
                sb.append( "        refreshDelete : \n" );
                
                if ( getCookie() != null )
                {
                    sb.append( "            cookie : '" ).
                        append( Strings.dumpBytes( getCookie() ) ).append( "'\n" );
                }
                
                sb.append( "            refreshDone : " ).append(  isRefreshDone() ).append( '\n' );
                break;
                
            case REFRESH_PRESENT :
                sb.append( "        refreshPresent : \n" );
                
                if ( getCookie() != null )
                {
                    sb.append( "            cookie : '" ).
                        append( Strings.dumpBytes( getCookie() ) ).append( "'\n" );
                }
                
                sb.append( "            refreshDone : " ).append(  isRefreshDone() ).append( '\n' );
                break;
                
            case SYNC_ID_SET :
                sb.append( "        syncIdSet : \n" );
                
                if ( getCookie() != null )
                {
                    sb.append( "            cookie : '" ).
                        append( Strings.dumpBytes( getCookie() ) ).append( "'\n" );
                }
                
                sb.append( "            refreshDeletes : " ).append(  isRefreshDeletes() ).append( '\n' );
                sb.append(  "            syncUUIDS : " );

                if ( getSyncUUIDs().size() != 0 )
                {
                    boolean isFirst = true;
                    
                    for ( byte[] syncUUID: getSyncUUIDs() )
                    {
                        if ( isFirst )
                        {
                            isFirst = false;
                        }
                        else
                        {
                            sb.append( ", " );
                        }
                        
                        sb.append( Arrays.toString ( syncUUID ) );
                    }
                    
                    sb.append( '\n' );
                }
                else
                {
                    sb.append(  "empty\n" );
                }
                
                break;
        }
        
        return sb.toString();
    }


    @Override
    public Asn1Object decode( byte[] controlBytes ) throws DecoderException
    {
        ByteBuffer bb = ByteBuffer.wrap( controlBytes );
        SyncInfoValueContainer container = new SyncInfoValueContainer( this );
        decoder.decode( bb, container );
        return this;
    }
}
