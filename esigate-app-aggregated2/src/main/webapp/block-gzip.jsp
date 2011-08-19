<%@ page contentType="text/html; charset=ISO-8859-1" %><%@ page import="java.util.zip.GZIPOutputStream,java.io.InputStream,java.io.OutputStream" %><%
response.setHeader("Content-encoding", "gzip");
OutputStream os = response.getOutputStream();
GZIPOutputStream gzipOutputStream = new GZIPOutputStream(response.getOutputStream());
InputStream in = application.getResourceAsStream("/block.html");
int data = in.read();
while (data != -1) {
	gzipOutputStream.write(data);
	data = in.read();
}
in.close();
gzipOutputStream.close();
%>