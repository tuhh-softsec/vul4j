
package org.apache.directory.shared.ldap.aci.protectedItem;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.apache.directory.shared.ldap.aci.ProtectedItem;

/**
 * Restricts the maximum number of attribute values allowed for a specified
 * attribute type. It is examined if the protected item is an attribute
 * value of the specified type and the permission sought is add. Values of
 * that attribute in the entry are counted without regard to context or
 * access control and as though the operation which adds the values were
 * successful. If the number of values in the attribute exceeds maxCount,
 * the ACI item is treated as not granting add access.
 */
public class MaxValueCountItem extends ProtectedItem
{
    /** The set of elements to protect */
    private final Set<MaxValueCountElem> items;


    /**
     * Creates a new instance.
     * 
     * @param items
     *            the collection of {@link MaxValueCountElem}s.
     */
    public MaxValueCountItem( Set<MaxValueCountElem> items )
    {
        this.items = Collections.unmodifiableSet( items );
    }


    /**
     * Returns an iterator of all {@link MaxValueCountElem}s.
     */
    public Iterator<MaxValueCountElem> iterator()
    {
        return items.iterator();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        hash = hash * 17 + items.hashCode();
        return hash;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( o instanceof MaxValueCountItem )
        {
            MaxValueCountItem that = ( MaxValueCountItem ) o;
            return this.items.equals( that.items );
        }

        return false;
    }


    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append( "maxValueCount {" );

        boolean isFirst = true;

        for ( MaxValueCountElem item : items )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                buf.append( ", " );
            }

            buf.append( item.toString() );
        }

        buf.append( "}" );

        return buf.toString();
    }
}
