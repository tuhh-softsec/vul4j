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

package org.apache.directory.shared.ldap.name;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.normalizers.OidNormalizer;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The DN class contains a DN (Distinguished Name). This class is immutable.
 *
 * Its specification can be found in RFC 2253,
 * "UTF-8 String Representation of Distinguished Names".
 *
 * We will store two representation of a DN :
 * - a user Provider representation, which is the parsed String given by a user
 * - an internal representation.
 *
 * A DN is formed of RDNs, in a specific order :
 *  RDN[n], RDN[n-1], ... RDN[1], RDN[0]
 *
 * It represents a tree, in which the root is the last RDN (RDN[0]) and the leaf
 * is the first RDN (RDN[n]).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DN implements Cloneable, Serializable, Comparable<DN>, Iterable<RDN>
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( DN.class );

    /**
     * Declares the Serial Version Uid.
     *
     * @see <a
     *      href="http://c2.com/cgi/wiki?AlwaysDeclareSerialVersionUid">Always
     *      Declare Serial Version Uid</a>
     */
    private static final long serialVersionUID = 1L;

    /** Value returned by the compareTo method if values are not equals */
    public static final int NOT_EQUAL = -1;

    /** Value returned by the compareTo method if values are equals */
    public static final int EQUAL = 0;

    /** A flag used to tell if the DN has been normalized */
    private AtomicBoolean normalized;

    // ~ Static fields/initializers
    // -----------------------------------------------------------------
    /**
     *  The RDNs that are elements of the DN
     * NOTE THAT THESE ARE IN THE OPPOSITE ORDER FROM THAT IMPLIED BY THE JAVADOC!
     * Rdn[0] is rdns.get(n) and Rdn[n] is rdns.get(0)
     * <br>
     * For instance,if the DN is "dc=c, dc=b, dc=a", then the RDNs are stored as :
     * [0] : dc=c
     * [1] : dc=b
     * [2] : dc=a
     */
    protected List<RDN> rdns = new ArrayList<RDN>( 5 );

    /** The user provided name */
    private String upName;

    /** The normalized name */
    private String normName;

    /** The bytes representation of the normName */
    private byte[] bytes;

    /** A null DN */
    public static final DN EMPTY_DN = new DN();

    /** the schema manager */
    private transient SchemaManager schemaManager;

    /**
     * An iterator over RDNs
     */
    private class RdnIterator implements Iterator<RDN>
    {
        // The current index
        int index;

        private RdnIterator()
        {
            index = rdns != null ? rdns.size() - 1 : -1;
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasNext()
        {
            return index >= 0;
        }

        /**
         * {@inheritDoc}
         */
        public RDN next()
        {
            return index >= 0 ? rdns.get( index-- ) : null;
        }

        /**
         * {@inheritDoc}
         */
        public void remove()
        {
            // Not implemented
        }
    }

    // ~ Methods
    // ------------------------------------------------------------------------------------

    /**
     * Construct an empty DN object
     */
    public DN()
    {
        this( ( SchemaManager ) null );
    }


    /**
     * Construct an empty DN object
     */
    public DN( SchemaManager schemaManger )
    {
        this.schemaManager = schemaManger;
        upName = "";
        normName = "";
        normalized = new AtomicBoolean( true );
    }


    /**
     * @see #DN(DN, SchemaManager)
     */
    public DN( DN dn ) throws LdapInvalidDnException
    {
       this( dn, null);
    }


    /**
     * Copies a DN to an DN.
     *
     * @param dn composed of String name components.
     * @param schemaManager the schema manager
     * @throws LdapInvalidDnException If the Name is invalid.
     */
    public DN( DN dn, SchemaManager schemaManager ) throws LdapInvalidDnException
    {
        this.schemaManager = schemaManager;

        if ( ( dn != null ) && ( dn.size() != 0 ) )
        {
            for ( int ii = 0; ii < dn.size(); ii++ )
            {
                String nameComponent = dn.get( ii );

                if ( nameComponent.length() > 0 )
                {
                    RDN newRdn = new RDN( nameComponent, schemaManager );

                    rdns.add( 0, newRdn );
                }
            }
        }

        toUpName();

        normalized = new AtomicBoolean();

        if( schemaManager != null )
        {
            normalize( schemaManager.getNormalizerMapping() );
        }
        else
        {
            normalizeInternal();
            normalized.set( false );
        }
    }


    /**
     * @see #DN(String, SchemaManager)
     */
    public DN( String upName ) throws LdapInvalidDnException
    {
        this( upName, null );
    }



    /**
     * Parse a String and checks that it is a valid DN <br>
     * <p>
     * &lt;distinguishedName&gt; ::= &lt;name&gt; | e <br>
     * &lt;name&gt; ::= &lt;name-component&gt; &lt;name-components&gt; <br>
     * &lt;name-components&gt; ::= &lt;spaces&gt; &lt;separator&gt;
     * &lt;spaces&gt; &lt;name-component&gt; &lt;name-components&gt; | e <br>
     * </p>
     *
     * @param upName The String that contains the DN.
     * @param schemaManager the schema manager (optional)
     * @throws LdapInvalidNameException if the String does not contain a valid DN.
     */
    public DN( String upName, SchemaManager schemaManager ) throws LdapInvalidDnException
    {
        if ( upName != null )
        {
            DnParser.parseInternal( upName, rdns );
        }

        normalized = new AtomicBoolean();

        if( schemaManager != null )
        {
            this.schemaManager = schemaManager;
            normalize( schemaManager.getNormalizerMapping() );
        }
        else
        {
            normalized.set( false );

            // Stores the representations of a DN : internal (as a string and as a
            // byte[]) and external.
            normalizeInternal();
        }

        this.upName = upName;
    }


    /**
     * @see #DN(SchemaManager, String...)
     */
    public DN( String... upRdns ) throws LdapInvalidDnException
    {
        this( null, upRdns );
    }


    /**
     * Creates a new instance of DN, using varargs to declare the RDNs. Each
     * String is either a full RDN, or a couple of AttributeType DI and a value.
     * If the String contains a '=' symbol, the the constructor will assume that
     * the String arg contains afull RDN, otherwise, it will consider that the
     * following arg is the value.
     * An example of usage would be :
     * <pre>
     * String exampleName = "example";
     * String baseDn = "dc=apache,dc=org";
     *
     * DN dn = new DN(
     *     "cn=Test",
     *     "ou", exampleName,
     *     baseDn);
     * </pre>
     *
     * @param schemaManager the schema manager
     * @param upRdns
     * @throws LdapInvalidDnException
     */
    public DN( SchemaManager schemaManager, String... upRdns ) throws LdapInvalidDnException
    {
        StringBuilder sb = new StringBuilder();
        boolean valueExpected = false;
        boolean isFirst = true;

        for ( String upRdn : upRdns )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else if ( !valueExpected )
            {
                sb.append( ',' );
            }

            if ( !valueExpected )
            {
                sb.append( upRdn );

                if ( upRdn.indexOf( '=' ) == -1 )
                {
                    valueExpected = true;
                }
            }
            else
            {
                sb.append( "=" ).append( upRdn );

                valueExpected = false;
            }
        }

        if ( valueExpected )
        {
            throw new LdapInvalidDnException( ResultCodeEnum.INVALID_DN_SYNTAX, I18n.err( I18n.ERR_04202 ) );
        }

        normalized = new AtomicBoolean();

        // Stores the representations of a DN : internal (as a string and as a
        // byte[]) and external.
        upName = sb.toString();
        DnParser.parseInternal( upName, rdns );

        if( schemaManager != null )
        {
            this.schemaManager = schemaManager;
            normalize( schemaManager.getNormalizerMapping() );
        }
        else
        {
            normalized.set( false );
            normalizeInternal();
        }
    }


    /**
     * Create a DN while deserializing it.
     *
     * Note : this constructor is used only by the deserialization method.
     * @param upName The user provided name
     * @param normName the normalized name
     * @param bytes the name as a byte[]
     */
    DN( String upName, String normName, byte[] bytes )
    {
        normalized = new AtomicBoolean( true );
        this.upName = upName;
        this.normName = normName;
        this.bytes = bytes;
    }


    /**
     * Creates a DN.
     *
     * Note: This is mostly used internally in the server
     *
     * @param upName The user provided name
     * @param normName the normalized name
     * @param bytes the name as a byte[]
     * @param rdnList the list of RDNs present in the DN
     */
    public DN( String upName, String normName, byte[] bytes, List<RDN> rdnList )
    {
        this( upName, normName, bytes );
        rdns.addAll( rdnList );
    }


    /**
     *
     * Creates a DN by based on the given RDN.
     *
     * @param rdn the RDN to be used in the DN
     */
    public DN( RDN rdn )
    {
        rdns.add( rdn );

        if( rdn.isNormalized() )
        {
            this.normName = rdn.getNormName();
            this.upName = rdn.getName();
            this.bytes = StringTools.getBytesUtf8( normName );
            normalized = new AtomicBoolean( true );
        }
        else
        {
            normalizeInternal();
            toUpName();
            normalized = new AtomicBoolean( false );
        }
    }


    /**
     * Static factory which creates a normalized DN from a String and a Map of OIDs.
     * <br>
     * This method is for test purpose only, and as it's package protected, won't be usable
     * out of this scope.
     *
     * @param name The DN as a String
     * @param oidsMap The OID mapping
     * @return A valid DN
     * @throws LdapInvalidNameException If the DN is invalid.
     * @throws LdapInvalidDnException If something went wrong.
     */
    /* No qualifier */ static DN normalize( String name, Map<String, OidNormalizer> oidsMap ) throws LdapInvalidDnException
    {
        if ( ( name == null ) || ( name.length() == 0 ) || ( oidsMap == null ) || ( oidsMap.isEmpty() ) )
        {
            return DN.EMPTY_DN;
        }

        DN newDn = new DN( name );

        for ( RDN rdn : newDn.rdns )
        {
            String upName = rdn.getName();
            rdnOidToName( rdn, oidsMap );
            rdn.normalize();
            rdn.setUpName( upName );
        }

        newDn.normalizeInternal();
        newDn.normalized.set( true );

        return newDn;
    }


    /**
     * Normalize the DN by triming useless spaces and lowercasing names.
     */
    void normalizeInternal()
    {
        normName = toNormName();
    }


    /**
     * Build the normalized DN as a String,
     *
     * @return A String representing the normalized DN
     */
    private String toNormName()
    {
        if ( rdns.size() == 0 )
        {
            bytes = null;
            return "";
        }
        else
        {
            StringBuffer sb = new StringBuffer();
            boolean isFirst = true;

            for ( RDN rdn : rdns )
            {
                if ( isFirst )
                {
                    isFirst = false;
                }
                else
                {
                    sb.append( ',' );
                }

                sb.append( rdn.getNormName() );
            }

            String newNormName = sb.toString();

            if ( ( normName == null ) || !normName.equals( newNormName ) )
            {
                bytes = StringTools.getBytesUtf8( newNormName );
                normName = newNormName;
            }

            return normName;
        }
    }


    /**
     * Return the normalized DN as a String. It returns the same value as the
     * getNormName method
     *
     * @return A String representing the normalized DN
     */
    public String toString()
    {
        return getName();
    }


    /**
     * Return the User Provided DN as a String,
     *
     * @return A String representing the User Provided DN
     */
    private String toUpName()
    {
        if ( rdns.size() == 0 )
        {
            upName = "";
        }
        else
        {
            StringBuffer sb = new StringBuffer();
            boolean isFirst = true;

            for ( RDN rdn : rdns )
            {
                if ( isFirst )
                {
                    isFirst = false;
                }
                else
                {
                    sb.append( ',' );
                }

                sb.append( rdn.getName() );
            }

            upName = sb.toString();
        }

        return upName;
    }


    /**
     * Return the User Provided prefix representation of the DN starting at the
     * posn position.
     *
     * If posn = 0, return an empty string.
     *
     * for DN : sn=smith, dc=apache, dc=org
     * getUpname(0) -> ""
     * getUpName(1) -> "dc=org"
     * getUpname(3) -> "sn=smith, dc=apache, dc=org"
     * getUpName(4) -> ArrayOutOfBoundException
     *
     * Warning ! The returned String is not exactly the
     * user provided DN, as spaces before and after each RDNs have been trimmed.
     *
     * @param posn
     *            The starting position
     * @return The truncated DN
     */
    private String getUpNamePrefix( int posn )
    {
        if ( posn == 0 )
        {
            return "";
        }

        if ( posn > rdns.size() )
        {
            String message = I18n.err( I18n.ERR_04203, posn, rdns.size() );
            LOG.error( message );
            throw new ArrayIndexOutOfBoundsException( message );
        }

        int start = rdns.size() - posn;
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;

        for ( int i = start; i < rdns.size(); i++ )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                sb.append( ',' );
            }

            sb.append( rdns.get( i ).getName() );
        }

        return sb.toString();
    }


    /**
     * Return the User Provided suffix representation of the DN starting at the
     * posn position.
     * If posn = 0, return an empty string.
     *
     * for DN : sn=smith, dc=apache, dc=org
     * getUpname(0) -> "sn=smith, dc=apache, dc=org"
     * getUpName(1) -> "sn=smith, dc=apache"
     * getUpname(3) -> "sn=smith"
     * getUpName(4) -> ""
     *
     * Warning ! The returned String is not exactly the user
     * provided DN, as spaces before and after each RDNs have been trimmed.
     *
     * @param posn The starting position
     * @return The truncated DN
     */
    private String getUpNameSuffix( int posn )
    {
        if ( posn > rdns.size() )
        {
            return "";
        }

        int end = rdns.size() - posn;
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;

        for ( int i = 0; i < end; i++ )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                sb.append( ',' );
            }

            sb.append( rdns.get( i ).getName() );
        }

        return sb.toString();
    }


    /**
     * Gets the hash code of this name.
     *
     * @see java.lang.Object#hashCode()
     * @return the instance hash code
     */
    public int hashCode()
    {
        int result = 37;

        for ( RDN rdn : rdns )
        {
            result = result * 17 + rdn.hashCode();
        }

        return result;
    }


    /**
     * Get the initial DN
     *
     * @return The DN as a String
     */
    public String getName()
    {
        return ( upName == null ? "" : upName );
    }


    /**
     * Sets the up name.
     *
     * Package private because DN is immutable, only used by the DN parser.
     *
     * @param upName the new up name
     */
    void setUpName( String upName )
    {
        this.upName = upName;
    }


    /**
     * Get the normalized DN
     *
     * @return The DN as a String
     */
    public String getNormName()
    {
        if ( normName == null )
        {
            normName = toNormName();
        }

        return normName;
    }


    /**
     * {@inheritDoc}
     */
    public int size()
    {
        return rdns.size();
    }


    /**
     * Get the number of bytes necessary to store this DN

     * @param dn The DN.
     * @return A integer, which is the size of the UTF-8 byte array
     */
    public static int getNbBytes( DN dn )
    {
        return dn.bytes == null ? 0 : dn.bytes.length;
    }


    /**
     * Get an UTF-8 representation of the normalized form of the DN
     *
     * @param dn The DN.
     * @return A byte[] representation of the DN
     */
    public static byte[] getBytes( DN dn )
    {
        return dn == null ? null : dn.bytes;
    }


    /**
     * Tells if the current DN is a parent of another DN.<br>
     * For instance, <b>dc=com</b> is a parent
     * of <b>dc=example, dc=com</b>
     *
     * @param dn The child
     * @return true if the current DN is a parent of the given DN
     */
    public boolean isParentOf( String dn )
    {
        try
        {
            return isParentOf( new DN( dn ) );
        }
        catch( LdapInvalidDnException lide )
        {
            return false;
        }
    }


    /**
     * Tells if the current DN is a parent of another DN.<br>
     * For instance, <b>dc=com</b> is a parent
     * of <b>dc=example, dc=com</b>
     *
     * @param dn The child
     * @return true if the current DN is a parent of the given DN
     */
    public boolean isParentOf( DN dn )
    {
        if ( dn == null )
        {
            return false;
        }

        return dn.isChildOf( this );
    }


    /**
     * Tells if a DN is a child of another DN.<br>
     * For instance, <b>dc=example, dc=com</b> is a child
     * of <b>dc=com</b>
     *
     * @param dn The parent
     * @return true if the current DN is a child of the given DN
     */
    public boolean isChildOf( String dn )
    {
        try
        {
            return isChildOf( new DN( dn ) );
        }
        catch( LdapInvalidDnException lide )
        {
            return false;
        }
    }


    /**
     * Tells if a DN is a child of another DN.<br>
     * For instance, <b>dc=example, dc=apache, dc=com</b> is a child
     * of <b>dc=com</b>
     *
     * @param dn The parent
     * @return true if the current DN is a child of the given DN
     */
    public boolean isChildOf( DN dn )
    {
        if ( ( dn == null ) || dn.isRootDSE() )
        {
            return true;
        }

        if ( dn.size() > size() )
        {
            // The name is longer than the current DN.
            return false;
        }

        // Ok, iterate through all the RDN of the name,
        // starting a the end of the current list.

        for ( int i = dn.size() - 1; i >= 0; i-- )
        {
            RDN nameRdn = dn.rdns.get( dn.rdns.size() - i - 1 );
            RDN ldapRdn = rdns.get( rdns.size() - i - 1 );

            if ( nameRdn.compareTo( ldapRdn ) != 0 )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Determines whether this name has a specific suffix. A name
     * <tt>name</tt> has a DN as a suffix if its right part contains the given DN
     *
     * Be aware that for a specific
     * DN like : <b>cn=xxx, ou=yyy</b> the hasSuffix method will return false with
     * <b>ou=yyy</b>, and true with <b>cn=xxx</b>
     *
     * @param dn the name to check
     * @return true if <tt>dn</tt> is a suffix of this name, false otherwise
     */
    public boolean hasSuffix( DN dn )
    {

        if ( dn == null )
        {
            return true;
        }

        if ( dn.size() == 0 )
        {
            return true;
        }

        if ( dn.size() > size() )
        {
            // The name is longer than the current DN.
            return false;
        }

        // Ok, iterate through all the RDN of the name,
        // starting a the end of the current list.

        for ( int i = 0; i < dn.size(); i++ )
        {
            RDN nameRdn = dn.rdns.get( i );
            RDN ldapRdn = rdns.get( i );

            if ( nameRdn.compareTo( ldapRdn ) != 0 )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Tells if the DN contains no RDN
     *
     * @return <code>true</code> if the DN is empty
     */
    public boolean isEmpty()
    {
        return ( rdns.size() == 0 );
    }


    /**
     * Tells if the DN is the RootDSE DN (ie, an empty DN)
     *
     * @return <code>true</code> if the DN is the RootDSE's DN
     */
    public boolean isRootDSE()
    {
        return ( rdns.size() == 0 );
    }


    /**
     * Get the given RDN as a String. The position is used in the
     * reverse order. Assuming that we have a DN like
     * <pre>dc=example,dc=apache,dc=org</pre>
     * then :
     * <li><code>get(0)</code> will return dc=org</li>
     * <li><code>get(1)</code> will return dc=apache</li>
     * <li><code>get(2)</code> will return dc=example</li>
     *
     * @param posn The position of the wanted RDN in the DN.
     */
    public String get( int posn )
    {
        if ( rdns.size() == 0 )
        {
            return "";
        }
        else
        {
            RDN rdn = rdns.get( rdns.size() - posn - 1 );

            return rdn.getNormName();
        }
    }


    /**
     * Retrieves a component of this name.
     *
     * @param posn
     *            the 0-based index of the component to retrieve. Must be in the
     *            range [0,size()).
     * @return the component at index posn
     * @throws ArrayIndexOutOfBoundsException
     *             if posn is outside the specified range
     */
    public RDN getRdn( int posn )
    {
        if ( rdns.size() == 0 )
        {
            return null;
        }
        else
        {
            RDN rdn = rdns.get( rdns.size() - posn - 1 );

            return rdn;
        }
    }


    /**
     * Retrieves the last (leaf) component of this name.
     *
     * @return the last component of this DN
     */
    public RDN getRdn()
    {
        if ( rdns.size() == 0 )
        {
            return null;
        }
        else
        {
            return rdns.get( 0 );
        }
    }


    /**
     * Retrieves all the components of this name.
     *
     * @return All the components
     */
    public List<RDN> getRdns()
    {
        return UnmodifiableList.decorate( rdns );
    }


    /**
     * {@inheritDoc}
     */
    public DN getPrefix( int posn )
    {
        if ( rdns.size() == 0 )
        {
            return EMPTY_DN;
        }

        if ( ( posn < 0 ) || ( posn > rdns.size() ) )
        {
            String message = I18n.err( I18n.ERR_04206, posn, rdns.size() );
            LOG.error( message );
            throw new ArrayIndexOutOfBoundsException( message );
        }

        DN newDN = new DN();

        for ( int i = rdns.size() - posn; i < rdns.size(); i++ )
        {
            // Don't forget to clone the rdns !
            newDN.rdns.add( ( RDN ) rdns.get( i ).clone() );
        }

        newDN.normName = newDN.toNormName();
        newDN.upName = getUpNamePrefix( posn );

        return newDN;
    }


    /**
     * {@inheritDoc}
     */
    public DN getSuffix( int posn )
    {
        if ( rdns.size() == 0 )
        {
            return EMPTY_DN;
        }

        if ( ( posn < 0 ) || ( posn > rdns.size() ) )
        {
            String message = I18n.err( I18n.ERR_04206, posn, rdns.size() );
            LOG.error( message );
            throw new ArrayIndexOutOfBoundsException( message );
        }

        DN newDN = new DN();

        for ( int i = 0; i < size() - posn; i++ )
        {
            // Don't forget to clone the rdns !
            newDN.rdns.add( ( RDN ) rdns.get( i ).clone() );
        }

        newDN.normName = newDN.toNormName();
        newDN.upName = getUpNameSuffix( posn );

        return newDN;
    }


    /**
     * Adds the components of a name -- in order -- at a specified position
     * within this name. Components of this name at or after the index of the
     * first new component are shifted up (away from 0) to accommodate the new
     * components. Compoenents are supposed to be normalized.
     *
     * @param posn the index in this name at which to add the new components.
     *            Must be in the range [0,size()]. Note this is from the opposite end as rnds.get(posn)
     * @param dn the components to add
     * @return a cloned and updated DN of the original DN, if no changes were applied the original DN will be returned
     * @throws ArrayIndexOutOfBoundsException
     *             if posn is outside the specified range
     * @throws LdapInvalidDnException
     *             if <tt>n</tt> is not a valid name, or if the addition of
     *             the components would violate the syntax rules of this name
     */
    public DN addAllNormalized( int posn, DN dn ) throws LdapInvalidDnException
    {
        if ( ( dn == null ) || ( dn.size() == 0 ) )
        {
            return this;
        }

        DN clonedDn = ( DN ) clone();

        // Concatenate the rdns
        clonedDn.rdns.addAll( clonedDn.size() - posn, dn.rdns );

        if ( StringTools.isEmpty( normName ) )
        {
            clonedDn.normName = dn.normName;
            clonedDn.bytes = dn.bytes;
            clonedDn.upName = dn.upName;
        }
        else
        {
            clonedDn.normName = dn.normName + "," + normName;
            clonedDn.bytes = StringTools.getBytesUtf8( normName );
            clonedDn.upName = dn.upName + "," + upName;
        }

        return clonedDn;
    }

    /**
     * {@inheritDoc}
     */
    public DN addAll( DN suffix ) throws LdapInvalidDnException
    {
        return addAll( rdns.size(), suffix );
    }


    /**
     * {@inheritDoc}
     */
    public DN addAll( int posn, DN dn ) throws LdapInvalidDnException
    {
        if ( ( dn == null ) || ( dn.size() == 0 ) )
        {
            return this;
        }

        DN clonedDn = ( DN ) clone();

        // Concatenate the rdns
        clonedDn.rdns.addAll( clonedDn.size() - posn, dn.rdns );

        // Regenerate the normalized name and the original string
        if ( clonedDn.isNormalized() && dn.isNormalized() )
        {
            if ( clonedDn.size() != 0 )
            {
                clonedDn.normName = dn.getNormName() + "," + normName;
                clonedDn.bytes = StringTools.getBytesUtf8( normName );
                clonedDn.upName = dn.getName() + "," + upName;
            }
        }
        else
        {
            if( schemaManager != null )
            {
                clonedDn.normalize( schemaManager );
            }
            else
            {
                clonedDn.normalizeInternal();
                clonedDn.normalized.set( false );
            }

            clonedDn.toUpName();
        }

        return clonedDn;
    }


    /**
     * {@inheritDoc}
     */
    public DN add( String comp ) throws LdapInvalidDnException
    {
        if ( comp.length() == 0 )
        {
            return this;
        }

        DN clonedDn = ( DN ) clone();
        // We have to parse the nameComponent which is given as an argument
        RDN newRdn = new RDN( comp, schemaManager );

        clonedDn.rdns.add( 0, newRdn );

        if( schemaManager != null )
        {
            clonedDn.normalize( schemaManager );
        }
        else
        {
            clonedDn.normalizeInternal();
            clonedDn.normalized.set( false );
        }

        clonedDn.toUpName();

        return clonedDn;
    }


    /**
     * Adds a single RDN to the (leaf) end of this name.
     *
     * @param newRdn the RDN to add
     * @return the updated cloned DN
     */
    public DN add( RDN newRdn )
    {
        DN clonedDn = ( DN ) clone();

        clonedDn.rdns.add( 0, newRdn );

        // FIXME this try-catch block shouldn't be here
        // instead this method should throw the LdapInvalidDnException
        try
        {
            if( clonedDn.isNormalized() && newRdn.isNormalized() )
            {
                clonedDn.normalizeInternal();
            }
            else
            {
                if( schemaManager != null )
                {
                    clonedDn.normalize( schemaManager );
                }
                else
                {
                    clonedDn.normalizeInternal();
                    clonedDn.normalized.set( false );
                }
            }
        }
        catch( LdapInvalidDnException e )
        {
            LOG.error( e.getMessage(), e );
        }

        clonedDn.toUpName();

        return clonedDn;
    }


    /**
     * {@inheritDoc}
     */
    public DN add( int posn, String comp ) throws LdapInvalidDnException
    {
        if ( ( posn < 0 ) || ( posn > size() ) )
        {
            String message = I18n.err( I18n.ERR_04206, posn, rdns.size() );
            LOG.error( message );
            throw new ArrayIndexOutOfBoundsException( message );
        }

        // We have to parse the nameComponent which is given as an argument
        RDN newRdn = new RDN( comp );

        DN clonedDn = ( DN ) clone();

        int realPos = clonedDn.size() - posn;
        clonedDn.rdns.add( realPos, newRdn );

        if( schemaManager != null )
        {
            clonedDn.normalize( schemaManager );
        }
        else
        {
            clonedDn.normalizeInternal();
            clonedDn.normalized.set( false );
        }

        clonedDn.toUpName();

        return clonedDn;
    }


    /**
     * {@inheritDoc}
     */
    public DN remove( int posn ) throws LdapInvalidDnException
    {
        if ( rdns.size() == 0 )
        {
            return this;
        }

        if ( ( posn < 0 ) || ( posn >= rdns.size() ) )
        {
            String message = I18n.err( I18n.ERR_04206, posn, rdns.size() );
            LOG.error( message );
            throw new ArrayIndexOutOfBoundsException( message );
        }

        DN clonedDn = ( DN ) clone();
        clonedDn._removeChild( posn );

        return clonedDn;
    }


    /**
     * removes a child (RDN) present at the given position
     *
     * @param posn the index of the child's position
     */
    private void _removeChild( int posn )
    {
        int realPos = size() - posn - 1;
        rdns.remove( realPos );

        normalizeInternal();
        toUpName();
    }


    /**
     * Gets the parent DN of this DN. Null if this DN doesn't have a parent, i.e. because it
     * is the empty DN.
     *
     * @return the parent DN of this DN
     */
    public DN getParent()
    {
        if ( isEmpty() )
        {
            return null;
        }

        return getPrefix( size() - 1 );
    }


    /**
     * {@inheritDoc}
     */
    protected Object clone()
    {
        try
        {
            DN dn = ( DN ) super.clone();
            dn.normalized = new AtomicBoolean( normalized.get() );
            dn.rdns = new ArrayList<RDN>();

            for ( RDN rdn : rdns )
            {
                dn.rdns.add( ( RDN ) rdn.clone() );
            }

            return dn;
        }
        catch ( CloneNotSupportedException cnse )
        {
            LOG.error( I18n.err( I18n.ERR_04207 ) );
            throw new Error( I18n.err( I18n.ERR_04208 ) );
        }
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * @return <code>true</code> if the two instances are equals
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof String )
        {
            return normName.equals( obj );
        }
        else if ( obj instanceof DN )
        {
            DN name = ( DN ) obj;

            if ( name.size() != this.size() )
            {
                return false;
            }

            for ( int i = 0; i < this.size(); i++ )
            {
                if ( name.rdns.get( i ).compareTo( rdns.get( i ) ) != 0 )
                {
                    return false;
                }
            }

            // All components matched so we return true
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public int compareTo( DN dn )
    {
        if ( dn.size() != size() )
        {
            return size() - dn.size();
        }

        for ( int i = rdns.size(); i > 0; i-- )
        {
            RDN rdn1 = rdns.get( i - 1 );
            RDN rdn2 = dn.rdns.get( i - 1 );
            int res = rdn1.compareTo( rdn2 );

            if ( res != 0 )
            {
                return res;
            }
        }

        return EQUAL;
    }


    private static AVA atavOidToName( AVA atav, Map<String, OidNormalizer> oidsMap )
        throws LdapInvalidDnException
    {
        String type = StringTools.trim( atav.getNormType() );

        if ( ( type.startsWith( "oid." ) ) || ( type.startsWith( "OID." ) ) )
        {
            type = type.substring( 4 );
        }

        if ( StringTools.isNotEmpty( type ) )
        {
            if ( oidsMap == null )
            {
                return atav;
            }
            else
            {
                OidNormalizer oidNormalizer = oidsMap.get( type.toLowerCase() );

                if ( oidNormalizer != null )
                {
                    try
                    {
                        return new AVA(
                            atav.getUpType(),
                            oidNormalizer.getAttributeTypeOid(),
                            atav.getUpValue(),
                            oidNormalizer.getNormalizer().normalize( atav.getNormValue() ),
                            atav.getUpName() );
                    }
                    catch ( LdapException le )
                    {
                        throw new LdapInvalidDnException( le.getMessage() );
                    }
                }
                else
                {
                    // We don't have a normalizer for this OID : just do nothing.
                    return atav;
                }
            }
        }
        else
        {
            // The type is empty : this is not possible...
            String msg = I18n.err( I18n.ERR_04209 );
            LOG.error( msg );
            throw new LdapInvalidDnException( ResultCodeEnum.INVALID_DN_SYNTAX, msg );
        }
    }


    /**
     * Transform a RDN by changing the value to its OID counterpart and
     * normalizing the value accordingly to its type.
     *
     * @param rdn The RDN to modify.
     * @param oidsMap The map of all existing oids and normalizer.
     * @throws LdapInvalidDnException If the RDN is invalid.
     */
    /** No qualifier */ static void rdnOidToName( RDN rdn, Map<String, OidNormalizer> oidsMap ) throws LdapInvalidDnException
    {
        if ( rdn.getNbAtavs() > 1 )
        {
            // We have more than one ATAV for this RDN. We will loop on all
            // ATAVs
            RDN rdnCopy = ( RDN ) rdn.clone();
            rdn.clear();

            for ( AVA val:rdnCopy )
            {
                AVA newAtav = atavOidToName( val, oidsMap );
                rdn.addAttributeTypeAndValue( newAtav );
            }
        }
        else
        {
            AVA val = rdn.getAtav();
            rdn.clear();
            AVA newAtav = atavOidToName( val, oidsMap );
            rdn.addAttributeTypeAndValue( newAtav );
        }
    }


    /**
     * Change the internal DN, using the OID instead of the first name or other
     * aliases. As we still have the UP name of each RDN, we will be able to
     * provide both representation of the DN. example : dn: 2.5.4.3=People,
     * dc=example, domainComponent=com will be transformed to : 2.5.4.3=People,
     * 0.9.2342.19200300.100.1.25=example, 0.9.2342.19200300.100.1.25=com
     * because 2.5.4.3 is the OID for cn and dc is the first
     * alias of the couple of aliases (dc, domaincomponent), which OID is
     * 0.9.2342.19200300.100.1.25.
     * This is really important do have such a representation, as 'cn' and
     * 'commonname' share the same OID.
     *
     * @param dn The DN to transform.
     * @param oidsMap The mapping between names and oids.
     * @return A normalized form of the DN.
     * @throws LdapInvalidDnException If something went wrong.
     */
    public static DN normalize( DN dn, Map<String, OidNormalizer> oidsMap ) throws LdapInvalidDnException
    {
        if ( ( dn == null ) || ( dn.size() == 0 ) || ( oidsMap == null ) || ( oidsMap.size() == 0 ) )
        {
            return dn;
        }

        for ( RDN rdn : dn.rdns )
        {
            String upName = rdn.getName();
            rdnOidToName( rdn, oidsMap );
            rdn.normalize();
            rdn.setUpName( upName );
        }

        dn.normalizeInternal();

        dn.normalized.set( true );
        return dn;
    }


    /**
     * Change the internal DN, using the OID instead of the first name or other
     * aliases. As we still have the UP name of each RDN, we will be able to
     * provide both representation of the DN. example : dn: 2.5.4.3=People,
     * dc=example, domainComponent=com will be transformed to : 2.5.4.3=People,
     * 0.9.2342.19200300.100.1.25=example, 0.9.2342.19200300.100.1.25=com
     * because 2.5.4.3 is the OID for cn and dc is the first
     * alias of the couple of aliases (dc, domaincomponent), which OID is
     * 0.9.2342.19200300.100.1.25.
     * This is really important do have such a representation, as 'cn' and
     * 'commonname' share the same OID.
     *
     * @param oidsMap The mapping between names and oids.
     * @throws LdapInvalidDnException If something went wrong.
     * @return The normalized DN
     */
    public DN normalize( Map<String, OidNormalizer> oidsMap ) throws LdapInvalidDnException
    {

        if ( ( oidsMap == null ) || ( oidsMap.isEmpty() ) )
        {
            return this;
        }

        if( normalized.get() )
        {
           return this;
        }

        synchronized ( this )
        {
            if ( size() == 0 )
            {
                normalized.set( true );
                return this;
            }

            for ( RDN rdn : rdns )
            {
                rdn.normalize( oidsMap );
            }

            normalizeInternal();

            normalized.set( true );

            return this;
        }
    }


    /**
     * normalizes the DN @see {@link #normalize(Map)} however
     * if the schema manager of the DN is null then sets the given schema manager
     * as the DN's schema manager.
     *
     * If both, the given schema manager and that of the DN are null then the
     * {@link #normalizeInternal()} will be called.
     *
     */
    public DN normalize( SchemaManager schemaManager ) throws LdapInvalidDnException
    {
        if( this.schemaManager == null )
        {
            this.schemaManager = schemaManager;
        }

        if( this.schemaManager != null )
        {
            return normalize( schemaManager.getNormalizerMapping() );
        }

        normalizeInternal();

        return this;
    }


    /**
     * Check if a DistinguishedName is syntactically valid.
     *
     * @param dn The DN to validate
     * @return <code>true></code> if the DN is valid, <code>false</code>
     * otherwise
     */
    public static boolean isValid( String dn )
    {
        return DnParser.validateInternal( dn );
    }


    /**
     * Tells if the DN has already been normalized or not
     *
     * @return <code>true</code> if the DN is already normalized.
     */
    public boolean isNormalized()
    {
        return normalized.get();
    }


    /**
     * {@inheritDoc}
     */
    public Iterator<RDN> iterator()
    {
        return new RdnIterator();
    }
}
