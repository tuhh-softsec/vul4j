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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public final class Dn implements Iterable<Rdn>
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
    private boolean normalized;

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

    /** The rootDSE */
    public static final Dn ROOT_DSE = new Dn();

    /** the schema manager */
    private SchemaManager schemaManager;

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


    /**
     * Construct an empty Dn object
     */
    public Dn()
    {
        this( ( SchemaManager ) null );
    }


    /**
     * Construct an empty Schema aware Dn object
     * 
     *  @param schemaManager The SchemaManager to use
     */
    public Dn( SchemaManager schemaManager )
    {
        this.schemaManager = schemaManager;
        upName = "";
        normName = "";
        normalized = true;
    }


    /**
     * Creates a new instance of Dn, using varargs to declare the RDNs. Each
     * String is either a full Rdn, or a couple of AttributeType DI and a value.
     * If the String contains a '=' symbol, the the constructor will assume that
     * the String arg contains afull Rdn, otherwise, it will consider that the
     * following arg is the value.<br/>
     * The created Dn is Schema aware.
     * <br/><br/>
     * An example of usage would be :
     * <pre>
     * String exampleName = "example";
     * String baseDn = "dc=apache,dc=org";
     *
     * Dn dn = new Dn( DefaultSchemaManager.INSTANCE,
     *     "cn=Test",
     *     "ou", exampleName,
     *     baseDn);
     * </pre>
     * @param schemaManager the schema manager
     * @param upRdns
     * @throws LdapInvalidDnException
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
     * following arg is the value.<br/>
     * The created Dn is Schema aware.
     * <br/><br/>
     * An example of usage would be :
     * <pre>
     * String exampleName = "example";
     * String baseDn = "dc=apache,dc=org";
     *
     * Dn dn = new Dn( DefaultSchemaManager.INSTANCE,
     *     "cn=Test",
     *     "ou", exampleName,
     *     baseDn);
     * </pre>
     * @param schemaManager the schema manager
     * @param upRdns
     * @throws LdapInvalidDnException
     */
    public Dn( SchemaManager schemaManager, String... upRdns ) throws LdapInvalidDnException
    {
        StringBuilder sb = new StringBuilder();
        boolean valueExpected = false;
        boolean isFirst = true;

        for ( String upRdn : upRdns )
        {
            if ( Strings.isEmpty( upRdn ) )
            {
                continue;
            }
            
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
        
        if ( !isFirst && valueExpected )
        {
            throw new LdapInvalidDnException( ResultCodeEnum.INVALID_DN_SYNTAX, I18n.err( I18n.ERR_04202 ) );
        }

        // Stores the representations of a Dn : internal (as a string and as a
        // byte[]) and external.
        upName = sb.toString();
        DnParser.parseInternal( upName, rdns );

        if ( schemaManager != null )
        {
            this.schemaManager = schemaManager;
            normalize( schemaManager );
        }
        else
        {
            normalized = false;
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
    Dn( SchemaManager schemaManager, String upName, String normName, Rdn... rdns )
    {
        this.schemaManager = schemaManager;
        normalized = schemaManager != null;
        this.upName = upName;
        this.normName = normName;
        bytes = Strings.getBytesUtf8( upName );
        this.rdns = Arrays.asList( rdns );
    }


    /**
     * Creates a Dn from a list of Rdns.
     *
     * @param rdns the list of Rdns to be used for the Dn
     */
    public Dn( Rdn... rdns )
    {
        if ( rdns == null )
        {
            return;
        }
        
        for ( Rdn rdn : rdns)
        {
            this.rdns.add( rdn.clone() );
        }

        normalizeInternal();
        toUpName();
        normalized = false;
    }


    /**
     * Creates a Dn concatenating a Rdn and a Dn.
     *
     * @param rdns the list of Rdns to be used for the Dn
     */
    public Dn( Rdn rdn, Dn dn ) throws LdapInvalidDnException
    {
        if ( ( dn == null ) || ( rdn == null ) )
        {
            throw new IllegalArgumentException( "Either the dn or the rdn is null" );
        }
        
        for ( Rdn rdnParent : dn )
        {
            rdns.add( rdnParent );
        }
        
        rdns.add( rdn );
        
        schemaManager = dn.schemaManager;

        if ( schemaManager != null )
        {
            normalize( schemaManager );
        }
        else
        {
            normalized = false;

            // Stores the representations of a Dn : internal (as a string and as a
            // byte[]) and external.
            normalizeInternal();
        }

        toUpName();
    }


    /**
     * Creates a Dn concatenating a Rdn and a Dn.
     *
     * @param rdns the list of Rdns to be used for the Dn
     */
    public Dn( SchemaManager schemaManager, Dn dn ) throws LdapInvalidDnException
    {
        if ( dn == null )
        {
            throw new IllegalArgumentException( "The dn is null" );
        }
        
        for ( Rdn rdnParent : dn )
        {
            rdns.add( rdnParent );
        }
        
        this.schemaManager = schemaManager;

        if ( schemaManager != null )
        {
            normalize( schemaManager );
        }
        else
        {
            normalized = false;

            // Stores the representations of a Dn : internal (as a string and as a
            // byte[]) and external.
            normalizeInternal();
        }

        toUpName();
    }


    /**
     * Creates a Schema aware Dn from a list of Rdns.
     *
     *  @param schemaManager The SchemaManager to use
     * @param rdns the list of Rdns to be used for the Dn
     */
    public Dn( SchemaManager schemaManager, Rdn... rdns )
    {
        if ( rdns == null )
        {
            return;
        }
        
        for ( Rdn rdn : rdns)
        {
            this.rdns.add( rdn.clone() );
        }

        try
        {
            normalized = false;
            
            if ( this.schemaManager != null )
            {
                normalize( schemaManager );
            }

            normalizeInternal();
            toUpName();
        }
        catch( LdapInvalidDnException lide )
        {
            throw new IllegalArgumentException( lide.getMessage() );
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
    public static Dn normalize( SchemaManager schemaManager, String name )
        throws LdapInvalidDnException
    {
        if ( ( name == null ) || ( name.length() == 0 ) || ( schemaManager == null ) )
        {
            return Dn.EMPTY_DN;
        }

        Dn newDn = new Dn( name );

        for ( Rdn rdn : newDn.rdns )
        {
            String upName = rdn.getName();
            rdnOidToName( rdn, schemaManager.getNormalizerMapping() );
            rdn.normalize();
            rdn.setUpName( upName );
        }

        newDn.normalizeInternal();
        newDn.normalized = true;

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
     * Get the associated SchemaManager if any.
     * 
     * @return The SchemaManager
     */
    public SchemaManager getSchemaManager()
    {
        return schemaManager;
    }
    
    
    /**
     * Tells if the Dn is schema aware.
     * 
     * @return <code>true</code> If the Dn has a schemaManager
     */
    public boolean hasSchemaManager()
    {
        return schemaManager != null;
    }


    /**
     * Return the user provided Dn as a String. It returns the same value as the
     * getName method
     *
     * @return A String representing the user provided Dn
     */
    @Override
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
    @Override
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
     * Get the user provided Dn
     *
     * @return The user provided Dn as a String
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
    /* No qualifier */ void setUpName( String upName )
    {
        this.upName = upName;
    }


    /**
     * Get the normalized Dn. If the Dn is schema aware, the AttributeType
     * will be represented using its OID :<br/>
     * <pre>
     * Dn dn = new Dn( schemaManager, "ou = Example , ou = com" );
     * assert( "2.5.4.11=example,2.5.4.11=com".equals( dn.getNormName ) );
     * </pre>
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
     * Get the number of RDNs present in the DN
     * @return The umber of RDNs in the DN
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
     * For instance, <b>dc=com</b> is a ancestor
     * of <b>dc=example, dc=com</b>
     *
     * @param dn The child
     * @return true if the current Dn is a parent of the given Dn
     */
    public boolean isAncestorOf( String dn )
    {
        try
        {
            return isAncestorOf( new Dn( dn ) );
        }
        catch ( LdapInvalidDnException lide )
        {
            return false;
        }
    }


    /**
     * Tells if the current Dn is a parent of another Dn.<br>
     * For instance, <b>dc=com</b> is a ancestor 
     * of <b>dc=example, dc=com</b>
     *
     * @param dn The child
     * @return true if the current Dn is a parent of the given Dn
     */
    public boolean isAncestorOf( Dn dn )
    {
        if ( dn == null )
        {
            return false;
        }

        return dn.isDescendantOf( this );
    }


    /**
     * Tells if a Dn is a child of another Dn.<br>
     * For instance, <b>dc=example, dc=com</b> is a descendant
     * of <b>dc=com</b>
     *
     * @param dn The parent
     * @return true if the current Dn is a child of the given Dn
     */
    public boolean isDescendantOf( String dn )
    {
        try
        {
            return isDescendantOf( new Dn( dn ) );
        }
        catch ( LdapInvalidDnException lide )
        {
            return false;
        }
    }


    /**
     * Tells if a Dn is a child of another Dn.<br>
     * For instance, <b>dc=example, dc=apache, dc=com</b> is a descendant
     * of <b>dc=com</b>
     *
     * @param dn The parent
     * @return true if the current Dn is a child of the given Dn
     */
    public boolean isDescendantOf( Dn dn )
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

            if ( !nameRdn.equals( ldapRdn ) )
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
     * Get the descendant of a given DN, using the ancestr DN. Assuming that
     * a DN has two parts :<br/>
     * DN = [descendant DN][ancestor DN]<br/>
     * To get back the descendant from the full DN, you just pass the ancestor DN
     * as a parameter. Here is a working example :
     * <pre>
     * Dn dn = new Dn( "cn=test, dc=server, dc=directory, dc=apache, dc=org" );
     * 
     * Dn descendant = dn.getDescendantOf( "dc=apache, dc=org" );
     * 
     * // At this point, the descendant contains cn=test, dc=server, dc=directory"
     * </pre> 
     */
    public Dn getDescendantOf( String ancestor ) throws LdapInvalidDnException
    {
        return getDescendantOf( new Dn( schemaManager, ancestor ) );
    }
    

    
    /**
     * Get the descendant of a given DN, using the ancestr DN. Assuming that
     * a DN has two parts :<br/>
     * DN = [descendant DN][ancestor DN]<br/>
     * To get back the descendant from the full DN, you just pass the ancestor DN
     * as a parameter. Here is a working example :
     * <pre>
     * Dn dn = new Dn( "cn=test, dc=server, dc=directory, dc=apache, dc=org" );
     * 
     * Dn descendant = dn.getDescendantOf( "dc=apache, dc=org" );
     * 
     * // At this point, the descendant contains cn=test, dc=server, dc=directory"
     * </pre> 
     */
    public Dn getDescendantOf( Dn ancestor ) throws LdapInvalidDnException
    {
        if ( ( ancestor == null ) || ( ancestor.size() == 0 ) )
        {
            return this;
        }
        
        if ( rdns.size() == 0 )
        {
            return EMPTY_DN;
        }
        
        int length = ancestor.size();
        
        if ( length > rdns.size() )
        {
            String message = I18n.err( I18n.ERR_04206, length, rdns.size() );
            LOG.error( message );
            throw new ArrayIndexOutOfBoundsException( message );
        }

        Dn newDn = new Dn( schemaManager );
        List<Rdn> rdnsAncestor = ancestor.getRdns();
        
        for ( int i = 0; i < ancestor.size(); i++ )
        {
            Rdn rdn = rdns.get( size() -1 - i );
            Rdn rdnDescendant = rdnsAncestor.get( ancestor.size() - 1 - i );
            
            if ( !rdn.equals( rdnDescendant ) )
            {
                throw new LdapInvalidDnException( ResultCodeEnum.INVALID_DN_SYNTAX );
            }
        }

        for ( int i = 0; i < rdns.size() - length; i++ )
        {
            // Don't forget to clone the rdns !
            newDn.rdns.add( rdns.get( i ).clone() );
        }

        newDn.toUpName();
        newDn.toNormName();
        newDn.normalized = normalized;

        return newDn;
    }

    /**
     * Get the ancestor of a given DN, using the descendant DN. Assuming that
     * a DN has two parts :<br/>
     * DN = [descendant DN][ancestor DN]<br/>
     * To get back the ancestor from the full DN, you just pass the descendant DN
     * as a parameter. Here is a working example :
     * <pre>
     * Dn dn = new Dn( "cn=test, dc=server, dc=directory, dc=apache, dc=org" );
     * 
     * Dn ancestor = dn.getAncestorOf( "cn=test, dc=server, dc=directory" );
     * 
     * // At this point, the ancestor contains "dc=apache, dc=org"
     * </pre> 
     */
    public Dn getAncestorOf( String descendant ) throws LdapInvalidDnException
    {
        return getAncestorOf( new Dn( schemaManager, descendant ) );
    }
    

    /**
     * Get the ancestor of a given DN, using the descendant DN. Assuming that
     * a DN has two parts :<br/>
     * DN = [descendant DN][ancestor DN]<br/>
     * To get back the ancestor from the full DN, you just pass the descendant DN
     * as a parameter. Here is a working example :
     * <pre>
     * Dn dn = new Dn( "cn=test, dc=server, dc=directory, dc=apache, dc=org" );
     * 
     * Dn ancestor = dn.getAncestorOf( new Dn( "cn=test, dc=server, dc=directory" ) );
     * 
     * // At this point, the ancestor contains "dc=apache, dc=org"
     * </pre> 
     */
    public Dn getAncestorOf( Dn descendant ) throws LdapInvalidDnException
    {
        if ( ( descendant == null ) || ( descendant.size() == 0 ) )
        {
            return this;
        }
        
        if ( rdns.size() == 0 )
        {
            return EMPTY_DN;
        }
        
        int length = descendant.size();
        
        if ( length > rdns.size() )
        {
            String message = I18n.err( I18n.ERR_04206, length, rdns.size() );
            LOG.error( message );
            throw new ArrayIndexOutOfBoundsException( message );
        }

        Dn newDn = new Dn( schemaManager );
        List<Rdn> rdnsDescendant = descendant.getRdns();
        
        for ( int i = 0; i < descendant.size(); i++ )
        {
            Rdn rdn = rdns.get( i );
            Rdn rdnDescendant = rdnsDescendant.get( i );
            
            if ( !rdn.equals( rdnDescendant ) )
            {
                throw new LdapInvalidDnException( ResultCodeEnum.INVALID_DN_SYNTAX );
            }
        }

        for ( int i = length; i < rdns.size(); i++ )
        {
            // Don't forget to clone the rdns !
            newDn.rdns.add( rdns.get( i ).clone() );
        }

        newDn.toUpName();
        newDn.toNormName();
        newDn.normalized = normalized;

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
            newDn.rdns.add( rdns.get( i ).clone() );
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

        Dn clonedDn = copy();

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
    public Dn addAll( Dn suffix )
    {
        Dn result = null;
        
        try
        {
            result = addAll( rdns.size(), suffix );
        }
        catch ( LdapInvalidDnException lie )
        {
            // Do nothing, 
        }

        return result;
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

        Dn clonedDn = copy();

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

                normalizeInternal();
            }
            else
            {
                clonedDn.normalizeInternal();
                clonedDn.normalized = false;
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

        Dn clonedDn = copy();

        // We have to parse the nameComponent which is given as an argument
        Rdn newRdn = new Rdn( schemaManager, comp );

        clonedDn.rdns.add( 0, newRdn );

        if ( schemaManager != null )
        {
            clonedDn.normalize( schemaManager );
        }
        else
        {
            clonedDn.normalizeInternal();
            clonedDn.normalized = false;
        }

        clonedDn.toUpName();
        clonedDn.toNormName();

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
        Dn clonedDn = copy();

        clonedDn.rdns.add( 0, newRdn.clone() );
        clonedDn.normalized = false;

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
                    clonedDn.normalized = false;
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
     * used only for deserialization.
     * 
     * {@inheritDoc}
     */
    /* No qualifier */ Dn addInternal( int posn, Rdn rdn )
    {
        if ( ( posn < 0 ) || ( posn > size() ) )
        {
            String message = I18n.err( I18n.ERR_04206, posn, rdns.size() );
            LOG.error( message );
            throw new ArrayIndexOutOfBoundsException( message );
        }

        // We have to parse the nameComponent which is given as an argument
        Rdn newRdn = rdn.clone();

        int realPos = size() - posn;
        rdns.add( realPos, newRdn );
        toUpName();

        return this;
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

        if ( rdns.size() == 0 )
        {
            return EMPTY_DN;
        }
        
        int posn = rdns.size() - 1;

        Dn newDn = new Dn( schemaManager );

        for ( int i = rdns.size() - posn; i < rdns.size(); i++ )
        {
            // Don't forget to clone the rdns !
            newDn.rdns.add( rdns.get( i ).clone() );
        }

        newDn.normName = newDn.toNormName();
        newDn.upName = getUpNamePrefix( posn );
        newDn.normalized = normalized;

        return newDn;
    }


    /**
     * {@inheritDoc}
     */
    //@Override
    private Dn copy()
    {
        Dn dn = new Dn( schemaManager );
        dn.normalized = normalized;
        dn.rdns = new ArrayList<Rdn>();

        for ( Rdn rdn : rdns )
        {
            dn.rdns.add( rdn.clone() );
        }

        return dn;
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * @return <code>true</code> if the two instances are equals
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof String )
        {
            return normName.equals( obj );
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
                if ( !name.rdns.get( i ).equals( rdns.get( i ) ) )
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
        // We have more than one ATAV for this Rdn. We will loop on all
        // ATAVs
        Rdn rdnCopy = rdn.clone();
        rdn.clear();

        for ( Ava val : rdnCopy )
        {
            Ava newAtav = atavOidToName( val, oidsMap );
            rdn.addAVA( null, newAtav );
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
            if ( normalized )
            {
                return this;
            }

            synchronized ( this )
            {
                if ( size() == 0 )
                {
                    normalized = true;
                    return this;
                }

                for ( Rdn rdn : rdns )
                {
                    rdn.normalize( schemaManager );
                }

                normalizeInternal();

                normalized = true;

                return this;
            }
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
        return normalized;
    }


    /**
     * Iterate over the inner Rdn. The Rdn are returned from 
     * the rightmost to the leftmost. For instance, the following code :<br/>
     * <pre>
     * Dn dn = new Dn( "sn=test, dc=apache, dc=org );
     * 
     * for ( Rdn rdn : dn )
     * {
     *     System.out.println( rdn.toString() );
     * }
     * </pre>
     * <br/>
     * will produce this output : <br/>
     * <pre>
     * dc=org
     * dc=apache
     * sn=test
     * </pre>
     * 
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
