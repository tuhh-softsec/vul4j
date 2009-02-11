package net.webassembletool.parse;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;
import net.webassembletool.RenderingException;
import net.webassembletool.RetrieveException;
import net.webassembletool.output.StringOutput;
import net.webassembletool.util.MockStringOutput;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XsltRendererTest extends TestCase {

    public void testRender() throws IOException, RenderingException,
            SAXException, ParserConfigurationException,
            XPathExpressionException {
        XsltRenderer tested = new XsltRenderer(null);
        StringWriter out = new StringWriter();
        StringOutput src = new MockStringOutput(
                "<list><item>item 1</item><item>item 2</item></list>");

        src.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT, "expected");
        try {
            tested.render(src, out, null);
            fail("should throw RetrieveException");
        } catch (RetrieveException e) {
            assertEquals(HttpServletResponse.SC_GATEWAY_TIMEOUT, e
                    .getStatusCode());
            assertEquals("expected", e.getStatusMessage());
            assertEquals(src.toString(), e.getErrorPageContent());
        }

        src.setStatusCode(HttpServletResponse.SC_OK);
        tested.render(src, out, null);
        assertEquals(src.toString(), out.toString().replaceAll("[\\t\\r\\n]",
                ""));

        IMocksControl control = EasyMock.createControl();
        final Document expDocument = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(
                        new InputSource(new StringReader(src.toString())));
        XPathExpression expressionMock = control
                .createMock(XPathExpression.class);
        out = new StringWriter();
        tested = new XsltRenderer(expressionMock) {
            @Override
            protected Document createSourceDocument(StringOutput src) {
                return expDocument;
            }
        };

        EasyMock.expect(
                expressionMock.evaluate(expDocument, XPathConstants.NODE))
                .andReturn(expDocument);
        control.replay();

        tested.render(src, out, null);
        control.verify();
        assertEquals(src.toString(), out.toString().replaceAll("[\\t\\r\\n]",
                ""));
    }
}
