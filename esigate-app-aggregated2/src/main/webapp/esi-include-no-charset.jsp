<%
	response.setContentType("text/html");
	response.getOutputStream().write("é".getBytes("ISO-8859-1"));
%>