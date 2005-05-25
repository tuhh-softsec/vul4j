package org.codehaus.plexus.util.xml;


public interface XMLWriter
{
    /**
     * Sets the encoding of the document.
     *
     * @param encoding the encoding
     * @throws IllegalStateException if the generation of the document has already started
     */
    void setEncoding( String encoding )
        throws IllegalStateException;

    /**
     * Sets the docType of the document.
     *
     * @param docType the docType
     * @throws IllegalStateException if the generation of the document has already started
     */
    void setDocType( String docType )
        throws IllegalStateException;

    void startElement( String name );

    void addAttribute( String key, String value );

    void writeText( String text );

    void writeMarkup( String text );

    void endElement();

}
