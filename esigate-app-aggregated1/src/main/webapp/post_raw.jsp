Posted body data : <%
	byte[] buffer = new byte[request.getContentLength()];
	int read = 0;
	request.getInputStream().read(buffer);
	out.write(new String(buffer)); //On precise pas l'encoding
	if(request.getParameter("param") != null) {
		out.write(request.getParameter("param"));
	}
%>