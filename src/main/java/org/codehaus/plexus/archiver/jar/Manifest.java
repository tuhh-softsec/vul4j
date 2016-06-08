/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.archiver.jar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.Attributes;
import org.codehaus.plexus.archiver.ArchiverException;

/**
 * Holds the data of a jar manifest.
 * <p/>
 * Manifests are processed according to the
 * {@link <a href="http://java.sun.com/j2se/1.4/docs/guide/jar/jar.html">Jar
 * file specification.</a>}.
 * Specifically, a manifest element consists of
 * a set of attributes and sections. These sections in turn may contain
 * attributes. Note in particular that this may result in manifest lines
 * greater than 72 bytes (including line break) being wrapped and continued
 * on the next line. If an application can not handle the continuation
 * mechanism, it is a defect in the application, not this task.
 *
 * @since Ant 1.4
 */
public class Manifest
    extends java.util.jar.Manifest implements Iterable<String>
{

    /**
     * The Name Attribute is the first in a named section
     */
    private static final String ATTRIBUTE_NAME = ManifestConstants.ATTRIBUTE_NAME;

    /**
     * The From Header is disallowed in a Manifest
     */
    private static final String ATTRIBUTE_FROM = ManifestConstants.ATTRIBUTE_FROM;

    /**
     * Default Manifest version if one is not specified
     */
    private static final String DEFAULT_MANIFEST_VERSION = ManifestConstants.DEFAULT_MANIFEST_VERSION;

    /**
     * The max length of a line in a Manifest
     */
    private static final int MAX_LINE_LENGTH = 72;

    /**
     * Max length of a line section which is continued. Need to allow
     * for the CRLF.
     */
    private static final int MAX_SECTION_LENGTH = MAX_LINE_LENGTH - 2;

    /**
     * The End-Of-Line marker in manifests
     */
    static final String EOL = "\r\n";

    public static class BaseAttribute
    {

        /**
         * The attribute's name
         */
        protected String name = null;

        /**
         * Get the Attribute's name
         *
         * @return the attribute's name.
         */
        public String getName()
        {
            return name;
        }

        @Override
        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( !( o instanceof BaseAttribute ) )
            {
                return false;
            }

            BaseAttribute that = (BaseAttribute) o;

            return !( name != null ? !name.equals( that.name ) : that.name != null );

        }

        @Override
        public int hashCode()
        {
            return name != null ? name.hashCode() : 0;
        }

    }

    /**
     * An attribute for the manifest.
     * Those attributes that are not nested into a section will be added to the "Main" section.
     */
    public static class Attribute
        extends BaseAttribute implements Iterable<String>
    {

        /**
         * The attribute's value
         */
        private Vector<String> values = new Vector<String>();

        /**
         * For multivalued attributes, this is the index of the attribute
         * currently being defined.
         */
        private int currentIndex = 0;

        /**
         * Construct an empty attribute
         */
        public Attribute()
        {
        }

        /**
         * Construct a manifest by specifying its name and value
         *
         * @param name the attribute's name
         * @param value the Attribute's value
         */
        public Attribute( String name, String value )
        {
            this.name = name;
            setValue( value );
        }

        @Override
        public Iterator<String> iterator()
        {
            return values.iterator();
        }

        /**
         * @see java.lang.Object#hashCode
         */
        @Override
        public int hashCode()
        {
            int hashCode = super.hashCode();
            hashCode += values.hashCode();
            return hashCode;
        }

        /**
         * @see java.lang.Object#equals
         */
        @Override
        public boolean equals( Object rhs )
        {
            if ( super.equals( rhs ) )
            {
                return false;
            }
            if ( rhs == null || rhs.getClass() != getClass() )
            {
                return false;
            }

            if ( rhs == this )
            {
                return true;
            }

            Attribute rhsAttribute = (Attribute) rhs;
            String lhsKey = getKey();
            String rhsKey = rhsAttribute.getKey();
            //noinspection SimplifiableIfStatement,ConstantConditions
            if ( ( lhsKey == null && rhsKey != null ) || ( lhsKey != null && rhsKey == null ) || !lhsKey.equals(
                rhsKey ) )
            {
                return false;
            }

            return rhsAttribute.values != null && values.equals( rhsAttribute.values );
        }

        /**
         * Set the Attribute's name; required
         *
         * @param name the attribute's name
         */
        public void setName( String name )
        {
            this.name = name;
        }

        /**
         * Get the attribute's Key - its name in lower case.
         *
         * @return the attribute's key.
         */
        public String getKey()
        {
            return getKey( name );
        }

        /**
         * Get the key for the specified attribute name - its name in lower case.
         *
         * @return the attribute's key.
         */
        private static String getKey( String name )
        {
            if ( name == null )
            {
                return null;
            }
            return name.toLowerCase( Locale.ENGLISH );
        }

        /**
         * Set the Attribute's value; required
         *
         * @param value the attribute's value
         */
        public void setValue( String value )
        {
            if ( currentIndex >= values.size() )
            {
                values.addElement( value );
                currentIndex = values.size() - 1;
            }
            else
            {
                values.setElementAt( value, currentIndex );
            }
        }

        /**
         * Get the Attribute's value.
         *
         * @return the attribute's value.
         */
        public String getValue()
        {
            if ( values.size() == 0 )
            {
                return null;
            }

            String fullValue = "";
            for ( String value : values )
            {
                fullValue += value + " ";
            }
            return fullValue.trim();
        }

        /**
         * Add a new value to this attribute - making it multivalued.
         *
         * @param value the attribute's additional value
         */
        public void addValue( String value )
        {
            currentIndex++;
            setValue( value );
        }

        /**
         * Writes the attribute out to a writer.
         *
         * @param writer the Writer to which the attribute is written
         *
         * @throws IOException if the attribute value cannot be written
         */
        void write( Writer writer )
            throws IOException
        {
            for ( String value : values )
            {
                writeValue( writer, value );
            }
        }

        /**
         * Write a single attribute value out. Should handle multiple lines of attribute value.
         *
         * @param writer the Writer to which the attribute is written
         * @param value the attribute value
         *
         * @throws IOException if the attribute value cannot be written
         */
        private void writeValue( Writer writer, String value )
            throws IOException
        {
            String nameValue = name + ": " + value;

            StringTokenizer tokenizer = new StringTokenizer( nameValue, "\n\r" );

            String prefix = "";

            while ( tokenizer.hasMoreTokens() )
            {
                writeLine( writer, prefix + tokenizer.nextToken() );
                prefix = " ";
            }
        }

        /**
         * Write a single Manifest line. Should handle more than 72 bytes of line
         *
         * @param writer the Writer to which the attribute is written
         * @param line the manifest line to be written
         *
         * @throws java.io.IOException when Io excepts
         */
        private void writeLine( Writer writer, String line )
            throws IOException
        {
            // Make sure we have at most 70 bytes in UTF-8 as specified excluding line break
            while ( line.getBytes( "UTF-8" ).length > MAX_SECTION_LENGTH )
            {
                // Try to find a MAX_SECTION_LENGTH
                // Use the minimum because we operate on at most chars and not bytes here otherwise
                // if we have more bytes than chars we will die in an IndexOutOfBoundsException.
                int breakIndex = Math.min( line.length(), MAX_SECTION_LENGTH );
                String section = line.substring( 0, breakIndex );
                while ( section.getBytes( "UTF-8" ).length > MAX_SECTION_LENGTH && breakIndex > 0 )
                {
                    breakIndex--;
                    section = line.substring( 0, breakIndex );
                }
                if ( breakIndex == 0 )
                {
                    throw new IOException( "Unable to write manifest line " + line );
                }
                writer.write( section + EOL );
                line = " " + line.substring( breakIndex );
            }
            writer.write( line + EOL );
        }

    }

    public class ExistingAttribute
        extends Attribute implements Iterable<String>
    {

        private final Attributes attributes;

        public ExistingAttribute( Attributes attributes, String name )
        {
            this.attributes = attributes;
            this.name = name;
        }

        @Override
        public Iterator<String> iterator()
        {
            return getKeys( attributes ).iterator();
        }

        @Override
        public void setName( String name )
        {
            throw new UnsupportedOperationException( "Cant do this" );
        }

        @Override
        public String getKey()
        {
            return name;
        }

        @Override
        public void setValue( String value )
        {
            attributes.putValue( name, value );
        }

        @Override
        public String getValue()
        {
            return attributes.getValue( name );
        }

        @Override
        public void addValue( String value )
        {
            String value1 = getValue();
            value1 = ( value1 != null ) ? " " + value : value;
            setValue( value1 );
        }

        @Override
        void write( Writer writer )
            throws IOException
        {
            throw new UnsupportedOperationException( "Cant do this" );
        }

    }

    private static Collection<String> getKeys( Attributes attributes )
    {
        Collection<String> result = new ArrayList<String>();
        for ( Object objectObjectEntry : attributes.keySet() )
        {
            result.add( objectObjectEntry.toString() );
        }
        return result;
    }

    /**
     * A manifest section - you can nest attribute elements into sections.
     * A section consists of a set of attribute values,
     * separated from other sections by a blank line.
     */
    public static class Section implements Iterable<String>
    {

        /**
         * Warnings for this section
         */
        private Vector<String> warnings = new Vector<String>();

        /**
         * The section's name if any. The main section in a
         * manifest is unnamed.
         */
        private String name = null;

        /**
         * The section's attributes.
         */
        private Hashtable<String, Attribute> attributes = new Hashtable<String, Attribute>();

        /**
         * Index used to retain the attribute ordering
         */
        private Vector<String> attributeIndex = new Vector<String>();

        /**
         * The name of the section; optional -default is the main section.
         *
         * @param name the section's name
         */
        public void setName( String name )
        {
            this.name = name;
        }

        /**
         * Get the Section's name.
         *
         * @return the section's name.
         */
        public String getName()
        {
            return name;
        }

        @Override
        public Iterator<String> iterator()
        {
            return attributes.keySet().iterator();
        }

        /**
         * Get a attribute of the section
         *
         * @param attributeName the name of the attribute
         *
         * @return a Manifest.Attribute instance if the attribute is
         * single-valued, otherwise a Vector of Manifest.Attribute
         * instances.
         */
        public Attribute getAttribute( String attributeName )
        {
            return attributes.get( attributeName.toLowerCase() );
        }

        /**
         * Add an attribute to the section.
         *
         * @param attribute the attribute to be added to the section
         *
         * @throws ManifestException if the attribute is not valid.
         */
        public void addConfiguredAttribute( Attribute attribute )
            throws ManifestException
        {
            String check = addAttributeAndCheck( attribute );
            if ( check != null )
            {
                throw new ManifestException(
                    "Specify the section name using " + "the \"name\" attribute of the <section> element rather "
                        + "than using a \"Name\" manifest attribute" );
            }
        }

        /**
         * Add an attribute to the section
         *
         * @param attribute the attribute to be added.
         *
         * @return the value of the attribute if it is a name
         * attribute - null other wise
         *
         * @throws ManifestException if the attribute already
         * exists in this section.
         */
        public String addAttributeAndCheck( Attribute attribute )
            throws ManifestException
        {
            if ( attribute.getName() == null || attribute.getValue() == null )
            {
                throw new ManifestException( "Attributes must have name and value" );
            }
            if ( attribute.getKey().equalsIgnoreCase( ATTRIBUTE_NAME ) )
            {
                warnings.addElement(
                    "\"" + ATTRIBUTE_NAME + "\" attributes " + "should not occur in the main section and must be the "
                        + "first element in all other sections: \"" + attribute.getName() + ": " + attribute.getValue()
                        + "\"" );
                return attribute.getValue();
            }

            if ( attribute.getKey().startsWith( Attribute.getKey( ATTRIBUTE_FROM ) ) )
            {
                warnings.addElement( "Manifest attributes should not start " + "with \"" + ATTRIBUTE_FROM + "\" in \""
                                         + attribute.getName() + ": " + attribute.getValue() + "\"" );
            }
            else
            {
                // classpath attributes go into a vector
                String attributeKey = attribute.getKey();
                if ( attributeKey.equalsIgnoreCase( ManifestConstants.ATTRIBUTE_CLASSPATH ) )
                {
                    Attribute classpathAttribute = attributes.get( attributeKey );

                    if ( classpathAttribute == null )
                    {
                        storeAttribute( attribute );
                    }
                    else
                    {
                        warnings.addElement( "Multiple Class-Path attributes " + "are supported but violate the Jar "
                                                 + "specification and may not be correctly "
                                                 + "processed in all environments" );

                        for ( String value : attribute )
                        {
                            classpathAttribute.addValue( value );
                        }
                    }
                }
                else if ( attributes.containsKey( attributeKey ) )
                {
                    throw new ManifestException( "The attribute \"" + attribute.getName() + "\" may not occur more "
                                                     + "than once in the same section" );
                }
                else
                {
                    storeAttribute( attribute );
                }
            }
            return null;
        }

        /**
         * Store an attribute and update the index.
         *
         * @param attribute the attribute to be stored
         */
        protected void storeAttribute( Attribute attribute )
        {
            if ( attribute == null )
            {
                return;
            }

            String attributeKey = attribute.getKey();
            attributes.put( attributeKey, attribute );
            if ( !attributeIndex.contains( attributeKey ) )
            {
                attributeIndex.addElement( attributeKey );
            }
        }

        /**
         * Get the warnings for this section.
         *
         * @return an Enumeration of warning strings.
         */
        public Enumeration<String> getWarnings()
        {
            return warnings.elements();
        }

        /**
         * @see java.lang.Object#hashCode
         */
        @Override
        public int hashCode()
        {
            int hashCode = 0;

            if ( name != null )
            {
                hashCode += name.hashCode();
            }

            hashCode += attributes.hashCode();
            return hashCode;
        }

        /**
         * @see java.lang.Object#equals
         */
        @Override
        public boolean equals( Object rhs )
        {
            if ( rhs == null || rhs.getClass() != getClass() )
            {
                return false;
            }

            if ( rhs == this )
            {
                return true;
            }

            Section rhsSection = (Section) rhs;

            return rhsSection.attributes != null && attributes.equals( rhsSection.attributes );
        }

    }

    public class ExistingSection implements Iterable<String>
    {

        private final Attributes backingAttributes;

        private final String sectionName;

        public ExistingSection( Attributes backingAttributes, String sectionName )
        {
            this.backingAttributes = backingAttributes;
            this.sectionName = sectionName;
        }

        @Override
        public Iterator<String> iterator()
        {
            return getKeys( backingAttributes ).iterator();
        }

        public ExistingAttribute getAttribute( String attributeName )
        {
            Attributes.Name name = new Attributes.Name( attributeName );
            return backingAttributes.containsKey( name )
                       ? new ExistingAttribute( backingAttributes, attributeName )
                       : null;

        }

        public String getName()
        {
            return sectionName;
        }

        public String getAttributeValue( String attributeName )
        {
            return backingAttributes.getValue( attributeName );
        }

        public void removeAttribute( String attributeName )
        {
            backingAttributes.remove( new Attributes.Name( attributeName ) );
        }

        public void addConfiguredAttribute( Attribute attribute )
            throws ManifestException
        {
            backingAttributes.putValue( attribute.getName(), attribute.getValue() );
        }

        public String addAttributeAndCheck( Attribute attribute )
            throws ManifestException
        {
            return remap( backingAttributes, attribute );
        }

        @Override
        public int hashCode()
        {
            return backingAttributes.hashCode();
        }

        @Override
        public boolean equals( Object rhs )
        {
            return rhs instanceof ExistingSection && backingAttributes.equals(
                ( (ExistingSection) rhs ).backingAttributes );
        }

    }

    @Override
    public Iterator<String> iterator()
    {
        return getEntries().keySet().iterator();
    }

    /**
     * The main section of this manifest
     */
    private Section mainSection = new Section();

    /**
     * Construct a manifest from Ant's default manifest file.
     *
     * @return the default manifest.
     *
     * @throws ArchiverException if there is a problem loading the
     * default manifest
     */
    public static Manifest getDefaultManifest()
        throws ArchiverException
    {
        final Manifest defaultManifest = new Manifest();
        defaultManifest.getMainAttributes().putValue( "Manifest-Version", "1.0" );

        String createdBy = "Plexus Archiver";

        final String plexusArchiverVersion = JdkManifestFactory.getArchiverVersion();

        if ( plexusArchiverVersion != null )
        {
            createdBy += " " + plexusArchiverVersion;
        }

        defaultManifest.getMainAttributes().putValue( "Created-By", createdBy );

        return defaultManifest;
    }

    /**
     * Construct an empty manifest
     */
    public Manifest()
    {
        setManifestVersion();
    }

    private void setManifestVersion()
    {
        getMainAttributes().put( Attributes.Name.MANIFEST_VERSION, "1.0" );
    }

    /**
     * Read a manifest file from the given reader
     *
     * @param r is the reader from which the Manifest is read
     *
     * @throws ManifestException if the manifest is not valid according
     * to the JAR spec
     * @throws IOException if the manifest cannot be read from the reader.
     * @deprecated This constructor does not properly map characters to bytes. Use
     * {@link #Manifest(InputStream)}. Will be removed in 4.0.
     */
    @Deprecated
    public Manifest( Reader r )
        throws ManifestException, IOException
    {
        super( getInputStream( r ) );
        setManifestVersion();
    }

    public Manifest( InputStream is )
        throws IOException
    {
        super( is );
        setManifestVersion();
    }

    /**
     * Add a section to the manifest
     *
     * @param section the manifest section to be added
     *
     * @throws ManifestException if the secti0on is not valid.
     */
    public void addConfiguredSection( Section section )
        throws ManifestException
    {
        String sectionName = section.getName();
        if ( sectionName == null )
        {
            throw new ManifestException( "Sections must have a name" );
        }
        Attributes attributes = getOrCreateAttributes( sectionName );
        for ( String s : section.attributes.keySet() )
        {

            Attribute attribute = section.getAttribute( s );
            attributes.putValue( attribute.getName(), attribute.getValue() );
        }
    }

    private Attributes getOrCreateAttributes( String name )
    {
        Attributes attributes = getAttributes( name );
        if ( attributes == null )
        {
            attributes = new Attributes();
            getEntries().put( name, attributes );
        }
        return attributes;
    }

    /**
     * Add an attribute to the manifest - it is added to the main section.
     *
     * @param attribute the attribute to be added.
     *
     * @throws ManifestException if the attribute is not valid.
     */
    public void addConfiguredAttribute( Attribute attribute )
        throws ManifestException
    {
        remap( getMainAttributes(), attribute );
    }

    /**
     * Writes the manifest out to a writer.
     *
     * @param writer the Writer to which the manifest is written
     *
     * @throws IOException if the manifest cannot be written
     */
    public void write( Writer writer )
        throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        super.write( byteArrayOutputStream );
        // We know that UTF-8 is the encoding of the JAR file specification
        writer.write( byteArrayOutputStream.toString( "UTF-8" ) );
    }

    /**
     * Convert the manifest to its string representation
     *
     * @return a multiline string with the Manifest as it
     * appears in a Manifest file.
     */
    @Override
    public String toString()
    {
        StringWriter sw = new StringWriter();
        try
        {
            write( sw );
        }
        catch ( IOException e )
        {
            return null;
        }
        return sw.toString();
    }

    /**
     * Get the warnings for this manifest.
     *
     * @return an enumeration of warning strings
     */
    Enumeration<String> getWarnings()
    {
        Vector<String> warnings = new Vector<String>();

        Enumeration<String> warnEnum = mainSection.getWarnings();
        while ( warnEnum.hasMoreElements() )
        {
            warnings.addElement( warnEnum.nextElement() );
        }

        return warnings.elements();
    }

    /**
     * Get the version of the manifest
     *
     * @return the manifest's version string
     */
    public String getManifestVersion()
    {
        /*
         The version of this manifest
         */
        return DEFAULT_MANIFEST_VERSION;
    }

    /**
     * Get the main section of the manifest
     *
     * @return the main section of the manifest
     */
    public ExistingSection getMainSection()
    {
        return new ExistingSection( getMainAttributes(), null );
    }

    /**
     * Get a particular section from the manifest
     *
     * @param name the name of the section desired.
     *
     * @return the specified section or null if that section
     * does not exist in the manifest
     */
    public ExistingSection getSection( String name )
    {
        Attributes attributes = getAttributes( name );
        if ( attributes != null )
        {
            return new ExistingSection( attributes, name );
        }
        return null;
    }

    @Deprecated
    private static InputStream getInputStream( Reader r )
        throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read;
        while ( ( read = r.read() ) != -1 )
        {
            byteArrayOutputStream.write( read );
        }
        return new ByteArrayInputStream( byteArrayOutputStream.toByteArray() );
    }

    public static String remap( Attributes backingAttributes, Attribute attribute )
        throws ManifestException
    {
        if ( attribute.getKey() == null || attribute.getValue() == null )
        {
            throw new ManifestException( "Attributes must have name and value" );
        }

        String attributeKey = attribute.getKey();
        if ( attributeKey.equalsIgnoreCase( ManifestConstants.ATTRIBUTE_CLASSPATH ) )
        {
            String classpathAttribute = backingAttributes.getValue( attributeKey );

            if ( classpathAttribute == null )
            {
                classpathAttribute = attribute.getValue();
            }
            else
            {
                classpathAttribute += " " + attribute.getValue();
            }
            backingAttributes.putValue( ManifestConstants.ATTRIBUTE_CLASSPATH, classpathAttribute );
        }
        else
        {
            backingAttributes.putValue( attribute.getName(), attribute.getValue() );
            if ( attribute.getKey().equalsIgnoreCase( ATTRIBUTE_NAME ) )
            {
                return attribute.getValue();
            }
        }
        return null;

    }

}
