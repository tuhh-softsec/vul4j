/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.extras.controls;


import java.util.Arrays;

import org.apache.directory.shared.ldap.model.message.controls.AbstractControl;
import org.apache.directory.shared.util.Strings;


/**
 * A syncRequestValue object, as defined in RFC 4533 :
 * <pre>
 * 2.2.  Sync Request Control
 *
 *    The Sync Request Control is an LDAP Control [RFC4511] where the
 *    controlType is the object identifier 1.3.6.1.4.1.4203.1.9.1.1 and the
 *    controlValue, an OCTET STRING, contains a BER-encoded
 *    syncRequestValue.  The criticality field is either TRUE or FALSE.
 *
 *       syncRequestValue ::= SEQUENCE {
 *           mode ENUMERATED {
 *               -- 0 unused
 *               refreshOnly       (1),
 *               -- 2 reserved
 *               refreshAndPersist (3)
 *           },
 *           cookie     syncCookie OPTIONAL,
 *           reloadHint BOOLEAN DEFAULT FALSE
 *       }
 *
 *    The Sync Request Control is only applicable to the SearchRequest
 *    Message.
 * </pre>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncRequestValueImpl extends AbstractControl implements SyncRequestValue
{
    /** The synchronization type */
    private SynchronizationModeEnum mode;

    /** The Sync cookie */
    private byte[] cookie;

    /** The reloadHint flag */
    private boolean isReloadHint;


    /**
     * Creates a new instance of SyncRequestValueImpl.
     */
    public SyncRequestValueImpl()
    {
        super( OID );
    }


    /**
     *
     * Creates a new instance of SyncRequestValueImpl.
     *
     * @param isCritical The critical flag
     */
    public SyncRequestValueImpl( boolean isCritical )
    {
        super( OID, isCritical );
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getCookie()
    {
        return this.cookie;
    }


    /**
     * {@inheritDoc}
     */
    public void setCookie( byte[] cookie )
    {
        this.cookie = cookie;
    }


    /**
     * {@inheritDoc}
     */
    public SynchronizationModeEnum getMode()
    {
        return mode;
    }


    /**
     * {@inheritDoc}
     */
    public void setMode( SynchronizationModeEnum mode )
    {
        this.mode = mode;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReloadHint()
    {
        return isReloadHint;
    }


    /**
     * {@inheritDoc}
     */
    public void setReloadHint( boolean reloadHint )
    {
        this.isReloadHint = reloadHint;
    }


    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        int h = 37;

        h = h*17 + super.hashCode();
        h = h*17 + ( isReloadHint ? 1 : 0 );
        h = h*17 + mode.getValue();

        if ( cookie != null )
        {
            for ( byte b : cookie )
            {
                h = h*17 + b;
            }
        }

        return h;
    }


    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals( Object o )
    {
        if ( !super.equals( o ) )
        {
            return false;
        }

        if ( !( o instanceof SyncRequestValue ) )
        {
            return false;
        }

        SyncRequestValue otherControl = ( SyncRequestValue ) o;

        return ( mode == otherControl.getMode() ) &&
            ( isReloadHint == otherControl.isReloadHint() ) &&
            ( Arrays.equals( cookie, otherControl.getCookie() ) );
    }


    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "    SyncRequestValue control :\n" );
        sb.append( "        oid : " ).append( getOid() ).append( '\n' );
        sb.append( "        critical : " ).append( isCritical() ).append( '\n' );
        sb.append( "        mode              : '" ).append( getMode() ).append( "'\n" );
        sb.append( "        cookie            : '" ).
            append( Strings.dumpBytes( getCookie() ) ).append( "'\n" );
        sb.append( "        refreshAndPersist : '" ).append( isReloadHint() ).append( "'\n" );

        return sb.toString();
    }
}
