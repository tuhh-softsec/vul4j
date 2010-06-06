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

package org.apache.directory.shared.ldap.ldif;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.directory.shared.ldap.entry.DefaultEntry;
import org.apache.directory.shared.ldap.entry.DefaultModification;
import org.apache.directory.shared.ldap.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.entry.StringValue;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.name.RDN;
import org.apache.directory.shared.ldap.util.UTFUtils;


/**
 * A entry to be populated by an ldif parser.
 * 
 * We will have different kind of entries : 
 * - added entries 
 * - deleted entries 
 * - modified entries 
 * - RDN modified entries 
 * - DN modified entries
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifEntry implements Cloneable, Externalizable
{
    private static final long serialVersionUID = 2L;
    
    /** Used in toArray() */
    public static final Modification[] EMPTY_MODS = new Modification[0];

    /** the change type */
    private ChangeType changeType;

    /** the modification item list */
    private List<Modification> modificationList;

    private Map<String, Modification> modificationItems;

    /** The new superior */
    private String newSuperior;

    /** The new rdn */
    private String newRdn;

    /** The delete old rdn flag */
    private boolean deleteOldRdn;

    /** the entry */
    private Entry entry;

    
    /** The control */
    private Control control;

    /**
     * Creates a new Entry object.
     */
    public LdifEntry()
    {
        changeType = ChangeType.Add; // Default LDIF content
        modificationList = new LinkedList<Modification>();
        modificationItems = new HashMap<String, Modification>();
        entry = new DefaultEntry( (DN)null );
        control = null;
    }

    
    /**
     * Set the Distinguished Name
     * 
     * @param dn
     *            The Distinguished Name
     */
    public void setDn( DN dn )
    {
        entry.setDn( (DN)dn.clone() );
    }

    
    /**
     * Set the Distinguished Name
     * 
     * @param dn The Distinguished Name
     */
    public void setDn( String dn ) throws LdapInvalidDnException
    {
        entry.setDn( new DN( dn ) );
    }


    /**
     * Set the modification type
     * 
     * @param changeType
     *            The change type
     * 
     */
    public void setChangeType( ChangeType changeType )
    {
        this.changeType = changeType;
    }

    /**
     * Set the change type
     * 
     * @param changeType
     *            The change type
     */
    public void setChangeType( String changeType )
    {
        if ( "add".equals( changeType ) )
        {
            this.changeType = ChangeType.Add;
        }
        else if ( "modify".equals( changeType ) )
        {
            this.changeType = ChangeType.Modify;
        }
        else if ( "moddn".equals( changeType ) )
        {
            this.changeType = ChangeType.ModDn;
        }
        else if ( "modrdn".equals( changeType ) )
        {
            this.changeType = ChangeType.ModRdn;
        }
        else if ( "delete".equals( changeType ) )
        {
            this.changeType = ChangeType.Delete;
        }
    }

    /**
     * Add a modification item (used by modify operations)
     * 
     * @param modification The modification to be added
     */
    public void addModificationItem( Modification modification )
    {
        if ( changeType == ChangeType.Modify )
        {
            modificationList.add( modification );
            modificationItems.put( modification.getAttribute().getId(), modification );
        }
    }

    /**
     * Add a modification item (used by modify operations)
     * 
     * @param modOp The operation. One of : 
     * - ModificationOperation.ADD_ATTRIBUTE
     * - ModificationOperation.REMOVE_ATTRIBUTE 
     * - ModificationOperation.REPLACE_ATTRIBUTE
     * 
     * @param attr The attribute to be added
     */
    public void addModificationItem( ModificationOperation modOp, EntryAttribute attr )
    {
        if ( changeType == ChangeType.Modify )
        {
            Modification item = new DefaultModification( modOp, attr );
            modificationList.add( item );
            modificationItems.put( attr.getId(), item );
        }
    }


    /**
     * Add a modification item
     * 
     * @param modOp The modification operation value. One of : 
     *  - ModificationOperation.ADD_ATTRIBUTE
     *  - ModificationOperation.REMOVE_ATTRIBUTE 
     *  - ModificationOperation.REPLACE_ATTRIBUTE
     * @param id The attribute's ID
     * @param value The attribute's value
     */
    public void addModificationItem( ModificationOperation modOp, String id, Object value )
    {
        if ( changeType == ChangeType.Modify )
        {
            EntryAttribute attr =  null;
            
            if ( value == null )
            {
                value = new StringValue( (String)null );
                attr = new DefaultEntryAttribute( id, (Value<?>)value );
            }
            else
            {
                attr = (EntryAttribute)value;
            }

            Modification item = new DefaultModification( modOp, attr );
            modificationList.add( item );
            modificationItems.put( id, item );
        }
    }


    /**
     * Add an attribute to the entry
     * 
     * @param attr
     *            The attribute to be added
     */
    public void addAttribute( EntryAttribute attr ) throws LdapException
    {
        entry.put( attr );
    }

    /**
     * Add an attribute to the entry
     * 
     * @param id
     *            The attribute ID
     * 
     * @param value
     *            The attribute value
     * 
     */
    public void addAttribute( String id, Object value ) throws LdapException
    {
        if ( value instanceof String )
        {
            entry.add( id, (String)value );
        }
        else
        {
            entry.add( id, (byte[])value );
        }
    }
    
    
    /**
     * Remove a list of Attributes from the LdifEntry
     *
     * @param ids The Attributes to remove
     * @return The list of removed EntryAttributes
     */
    public List<EntryAttribute> removeAttribute( String... ids )
    {
        if ( entry.containsAttribute( ids ) )
        {
            return entry.removeAttributes( ids );
        }
        else
        {
            return null;
        }
    }

    /**
     * Add an attribute value to an existing attribute
     * 
     * @param id
     *            The attribute ID
     * 
     * @param value
     *            The attribute value
     * 
     */
    public void putAttribute( String id, Object value ) throws LdapException
    {
        if ( value instanceof String )
        {
            entry.add( id, (String)value );
        }
        else
        {
            entry.add( id, (byte[])value );
        }
    }

    /**
     * Get the change type
     * 
     * @return The change type. One of : ADD = 0; MODIFY = 1; MODDN = 2; MODRDN =
     *         3; DELETE = 4;
     */
    public ChangeType getChangeType()
    {
        return changeType;
    }

    /**
     * @return The list of modification items
     */
    public List<Modification> getModificationItems()
    {
        return modificationList;
    }


    /**
     * Gets the modification items as an array.
     *
     * @return modification items as an array.
     */
    public Modification[] getModificationItemsArray()
    {
        return modificationList.toArray( EMPTY_MODS );
    }


    /**
     * @return The entry Distinguished name
     */
    public DN getDn()
    {
        return entry.getDn();
    }

    /**
     * @return The number of entry modifications
     */
    public int size()
    {
        return modificationList.size();
    }

    /**
     * Returns a attribute given it's id
     * 
     * @param attributeId
     *            The attribute Id
     * @return The attribute if it exists
     */
    public EntryAttribute get( String attributeId )
    {
        if ( "dn".equalsIgnoreCase( attributeId ) )
        {
            return new DefaultEntryAttribute( "dn", entry.getDn().getName() );
        }

        return entry.get( attributeId );
    }

    /**
     * Get the entry's entry
     * 
     * @return the stored Entry
     */
    public Entry getEntry()
    {
        if ( isEntry() )
        {
            return entry;
        }
        else
        {
            return null;
        }
    }

    /**
     * @return True, if the old RDN should be deleted.
     */
    public boolean isDeleteOldRdn()
    {
        return deleteOldRdn;
    }

    /**
     * Set the flage deleteOldRdn
     * 
     * @param deleteOldRdn
     *            True if the old RDN should be deleted
     */
    public void setDeleteOldRdn( boolean deleteOldRdn )
    {
        this.deleteOldRdn = deleteOldRdn;
    }

    /**
     * @return The new RDN
     */
    public String getNewRdn()
    {
        return newRdn;
    }

    /**
     * Set the new RDN
     * 
     * @param newRdn
     *            The new RDN
     */
    public void setNewRdn( String newRdn )
    {
        this.newRdn = newRdn;
    }

    /**
     * @return The new superior
     */
    public String getNewSuperior()
    {
        return newSuperior;
    }

    /**
     * Set the new superior
     * 
     * @param newSuperior
     *            The new Superior
     */
    public void setNewSuperior( String newSuperior )
    {
        this.newSuperior = newSuperior;
    }

    /**
     * @return True if the entry is an ADD entry
     */
    public boolean isChangeAdd()
    {
        return changeType == ChangeType.Add;
    }

    /**
     * @return True if the entry is a DELETE entry
     */
    public boolean isChangeDelete()
    {
        return changeType == ChangeType.Delete;
    }

    /**
     * @return True if the entry is a MODDN entry
     */
    public boolean isChangeModDn()
    {
        return changeType == ChangeType.ModDn;
    }

    /**
     * @return True if the entry is a MODRDN entry
     */
    public boolean isChangeModRdn()
    {
        return changeType == ChangeType.ModRdn;
    }

    /**
     * @return True if the entry is a MODIFY entry
     */
    public boolean isChangeModify()
    {
        return changeType == ChangeType.Modify;
    }

    /**
     * Tells if the current entry is a added one
     *
     * @return <code>true</code> if the entry is added
     */
    public boolean isEntry()
    {
        return changeType == ChangeType.Add;
    }

    /**
     * @return The associated control, if any
     */
    public Control getControl()
    {
        return control;
    }

    /**
     * Add a control to the entry
     * 
     * @param control
     *            The control
     */
    public void setControl( Control control )
    {
        this.control = control;
    }

    /**
     * Clone method
     * @return a clone of the current instance
     * @exception CloneNotSupportedException If there is some problem while cloning the instance
     */
    public LdifEntry clone() throws CloneNotSupportedException
    {
        LdifEntry clone = (LdifEntry) super.clone();

        if ( modificationList != null )
        {
            for ( Modification modif:modificationList )
            {
                Modification modifClone = new DefaultModification( modif.getOperation(), 
                    (EntryAttribute) modif.getAttribute().clone() );
                clone.modificationList.add( modifClone );
            }
        }

        if ( modificationItems != null )
        {
            for ( String key:modificationItems.keySet() )
            {
                Modification modif = modificationItems.get( key );
                Modification modifClone = new DefaultModification( modif.getOperation(), 
                    (EntryAttribute) modif.getAttribute().clone() );
                clone.modificationItems.put( key, modifClone );
            }

        }

        if ( entry != null )
        {
            clone.entry = entry.clone();
        }

        return clone;
    }

    
    /**
     * @return a String representing the Entry, as a LDIF 
     */
    public String toString()
    {
        try
        {
            return LdifUtils.convertToLdif( this );
        }
        catch ( LdapException ne )
        {
            return "";
        }
    }
    
    
    /**
     * @see Object#hashCode()
     * 
     * @return the instance's hash code
     */
    public int hashCode()
    {
        int result = 37;

        if ( entry != null && entry.getDn() != null )
        {
            result = result*17 + entry.getDn().hashCode();
        }
        
        if ( changeType != null )
        {
            result = result*17 + changeType.hashCode();
            
            // Check each different cases
            switch ( changeType )
            {
                case Add :
                    // Checks the attributes
                    if ( entry != null )
                    {
                        result = result * 17 + entry.hashCode();
                    }
                    
                    break;

                case Delete :
                    // Nothing to compute
                    break;
                    
                case Modify :
                    if ( modificationList != null )
                    {
                        result = result * 17 + modificationList.hashCode();
                        
                        for ( Modification modification:modificationList )
                        {
                            result = result * 17 + modification.hashCode();
                        }
                    }
                    
                    break;
                    
                case ModDn :
                case ModRdn :
                    result = result * 17 + ( deleteOldRdn ? 1 : -1 ); 
                    
                    if ( newRdn != null )
                    {
                        result = result*17 + newRdn.hashCode();
                    }
                    
                    if ( newSuperior != null )
                    {
                        result = result*17 + newSuperior.hashCode();
                    }
                    
                    break;
                    
                default :
                    break; // do nothing
            }
        }

        if ( control != null )
        {
            result = result * 17 + control.hashCode();
        }

        return result;
    }
    
    /**
     * @see Object#equals(Object)
     * @return <code>true</code> if both values are equal
     */
    public boolean equals( Object o )
    {
        // Basic equals checks
        if ( this == o )
        {
            return true;
        }
        
        if ( o == null )
        {
           return false;
        }
        
        if ( ! (o instanceof LdifEntry ) )
        {
            return false;
        }
        
        LdifEntry otherEntry = (LdifEntry)o;
        
        // Check the DN
        DN thisDn = entry.getDn();
        DN dnEntry = otherEntry.getDn();
        
        if ( !thisDn.equals( dnEntry ) )
        {
            return false;
        }

        
        // Check the changeType
        if ( changeType != otherEntry.changeType )
        {
            return false;
        }
        
        // Check each different cases
        switch ( changeType )
        {
            case Add :
                // Checks the attributes
                if ( entry.size() != otherEntry.entry.size() )
                {
                    return false;
                }
                
                if ( !entry.equals( otherEntry.entry ) )
                {
                    return false;
                }
                
                break;

            case Delete :
                // Nothing to do, if the DNs are equals
                break;
                
            case Modify :
                // Check the modificationItems list

                // First, deal with special cases
                if ( modificationList == null )
                {
                    if ( otherEntry.modificationList != null )
                    {
                        return false;
                    }
                    else
                    {
                        break;
                    }
                }
                
                if ( otherEntry.modificationList == null )
                {
                    return false;
                }
                
                if ( modificationList.size() != otherEntry.modificationList.size() )
                {
                    return false;
                }
                
                // Now, compares the contents
                int i = 0;
                
                for ( Modification modification:modificationList )
                {
                    if ( ! modification.equals( otherEntry.modificationList.get( i ) ) )
                    {
                        return false;
                    }
                    
                    i++;
                }
                
                break;
                
            case ModDn :
            case ModRdn :
                // Check the deleteOldRdn flag
                if ( deleteOldRdn != otherEntry.deleteOldRdn )
                {
                    return false;
                }
                
                // Check the newRdn value
                try
                {
                    RDN thisNewRdn = new RDN( newRdn );
                    RDN entryNewRdn = new RDN( otherEntry.newRdn );

                    if ( !thisNewRdn.equals( entryNewRdn ) )
                    {
                        return false;
                    }
                }
                catch ( LdapInvalidDnException ine )
                {
                    return false;
                }
                
                // Check the newSuperior value
                try
                {
                    DN thisNewSuperior = new DN( newSuperior );
                    DN entryNewSuperior = new DN( otherEntry.newSuperior );
                    
                    if ( ! thisNewSuperior.equals(  entryNewSuperior ) )
                    {
                        return false;
                    }
                }
                catch ( LdapInvalidDnException ine )
                {
                    return false;
                }
                
                break;
                
            default :
                break; // do nothing
        }
        
        if ( control != null )
        {
            return control.equals( otherEntry.control );
        }
        else 
        {
            return otherEntry.control == null;
        }
    }


    /**
     * @see Externalizable#readExternal(ObjectInput)
     * 
     * @param in The stream from which the LdifEntry is read
     * @throws IOException If the stream can't be read
     * @throws ClassNotFoundException If the LdifEntry can't be created 
     */
    public void readExternal( ObjectInput in ) throws IOException , ClassNotFoundException
    {
        // Read the changeType
        int type = in.readInt();
        changeType = ChangeType.getChangeType( type );
        entry = (Entry)in.readObject();
        
        switch ( changeType )
        {
            case Add :
                // Fallback
            case Delete :
                // we don't have anything to read, but the control
                break;

            case ModDn :
                // Fallback
            case ModRdn :
                deleteOldRdn = in.readBoolean();
                
                if ( in.readBoolean() )
                {
                    newRdn = UTFUtils.readUTF( in );
                }
                
                if ( in.readBoolean() )
                {
                    newSuperior = UTFUtils.readUTF( in );
                }
                
                break;
                
            case Modify :
                // Read the modification
                int nbModifs = in.readInt();
                
                
                for ( int i = 0; i < nbModifs; i++ )
                {
                    int operation = in.readInt();
                    String modStr = UTFUtils.readUTF( in );
                    DefaultEntryAttribute value = (DefaultEntryAttribute)in.readObject();
                    
                    addModificationItem( ModificationOperation.getOperation( operation ), modStr, value );
                }
                
                break;
        }
        
        if ( in.available() > 0 )
        {
            // We have a control
            control = (Control)in.readObject();
        }
    }


    /**
     * @see Externalizable#readExternal(ObjectInput)<p>
     *
     *@param out The stream in which the ChangeLogEvent will be serialized. 
     *
     *@throws IOException If the serialization fail
     */
    public void writeExternal( ObjectOutput out ) throws IOException
    {
        // Write the changeType
        out.writeInt( changeType.getChangeType() );
        
        // Write the entry
        out.writeObject( entry );
        
        // Write the data
        switch ( changeType )
        {
            case Add :
                // Fallback
            case Delete :
                // we don't have anything to write, but the control
                break;

            case ModDn :
                // Fallback
            case ModRdn :
                out.writeBoolean( deleteOldRdn );
                
                if ( newRdn != null )
                {
                    out.writeBoolean( true );
                    UTFUtils.writeUTF( out, newRdn );
                }
                else
                {
                    out.writeBoolean( false );
                }
                
                if ( newSuperior != null )
                {
                    out.writeBoolean( true );
                    UTFUtils.writeUTF( out, newSuperior );
                }
                else
                {
                    out.writeBoolean( false );
                }
                break;
                
            case Modify :
                // Read the modification
                out.writeInt( modificationList.size() );
                
                for ( Modification modification:modificationList )
                {
                    out.writeInt( modification.getOperation().getValue() );
                    UTFUtils.writeUTF( out, modification.getAttribute().getId() );
                    
                    EntryAttribute attribute = modification.getAttribute();
                    out.writeObject( attribute );
                }
                
                break;
        }
        
        if ( control != null )
        {
            // Write the control
            out.writeObject( control );
            
        }
        
        // and flush the result
        out.flush();
    }
}
