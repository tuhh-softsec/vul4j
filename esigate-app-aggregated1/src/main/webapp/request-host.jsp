<%@page session="false" language="java" contentType="text/plain; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.Enumeration"%><%
	Enumeration<String> hostHeaders = request.getHeaders("Host");
	String hosts = "[";
	while (hostHeaders.hasMoreElements()) {
		if(hosts.length()>1)
			hosts+=";";
		hosts += hostHeaders.nextElement();
	}
	hosts += "]";
%>request.getServerName()=<%=request.getServerName()%>
request.getServerPort()=<%=request.getServerPort()%>
request.getHeaders("Host")=<%=hosts%>
request.getLocalName()=<%=request.getLocalName()%>