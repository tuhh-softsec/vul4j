<%@ page contentType="text/html; charset=ISO-8859-1" %><%@ page import="java.util.zip.GZIPOutputStream,java.io.InputStream,java.io.OutputStream" %><%
response.setHeader("Content-encoding", "gzip");
OutputStream os = response.getOutputStream();
GZIPOutputStream gzipOutputStream = new GZIPOutputStream(response.getOutputStream());
gzipOutputStream.write("Hello world".getBytes("ISO-8859-1"));
gzipOutputStream.close();
%>