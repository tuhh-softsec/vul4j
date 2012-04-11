<%@page session="false" language="java" contentType="text/plain; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.Enumeration"%><%
	// This JSP sends a response depending only on the request headers.
	// The request headers to send are:
	// X-response-code: the return code (default 200)
	// X-response-header-<headerName>: a http header to send back
	// X-response-body: the body of the response
	// In the response this page sends back all request headers:
	// X-request-header-<headerName>: a request header received
   	Enumeration names = request.getHeaderNames();
   	while(names.hasMoreElements()){
   		String name = names.nextElement().toString();
       	Enumeration values = request.getHeaders(name);
       	while(values.hasMoreElements()){
       		String value = values.nextElement().toString();
       		if (name.equalsIgnoreCase("X-response-code")) {
       			response.setStatus(Integer.parseInt(value));
       		} else if(name.equalsIgnoreCase("X-response-body")) {
       			response.getWriter().append(value);
       		} else if(name.toLowerCase().startsWith("x-response-header-")) {
       			String headerName = name.substring(18);
       			if("Date".equalsIgnoreCase(name))
       				// Can have only 1 date header
       				response.setDateHeader(name, System.currentTimeMillis()+86400000);
       			else
	       			response.addHeader(headerName, value);
       		} else {
      			response.addHeader("X-request-header-" + name, value);      			
       		}
       	}
    }
		response.setDateHeader("Date", System.currentTimeMillis()+86400000);
%>