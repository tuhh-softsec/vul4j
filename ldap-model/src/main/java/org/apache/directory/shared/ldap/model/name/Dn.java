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

package org.apache.directory.shared.ldap.model.name;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.model.schema.normalizers.OidNormalizer;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Dn class contains a Dn (Distinguished Name). This class is immutable.
 * <br/>
 * Its specification can be found in RFC 2253,
 * "UTF-8 String Representation of Distinguished Names".
 * <br/>
 * We will store two representation of a Dn :
 * <ul>
 * <li>a user Provider representation, which is the parsed String given by a user</li>
 * <li>an internal representation.</li>
 * </ul>
 *
 * A Dn is formed of RDNs, in a specific order :<br/>
 *  Rdn[n], Rdn[n-1], ... Rdn[1], Rdn[0]<br/>
 *
 * It represents a tree, in which the root is the last Rdn (Rdn[0]) and the leaf
 * is the first Rdn (Rdn[n]).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Dn implements Cloneable, Serializable, Comparable<Dn>, Iterable<Rdn>
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( Dn.class );

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

    /** A flag used to tell if the Dn has been normalized */
    private AtomicBoolean normalized;

    // ~ Static fields/initializers
    // -----------------------------------------------------------------
    /**
     *  The RDNs that are elements of the Dn
     * NOTE THAT THESE ARE IN THE OPPOSITE ORDER FROM THAT IMPLIED BY THE JAVADOC!
     * Rdn[0] is rdns.get(n) and Rdn[n] is rdns.get(0)
     * <br>
     * For instance,if the Dn is "dc=c, dc=b, dc=a", then the RDNs are stored as :
     * [0] : dc=c
     * [1] : dc=b
     * [2] : dc=a
     */
    protected List<Rdn> rdns = new ArrayList<Rdn>( 5 );

    /** The user provided name */
    private String upName;

    /** The normalized name */
    private String normName;

    /** The bytes representation of the normName */
    private byte[] bytes;

    /** A null Dn */
    public static final Dn EMPTY_DN = new Dn();

    /** the schema manager */
    private transient SchemaManager schemaManager;

    /**
     * An iterator over RDNs
     */
    private final class RdnIterator implements Iterator<Rdn>
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
        public Rdn next()
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
     * Construct an empty Dn object
     */
    public Dn()
    {
        this( ( SchemaManager ) null );
    }


    /**
     * Construct an empty Dn object
     */
    public Dn(SchemaManager schemaManger)
    {
        this.schemaManager = schemaManger;
        upName = "";
        normName = "";
        normalized = new AtomicBoolean( true );
    }


    /**
     * @see #Dn(Dn, SchemaManager)
     */
    public Dn(Dn dn) throws LdapInvalidDnException
    {
        this( dn, null );
    }


    /**
     * Copies a Dn to an Dn.
     *
     * @param dn composed of String name components.
     * @param schemaManager the schema manager
     * @throws LdapInvalidDnException If the Name is invalid.
     */
    public Dn(Dn dn, SchemaManager schemaManager) throws LdapInvalidDnException
    {
        this.schemaManager = schemaManager;

        if ( ( dn != null ) && ( dn.size() != 0 ) )
        {
            for ( Rdn rdn : dn )
            {
                rdns.add( 0, rdn.clone() );
            }
        }

        toUpName();

        normalized = new AtomicBoolean();

        if ( schemaManager != null )
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
     * @see #Dn(String, SchemaManager)
     */
    public Dn(String upName) throws LdapInvalidDnException
    {
        this( upName, null );
    }


    /**
     * Parse a String and checks that it is a valid Dn <br>
     * <p>
     * &lt;distinguishedName&gt; ::= &lt;name&gt; | e <br>
     * &lt;name&gt; ::= &lt;name-component&gt; &lt;name-components&gt; <br>
     * &lt;name-components&gt; ::= &lt;spaces&gt; &lt;separator&gt;
     * &lt;spaces&gt; &lt;name-component&gt; &lt;name-components&gt; | e <br>
     * </p>
     *
     * @param upName The String that contains the Dn.
     * @param schemaManager the schema manager (optional)
     * @throws LdapInvalidNameException if the String does not contain a valid Dn.
     */
    public Dn(String upName, SchemaManager schemaManager) throws LdapInvalidDnException
    {
        if ( upName != null )
        {
            DnParser.parseInternal( upName, rdns );
        }

        normalized = new AtomicBoolean();

        if ( schemaManager != null )
        {
            this.schemaManager = schemaManager;
            normalize( schemaManager.getNormalizerMapping() );
        }
        else
        {
            normalized.set( false );

            // Stores the representations of a Dn : internal (as a string and as a
            // byte[]) and external.
            normalizeInternal();
        }

        this.upName = upName;
    }


    /**
     * @see #Dn(SchemaManager, String...)
     */
    public Dn(String... upRdns) throws LdapInvalidDnException
    {
        this( null, upRdns );
    }


    /**
     * Creates a new instance of Dn, using varargs to declare the RDNs. Each
     * String is either a full Rdn, or a couple of AttributeType DI and a value.
     * If the String contains a '=' symbol, the the constructor will assume that
     * the String arg contains afull Rdn, otherwise, it will consider that the
     * following arg is the value.
     * An example of usage would be :
     * <pre>
     * String exampleName = "example";
     * String baseDn = "dc=apache,dc=org";
     *
     * Dn dn = new Dn(
     *     "cn=Test",
     *     "ou", exampleName,
     *     baseDn);
     * </pre>
     *
     * @param schemaManager the schema manager
     * @param upRdns
     * @throws LdapInvalidDnException
     */
    public Dn(SchemaManager schemaManager, String... upRdns) throws LdapInvalidDnException
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

        // Stores the representations of a Dn : internal (as a string and as a
        // byte[]) and external.
        upName = sb.toString();
        DnParser.parseInternal( upName, rdns );

        if ( schemaManager != null )
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
     * Create a Dn while deserializing it.
     *
     * Note : this constructor is used only by the deserialization method.
     * @param upName The user provided name
     * @param normName the normalized name
     * @param bytes the name as a byte[]
     */
    Dn(String upName, String normName, byte[] bytes)
    {
        normalized = new AtomicBoolean( true );
        this.upName = upName;
        this.normName = normName;
        this.bytes = bytes;
    }


    /**
     * Creates a Dn.
     *
     * Note: This is mostly used internally in the server
     *
     * @param upName The user provided name
     * @param normName the normalized name
     * @param bytes the name as a byte[]
     * @param rdnList the list of RDNs present in the Dn
     */
    public Dn(String upName, String normName, byte[] bytes, List<Rdn> rdnList)
    {
        this( upName, normName, bytes );
        rdns.addAll( rdnList );
    }


    /**
     *
     * Creates a Dn by based on the given Rdn.
     *
     * @param rdn the Rdn to be used in the Dn
     */
    public Dn(Rdn rdn)
    {
        rdns.add( rdn.clone() );

        if ( rdn.isNormalized() )
        {
            this.normName = rdn.getNormName();
            this.upName = rdn.getName();
            this.bytes = Strings.getBytesUtf8(normName);
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
     * Static factory which creates a normalized Dn from a String and a Map of OIDs.
     * <br>
     * This method is for test purpose only, and as it's package protected, won't be usable
     * out of this scope.
     *
     * @param name The Dn as a String
     * @param oidsMap The OID mapping
     * @return A valid Dn
     * @throws LdapInvalidNameException If the Dn is invalid.
     * @throws LdapInvalidDnException If something went wrong.
     */
    /* No qualifier */static Dn normalize( String name, Map<String, OidNormalizer> oidsMap )
        throws LdapInvalidDnException
    {
        if ( ( name == null ) || ( name.length() == 0 ) || ( oidsMap == null ) || ( oidsMap.isEmpty() ) )
        {
            return Dn.EMPTY_DN;
        }

        Dn newDn = new Dn( name );

        for ( Rdn rdn : newDn.rdns )
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
     * Normalize the Dn by triming useless spaces and lowercasing names.
     */
    void normalizeInternal()
    {
        normName = toNormName();
    }


    /**
     * Build the normalized Dn as a String,
     *
     * @return A String representing the normalized Dn
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

            for ( Rdn rdn : rdns )
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
                bytes = Strings.getBytesUtf8(newNormName);
                normName = newNormName;
            }

            return normName;
        }
    }


    /**
     * Return the normalized Dn as a String. It returns the same value as the
     * getNormName method
     *
     * @return A String representing the normalized Dn
     */
    public String toString()
    {
        return getName();
    }


    /**
     * Return the User Provided Dn as a String,
     *
     * @return A String representing the User Provided Dn
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

            for ( Rdn rdn : rdns )
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
     * Return the User Provided prefix representation of the Dn starting at the
     * posn position.
     *
     * If posn = 0, return an empty string.
     *
     * for Dn : sn=smith, dc=apache, dc=org
     * getUpname(0) -> ""
     * getUpName(1) -> "dc=org"
     * getUpname(3) -> "sn=smith, dc=apache, dc=org"
     * getUpName(4) -> ArrayOutOfBoundException
     *
     * Warning ! The returned String is not exactly the
     * user provided Dn, as spaces before and after each RDNs have been trimmed.
     *
     * @param posn
     *            The starting position
     * @return The truncated Dn
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
     * Return the User Provided suffix representation of the Dn starting at the
     * posn position.
     * If posn = 0, return an empty string.
     *
     * for Dn : sn=smith, dc=apache, dc=org
     * getUpname(0) -> "sn=smith, dc=apache, dc=org"
     * getUpName(1) -> "sn=smith, dc=apache"
     * getUpname(3) -> "sn=smith"
     * getUpName(4) -> ""
     *
     * Warning ! The returned String is not exactly the user
     * provided Dn, as spaces before and after each RDNs have been trimmed.
     *
     * @param posn The starting position
     * @return The truncated Dn
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

        for ( Rdn rdn : rdns )
        {
            result = result * 17 + rdn.hashCode();
        }

        return result;
    }


    /**
     * Get the initial Dn
     *
     * @return The Dn as a String
     */
    public String getName()
    {
        return ( upName == null ? "" : upName );
    }


    /**
     * Sets the up name.
     *
     * Package private because Dn is immutable, only used by the Dn parser.
     *
     * @param upName the new up name
     */
    void setUpName( String upName )
    {
        this.upName = upName;
    }


    /**
     * Get the normalized Dn
     *
     * @return The Dn as a String
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
     * Get the number of bytes necessary to store this Dn

     * @param dn The Dn.
     * @return A integer, which is the size of the UTF-8 byte array
     */
    public static int getNbBytes( Dn dn )
    {
        return dn.bytes == null ? 0 : dn.bytes.length;
    }


    /**
     * Get an UTF-8 representation of the normalized form of the Dn
     *
     * @param dn The Dn.
     * @return A byte[] representation of the Dn
     */
    public static byte[] getBytes( Dn dn )
    {
        return dn == null ? null : dn.bytes;
    }


    /**
     * Tells if the current Dn is a parent of another Dn.<br>
     * For instance, <b>dc=com</b> is a parent
     * of <b>dc=example, dc=com</b>
     *
     * @param dn The child
     * @return true if the current Dn is a parent of the given Dn
     */
    public boolean isParentOf( String dn )
    {
        try
        {
            return isParentOf( new Dn( dn ) );
        }
        catch ( LdapInvalidDnException lide )
        {
            return false;
        }
    }


    /**
     * Tells if the current Dn is a parent of another Dn.<br>
     * For instance, <b>dc=com</b> is a parent
     * of <b>dc=example, dc=com</b>
     *
     * @param dn The child
     * @return true if the current Dn is a parent of the given Dn
     */
    public boolean isParentOf( Dn dn )
    {
        if ( dn == null )
        {
            return false;
        }

        return dn.isChildOf( this );
    }


    /**
     * Tells if a Dn is a child of another Dn.<br>
     * For instance, <b>dc=example, dc=com</b> is a child
     * of <b>dc=com</b>
     *
     * @param dn The parent
     * @return true if the current Dn is a child of the given Dn
     */
    public boolean isChildOf( String dn )
    {
        try
        {
            return isChildOf( new Dn( dn ) );
        }
        catch ( LdapInvalidDnException lide )
        {
            return false;
        }
    }


    /**
     * Tells if a Dn is a child of another Dn.<br>
     * For instance, <b>dc=example, dc=apache, dc=com</b> is a child
     * of <b>dc=com</b>
     *
     * @param dn The parent
     * @return true if the current Dn is a child of the given Dn
     */
    public boolean isChildOf( Dn dn )
    {
        if ( ( dn == null ) || dn.isRootDSE() )
        {
            return true;
        }

        if ( dn.size() > size() )
        {
            // The name is longer than the current Dn.
            return false;
        }

        // Ok, iterate through all the Rdn of the name,
        // starting a the end of the current list.

        for ( int i = dn.size() - 1; i >= 0; i-- )
        {
            Rdn nameRdn = dn.rdns.get( dn.rdns.size() - i - 1 );
            Rdn ldapRdn = rdns.get( rdns.size() - i - 1 );

            if ( nameRdn.compareTo( ldapRdn ) != 0 )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Determines whether this name has a specific suffix. A name
     * <tt>name</tt> has a Dn as a suffix if its right part contains the given Dn
     *
     * Be aware that for a specific
     * Dn like : <b>cn=xxx, ou=yyy</b> the hasSuffix method will return false with
     * <b>ou=yyy</b>, and true with <b>cn=xxx</b>
     *
     * @param dn the name to check
     * @return true if <tt>dn</tt> is a suffix of this name, false otherwise
     */
    public boolean hasSuffix( Dn dn )
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
            // The name is longer than the current Dn.
            return false;
        }

        // Ok, iterate through all the Rdn of the name,
        // starting a the end of the current list.

        for ( int i = 0; i < dn.size(); i++ )
        {
            Rdn nameRdn = dn.rdns.get( i );
            Rdn ldapRdn = rdns.get( i );

            if ( nameRdn.compareTo( ldapRdn ) != 0 )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Tells if the Dn contains no Rdn
     *
     * @return <code>true</code> if the Dn is empty
     */
    public boolean isEmpty()
    {
        return ( rdns.size() == 0 );
    }


    /**
     * Tells if the Dn is the RootDSE Dn (ie, an empty Dn)
     *
     * @return <code>true</code> if the Dn is the RootDSE's Dn
     */
    public boolean isRootDSE()
    {
        return ( rdns.size() == 0 );
    }


    /**
     * Get the given Rdn as a String. The position is used in the
     * reverse order. Assuming that we have a Dn like
     * <pre>dc=example,dc=apache,dc=org</pre>
     * then :
     * <li><code>get(0)</code> will return dc=org</li>
     * <li><code>get(1)</code> will return dc=apache</li>
     * <li><code>get(2)</code> will return dc=example</li>
     *
     * @param posn The position of the wanted Rdn in the Dn.
     */
    public String get( int posn )
    {
        if ( rdns.size() == 0 )
        {
            return "";
        }
        else
        {
            Rdn rdn = rdns.get( rdns.size() - posn - 1 );

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
    public Rdn getRdn( int posn )
    {
        if ( rdns.size() == 0 )
        {
            return null;
        }
        else
        {
            Rdn rdn = rdns.get( rdns.size() - posn - 1 );

            return rdn.clone();
        }
    }


    /**
     * Retrieves the last (leaf) component of this name.
     *
     * @return the last component of this Dn
     */
    public Rdn getRdn()
    {
        if ( rdns.size() == 0 )
        {
            return null;
        }
        else
        {
            return rdns.get( 0 ).clone();
        }
    }


    /**
     * Retrieves all the components of this name.
     *
     * @return All the components
     */
    @SuppressWarnings("unchecked")
    public List<Rdn> getRdns()
    {
        return UnmodifiableList.decorate( rdns );
    }


    /**
     * {@inheritDoc}
     */
    public Dn getPrefix( int posn )
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

        Dn newDn = new Dn();

        for ( int i = rdns.size() - posn; i < rdns.size(); i++ )
        {
            // Don't forget to clone the rdns !
            newDn.rdns.add( (Rdn) rdns.get( i ).clone() );
        }

        newDn.normName = newDn.toNormName();
        newDn.upName = getUpNamePrefix( posn );
        newDn.normalized.set( normalized.get() );

        return newDn;
    }


    /**
     * {@inheritDoc}
     */
    public Dn getSuffix( int posn )
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

        Dn newDn = new Dn();

        for ( int i = 0; i < size() - posn; i++ )
        {
            // Don't forget to clone the rdns !
            newDn.rdns.add( (Rdn) rdns.get( i ).clone() );
        }

        newDn.normName = newDn.toNormName();
        newDn.upName = getUpNameSuffix( posn );

        return newDn;
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
     * @return a cloned and updated Dn of the original Dn, if no changes were applied the original Dn will be returned
     * @throws ArrayIndexOutOfBoundsException
     *             if posn is outside the specified range
     * @throws LdapInvalidDnException
     *             if <tt>n</tt> is not a valid name, or if the addition of
     *             the components would violate the syntax rules of this name
     */
    public Dn addAllNormalized( int posn, Dn dn ) throws LdapInvalidDnException
    {
        if ( ( dn == null ) || ( dn.size() == 0 ) )
        {
            return this;
        }

        Dn clonedDn = clone();

        // Concatenate the rdns
        clonedDn.rdns.addAll( clonedDn.size() - posn, dn.rdns );

        if ( Strings.isEmpty(normName) )
        {
            clonedDn.normName = dn.normName;
            clonedDn.bytes = dn.bytes;
            clonedDn.upName = dn.upName;
        }
        else
        {
            clonedDn.normName = dn.normName + "," + normName;
            clonedDn.bytes = Strings.getBytesUtf8(normName);
            clonedDn.upName = dn.upName + "," + upName;
        }

        return clonedDn;
    }


    /**
     * {@inheritDoc}
     */
    public Dn addAll( Dn suffix ) throws LdapInvalidDnException
    {
        return addAll( rdns.size(), suffix );
    }


    /**
     * {@inheritDoc}
     */
    public Dn addAll( int posn, Dn dn ) throws LdapInvalidDnException
    {
        if ( ( dn == null ) || ( dn.size() == 0 ) )
        {
            return this;
        }

        Dn clonedDn = clone();

        // Concatenate the rdns
        clonedDn.rdns.addAll( clonedDn.size() - posn, dn.rdns );

        // Regenerate the normalized name and the original string
        if ( clonedDn.isNormalized() && dn.isNormalized() )
        {
            if ( clonedDn.size() != 0 )
            {
                clonedDn.normName = dn.getNormName() + "," + normName;
                clonedDn.bytes = Strings.getBytesUtf8(normName);
                clonedDn.upName = dn.getName() + "," + upName;
            }
        }
        else
        {
            if ( schemaManager != null )
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
    public Dn add( String comp ) throws LdapInvalidDnException
    {
        if ( comp.length() == 0 )
        {
            return this;
        }

        Dn clonedDn = clone();

        // We have to parse the nameComponent which is given as an argument
        Rdn newRdn = new Rdn( comp, schemaManager );

        clonedDn.rdns.add( 0, newRdn );

        if ( schemaManager != null )
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
     * Adds a single Rdn to the (leaf) end of this name.
     *
     * @param newRdn the Rdn to add
     * @return the updated cloned Dn
     */
    public Dn add( Rdn newRdn )
    {
        Dn clonedDn = clone();

        clonedDn.rdns.add( 0, newRdn.clone() );
        clonedDn.normalized.getAndSet( false );

        // FIXME this try-catch block shouldn't be here
        // instead this method should throw the LdapInvalidDnException
        try
        {
            if ( clonedDn.isNormalized() && newRdn.isNormalized() )
            {
                clonedDn.normalizeInternal();
            }
            else
            {
                if ( schemaManager != null )
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
        catch ( LdapInvalidDnException e )
        {
            LOG.error( e.getMessage(), e );
        }

        clonedDn.toUpName();

        return clonedDn;
    }


    /**
     * {@inheritDoc}
     */
    public Dn add( int posn, String comp ) throws LdapInvalidDnException
    {
        if ( ( posn < 0 ) || ( posn > size() ) )
        {
            String message = I18n.err( I18n.ERR_04206, posn, rdns.size() );
            LOG.error( message );
            throw new ArrayIndexOutOfBoundsException( message );
        }

        // We have to parse the nameComponent which is given as an argument
        Rdn newRdn = new Rdn( comp );

        Dn clonedDn = clone();

        int realPos = clonedDn.size() - posn;
        clonedDn.rdns.add( realPos, newRdn );

        if ( schemaManager != null )
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
    public Dn remove( int posn ) throws LdapInvalidDnException
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

        Dn clonedDn = clone();
        clonedDn._removeChild( posn );

        return clonedDn;
    }


    /**
     * removes a child (Rdn) present at the given position
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
     * Gets the parent Dn of this Dn. Null if this Dn doesn't have a parent, i.e. because it
     * is the empty Dn.
     *
     * @return the parent Dn of this Dn
     */
    public Dn getParent()
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
    protected Dn clone()
    {
        try
        {
            Dn dn = (Dn) super.clone();
            dn.normalized = new AtomicBoolean( normalized.get() );
            dn.rdns = new ArrayList<Rdn>();

            for ( Rdn rdn : rdns )
            {
                dn.rdns.add( (Rdn) rdn.clone() );
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
            return normName.equals( ( String ) obj );
        }
        else if ( obj instanceof Dn)
        {
            Dn name = (Dn) obj;

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
    public int compareTo( Dn dn )
    {
        if ( dn.size() != size() )
        {
            return size() - dn.size();
        }

        for ( int i = rdns.size(); i > 0; i-- )
        {
            Rdn rdn1 = rdns.get( i - 1 );
            Rdn rdn2 = dn.rdns.get( i - 1 );
            int res = rdn1.compareTo( rdn2 );

            if ( res != 0 )
            {
                return res;
            }
        }

        return EQUAL;
    }


    private static Ava atavOidToName( Ava atav, Map<String, OidNormalizer> oidsMap )
        throws LdapInvalidDnException
    {
        String type = Strings.trim(atav.getNormType());

        if ( ( type.startsWith( "oid." ) ) || ( type.startsWith( "OID." ) ) )
        {
            type = type.substring( 4 );
        }

        if ( Strings.isNotEmpty(type) )
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
                        return new Ava(
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
     * Transform a Rdn by changing the value to its OID counterpart and
     * normalizing the value accordingly to its type.
     *
     * @param rdn The Rdn to modify.
     * @param oidsMap The map of all existing oids and normalizer.
     * @throws LdapInvalidDnException If the Rdn is invalid.
     */
    /** No qualifier */
    static void rdnOidToName( Rdn rdn, Map<String, OidNormalizer> oidsMap ) throws LdapInvalidDnException
    {
        if ( rdn.getNbAtavs() > 1 )
        {
            // We have more than one ATAV for this Rdn. We will loop on all
            // ATAVs
            Rdn rdnCopy = (Rdn) rdn.clone();
            rdn.clear();

            for ( Ava val : rdnCopy )
            {
                Ava newAtav = atavOidToName( val, oidsMap );
                rdn.addAVA( newAtav );
            }
        }
        else
        {
            Ava val = rdn.getAVA();
            rdn.clear();
            Ava newAtav = atavOidToName( val, oidsMap );
            rdn.addAVA( newAtav );
        }
    }


    /**
     * Change the internal Dn, using the OID instead of the first name or other
     * aliases. As we still have the UP name of each Rdn, we will be able to
     * provide both representation of the Dn. example : dn: 2.5.4.3=People,
     * dc=example, domainComponent=com will be transformed to : 2.5.4.3=People,
     * 0.9.2342.19200300.100.1.25=example, 0.9.2342.19200300.100.1.25=com
     * because 2.5.4.3 is the OID for cn and dc is the first
     * alias of the couple of aliases (dc, domaincomponent), which OID is
     * 0.9.2342.19200300.100.1.25.
     * This is really important do have such a representation, as 'cn' and
     * 'commonname' share the same OID.
     *
     * @param dn The Dn to transform.
     * @param oidsMap The mapping between names and oids.
     * @return A normalized form of the Dn.
     * @throws LdapInvalidDnException If something went wrong.
     */
    public static Dn normalize( Dn dn, Map<String, OidNormalizer> oidsMap ) throws LdapInvalidDnException
    {
        if ( ( dn == null ) || ( dn.size() == 0 ) || ( oidsMap == null ) || ( oidsMap.size() == 0 ) )
        {
            return dn;
        }

        for ( Rdn rdn : dn.rdns )
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
     * Change the internal Dn, using the OID instead of the first name or other
     * aliases. As we still have the UP name of each Rdn, we will be able to
     * provide both representation of the Dn. example : dn: 2.5.4.3=People,
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
     * @return The normalized Dn
     */
    public Dn normalize( Map<String, OidNormalizer> oidsMap ) throws LdapInvalidDnException
    {
        if ( ( oidsMap == null ) || ( oidsMap.isEmpty() ) )
        {
            return this;
        }

        if ( normalized.get() )
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

            for ( Rdn rdn : rdns )
            {
                rdn.normalize( oidsMap );
            }

            normalizeInternal();

            normalized.set( true );

            return this;
        }
    }


    /**
     * normalizes the Dn @see {@link #normalize(Map)} however
     * if the schema manager of the Dn is null then sets the given schema manager
     * as the Dn's schema manager.
     *
     * If both, the given schema manager and that of the Dn are null then the
     * {@link #normalizeInternal()} will be called.
     *
     */
    public Dn normalize( SchemaManager schemaManager ) throws LdapInvalidDnException
    {
        if ( this.schemaManager == null )
        {
            this.schemaManager = schemaManager;
        }

        if ( this.schemaManager != null )
        {
            return normalize( schemaManager.getNormalizerMapping() );
        }

        normalizeInternal();

        return this;
    }


    /**
     * Check if a DistinguishedName is syntactically valid.
     *
     * @param dn The Dn to validate
     * @return <code>true></code> if the Dn is valid, <code>false</code>
     * otherwise
     */
    public static boolean isValid( String dn )
    {
        return DnParser.validateInternal( dn );
    }


    /**
     * Tells if the Dn has already been normalized or not
     *
     * @return <code>true</code> if the Dn is already normalized.
     */
    public boolean isNormalized()
    {
        return normalized.get();
    }


    /**
     * {@inheritDoc}
     */
    public Iterator<Rdn> iterator()
    {
        return new RdnIterator();
    }


    /**
     * Check if a DistinguishedName is null or empty.
     *
     * @param dn The Dn to validate
     * @return <code>true></code> if the Dn is null or empty, <code>false</code>
     * otherwise
     */
    public static boolean isNullOrEmpty( Dn dn )
    {
        if ( dn != null )
        {
            return dn.isEmpty();
        }

        return true;
    }
}
