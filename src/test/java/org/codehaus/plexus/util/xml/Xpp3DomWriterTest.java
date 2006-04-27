package org.codehaus.plexus.util.xml;

import junit.framework.TestCase;

import java.io.StringWriter;

/**
 * @author Edwin Punzalan
 */
public class Xpp3DomWriterTest
    extends TestCase
{

    private static final String LS = System.getProperty("line.separator");

    public void testWriter()
    {
        StringWriter writer = new StringWriter();

        Xpp3DomWriter.write( writer, createXpp3Dom() );

        assertEquals( "Check if output matches", createExpectedXML(), writer.toString() );
    }

    private String createExpectedXML()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "<root>" );
        buf.append( LS );
        buf.append( "  <el1>element1</el1>" );
        buf.append( LS );
        buf.append( "  <el2 att2=\"attribute2&#10;nextline\">" );
        buf.append( LS );
        buf.append( "    <el3 att3=\"attribute3\">element3</el3>" );
        buf.append( LS );
        buf.append( "  </el2>" );
        buf.append( LS );
        buf.append( "  <el4></el4>" );
        buf.append( LS );
        buf.append( "</root>" );

        return buf.toString();
    }

    private Xpp3Dom createXpp3Dom()
    {
        Xpp3Dom dom = new Xpp3Dom( "root" );

        Xpp3Dom el1 = new Xpp3Dom( "el1" );
        el1.setValue( "element1" );
        dom.addChild( el1 );

        Xpp3Dom el2 = new Xpp3Dom( "el2" );
        el2.setAttribute( "att2", "attribute2\nnextline" );
        dom.addChild( el2 );

        Xpp3Dom el3 = new Xpp3Dom( "el3" );
        el3.setAttribute( "att3", "attribute3" );
        el3.setValue( "element3" );
        el2.addChild( el3 );

        Xpp3Dom el4 = new Xpp3Dom( "el4" );
        el4.setValue( "" );
        dom.addChild( el4 );

        return dom;
    }
}
