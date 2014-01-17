package org.esigate.servlet;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MockHttpServletResponse implements HttpServletResponse {
    private ServletOutputStream outputStream;
    private ByteArrayOutputStream outputStreamContent;
    private PrintWriter writer;
    private StringWriter writerContent;

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (writer != null) {
            throw new IllegalStateException("Writer already obtained");
        }
        if (outputStream == null) {
            outputStreamContent = new ByteArrayOutputStream();
            outputStream = new ServletOutputStream() {

                @Override
                public void write(int b) {
                    outputStreamContent.write(b);
                }
            };
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() {
        if (outputStream != null) {
            throw new IllegalStateException("OutputStream already obtained");
        }
        if (writer == null) {
            writerContent = new StringWriter();
            writer = new PrintWriter(writerContent);
        }
        return writer;
    }

    public String getWriterContent() {
        if (writer == null) {
            return null;
        } else {
            return writerContent.toString();
        }
    }

    public String getOutputStreamContentAsString(String charsetName) throws UnsupportedEncodingException {
        if (outputStream == null) {
            return null;
        } else {
            return outputStreamContent.toString(charsetName);
        }
    }

    @Override
    public void setCharacterEncoding(String charset) {
    }

    @Override
    public void setContentLength(int len) {
    }

    @Override
    public void setContentType(String type) {
    }

    @Override
    public void setBufferSize(int size) {
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() {
    }

    @Override
    public void resetBuffer() {
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {
    }

    @Override
    public void setLocale(Locale loc) {
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void addCookie(Cookie cookie) {
    }

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) {
    }

    @Override
    public void sendError(int sc) {
    }

    @Override
    public void sendRedirect(String location) {
    }

    @Override
    public void setDateHeader(String name, long date) {
    }

    @Override
    public void addDateHeader(String name, long date) {
    }

    @Override
    public void setHeader(String name, String value) {
    }

    @Override
    public void addHeader(String name, String value) {
    }

    @Override
    public void setIntHeader(String name, int value) {
    }

    @Override
    public void addIntHeader(String name, int value) {
    }

    @Override
    public void setStatus(int sc) {
    }

    @Override
    public void setStatus(int sc, String sm) {
    }

}
