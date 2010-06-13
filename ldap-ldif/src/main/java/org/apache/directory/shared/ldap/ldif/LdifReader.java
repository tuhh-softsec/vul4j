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


import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.directory.shared.asn1.primitives.OID;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.name.DnParser;
import org.apache.directory.shared.ldap.name.RDN;
import org.apache.directory.shared.ldap.util.Base64;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <pre>
 *  &lt;ldif-file&gt; ::= &quot;version:&quot; &lt;fill&gt; &lt;number&gt; &lt;seps&gt; &lt;dn-spec&gt; &lt;sep&gt; 
 *  &lt;ldif-content-change&gt;
 *  
 *  &lt;ldif-content-change&gt; ::= 
 *    &lt;number&gt; &lt;oid&gt; &lt;options-e&gt; &lt;value-spec&gt; &lt;sep&gt; 
 *    &lt;attrval-specs-e&gt; &lt;ldif-attrval-record-e&gt; | 
 *    &lt;alpha&gt; &lt;chars-e&gt; &lt;options-e&gt; &lt;value-spec&gt; &lt;sep&gt; 
 *    &lt;attrval-specs-e&gt; &lt;ldif-attrval-record-e&gt; | 
 *    &quot;control:&quot; &lt;fill&gt; &lt;number&gt; &lt;oid&gt; &lt;spaces-e&gt; 
 *    &lt;criticality&gt; &lt;value-spec-e&gt; &lt;sep&gt; &lt;controls-e&gt; 
 *        &quot;changetype:&quot; &lt;fill&gt; &lt;changerecord-type&gt; &lt;ldif-change-record-e&gt; |
 *    &quot;changetype:&quot; &lt;fill&gt; &lt;changerecord-type&gt; &lt;ldif-change-record-e&gt;
 *                              
 *  &lt;ldif-attrval-record-e&gt; ::= &lt;seps&gt; &lt;dn-spec&gt; &lt;sep&gt; &lt;attributeType&gt; 
 *    &lt;options-e&gt; &lt;value-spec&gt; &lt;sep&gt; &lt;attrval-specs-e&gt; 
 *    &lt;ldif-attrval-record-e&gt; | e
 *                              
 *  &lt;ldif-change-record-e&gt; ::= &lt;seps&gt; &lt;dn-spec&gt; &lt;sep&gt; &lt;controls-e&gt; 
 *    &quot;changetype:&quot; &lt;fill&gt; &lt;changerecord-type&gt; &lt;ldif-change-record-e&gt; | e
 *                              
 *  &lt;dn-spec&gt; ::= &quot;dn:&quot; &lt;fill&gt; &lt;safe-string&gt; | &quot;dn::&quot; &lt;fill&gt; &lt;base64-string&gt;
 *                              
 *  &lt;controls-e&gt; ::= &quot;control:&quot; &lt;fill&gt; &lt;number&gt; &lt;oid&gt; &lt;spaces-e&gt; &lt;criticality&gt; 
 *    &lt;value-spec-e&gt; &lt;sep&gt; &lt;controls-e&gt; | e
 *                              
 *  &lt;criticality&gt; ::= &quot;true&quot; | &quot;false&quot; | e
 *                              
 *  &lt;oid&gt; ::= '.' &lt;number&gt; &lt;oid&gt; | e
 *                              
 *  &lt;attrval-specs-e&gt; ::= &lt;number&gt; &lt;oid&gt; &lt;options-e&gt; &lt;value-spec&gt; 
 *  &lt;sep&gt; &lt;attrval-specs-e&gt; | 
 *    &lt;alpha&gt; &lt;chars-e&gt; &lt;options-e&gt; &lt;value-spec&gt; &lt;sep&gt; &lt;attrval-specs-e&gt; | e
 *                              
 *  &lt;value-spec-e&gt; ::= &lt;value-spec&gt; | e
 *  
 *  &lt;value-spec&gt; ::= ':' &lt;fill&gt; &lt;safe-string-e&gt; | 
 *    &quot;::&quot; &lt;fill&gt; &lt;base64-chars&gt; | 
 *    &quot;:&lt;&quot; &lt;fill&gt; &lt;url&gt;
 *  
 *  &lt;attributeType&gt; ::= &lt;number&gt; &lt;oid&gt; | &lt;alpha&gt; &lt;chars-e&gt;
 *  
 *  &lt;options-e&gt; ::= ';' &lt;char&gt; &lt;chars-e&gt; &lt;options-e&gt; |e
 *                              
 *  &lt;chars-e&gt; ::= &lt;char&gt; &lt;chars-e&gt; |  e
 *  
 *  &lt;changerecord-type&gt; ::= &quot;add&quot; &lt;sep&gt; &lt;attributeType&gt; 
 *  &lt;options-e&gt; &lt;value-spec&gt; &lt;sep&gt; &lt;attrval-specs-e&gt; | 
 *    &quot;delete&quot; &lt;sep&gt; | 
 *    &quot;modify&quot; &lt;sep&gt; &lt;mod-type&gt; &lt;fill&gt; &lt;attributeType&gt; 
 *    &lt;options-e&gt; &lt;sep&gt; &lt;attrval-specs-e&gt; &lt;sep&gt; '-' &lt;sep&gt; &lt;mod-specs-e&gt; | 
 *    &quot;moddn&quot; &lt;sep&gt; &lt;newrdn&gt; &lt;sep&gt; &quot;deleteoldrdn:&quot; 
 *    &lt;fill&gt; &lt;0-1&gt; &lt;sep&gt; &lt;newsuperior-e&gt; &lt;sep&gt; |
 *    &quot;modrdn&quot; &lt;sep&gt; &lt;newrdn&gt; &lt;sep&gt; &quot;deleteoldrdn:&quot; 
 *    &lt;fill&gt; &lt;0-1&gt; &lt;sep&gt; &lt;newsuperior-e&gt; &lt;sep&gt;
 *  
 *  &lt;newrdn&gt; ::= ':' &lt;fill&gt; &lt;safe-string&gt; | &quot;::&quot; &lt;fill&gt; &lt;base64-chars&gt;
 *  
 *  &lt;newsuperior-e&gt; ::= &quot;newsuperior&quot; &lt;newrdn&gt; | e
 *  
 *  &lt;mod-specs-e&gt; ::= &lt;mod-type&gt; &lt;fill&gt; &lt;attributeType&gt; &lt;options-e&gt; 
 *    &lt;sep&gt; &lt;attrval-specs-e&gt; &lt;sep&gt; '-' &lt;sep&gt; &lt;mod-specs-e&gt; | e
 *  
 *  &lt;mod-type&gt; ::= &quot;add:&quot; | &quot;delete:&quot; | &quot;replace:&quot;
 *  
 *  &lt;url&gt; ::= &lt;a Uniform Resource Locator, as defined in [6]&gt;
 *  
 *  
 *  
 *  LEXICAL
 *  -------
 *  
 *  &lt;fill&gt;           ::= ' ' &lt;fill&gt; | e
 *  &lt;char&gt;           ::= &lt;alpha&gt; | &lt;digit&gt; | '-'
 *  &lt;number&gt;         ::= &lt;digit&gt; &lt;digits&gt;
 *  &lt;0-1&gt;            ::= '0' | '1'
 *  &lt;digits&gt;         ::= &lt;digit&gt; &lt;digits&gt; | e
 *  &lt;digit&gt;          ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
 *  &lt;seps&gt;           ::= &lt;sep&gt; &lt;seps-e&gt; 
 *  &lt;seps-e&gt;         ::= &lt;sep&gt; &lt;seps-e&gt; | e
 *  &lt;sep&gt;            ::= 0x0D 0x0A | 0x0A
 *  &lt;spaces&gt;         ::= ' ' &lt;spaces-e&gt;
 *  &lt;spaces-e&gt;       ::= ' ' &lt;spaces-e&gt; | e
 *  &lt;safe-string-e&gt;  ::= &lt;safe-string&gt; | e
 *  &lt;safe-string&gt;    ::= &lt;safe-init-char&gt; &lt;safe-chars&gt;
 *  &lt;safe-init-char&gt; ::= [0x01-0x09] | 0x0B | 0x0C | [0x0E-0x1F] | [0x21-0x39] | 0x3B | [0x3D-0x7F]
 *  &lt;safe-chars&gt;     ::= &lt;safe-char&gt; &lt;safe-chars&gt; | e
 *  &lt;safe-char&gt;      ::= [0x01-0x09] | 0x0B | 0x0C | [0x0E-0x7F]
 *  &lt;base64-string&gt;  ::= &lt;base64-char&gt; &lt;base64-chars&gt;
 *  &lt;base64-chars&gt;   ::= &lt;base64-char&gt; &lt;base64-chars&gt; | e
 *  &lt;base64-char&gt;    ::= 0x2B | 0x2F | [0x30-0x39] | 0x3D | [0x41-9x5A] | [0x61-0x7A]
 *  &lt;alpha&gt;          ::= [0x41-0x5A] | [0x61-0x7A]
 *  
 *  COMMENTS
 *  --------
 *  - The ldap-oid VN is not correct in the RFC-2849. It has been changed from 1*DIGIT 0*1(&quot;.&quot; 1*DIGIT) to
 *  DIGIT+ (&quot;.&quot; DIGIT+)*
 *  - The mod-spec lacks a sep between *attrval-spec and &quot;-&quot;.
 *  - The BASE64-UTF8-STRING should be BASE64-CHAR BASE64-STRING
 *  - The ValueSpec rule must accept multilines values. In this case, we have a LF followed by a 
 *  single space before the continued value.
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifReader implements Iterable<LdifEntry>, Closeable
{
    /** A logger */
    private static final Logger LOG = LoggerFactory.getLogger( LdifReader.class );

    /** 
     * A private class to track the current position in a line 
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public class Position
    {
        /** The current position */
        private int pos;


        /**
         * Creates a new instance of Position.
         */
        public Position()
        {
            pos = 0;
        }


        /**
         * Increment the current position by one
         *
         */
        public void inc()
        {
            pos++;
        }


        /**
         * Increment the current position by the given value
         *
         * @param val The value to add to the current position
         */
        public void inc( int val )
        {
            pos += val;
        }
    }

    /** A list of read lines */
    protected List<String> lines;

    /** The current position */
    protected Position position;

    /** The ldif file version default value */
    protected static final int DEFAULT_VERSION = 1;

    /** The ldif version */
    protected int version;

    /** Type of element read */
    protected static final int LDIF_ENTRY = 0;

    protected static final int CHANGE = 1;

    protected static final int UNKNOWN = 2;

    /** Size limit for file contained values */
    protected long sizeLimit = SIZE_LIMIT_DEFAULT;

    /** The default size limit : 1Mo */
    protected static final long SIZE_LIMIT_DEFAULT = 1024000;

    /** State values for the modify operation */
    protected static final int MOD_SPEC = 0;

    protected static final int ATTRVAL_SPEC = 1;

    protected static final int ATTRVAL_SPEC_OR_SEP = 2;

    /** Iterator prefetched entry */
    protected LdifEntry prefetched;

    /** The ldif Reader */
    protected Reader reader;

    /** A flag set if the ldif contains entries */
    protected boolean containsEntries;

    /** A flag set if the ldif contains changes */
    protected boolean containsChanges;

    /**
     * An Exception to handle error message, has Iterator.next() can't throw
     * exceptions
     */
    protected Exception error;


    /**
     * Constructors
     */
    public LdifReader()
    {
        lines = new ArrayList<String>();
        position = new Position();
        version = DEFAULT_VERSION;
    }


    private void init( BufferedReader reader ) throws LdapLdifException, LdapException
    {
        this.reader = reader;
        lines = new ArrayList<String>();
        position = new Position();
        version = DEFAULT_VERSION;
        containsChanges = false;
        containsEntries = false;

        // First get the version - if any -
        version = parseVersion();
        prefetched = parseEntry();
    }


    /**
     * A constructor which takes a file name
     * 
     * @param ldifFileName A file name containing ldif formated input
     * @throws LdapLdifException
     *             If the file cannot be processed or if the format is incorrect
     */
    public LdifReader( String ldifFileName ) throws LdapLdifException
    {
        File file = new File( ldifFileName );

        if ( !file.exists() )
        {
            String msg = I18n.err( I18n.ERR_12010, file.getAbsoluteFile() );
            LOG.error( msg );
            throw new LdapLdifException( msg );
        }

        if ( !file.canRead() )
        {
            String msg = I18n.err( I18n.ERR_12011, file.getName() );
            LOG.error( msg );
            throw new LdapLdifException( msg );
        }

        try
        {
            init( new BufferedReader( new FileReader( file ) ) );
        }
        catch ( FileNotFoundException fnfe )
        {
            String msg = I18n.err( I18n.ERR_12010, file.getAbsoluteFile() );
            LOG.error( msg );
            throw new LdapLdifException( msg );
        }
        catch ( LdapInvalidDnException lide )
        {
            throw new LdapLdifException( lide.getMessage() );
        }
        catch ( LdapException le )
        {
            throw new LdapLdifException( le.getMessage() );
        }
    }


    /**
     * A constructor which takes a Reader
     * 
     * @param in
     *            A Reader containing ldif formated input
     * @throws LdapLdifException
     *             If the file cannot be processed or if the format is incorrect
     * @throws LdapException 
     */
    public LdifReader( Reader in ) throws LdapLdifException, LdapException
    {
        init( new BufferedReader( in ) );
    }


    /**
     * A constructor which takes an InputStream
     * 
     * @param in
     *            An InputStream containing ldif formated input
     * @throws LdapLdifException
     *             If the file cannot be processed or if the format is incorrect
     * @throws LdapException 
     */
    public LdifReader( InputStream in ) throws LdapLdifException, LdapException
    {
        init( new BufferedReader( new InputStreamReader( in ) ) );
    }


    /**
     * A constructor which takes a File
     * 
     * @param file
     *            A File containing ldif formated input
     * @throws LdapLdifException
     *             If the file cannot be processed or if the format is incorrect
     */
    public LdifReader( File file ) throws LdapLdifException
    {
        if ( !file.exists() )
        {
            String msg = I18n.err( I18n.ERR_12010, file.getAbsoluteFile() );
            LOG.error( msg );
            throw new LdapLdifException( msg );
        }

        if ( !file.canRead() )
        {
            String msg = I18n.err( I18n.ERR_12011, file.getName() );
            LOG.error( msg );
            throw new LdapLdifException( msg );
        }

        try
        {
            init( new BufferedReader( new FileReader( file ) ) );
        }
        catch ( FileNotFoundException fnfe )
        {
            String msg = I18n.err( I18n.ERR_12010, file.getAbsoluteFile() );
            LOG.error( msg );
            throw new LdapLdifException( msg );
        }
        catch ( LdapInvalidDnException lide )
        {
            throw new LdapLdifException( lide.getMessage() );
        }
        catch ( LdapException le )
        {
            throw new LdapLdifException( le.getMessage() );
        }
    }


    /**
     * @return The ldif file version
     */
    public int getVersion()
    {
        return version;
    }


    /**
     * @return The maximum size of a file which is used into an attribute value.
     */
    public long getSizeLimit()
    {
        return sizeLimit;
    }


    /**
     * Set the maximum file size that can be accepted for an attribute value
     * 
     * @param sizeLimit
     *            The size in bytes
     */
    public void setSizeLimit( long sizeLimit )
    {
        this.sizeLimit = sizeLimit;
    }


    // <fill> ::= ' ' <fill> | ���������
    private static void parseFill( char[] document, Position position )
    {

        while ( StringTools.isCharASCII( document, position.pos, ' ' ) )
        {
            position.inc();
        }
    }


    /**
     * Parse a number following the rules :
     * 
     * &lt;number&gt; ::= &lt;digit&gt; &lt;digits&gt; &lt;digits&gt; ::= &lt;digit&gt; &lt;digits&gt; | e &lt;digit&gt;
     * ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
     * 
     * Check that the number is in the interval
     * 
     * @param document The document containing the number to parse
     * @param position The current position in the document
     * @return a String representing the parsed number
     */
    private static String parseNumber( char[] document, Position position )
    {
        int initPos = position.pos;

        while ( true )
        {
            if ( StringTools.isDigit( document, position.pos ) )
            {
                position.inc();
            }
            else
            {
                break;
            }
        }

        if ( position.pos == initPos )
        {
            return null;
        }
        else
        {
            return new String( document, initPos, position.pos - initPos );
        }
    }


    /**
     * Parse the changeType
     * 
     * @param line
     *            The line which contains the changeType
     * @return The operation.
     */
    private ChangeType parseChangeType( String line )
    {
        ChangeType operation = ChangeType.Add;

        String modOp = StringTools.trim( line.substring( "changetype:".length() + 1 ) );

        if ( "add".equalsIgnoreCase( modOp ) )
        {
            operation = ChangeType.Add;
        }
        else if ( "delete".equalsIgnoreCase( modOp ) )
        {
            operation = ChangeType.Delete;
        }
        else if ( "modify".equalsIgnoreCase( modOp ) )
        {
            operation = ChangeType.Modify;
        }
        else if ( "moddn".equalsIgnoreCase( modOp ) )
        {
            operation = ChangeType.ModDn;
        }
        else if ( "modrdn".equalsIgnoreCase( modOp ) )
        {
            operation = ChangeType.ModRdn;
        }

        return operation;
    }


    /**
     * Parse the DN of an entry
     * 
     * @param line
     *            The line to parse
     * @return A DN
     * @throws LdapLdifException
     *             If the DN is invalid
     */
    private String parseDn( String line ) throws LdapLdifException
    {
        String dn = null;

        String lowerLine = line.toLowerCase();

        if ( lowerLine.startsWith( "dn:" ) || lowerLine.startsWith( "DN:" ) )
        {
            // Ok, we have a DN. Is it base 64 encoded ?
            int length = line.length();

            if ( length == 3 )
            {
                // The DN is empty : error
                LOG.error( I18n.err( I18n.ERR_12012 ) );
                throw new LdapLdifException( I18n.err( I18n.ERR_12013 ) );
            }
            else if ( line.charAt( 3 ) == ':' )
            {
                if ( length > 4 )
                {
                    // This is a base 64 encoded DN.
                    String trimmedLine = line.substring( 4 ).trim();

                    try
                    {
                        dn = new String( Base64.decode( trimmedLine.toCharArray() ), "UTF-8" );
                    }
                    catch ( UnsupportedEncodingException uee )
                    {
                        // The DN is not base 64 encoded
                        LOG.error( I18n.err( I18n.ERR_12014 ) );
                        throw new LdapLdifException( I18n.err( I18n.ERR_12015 ) );
                    }
                }
                else
                {
                    // The DN is empty : error
                    LOG.error( I18n.err( I18n.ERR_12012 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12013 ) );
                }
            }
            else
            {
                dn = line.substring( 3 ).trim();
            }
        }
        else
        {
            LOG.error( I18n.err( I18n.ERR_12016 ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12013 ) );
        }

        // Check that the DN is valid. If not, an exception will be thrown
        try
        {
            DnParser.parseInternal( dn, new ArrayList<RDN>() );
        }
        catch ( LdapInvalidDnException ine )
        {
            LOG.error( I18n.err( I18n.ERR_12017, dn ) );
            throw new LdapLdifException( ine.getMessage() );
        }

        return dn;
    }


    /**
     * Parse the value part.
     * 
     * @param line
     *            The line which contains the value
     * @param pos
     *            The starting position in the line
     * @return A String or a byte[], depending of the kind of value we get
     */
    protected static Object parseSimpleValue( String line, int pos )
    {
        if ( line.length() > pos + 1 )
        {
            char c = line.charAt( pos + 1 );

            if ( c == ':' )
            {
                String value = StringTools.trim( line.substring( pos + 2 ) );

                return Base64.decode( value.toCharArray() );
            }
            else
            {
                return StringTools.trim( line.substring( pos + 1 ) );
            }
        }
        else
        {
            return null;
        }
    }


    /**
     * Parse the value part.
     * 
     * @param line The line which contains the value
     * @param pos The starting position in the line
     * @return A String or a byte[], depending of the kind of value we get
     * @throws LdapLdifException
     *             If something went wrong
     */
    protected Object parseValue( String line, int pos ) throws LdapLdifException
    {
        if ( line.length() > pos + 1 )
        {
            char c = line.charAt( pos + 1 );

            if ( c == ':' )
            {
                String value = StringTools.trim( line.substring( pos + 2 ) );

                return Base64.decode( value.toCharArray() );
            }
            else if ( c == '<' )
            {
                String urlName = StringTools.trim( line.substring( pos + 2 ) );

                try
                {
                    URL url = new URL( urlName );

                    if ( "file".equals( url.getProtocol() ) )
                    {
                        String fileName = url.getFile();

                        File file = new File( fileName );

                        if ( !file.exists() )
                        {
                            LOG.error( I18n.err( I18n.ERR_12018, fileName ) );
                            throw new LdapLdifException( I18n.err( I18n.ERR_12019 ) );
                        }
                        else
                        {
                            long length = file.length();

                            if ( length > sizeLimit )
                            {
                                LOG.error( I18n.err( I18n.ERR_12020, fileName ) );
                                throw new LdapLdifException( I18n.err( I18n.ERR_12021 ) );
                            }
                            else
                            {
                                byte[] data = new byte[( int ) length];
                                DataInputStream inf = null;

                                try
                                {
                                    inf = new DataInputStream( new FileInputStream( file ) );
                                    inf.read( data );

                                    return data;
                                }
                                catch ( FileNotFoundException fnfe )
                                {
                                    // We can't reach this point, the file
                                    // existence has already been
                                    // checked
                                    LOG.error( I18n.err( I18n.ERR_12018, fileName ) );
                                    throw new LdapLdifException( I18n.err( I18n.ERR_12019 ) );
                                }
                                catch ( IOException ioe )
                                {
                                    LOG.error( I18n.err( I18n.ERR_12022, fileName ) );
                                    throw new LdapLdifException( I18n.err( I18n.ERR_12023 ) );
                                }
                                finally
                                {
                                    try
                                    {
                                        inf.close();
                                    }
                                    catch ( IOException ioe )
                                    {
                                        LOG.error( I18n.err( I18n.ERR_12024, ioe.getMessage() ) );
                                        // Just do nothing ...
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        LOG.error( I18n.err( I18n.ERR_12025 ) );
                        throw new LdapLdifException( I18n.err( I18n.ERR_12026 ) );
                    }
                }
                catch ( MalformedURLException mue )
                {
                    LOG.error( I18n.err( I18n.ERR_12027, urlName ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12028 ) );
                }
            }
            else
            {
                return StringTools.trim( line.substring( pos + 1 ) );
            }
        }
        else
        {
            return null;
        }
    }


    /**
     * Parse a control. The grammar is : &lt;control&gt; ::= "control:" &lt;fill&gt;
     * &lt;ldap-oid&gt; &lt;critical-e&gt; &lt;value-spec-e&gt; &lt;sep&gt; &lt;critical-e&gt; ::= &lt;spaces&gt;
     * &lt;boolean&gt; | e &lt;boolean&gt; ::= "true" | "false" &lt;value-spec-e&gt; ::=
     * &lt;value-spec&gt; | e &lt;value-spec&gt; ::= ":" &lt;fill&gt; &lt;SAFE-STRING-e&gt; | "::"
     * &lt;fill&gt; &lt;BASE64-STRING&gt; | ":<" &lt;fill&gt; &lt;url&gt;
     * 
     * It can be read as : "control:" &lt;fill&gt; &lt;ldap-oid&gt; [ " "+ ( "true" |
     * "false") ] [ ":" &lt;fill&gt; &lt;SAFE-STRING-e&gt; | "::" &lt;fill&gt; &lt;BASE64-STRING&gt; | ":<"
     * &lt;fill&gt; &lt;url&gt; ]
     * 
     * @param line The line containing the control
     * @return A control
     * @exception LdapLdifException If the control has no OID or if the OID is incorrect,
     * of if the criticality is not set when it's mandatory.
     */
    private Control parseControl( String line ) throws LdapLdifException
    {
        String lowerLine = line.toLowerCase().trim();
        char[] controlValue = line.trim().toCharArray();
        int pos = 0;
        int length = controlValue.length;

        // Get the <ldap-oid>
        if ( pos > length )
        {
            // No OID : error !
            LOG.error( I18n.err( I18n.ERR_12029 ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12030 ) );
        }

        int initPos = pos;

        while ( StringTools.isCharASCII( controlValue, pos, '.' ) || StringTools.isDigit( controlValue, pos ) )
        {
            pos++;
        }

        if ( pos == initPos )
        {
            // Not a valid OID !
            LOG.error( I18n.err( I18n.ERR_12029 ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12030 ) );
        }

        // Create and check the OID
        String oidString = lowerLine.substring( 0, pos );

        if ( !OID.isOID( oidString ) )
        {
            LOG.error( I18n.err( I18n.ERR_12031, oidString ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12032 ) );
        }

        LdifControl control = new LdifControl( oidString );

        // Get the criticality, if any
        // Skip the <fill>
        while ( StringTools.isCharASCII( controlValue, pos, ' ' ) )
        {
            pos++;
        }

        // Check if we have a "true" or a "false"
        int criticalPos = lowerLine.indexOf( ':' );

        int criticalLength = 0;

        if ( criticalPos == -1 )
        {
            criticalLength = length - pos;
        }
        else
        {
            criticalLength = criticalPos - pos;
        }

        if ( ( criticalLength == 4 ) && ( "true".equalsIgnoreCase( lowerLine.substring( pos, pos + 4 ) ) ) )
        {
            control.setCritical( true );
        }
        else if ( ( criticalLength == 5 ) && ( "false".equalsIgnoreCase( lowerLine.substring( pos, pos + 5 ) ) ) )
        {
            control.setCritical( false );
        }
        else if ( criticalLength != 0 )
        {
            // If we have a criticality, it should be either "true" or "false",
            // nothing else
            LOG.error( I18n.err( I18n.ERR_12033 ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12034 ) );
        }

        if ( criticalPos > 0 )
        {
            // We have a value. It can be a normal value, a base64 encoded value
            // or a file contained value
            if ( StringTools.isCharASCII( controlValue, criticalPos + 1, ':' ) )
            {
                // Base 64 encoded value
                byte[] value = Base64.decode( line.substring( criticalPos + 2 ).toCharArray() );
                control.setValue( value );
            }
            else if ( StringTools.isCharASCII( controlValue, criticalPos + 1, '<' ) )
            {
                // File contained value
                // TODO : Add the missing code
            }
            else
            {
                // Standard value
                byte[] value = new byte[length - criticalPos - 1];

                for ( int i = 0; i < length - criticalPos - 1; i++ )
                {
                    value[i] = ( byte ) controlValue[i + criticalPos + 1];
                }

                control.setValue( value );
            }
        }

        return control;
    }


    /**
     * Parse an AttributeType/AttributeValue
     * 
     * @param line The line to parse
     * @return the parsed Attribute
     */
    public static EntryAttribute parseAttributeValue( String line )
    {
        int colonIndex = line.indexOf( ':' );

        if ( colonIndex != -1 )
        {
            String attributeType = line.toLowerCase().substring( 0, colonIndex );
            Object attributeValue = parseSimpleValue( line, colonIndex );

            // Create an attribute
            if ( attributeValue instanceof String )
            {
                return new DefaultEntryAttribute( attributeType, (String)attributeValue );
            }
            else
            {
                return new DefaultEntryAttribute( attributeType, (byte[])attributeValue );
            }
        }
        else
        {
            return null;
        }
    }


    /**
     * Parse an AttributeType/AttributeValue
     * 
     * @param entry The entry where to store the value
     * @param line The line to parse
     * @param lowerLine The same line, lowercased
     * @throws LdapLdifException If anything goes wrong
     */
    public void parseAttributeValue( LdifEntry entry, String line, String lowerLine ) throws LdapLdifException, LdapException
    {
        int colonIndex = line.indexOf( ':' );

        String attributeType = lowerLine.substring( 0, colonIndex );

        // We should *not* have a DN twice
        if ( attributeType.equals( "dn" ) )
        {
            LOG.error( I18n.err( I18n.ERR_12002 ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12003 ) );
        }

        Object attributeValue = parseValue( line, colonIndex );

        // Update the entry
        entry.addAttribute( attributeType, attributeValue );
    }


    /**
     * Parse a ModRDN operation
     * 
     * @param entry
     *            The entry to update
     * @param iter
     *            The lines iterator
     * @throws LdapLdifException
     *             If anything goes wrong
     */
    private void parseModRdn( LdifEntry entry, Iterator<String> iter ) throws LdapLdifException
    {
        // We must have two lines : one starting with "newrdn:" or "newrdn::",
        // and the second starting with "deleteoldrdn:"
        if ( iter.hasNext() )
        {
            String line = iter.next();
            String lowerLine = line.toLowerCase();

            if ( lowerLine.startsWith( "newrdn::" ) || lowerLine.startsWith( "newrdn:" ) )
            {
                int colonIndex = line.indexOf( ':' );
                Object attributeValue = parseValue( line, colonIndex );
                entry.setNewRdn( attributeValue instanceof String ? ( String ) attributeValue : StringTools
                    .utf8ToString( ( byte[] ) attributeValue ) );
            }
            else
            {
                LOG.error( I18n.err( I18n.ERR_12035 ) );
                throw new LdapLdifException( I18n.err( I18n.ERR_12036 ) );
            }

        }
        else
        {
            LOG.error( I18n.err( I18n.ERR_12035 ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12037 ) );
        }

        if ( iter.hasNext() )
        {
            String line = iter.next();
            String lowerLine = line.toLowerCase();

            if ( lowerLine.startsWith( "deleteoldrdn:" ) )
            {
                int colonIndex = line.indexOf( ':' );
                Object attributeValue = parseValue( line, colonIndex );
                entry.setDeleteOldRdn( "1".equals( attributeValue ) );
            }
            else
            {
                LOG.error( I18n.err( I18n.ERR_12038 ) );
                throw new LdapLdifException( I18n.err( I18n.ERR_12039 ) );
            }
        }
        else
        {
            LOG.error( I18n.err( I18n.ERR_12038 ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12039 ) );
        }
    }


    /**
     * Parse a modify change type.
     * 
     * The grammar is : &lt;changerecord&gt; ::= "changetype:" FILL "modify" SEP
     * &lt;mod-spec&gt; &lt;mod-specs-e&gt; &lt;mod-spec&gt; ::= "add:" &lt;mod-val&gt; | "delete:"
     * &lt;mod-val-del&gt; | "replace:" &lt;mod-val&gt; &lt;mod-specs-e&gt; ::= &lt;mod-spec&gt;
     * &lt;mod-specs-e&gt; | e &lt;mod-val&gt; ::= FILL ATTRIBUTE-DESCRIPTION SEP
     * ATTRVAL-SPEC &lt;attrval-specs-e&gt; "-" SEP &lt;mod-val-del&gt; ::= FILL
     * ATTRIBUTE-DESCRIPTION SEP &lt;attrval-specs-e&gt; "-" SEP &lt;attrval-specs-e&gt; ::=
     * ATTRVAL-SPEC &lt;attrval-specs&gt; | e *
     * 
     * @param entry The entry to feed
     * @param iter The lines
     * @exception LdapLdifException If the modify operation is invalid
     */
    private void parseModify( LdifEntry entry, Iterator<String> iter ) throws LdapLdifException
    {
        int state = MOD_SPEC;
        String modified = null;
        ModificationOperation modificationType = ModificationOperation.ADD_ATTRIBUTE;
        EntryAttribute attribute = null;

        // The following flag is used to deal with empty modifications
        boolean isEmptyValue = true;

        while ( iter.hasNext() )
        {
            String line = iter.next();
            String lowerLine = line.toLowerCase();

            if ( lowerLine.startsWith( "-" ) )
            {
                if ( state != ATTRVAL_SPEC_OR_SEP )
                {
                    LOG.error( I18n.err( I18n.ERR_12040 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12041 ) );
                }
                else
                {
                    if ( isEmptyValue )
                    {
                        // Update the entry
                        entry.addModificationItem( modificationType, modified, null );
                    }
                    else
                    {
                        // Update the entry with the attribute
                        entry.addModificationItem( modificationType, attribute );
                    }

                    state = MOD_SPEC;
                    isEmptyValue = true;
                    continue;
                }
            }
            else if ( lowerLine.startsWith( "add:" ) )
            {
                if ( ( state != MOD_SPEC ) && ( state != ATTRVAL_SPEC ) )
                {
                    LOG.error( I18n.err( I18n.ERR_12042 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12043 ) );
                }

                modified = StringTools.trim( line.substring( "add:".length() ) );
                modificationType = ModificationOperation.ADD_ATTRIBUTE;
                attribute = new DefaultEntryAttribute( modified );

                state = ATTRVAL_SPEC;
            }
            else if ( lowerLine.startsWith( "delete:" ) )
            {
                if ( ( state != MOD_SPEC ) && ( state != ATTRVAL_SPEC ) )
                {
                    LOG.error( I18n.err( I18n.ERR_12042 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12043 ) );
                }

                modified = StringTools.trim( line.substring( "delete:".length() ) );
                modificationType = ModificationOperation.REMOVE_ATTRIBUTE;
                attribute = new DefaultEntryAttribute( modified );

                state = ATTRVAL_SPEC_OR_SEP;
            }
            else if ( lowerLine.startsWith( "replace:" ) )
            {
                if ( ( state != MOD_SPEC ) && ( state != ATTRVAL_SPEC ) )
                {
                    LOG.error( I18n.err( I18n.ERR_12042 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12043 ) );
                }

                modified = StringTools.trim( line.substring( "replace:".length() ) );
                modificationType = ModificationOperation.REPLACE_ATTRIBUTE;
                attribute = new DefaultEntryAttribute( modified );

                state = ATTRVAL_SPEC_OR_SEP;
            }
            else
            {
                if ( ( state != ATTRVAL_SPEC ) && ( state != ATTRVAL_SPEC_OR_SEP ) )
                {
                    LOG.error( I18n.err( I18n.ERR_12040 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12043 ) );
                }

                // A standard AttributeType/AttributeValue pair
                int colonIndex = line.indexOf( ':' );

                String attributeType = line.substring( 0, colonIndex );

                if ( !attributeType.equalsIgnoreCase( modified ) )
                {
                    LOG.error( I18n.err( I18n.ERR_12044 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12045 ) );
                }

                // We should *not* have a DN twice
                if ( attributeType.equalsIgnoreCase( "dn" ) )
                {
                    LOG.error( I18n.err( I18n.ERR_12002 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12003 ) );
                }

                Object attributeValue = parseValue( line, colonIndex );

                if ( attributeValue instanceof String )
                {
                    attribute.add( ( String ) attributeValue );
                }
                else
                {
                    attribute.add( ( byte[] ) attributeValue );
                }

                isEmptyValue = false;

                state = ATTRVAL_SPEC_OR_SEP;
            }
        }
    }


    /**
     * Parse a change operation. We have to handle different cases depending on
     * the operation. 1) Delete : there should *not* be any line after the
     * "changetype: delete" 2) Add : we must have a list of AttributeType :
     * AttributeValue elements 3) ModDN : we must have two following lines: a
     * "newrdn:" and a "deleteoldrdn:" 4) ModRDN : the very same, but a
     * "newsuperior:" line is expected 5) Modify :
     * 
     * The grammar is : &lt;changerecord&gt; ::= "changetype:" FILL "add" SEP
     * &lt;attrval-spec&gt; &lt;attrval-specs-e&gt; | "changetype:" FILL "delete" |
     * "changetype:" FILL "modrdn" SEP &lt;newrdn&gt; SEP &lt;deleteoldrdn&gt; SEP | // To
     * be checked "changetype:" FILL "moddn" SEP &lt;newrdn&gt; SEP &lt;deleteoldrdn&gt; SEP
     * &lt;newsuperior&gt; SEP | "changetype:" FILL "modify" SEP &lt;mod-spec&gt;
     * &lt;mod-specs-e&gt; &lt;newrdn&gt; ::= "newrdn:" FILL RDN | "newrdn::" FILL
     * BASE64-RDN &lt;deleteoldrdn&gt; ::= "deleteoldrdn:" FILL "0" | "deleteoldrdn:"
     * FILL "1" &lt;newsuperior&gt; ::= "newsuperior:" FILL DN | "newsuperior::" FILL
     * BASE64-DN &lt;mod-specs-e&gt; ::= &lt;mod-spec&gt; &lt;mod-specs-e&gt; | e &lt;mod-spec&gt; ::=
     * "add:" &lt;mod-val&gt; | "delete:" &lt;mod-val&gt; | "replace:" &lt;mod-val&gt; &lt;mod-val&gt;
     * ::= FILL ATTRIBUTE-DESCRIPTION SEP ATTRVAL-SPEC &lt;attrval-specs-e&gt; "-" SEP
     * &lt;attrval-specs-e&gt; ::= ATTRVAL-SPEC &lt;attrval-specs&gt; | e
     * 
     * @param entry The entry to feed
     * @param iter The lines iterator
     * @param operation The change operation (add, modify, delete, moddn or modrdn)
     * @exception LdapLdifException If the change operation is invalid
     */
    private void parseChange( LdifEntry entry, Iterator<String> iter, ChangeType operation ) throws LdapLdifException, LdapException
    {
        // The changetype and operation has already been parsed.
        entry.setChangeType( operation );

        switch ( operation.getChangeType() )
        {
            case ChangeType.DELETE_ORDINAL:
                // The change type will tell that it's a delete operation,
                // the dn is used as a key.
                return;

            case ChangeType.ADD_ORDINAL:
                // We will iterate through all attribute/value pairs
                while ( iter.hasNext() )
                {
                    String line = iter.next();
                    String lowerLine = line.toLowerCase();
                    parseAttributeValue( entry, line, lowerLine );
                }

                return;

            case ChangeType.MODIFY_ORDINAL:
                parseModify( entry, iter );
                return;

            case ChangeType.MODRDN_ORDINAL:// They are supposed to have the same syntax ???
            case ChangeType.MODDN_ORDINAL:
                // First, parse the modrdn part
                parseModRdn( entry, iter );

                // The next line should be the new superior
                if ( iter.hasNext() )
                {
                    String line = iter.next();
                    String lowerLine = line.toLowerCase();

                    if ( lowerLine.startsWith( "newsuperior:" ) )
                    {
                        int colonIndex = line.indexOf( ':' );
                        Object attributeValue = parseValue( line, colonIndex );
                        entry.setNewSuperior( attributeValue instanceof String ? ( String ) attributeValue
                            : StringTools.utf8ToString( ( byte[] ) attributeValue ) );
                    }
                    else
                    {
                        if ( operation == ChangeType.ModDn )
                        {
                            LOG.error( I18n.err( I18n.ERR_12046 ) );
                            throw new LdapLdifException( I18n.err( I18n.ERR_12047 ) );
                        }
                    }
                }
                else
                {
                    if ( operation == ChangeType.ModDn )
                    {
                        LOG.error( I18n.err( I18n.ERR_12046 ) );
                        throw new LdapLdifException( I18n.err( I18n.ERR_12047 ) );
                    }
                }

                return;

            default:
                // This is an error
                LOG.error( I18n.err( I18n.ERR_12048 ) );
                throw new LdapLdifException( I18n.err( I18n.ERR_12049 ) );
        }
    }


    /**
     * Parse a ldif file. The following rules are processed :
     * 
     * &lt;ldif-file&gt; ::= &lt;ldif-attrval-record&gt; &lt;ldif-attrval-records&gt; |
     * &lt;ldif-change-record&gt; &lt;ldif-change-records&gt; &lt;ldif-attrval-record&gt; ::=
     * &lt;dn-spec&gt; &lt;sep&gt; &lt;attrval-spec&gt; &lt;attrval-specs&gt; &lt;ldif-change-record&gt; ::=
     * &lt;dn-spec&gt; &lt;sep&gt; &lt;controls-e&gt; &lt;changerecord&gt; &lt;dn-spec&gt; ::= "dn:" &lt;fill&gt;
     * &lt;distinguishedName&gt; | "dn::" &lt;fill&gt; &lt;base64-distinguishedName&gt;
     * &lt;changerecord&gt; ::= "changetype:" &lt;fill&gt; &lt;change-op&gt;
     * 
     * @return the parsed ldifEntry
     * @exception LdapLdifException If the ldif file does not contain a valid entry 
     * @throws LdapException 
     */
    private LdifEntry parseEntry() throws LdapLdifException, LdapException
    {
        if ( ( lines == null ) || ( lines.size() == 0 ) )
        {
            LOG.debug( "The entry is empty : end of ldif file" );
            return null;
        }

        // The entry must start with a dn: or a dn::
        String line = lines.get( 0 );

        String name = parseDn( line );

        DN dn = new DN( name );

        // Ok, we have found a DN
        LdifEntry entry = new LdifEntry();
        entry.setDn( dn );

        // We remove this dn from the lines
        lines.remove( 0 );

        // Now, let's iterate through the other lines
        Iterator<String> iter = lines.iterator();

        // This flag is used to distinguish between an entry and a change
        int type = UNKNOWN;

        // The following boolean is used to check that a control is *not*
        // found elswhere than just after the dn
        boolean controlSeen = false;

        // We use this boolean to check that we do not have AttributeValues
        // after a change operation
        boolean changeTypeSeen = false;

        ChangeType operation = ChangeType.Add;
        String lowerLine = null;
        Control control = null;

        while ( iter.hasNext() )
        {
            // Each line could start either with an OID, an attribute type, with
            // "control:" or with "changetype:"
            line = iter.next();
            lowerLine = line.toLowerCase();

            // We have three cases :
            // 1) The first line after the DN is a "control:"
            // 2) The first line after the DN is a "changeType:"
            // 3) The first line after the DN is anything else
            if ( lowerLine.startsWith( "control:" ) )
            {
                if ( containsEntries )
                {
                    LOG.error( I18n.err( I18n.ERR_12004 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12005 ) );
                }

                containsChanges = true;

                if ( controlSeen )
                {
                    LOG.error( I18n.err( I18n.ERR_12050 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12051 ) );
                }

                // Parse the control
                control = parseControl( line.substring( "control:".length() ) );
                entry.addControl( control );
            }
            else if ( lowerLine.startsWith( "changetype:" ) )
            {
                if ( containsEntries )
                {
                    LOG.error( I18n.err( I18n.ERR_12004 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12005 ) );
                }

                containsChanges = true;

                if ( changeTypeSeen )
                {
                    LOG.error( I18n.err( I18n.ERR_12052 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12053 ) );
                }

                // A change request
                type = CHANGE;
                controlSeen = true;

                operation = parseChangeType( line );

                // Parse the change operation in a separate function
                parseChange( entry, iter, operation );
                changeTypeSeen = true;
            }
            else if ( line.indexOf( ':' ) > 0 )
            {
                if ( containsChanges )
                {
                    LOG.error( I18n.err( I18n.ERR_12004 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12005 ) );
                }

                containsEntries = true;

                if ( controlSeen || changeTypeSeen )
                {
                    LOG.error( I18n.err( I18n.ERR_12054 ) );
                    throw new LdapLdifException( I18n.err( I18n.ERR_12055 ) );
                }

                parseAttributeValue( entry, line, lowerLine );
                type = LDIF_ENTRY;
            }
            else
            {
                // Invalid attribute Value
                LOG.error( I18n.err( I18n.ERR_12056 ) );
                throw new LdapLdifException( I18n.err( I18n.ERR_12057 ) );
            }
        }

        if ( type == LDIF_ENTRY )
        {
            LOG.debug( "Read an entry : {}", entry );
        }
        else if ( type == CHANGE )
        {
            entry.setChangeType( operation );
            LOG.debug( "Read a modification : {}", entry );
        }
        else
        {
            LOG.error( I18n.err( I18n.ERR_12058 ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12059 ) );
        }

        return entry;
    }


    /**
     * Parse the version from the ldif input.
     * 
     * @return A number representing the version (default to 1)
     * @throws LdapLdifException
     *             If the version is incorrect
     * @throws LdapLdifException
     *             If the input is incorrect
     */
    private int parseVersion() throws LdapLdifException
    {
        int ver = DEFAULT_VERSION;

        // First, read a list of lines
        readLines();

        if ( lines.size() == 0 )
        {
            LOG.warn( "The ldif file is empty" );
            return ver;
        }

        // get the first line
        String line = lines.get( 0 );

        // <ldif-file> ::= "version:" <fill> <number>
        char[] document = line.toCharArray();
        String versionNumber = null;

        if ( line.startsWith( "version:" ) )
        {
            position.inc( "version:".length() );
            parseFill( document, position );

            // Version number. Must be '1' in this version
            versionNumber = parseNumber( document, position );

            // We should not have any other chars after the number
            if ( position.pos != document.length )
            {
                LOG.error( I18n.err( I18n.ERR_12060 ) );
                throw new LdapLdifException( I18n.err( I18n.ERR_12061 ) );
            }

            try
            {
                ver = Integer.parseInt( versionNumber );
            }
            catch ( NumberFormatException nfe )
            {
                LOG.error( I18n.err( I18n.ERR_12060 ) );
                throw new LdapLdifException( I18n.err( I18n.ERR_12061 ) );
            }

            LOG.debug( "Ldif version : {}", versionNumber );

            // We have found the version, just discard the line from the list
            lines.remove( 0 );

            // and read the next lines if the current buffer is empty
            if ( lines.size() == 0 )
            {
                readLines();
            }
        }
        else
        {
            LOG.info( "No version information : assuming version: 1" );
        }

        return ver;
    }


    /**
     * Reads an entry in a ldif buffer, and returns the resulting lines, without
     * comments, and unfolded.
     * 
     * The lines represent *one* entry.
     * 
     * @throws LdapLdifException If something went wrong
     */
    protected void readLines() throws LdapLdifException
    {
        String line = null;
        boolean insideComment = true;
        boolean isFirstLine = true;

        lines.clear();
        StringBuffer sb = new StringBuffer();

        try
        {
            while ( ( line = ( ( BufferedReader ) reader ).readLine() ) != null )
            {
                if ( line.length() == 0 )
                {
                    if ( isFirstLine )
                    {
                        continue;
                    }
                    else
                    {
                        // The line is empty, we have read an entry
                        insideComment = false;
                        break;
                    }
                }

                // We will read the first line which is not a comment
                switch ( line.charAt( 0 ) )
                {
                    case '#':
                        insideComment = true;
                        break;

                    case ' ':
                        isFirstLine = false;

                        if ( insideComment )
                        {
                            continue;
                        }
                        else if ( sb.length() == 0 )
                        {
                            LOG.error( I18n.err( I18n.ERR_12062 ) );
                            throw new LdapLdifException( I18n.err( I18n.ERR_12061 ) );
                        }
                        else
                        {
                            sb.append( line.substring( 1 ) );
                        }

                        insideComment = false;
                        break;

                    default:
                        isFirstLine = false;

                        // We have found a new entry
                        // First, stores the previous one if any.
                        if ( sb.length() != 0 )
                        {
                            lines.add( sb.toString() );
                        }

                        sb = new StringBuffer( line );
                        insideComment = false;
                        break;
                }
            }
        }
        catch ( IOException ioe )
        {
            throw new LdapLdifException( I18n.err( I18n.ERR_12063 ) );
        }

        // Stores the current line if necessary.
        if ( sb.length() != 0 )
        {
            lines.add( sb.toString() );
        }
    }


    /**
     * Parse a ldif file (using the default encoding).
     * 
     * @param fileName
     *            The ldif file
     * @return A list of entries
     * @throws LdapLdifException
     *             If the parsing fails
     */
    public List<LdifEntry> parseLdifFile( String fileName ) throws LdapLdifException
    {
        return parseLdifFile( fileName, Charset.forName( StringTools.getDefaultCharsetName() ).toString() );
    }


    /**
     * Parse a ldif file, decoding it using the given charset encoding
     * 
     * @param fileName
     *            The ldif file
     * @param encoding
     *            The charset encoding to use
     * @return A list of entries
     * @throws LdapLdifException
     *             If the parsing fails
     */
    // This will suppress PMD.EmptyCatchBlock warnings in this method
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public List<LdifEntry> parseLdifFile( String fileName, String encoding ) throws LdapLdifException
    {
        if ( StringTools.isEmpty( fileName ) )
        {
            LOG.error( I18n.err( I18n.ERR_12064 ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12065 ) );
        }

        File file = new File( fileName );

        if ( !file.exists() )
        {
            LOG.error( I18n.err( I18n.ERR_12066, fileName ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12067, fileName ) );
        }

        BufferedReader reader = null;

        // Open the file and then get a channel from the stream
        try
        {
            reader = new BufferedReader(
                new InputStreamReader( 
                    new FileInputStream( file ), Charset.forName( encoding ) ) );

            return parseLdif( reader );
        }
        catch ( FileNotFoundException fnfe )
        {
            LOG.error( I18n.err( I18n.ERR_12068, fileName ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12067, fileName ) );
        }
        catch ( LdapException le )
        {
            le.printStackTrace();
            throw new LdapLdifException( le.getMessage() );
        }
        finally
        {
            // close the reader
            try
            {
                if ( reader != null )
                {
                    reader.close();
                }
            }
            catch ( IOException ioe )
            {
                // Nothing to do
            }
        }
    }


    /**
     * A method which parses a ldif string and returns a list of entries.
     * 
     * @param ldif
     *            The ldif string
     * @return A list of entries, or an empty List
     * @throws LdapLdifException
     *             If something went wrong
     */
    // This will suppress PMD.EmptyCatchBlock warnings in this method
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public List<LdifEntry> parseLdif( String ldif ) throws LdapLdifException
    {
        LOG.debug( "Starts parsing ldif buffer" );

        if ( StringTools.isEmpty( ldif ) )
        {
            return new ArrayList<LdifEntry>();
        }

        BufferedReader reader = new BufferedReader( 
            new StringReader( ldif ) );

        try
        {
            List<LdifEntry> entries = parseLdif( reader );

            if ( LOG.isDebugEnabled() )
            {
                LOG.debug( "Parsed {} entries.", ( entries == null ? Integer.valueOf( 0 ) : Integer.valueOf( entries
                    .size() ) ) );
            }

            return entries;
        }
        catch ( LdapLdifException ne )
        {
            LOG.error( I18n.err( I18n.ERR_12069, ne.getLocalizedMessage() ) );
            throw new LdapLdifException( I18n.err( I18n.ERR_12070 ) );
        }
        catch ( LdapException le )
        {
            throw new LdapLdifException( le.getMessage() );
        }
        finally
        {
            // Close the reader
            try
            {
                if ( reader != null )
                {
                    reader.close();
                }
            }
            catch ( IOException ioe )
            {
                // Nothing to do
            }

        }
    }


    // ------------------------------------------------------------------------
    // Iterator Methods
    // ------------------------------------------------------------------------

    /**
     * Gets the next LDIF on the channel.
     * 
     * @return the next LDIF as a String.
     * @exception NoSuchElementException If we can't read the next entry
     */
    private LdifEntry nextInternal()
    {
        try
        {
            LOG.debug( "next(): -- called" );

            LdifEntry entry = prefetched;
            readLines();

            try
            {
                prefetched = parseEntry();
            }
            catch ( LdapLdifException ne )
            {
                error = ne;
                throw new NoSuchElementException( ne.getMessage() );
            }
            catch ( LdapException le )
            {
                throw new NoSuchElementException( le.getMessage() );
            }

            LOG.debug( "next(): -- returning ldif {}\n", entry );

            return entry;
        }
        catch ( LdapLdifException ne )
        {
            LOG.error( I18n.err( I18n.ERR_12071 ) );
            error = ne;
            return null;
        }
    }


    /**
     * Gets the next LDIF on the channel.
     * 
     * @return the next LDIF as a String.
     * @exception NoSuchElementException If we can't read the next entry
     */
    public LdifEntry next()
    {
        return nextInternal();
    }


    /**
     * Tests to see if another LDIF is on the input channel.
     * 
     * @return true if another LDIF is available false otherwise.
     */
    private boolean hasNextInternal()
    {
        return null != prefetched;
    }


    /**
     * Tests to see if another LDIF is on the input channel.
     * 
     * @return true if another LDIF is available false otherwise.
     */
    public boolean hasNext()
    {
        LOG.debug( "hasNext(): -- returning {}", ( prefetched != null ) ? Boolean.TRUE : Boolean.FALSE );

        return hasNextInternal();
    }


    /**
     * Always throws UnsupportedOperationException!
     * 
     * @see java.util.Iterator#remove()
     */
    private void removeInternal()
    {
        throw new UnsupportedOperationException();
    }


    /**
     * Always throws UnsupportedOperationException!
     * 
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        removeInternal();
    }


    /**
     * @return An iterator on the file
     */
    public Iterator<LdifEntry> iterator()
    {
        return new Iterator<LdifEntry>()
        {
            public boolean hasNext()
            {
                return hasNextInternal();
            }


            public LdifEntry next()
            {
                return nextInternal();
            }


            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }


    /**
     * @return True if an error occured during parsing
     */
    public boolean hasError()
    {
        return error != null;
    }


    /**
     * @return The exception that occurs during an entry parsing
     */
    public Exception getError()
    {
        return error;
    }


    /**
     * The main entry point of the LdifParser. It reads a buffer and returns a
     * List of entries.
     * 
     * @param reader
     *            The buffer being processed
     * @return A list of entries
     * @throws LdapLdifException
     *             If something went wrong
     * @throws LdapException 
     */
    public List<LdifEntry> parseLdif( BufferedReader reader ) throws LdapLdifException, LdapException
    {
        // Create a list that will contain the read entries
        List<LdifEntry> entries = new ArrayList<LdifEntry>();

        this.reader = reader;

        // First get the version - if any -
        version = parseVersion();
        prefetched = parseEntry();

        // When done, get the entries one by one.
        try
        {
            for ( LdifEntry entry : this )
            {
                if ( entry != null )
                {
                    entries.add( entry );
                }
            }
        }
        catch ( NoSuchElementException nsee )
        {
            throw new LdapLdifException( I18n.err( I18n.ERR_12072, error.getLocalizedMessage() ) );
        }

        return entries;
    }


    /**
     * @return True if the ldif file contains entries, fals if it contains
     *         changes
     */
    public boolean containsEntries()
    {
        return containsEntries;
    }


    /**
     * {@inheritDoc}
     */
    public void close() throws IOException
    {
        if ( reader != null )
        {
            position = new Position();
            reader.close();
            containsEntries = false;
            containsChanges = false;
        }
    }
}


