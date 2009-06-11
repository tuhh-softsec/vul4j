package net.webassembletool.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

public class OutputTest extends TestCase {

    public void testCopyHeaders() {
        Output tested = new MockOutput();
        String name = "hEaDeR";
        tested.addHeader(name.toLowerCase(), "old");
        tested.addHeader(name.toUpperCase(), "old");
        tested.addHeader(name, "old");

        Output actual = new MockOutput();
        tested.copyHeaders(actual);
        assertEquals("headers are different", 3, actual.getHeaders().size());
        assertEquals("headers are different", tested.getHeaders(), actual
                .getHeaders());
    }

    public void testWrite() throws UnsupportedEncodingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Output tested = new MockOutput(out);
        tested.setCharsetName("UTF-8");

        tested.write("expected");
        assertEquals("expected", new String(out.toByteArray(), "UTF-8"));

        tested.setCharsetName("UNSUPPORTED");
        try {
            tested.write("unsupported");
            fail("should throw OutputException");
        } catch (OutputException e) {
            assertNotNull(e.getCause());
            assertTrue(
                    "cause should be instance of UnsupportedEncodingException",
                    e.getCause() instanceof UnsupportedEncodingException);
        }

        tested = new MockOutput(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException();
            }
        });
        tested.setCharsetName("UTF-8");
        try {
            tested.write("unexpected");
            fail("should throw OutputException");
        } catch (OutputException e) {
            assertNotNull(e.getCause());
            assertTrue("cause should be instance of IOException",
                    e.getCause() instanceof IOException);
        }
    }

    private final static class MockOutput extends Output {
        private final OutputStream out;

        public MockOutput() {
            this(null);
        }

        public MockOutput(OutputStream out) {
            this.out = out;
        }

        @Override
        public void close() {
            // Nothing to do
        }

        @Override
        public OutputStream getOutputStream() {
            return out;
        }

        @Override
        public void open() {
            // Nothing to do
        }
    }

}
