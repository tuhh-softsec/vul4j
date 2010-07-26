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

import java.io.UnsupportedEncodingException;

import javax.naming.directory.Attributes;

import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapInvalidAttributeValueException;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.apache.directory.shared.ldap.util.Base64;
import org.apache.directory.shared.ldap.util.StringTools;



/**
 * Some LDIF useful methods
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifUtils
{
    /** The array that will be used to match the first char.*/
    private static boolean[] LDIF_SAFE_STARTING_CHAR_ALPHABET = new boolean[128];

    /** The array that will be used to match the other chars.*/
    private static boolean[] LDIF_SAFE_OTHER_CHARS_ALPHABET = new boolean[128];

    /** The default length for a line in a ldif file */
    private static final int DEFAULT_LINE_LENGTH = 80;

    private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    static
    {
        // Initialization of the array that will be used to match the first char.
        for (int i = 0; i < 128; i++)
        {
            LDIF_SAFE_STARTING_CHAR_ALPHABET[i] = true;
        }

        LDIF_SAFE_STARTING_CHAR_ALPHABET[0] = false; // 0 (NUL)
        LDIF_SAFE_STARTING_CHAR_ALPHABET[10] = false; // 10 (LF)
        LDIF_SAFE_STARTING_CHAR_ALPHABET[13] = false; // 13 (CR)
        LDIF_SAFE_STARTING_CHAR_ALPHABET[32] = false; // 32 (SPACE)
        LDIF_SAFE_STARTING_CHAR_ALPHABET[58] = false; // 58 (:)
        LDIF_SAFE_STARTING_CHAR_ALPHABET[60] = false; // 60 (>)

        // Initialization of the array that will be used to match the other chars.
        for (int i = 0; i < 128; i++)
        {
            LDIF_SAFE_OTHER_CHARS_ALPHABET[i] = true;
        }

        LDIF_SAFE_OTHER_CHARS_ALPHABET[0] = false; // 0 (NUL)
        LDIF_SAFE_OTHER_CHARS_ALPHABET[10] = false; // 10 (LF)
        LDIF_SAFE_OTHER_CHARS_ALPHABET[13] = false; // 13 (CR)
    }


    /**
     * Checks if the input String contains only safe values, that is, the data
     * does not need to be encoded for use with LDIF. The rules for checking safety
     * are based on the rules for LDIF (LDAP Data Interchange Format) per RFC 2849.
     * The data does not need to be encoded if all the following are true:
     *
     * The data cannot start with the following char values:
     *         00 (NUL)
     *         10 (LF)
     *         13 (CR)
     *         32 (SPACE)
     *         58 (:)
     *         60 (<)
     *         Any character with value greater than 127
     *
     * The data cannot contain any of the following char values:
     *         00 (NUL)
     *         10 (LF)
     *         13 (CR)
     *         Any character with value greater than 127
     *
     * The data cannot end with a space.
     *
     * @param str the String to be checked
     * @return true if encoding not required for LDIF
     */
    public static boolean isLDIFSafe( String str )
    {
        if ( StringTools.isEmpty( str ) )
        {
            // A null string is LDIF safe
            return true;
        }

        // Checking the first char
        char currentChar = str.charAt(0);

        if ( ( currentChar > 127 ) || !LDIF_SAFE_STARTING_CHAR_ALPHABET[currentChar] )
        {
            return false;
        }

        // Checking the other chars
        for (int i = 1; i < str.length(); i++)
        {
            currentChar = str.charAt(i);

            if ( ( currentChar > 127 ) || !LDIF_SAFE_OTHER_CHARS_ALPHABET[currentChar] )
            {
                return false;
            }
        }

        // The String cannot end with a space
        return ( currentChar != ' ' );
    }


    /**
     * Convert an Attributes as LDIF
     * @param attrs the Attributes to convert
     * @return the corresponding LDIF code as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertToLdif( Attributes attrs ) throws LdapException
    {
        return convertAttributesToLdif( AttributeUtils.toClientEntry( attrs, null ), DEFAULT_LINE_LENGTH );
    }


    /**
     * Convert an Attributes as LDIF
     * @param attrs the Attributes to convert
     * @return the corresponding LDIF code as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertToLdif( Attributes attrs, int length ) throws LdapException
    {
        return convertAttributesToLdif( AttributeUtils.toClientEntry( attrs, null ), length );
    }


    /**
     * Convert an Attributes as LDIF. The DN is written.
     * @param attrs the Attributes to convert
     * @return the corresponding LDIF code as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertToLdif( Attributes attrs, DN dn, int length ) throws LdapException
    {
        return convertEntryToLdif( AttributeUtils.toClientEntry( attrs, dn ), length );
    }


    /**
     * Convert an Attributes as LDIF. The DN is written.
     * @param attrs the Attributes to convert
     * @return the corresponding LDIF code as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertToLdif( Attributes attrs, DN dn ) throws LdapException
    {
        return convertEntryToLdif( AttributeUtils.toClientEntry( attrs, dn ), DEFAULT_LINE_LENGTH );
    }


    /**
     * Convert an Entry to LDIF
     * @param entry the Entry to convert
     * @return the corresponding LDIF code as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertEntryToLdif( Entry entry ) throws LdapException
    {
        return convertEntryToLdif( entry, DEFAULT_LINE_LENGTH );
    }


    /**
     * Convert an Entry to LDIF including a version number at the top
     * @param entry the Entry to convert
     * @param includeVersionInfo flag to tell whether to include version number or not
     * @return the corresponding LDIF code as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertEntryToLdif( Entry entry, boolean includeVersionInfo ) throws LdapException
    {
        String ldif = convertEntryToLdif( entry, DEFAULT_LINE_LENGTH );

        if( includeVersionInfo )
        {
            ldif = "version: 1" + LINE_SEPARATOR + ldif;
        }

        return ldif;
    }


    /**
     * Convert all the Entry's attributes to LDIF. The DN is not written
     * @param entry the Entry to convert
     * @return the corresponding LDIF code as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertAttributesToLdif( Entry entry ) throws LdapException
    {
        return convertAttributesToLdif( entry, DEFAULT_LINE_LENGTH );
    }


    /**
     * Convert a LDIF String to an attributes.
     *
     * @param ldif The LDIF string containing an attribute value
     * @return An Attributes instance
     * @exception LdapException If the LDIF String cannot be converted to an Attributes
     */
    public static Attributes convertAttributesFromLdif( String ldif ) throws LdapLdifException
    {
        LdifAttributesReader reader = new  LdifAttributesReader();

        return AttributeUtils.toAttributes( reader.parseEntry( ldif ) );
    }


    /**
     * Convert an Entry as LDIF
     * @param entry the Entry to convert
     * @param length the expected line length
     * @return the corresponding LDIF code as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertEntryToLdif( Entry entry, int length ) throws LdapException
    {
        StringBuilder sb = new StringBuilder();

        if ( entry.getDn() != null )
        {
            // First, dump the DN
            if ( isLDIFSafe( entry.getDn().getName() ) )
            {
                sb.append( stripLineToNChars( "dn: " + entry.getDn().getName(), length ) );
            }
            else
            {
                sb.append( stripLineToNChars( "dn:: " + encodeBase64( entry.getDn().getName() ), length ) );
            }

            sb.append( '\n' );
        }

        // Then all the attributes
        for ( EntryAttribute attribute:entry )
        {
            sb.append( convertToLdif( attribute, length ) );
        }

        return sb.toString();
    }


    /**
     * Convert the Entry's attributes to LDIF. The DN is not written.
     * @param entry the Entry to convert
     * @param length the expected line length
     * @return the corresponding LDIF code as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertAttributesToLdif( Entry entry, int length ) throws LdapException
    {
        StringBuilder sb = new StringBuilder();

        // Then all the attributes
        for ( EntryAttribute attribute:entry )
        {
            sb.append( convertToLdif( attribute, length ) );
        }

        return sb.toString();
    }


    /**
     * Convert an LdifEntry to LDIF
     * @param entry the LdifEntry to convert
     * @return the corresponding LDIF as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertToLdif( LdifEntry entry ) throws LdapException
    {
        return convertToLdif( entry, DEFAULT_LINE_LENGTH );
    }


    /**
     * Convert an LdifEntry to LDIF
     * @param entry the LdifEntry to convert
     * @param length The maximum line's length
     * @return the corresponding LDIF as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertToLdif( LdifEntry entry, int length ) throws LdapException
    {
        StringBuilder sb = new StringBuilder();

        // First, dump the DN
        if ( isLDIFSafe( entry.getDn().getName() ) )
        {
            sb.append( stripLineToNChars( "dn: " + entry.getDn(), length ) );
        }
        else
        {
            sb.append( stripLineToNChars( "dn:: " + encodeBase64( entry.getDn().getName() ), length ) );
        }

        sb.append( '\n' );

        // Dump the ChangeType
        String changeType = entry.getChangeType().toString().toLowerCase();

        if ( entry.getChangeType() != ChangeType.None )
        {
            // First dump the controls if any
            if ( entry.hasControls() )
            {
                for ( Control control : entry.getControls().values() )
                {
                    StringBuilder controlStr = new StringBuilder();

                    controlStr.append( "control: " ).append( control.getOid() );
                    controlStr.append( " " ).append( control.isCritical() );

                    if ( control.hasValue() )
                    {
                        controlStr.append( "::" ).append( Base64.encode( control.getValue() ) );
                    }

                    sb.append( stripLineToNChars( controlStr.toString(), length ) );
                    sb.append( '\n' );
                }
            }

            sb.append( stripLineToNChars( "changetype: " + changeType, length ) );
            sb.append( '\n' );
        }

        switch ( entry.getChangeType() )
        {
            case None :
                if ( entry.hasControls() )
                {
                    sb.append( stripLineToNChars( "changetype: " + ChangeType.Add, length ) );
                }

                // Fallthrough

            case Add :
                if ( ( entry.getEntry() == null ) )
                {
                    throw new LdapException( I18n.err( I18n.ERR_12082 ) );
                }

                // Now, iterate through all the attributes
                for ( EntryAttribute attribute:entry.getEntry() )
                {
                    sb.append( convertToLdif( attribute, length ) );
                }

                break;

            case Delete :
                if ( entry.getEntry() != null )
                {
                    throw new LdapException( I18n.err( I18n.ERR_12081 ) );
                }

                break;

            case ModDn :
            case ModRdn :
                if ( entry.getEntry() != null )
                {
                    throw new LdapException( I18n.err( I18n.ERR_12083 ) );
                }


                // Stores the new RDN
                EntryAttribute newRdn = new DefaultEntryAttribute( "newrdn", entry.getNewRdn() );
                sb.append( convertToLdif( newRdn, length ) );

                // Stores the deleteoldrdn flag
                sb.append( "deleteoldrdn: " );

                if ( entry.isDeleteOldRdn() )
                {
                    sb.append( "1" );
                }
                else
                {
                    sb.append( "0" );
                }

                sb.append( '\n' );

                // Stores the optional newSuperior
                if ( ! StringTools.isEmpty( entry.getNewSuperior() ) )
                {
                    EntryAttribute newSuperior = new DefaultEntryAttribute( "newsuperior", entry.getNewSuperior() );
                    sb.append( convertToLdif( newSuperior, length ) );
                }

                break;

            case Modify :
                for ( Modification modification:entry.getModificationItems() )
                {
                    switch ( modification.getOperation() )
                    {
                        case ADD_ATTRIBUTE :
                            sb.append( "add: " );
                            break;

                        case REMOVE_ATTRIBUTE :
                            sb.append( "delete: " );
                            break;

                        case REPLACE_ATTRIBUTE :
                            sb.append( "replace: " );
                            break;

                        default :
                            break; // Do nothing

                    }

                    sb.append( modification.getAttribute().getId() );
                    sb.append( '\n' );

                    sb.append( convertToLdif( modification.getAttribute() ) );
                    sb.append( "-\n" );
                }
                break;

            default :
                break; // Do nothing

        }

        sb.append( '\n' );

        return sb.toString();
    }

    /**
     * Base64 encode a String
     * @param str The string to encode
     * @return the base 64 encoded string
     */
    private static String encodeBase64( String str )
    {
        char[] encoded =null;

        try
        {
            // force encoding using UTF-8 charset, as required in RFC2849 note 7
            encoded = Base64.encode( str.getBytes( "UTF-8" ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            encoded = Base64.encode( str.getBytes() );
        }

        return new String( encoded );
    }


    /**
     * Converts an EntryAttribute to LDIF
     * @param attr the >EntryAttribute to convert
     * @return the corresponding LDIF code as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertToLdif( EntryAttribute attr ) throws LdapException
    {
        return convertToLdif( attr, DEFAULT_LINE_LENGTH );
    }


    /**
     * Converts an EntryAttribute as LDIF
     * @param attr the EntryAttribute to convert
     * @param length the expected line length
     * @return the corresponding LDIF code as a String
     * @throws LdapException If a naming exception is encountered.
     */
    public static String convertToLdif( EntryAttribute attr, int length ) throws LdapException
    {
        StringBuilder sb = new StringBuilder();

        for ( Value<?> value:attr )
        {
            StringBuilder lineBuffer = new StringBuilder();

            lineBuffer.append( attr.getUpId() );

            // First, deal with null value (which is valid)
            if ( value.isNull() )
            {
                lineBuffer.append( ':' );
            }
            else if ( value.isBinary() )
            {
                // It is binary, so we have to encode it using Base64 before adding it
                char[] encoded = Base64.encode( value.getBytes() );

                lineBuffer.append( ":: " + new String( encoded ) );
            }
            else if ( !value.isBinary() )
            {
                // It's a String but, we have to check if encoding isn't required
                String str = value.getString();

                if ( !LdifUtils.isLDIFSafe( str ) )
                {
                    lineBuffer.append( ":: " + encodeBase64( str ) );
                }
                else
                {
                    lineBuffer.append( ":" );

                    if ( str != null)
                    {
                        lineBuffer.append( " " ).append( str );
                    }
                }
            }

            lineBuffer.append( "\n" );
            sb.append( stripLineToNChars( lineBuffer.toString(), length ) );
        }

        return sb.toString();
    }


    /**
     * Strips the String every n specified characters
     * @param str the string to strip
     * @param nbChars the number of characters
     * @return the stripped String
     */
    public static String stripLineToNChars( String str, int nbChars)
    {
        int strLength = str.length();

        if ( strLength <= nbChars )
        {
            return str;
        }

        if ( nbChars < 2 )
        {
            throw new IllegalArgumentException( I18n.err( I18n.ERR_12084 ) );
        }

        // We will first compute the new size of the LDIF result
        // It's at least nbChars chars plus one for \n
        int charsPerLine = nbChars - 1;

        int remaining = ( strLength - nbChars ) % charsPerLine;

        int nbLines = 1 + ( ( strLength - nbChars ) / charsPerLine ) +
                        ( remaining == 0 ? 0 : 1 );

        int nbCharsTotal = strLength + nbLines + nbLines - 2;

        char[] buffer = new char[ nbCharsTotal ];
        char[] orig = str.toCharArray();

        int posSrc = 0;
        int posDst = 0;

        System.arraycopy( orig, posSrc, buffer, posDst, nbChars );
        posSrc += nbChars;
        posDst += nbChars;

        for ( int i = 0; i < nbLines - 2; i ++ )
        {
            buffer[posDst++] = '\n';
            buffer[posDst++] = ' ';

            System.arraycopy( orig, posSrc, buffer, posDst, charsPerLine );
            posSrc += charsPerLine;
            posDst += charsPerLine;
        }

        buffer[posDst++] = '\n';
        buffer[posDst++] = ' ';
        System.arraycopy( orig, posSrc, buffer, posDst, remaining == 0 ? charsPerLine : remaining );

        return new String( buffer );
    }


    /**
     * Build a new Attributes instance from a LDIF list of lines. The values can be
     * either a complete AVA, or a couple of AttributeType ID and a value (a String or
     * a byte[]). The following sample shows the three cases :
     *
     * <pre>
     * Attribute attr = AttributeUtils.createAttributes(
     *     "objectclass: top",
     *     "cn", "My name",
     *     "jpegPhoto", new byte[]{0x01, 0x02} );
     * </pre>
     *
     * @param avas The AttributeType and Values, using a ldif format, or a couple of
     * Attribute ID/Value
     * @return An Attributes instance
     * @throws LdapException If the data are invalid
     * @throws LdapLdifException
     */
    public static Attributes createAttributes( Object... avas ) throws LdapException, LdapLdifException
    {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        boolean valueExpected = false;

        for ( Object ava : avas)
        {
            if ( !valueExpected )
            {
                if ( !(ava instanceof String) )
                {
                    throw new LdapInvalidAttributeValueException( ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX, I18n.err( I18n.ERR_12085, (pos+1) ) );
                }

                String attribute = (String)ava;
                sb.append( attribute );

                if ( attribute.indexOf( ':' ) != -1 )
                {
                    sb.append( '\n' );
                }
                else
                {
                    valueExpected = true;
                }
            }
            else
            {
                if ( ava instanceof String )
                {
                    sb.append( ": " ).append( (String)ava ).append( '\n' );
                }
                else if ( ava instanceof byte[] )
                {
                    sb.append( ":: " );
                    sb.append( new String( Base64.encode( (byte[] )ava ) ) );
                    sb.append( '\n' );
                }
                else
                {
                    throw new LdapInvalidAttributeValueException( ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX, I18n.err( I18n.ERR_12086, (pos+1) ) );
                }

                valueExpected = false;
            }
        }

        if ( valueExpected )
        {
            throw new LdapInvalidAttributeValueException( ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX, I18n.err( I18n.ERR_12087 ) );
        }

        LdifAttributesReader reader = new LdifAttributesReader();
        Attributes attributes = AttributeUtils.toAttributes( reader.parseEntry( sb.toString() ) );

        return attributes;
    }


    /**
     * Build a new Attributes instance from a LDIF list of lines. The values can be
     * either a complete AVA, or a couple of AttributeType ID and a value (a String or
     * a byte[]). The following sample shows the three cases :
     *
     * <pre>
     * Attribute attr = AttributeUtils.createAttributes(
     *     "objectclass: top",
     *     "cn", "My name",
     *     "jpegPhoto", new byte[]{0x01, 0x02} );
     * </pre>
     *
     * @param avas The AttributeType and Values, using a ldif format, or a couple of
     * Attribute ID/Value
     * @return An Attributes instance
     * @throws LdapException If the data are invalid
     * @throws LdapLdifException
     */
    public static Entry createEntry( DN dn, Object... avas ) throws LdapException, LdapLdifException
    {
        return createEntry( null, dn, avas );
    }


    /**
     * Build a new Attributes instance from a LDIF list of lines. The values can be
     * either a complete AVA, or a couple of AttributeType ID and a value (a String or
     * a byte[]). The following sample shows the three cases :
     *
     * <pre>
     * Attribute attr = AttributeUtils.createAttributes(
     *     "objectclass: top",
     *     "cn", "My name",
     *     "jpegPhoto", new byte[]{0x01, 0x02} );
     * </pre>
     *
     * @param avas The AttributeType and Values, using a ldif format, or a couple of
     * Attribute ID/Value
     * @return An Attributes instance
     * @throws LdapException If the data are invalid
     * @throws LdapLdifException
     */
    public static Entry createEntry( SchemaManager schemaManager, DN dn, Object... avas ) throws LdapException, LdapLdifException
    {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        boolean valueExpected = false;

        for ( Object ava : avas)
        {
            if ( !valueExpected )
            {
                if ( !(ava instanceof String) )
                {
                    throw new LdapInvalidAttributeValueException( ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX, I18n.err( I18n.ERR_12085, (pos+1) ) );
                }

                String attribute = (String)ava;
                sb.append( attribute );

                if ( attribute.indexOf( ':' ) != -1 )
                {
                    sb.append( '\n' );
                }
                else
                {
                    valueExpected = true;
                }
            }
            else
            {
                if ( ava instanceof String )
                {
                    sb.append( ": " ).append( (String)ava ).append( '\n' );
                }
                else if ( ava instanceof byte[] )
                {
                    sb.append( ":: " );
                    sb.append( new String( Base64.encode( (byte[] )ava ) ) );
                    sb.append( '\n' );
                }
                else
                {
                    throw new LdapInvalidAttributeValueException( ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX, I18n.err( I18n.ERR_12086, (pos+1) ) );
                }

                valueExpected = false;
            }
        }

        if ( valueExpected )
        {
            throw new LdapInvalidAttributeValueException( ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX, I18n.err( I18n.ERR_12087 ) );
        }

        LdifAttributesReader reader = new LdifAttributesReader();
        Entry entry = reader.parseEntry( schemaManager, sb.toString() );
        entry.setDn( dn );

        return entry;
    }
}
