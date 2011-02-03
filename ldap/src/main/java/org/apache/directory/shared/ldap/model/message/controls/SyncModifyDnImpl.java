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
package org.apache.directory.shared.ldap.model.message.controls;


/**
 * A simple {@link SyncModifyDn} implementation to hold properties.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncModifyDnImpl extends AbstractControl implements SyncModifyDn
{
    /** the entry's Dn to be changed */
    private String entryDn;

    /** target entry's new parent Dn */
    private String newSuperiorDn;

    /** the new Rdn */
    private String newRdn;

    /** flag to indicate whether to delete the old Rdn */
    private boolean deleteOldRdn = false;

    private SyncModifyDnType modDnType;


    /**
     * Creates a new instance of SyncModifyDnImpl.
     */
    public SyncModifyDnImpl()
    {
        super( OID );
    }


    /**
     *
     * Creates a new instance of SyncModifyDnImpl.
     *
     * @param isCritical The critical flag
     */
    public SyncModifyDnImpl( boolean isCritical )
    {
        super( OID, isCritical );
    }


    /**
     * {@inheritDoc}
     */
    public String getEntryDn()
    {
        return entryDn;
    }


    /**
     * {@inheritDoc}
     */
    public void setEntryDn( String entryDn )
    {
        this.entryDn = entryDn;
    }


    /**
     * {@inheritDoc}
     */
    public String getNewSuperiorDn()
    {
        return newSuperiorDn;
    }


    /**
     * {@inheritDoc}
     */
    public void setNewSuperiorDn( String newSuperiorDn )
    {
        this.newSuperiorDn = newSuperiorDn;
    }


    /**
     * {@inheritDoc}
     */
    public String getNewRdn()
    {
        return newRdn;
    }


    /**
     * {@inheritDoc}
     */
    public void setNewRdn( String newRdn )
    {
        this.newRdn = newRdn;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDeleteOldRdn()
    {
        return deleteOldRdn;
    }


    /**
     * {@inheritDoc}
     */
    public void setDeleteOldRdn( boolean deleteOldRdn )
    {
        this.deleteOldRdn = deleteOldRdn;
    }


    /**
     * {@inheritDoc}
     */
    public SyncModifyDnType getModDnType()
    {
        return modDnType;
    }


    /**
     * {@inheritDoc}
     */
    public void setModDnType( SyncModifyDnType modDnType )
    {
        this.modDnType = modDnType;
    }




    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        int h = 37;

        h = h*17 + super.hashCode();
        h = h*17 + modDnType.getValue();
        h = h*17 + ( deleteOldRdn ? 1 : 0 );

        if ( entryDn != null )
        {
            h = h*17 + entryDn.hashCode();
        }

        if ( newRdn != null )
        {
            h = h*17 + newRdn.hashCode();
        }

        if ( newSuperiorDn != null )
        {
            h = h*17 + newSuperiorDn.hashCode();
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

        SyncModifyDn otherControl = ( SyncModifyDn ) o;

        if ( newRdn != null )
        {
            if ( newRdn.equals( otherControl.getNewRdn() ) )
            {
                return false;
            }
        }
        else
        {
            if ( otherControl.getNewRdn() != null )
            {
                return false;
            }
        }

        if ( newSuperiorDn != null )
        {
            if ( newSuperiorDn.equals( otherControl.getNewSuperiorDn() ) )
            {
                return false;
            }
        }
        else
        {
            if ( otherControl.getNewSuperiorDn() != null )
            {
                return false;
            }
        }

        return ( deleteOldRdn == otherControl.isDeleteOldRdn() ) &&
            ( modDnType == otherControl.getModDnType() ) &&
            ( entryDn.equals( otherControl.getEntryDn() ) &&
            ( isCritical() == otherControl.isCritical() ) );
    }


    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "  SyncModifyDn control :\n" );
        sb.append( "   oid          : '" ).append( getOid() ).append( '\n' );
        sb.append( "   critical     : '" ).append( isCritical() ).append( '\n' );
        sb.append( "   entryDn      : '" ).append( entryDn ).append( "'\n" );
        sb.append( "   newSuperior  : '" ).append( newSuperiorDn ).append( "'\n" );
        sb.append( "   newRdn       : '" ).append( newRdn ).append( "'\n" );
        sb.append( "   deleteOldRdn : '" ).append( deleteOldRdn ).append( "'\n" );

        return sb.toString();
    }
}
