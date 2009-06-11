package net.webassembletool.parse;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
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

    public void testRender1() throws IOException, RenderingException {
        XsltRenderer tested = new XsltRenderer(null, null);
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
    }

    public void testRender2() throws IOException, RenderingException,
            SAXException, ParserConfigurationException,
            XPathExpressionException {
        StringOutput src = new MockStringOutput(
                "<list><item>item 1</item><item>item 2</item></list>");
        src.setStatusCode(HttpServletResponse.SC_OK);
        StringWriter out = new StringWriter();

        IMocksControl control = EasyMock.createControl();
        Document expDocument = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(
                        new InputSource(new StringReader(src.toString())));
        XPathExpression expressionMock = control
                .createMock(XPathExpression.class);
        XsltRenderer tested = new MockXsltRenderer(expressionMock, null,
                expDocument);

        EasyMock.expect(
                expressionMock.evaluate(expDocument, XPathConstants.NODE))
                .andReturn(expDocument);
        control.replay();

        tested.render(src, out, null);
        control.verify();
        assertEquals(src.toString(), out.toString().replaceAll("[\\t\\r\\n]",
                ""));
    }

    public void testRender3() throws IOException, RenderingException,
            SAXException, ParserConfigurationException,
            XPathExpressionException {
        StringOutput src = new MockStringOutput(
                "<list><item>item 1</item><item>item 2</item></list>");
        src.setStatusCode(HttpServletResponse.SC_OK);
        StringWriter out = new StringWriter();

        IMocksControl control = EasyMock.createControl();
        Document expDocument = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(
                        new InputSource(new StringReader(src.toString())));
        @SuppressWarnings("synthetic-access")
        Transformer expTransformer = new MockTransformer() {
            @Override
            public void transform(Source xmlSource, Result outputTarget) {
                assertNotNull(xmlSource);
                assertNotNull(outputTarget);
                assertTrue(xmlSource instanceof DOMSource);
                assertTrue(outputTarget instanceof DOMResult);
                ((DOMResult) outputTarget).setNode(((DOMSource) xmlSource)
                        .getNode());
            }

        };
        XPathExpression expressionMock = control
                .createMock(XPathExpression.class);
        XsltRenderer tested = new MockXsltRenderer(expressionMock,
                expTransformer, expDocument);

        EasyMock.expect(
                expressionMock.evaluate(expDocument, XPathConstants.NODE))
                .andReturn(expDocument);
        control.replay();

        tested.render(src, out, null);
        control.verify();
        assertEquals(src.toString(), out.toString().replaceAll("[\\t\\r\\n]",
                ""));
    }

    private static final class MockXsltRenderer extends XsltRenderer {
        private final Document document;

        public MockXsltRenderer(XPathExpression expr, Transformer transformer,
                Document document) {
            super(expr, transformer);
            this.document = document;
        }

        @Override
        protected Document createSourceDocument(StringOutput src) {
            return document;
        }
    }

    private static abstract class MockTransformer extends Transformer {

        @Override
        public void clearParameters() {
            throw new IllegalStateException("unexpected call");
        }

        @Override
        public ErrorListener getErrorListener() {
            throw new IllegalStateException("unexpected call");
        }

        @Override
        public Properties getOutputProperties() {
            throw new IllegalStateException("unexpected call");
        }

        @Override
        public String getOutputProperty(String name) {
            throw new IllegalStateException("unexpected call");
        }

        @Override
        public Object getParameter(String name) {
            throw new IllegalStateException("unexpected call");
        }

        @Override
        public URIResolver getURIResolver() {
            throw new IllegalStateException("unexpected call");
        }

        @Override
        public void setErrorListener(ErrorListener listener) {
            throw new IllegalStateException("unexpected call");
        }

        @Override
        public void setOutputProperties(Properties oformat) {
            throw new IllegalStateException("unexpected call");
        }

        @Override
        public void setOutputProperty(String name, String value) {
            throw new IllegalStateException("unexpected call");
        }

        @Override
        public void setParameter(String name, Object value) {
            throw new IllegalStateException("unexpected call");
        }

        @Override
        public void setURIResolver(URIResolver resolver) {
            throw new IllegalStateException("unexpected call");
        }
    }
}
