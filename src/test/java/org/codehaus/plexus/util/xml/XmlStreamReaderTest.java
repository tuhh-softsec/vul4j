package org.codehaus.plexus.util.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.codehaus.plexus.util.IOUtil;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

public class XmlStreamReaderTest
    extends TestCase
{
    /** french */
    private static final String TEXT_LATIN1 = "eacute: \u00E9";
    /** greek */
    private static final String TEXT_LATIN7 = "alpha: \u03B1";
    /** euro support */
    private static final String TEXT_LATIN15 = "euro: \u20AC";
    /** japanese */
    private static final String TEXT_EUC_JP = "hiragana A: \u3042";
    /** Unicode: support everything */
    private static final String TEXT_UNICODE =
        TEXT_LATIN1 + ", " +
        TEXT_LATIN7 + ", " +
        TEXT_LATIN15 + ", " +
        TEXT_EUC_JP;

    private static String createXmlContent( String text, String encoding )
    {
        String xmlDecl = "<?xml version=\"1.0\"?>";
        if ( encoding != null )
        {
            xmlDecl = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>";
        }
        String xml = xmlDecl + "\n<text>" + text + "</text>";
        return xml;
    }
    
    private static void checkXmlContent( String xml, String encoding )
    throws IOException
    {
        byte[] xmlContent = xml.getBytes( encoding );
        XmlStreamReader reader = new XmlStreamReader( new ByteArrayInputStream( xmlContent ) );
        String result = IOUtil.toString( reader );
        assertEquals( xml, result );
    }

    private static void checkXmlStreamReader( String text, String encoding )
    throws IOException
    {
        String xml = createXmlContent( text, encoding );
        String effectiveEncoding = ( encoding == null ) ? "UTF-8" : encoding;
        checkXmlContent( xml, effectiveEncoding );
    }

    public void testNoXmlHeader()
    throws IOException
    {
        String xml = "<text>text with no XML header</text>";
        checkXmlContent( xml, "UTF-8" );
    }

    public void testDefaultEncoding()
    throws IOException
    {
        checkXmlStreamReader( TEXT_UNICODE, null );
    }

    public void testUTF8Encoding()
    throws IOException
    {
        checkXmlStreamReader( TEXT_UNICODE, "UTF-8" );
    }

    public void testUTF16Encoding()
    throws IOException
    {
        checkXmlStreamReader( TEXT_UNICODE, "UTF-16" );
    }

    public void testUTF16BEEncoding()
    throws IOException
    {
        checkXmlStreamReader( TEXT_UNICODE, "UTF-16BE" );
    }

    public void testUTF16LEEncoding()
    throws IOException
    {
        checkXmlStreamReader( TEXT_UNICODE, "UTF-16LE" );
    }

    public void testLatin1Encoding()
    throws IOException
    {
        checkXmlStreamReader( TEXT_LATIN1, "ISO-8859-1" );
    }

    public void testLatin7Encoding()
    throws IOException
    {
        checkXmlStreamReader( TEXT_LATIN7, "ISO-8859-7" );
    }

    public void testLatin15Encoding()
    throws IOException
    {
        checkXmlStreamReader( TEXT_LATIN15, "ISO-8859-15" );
    }

    public void testEUC_JPEncoding()
    throws IOException
    {
        checkXmlStreamReader( TEXT_EUC_JP, "EUC-JP" );
    }

    public void testEBCDICEncoding()
    throws IOException
    {
        checkXmlStreamReader( "simple text in EBCDIC", "CP1047" );
    }

    public void testInappropriateEncoding()
    throws IOException
    {
        try
        {
            checkXmlStreamReader( TEXT_UNICODE, "ISO-8859-2" );
            fail( "Check should have failed, since some characters are not available in the specified encoding" );
        }
        catch ( ComparisonFailure cf )
        {
            // expected failure, since the encoding does not contain some characters
        }
    }
}
