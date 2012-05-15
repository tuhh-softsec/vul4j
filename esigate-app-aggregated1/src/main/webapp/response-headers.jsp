<%@page session="false" language="java" import="java.util.Enumeration"%><%
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
				if("Content-type".equalsIgnoreCase(name))
       				response.setContentType(value);
       			else
	       			response.addHeader(headerName, value);
       		} else {
      			response.addHeader("X-request-header-" + name, value);      			
       		}
       	}
    }
%>