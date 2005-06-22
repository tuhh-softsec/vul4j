package org.codehaus.plexus.util.xml;

public interface XMLWriter
{
    void startElement( String name );

    void addAttribute( String key, String value );

    void writeText( String text );

    void writeMarkup( String text );

    void endElement();

}
