<%@page session="false" language="java" contentType="text/plain; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.Enumeration"%><%
    String name = request.getParameter("name");
	String result = "";
    if (name != null) {
    	Enumeration values = request.getHeaders(name);
    	while(values.hasMoreElements()){
    		if (result.length()>0)
    			result += "\n";
    		result += name.toLowerCase() + ": " + values.nextElement();
    	}
    } else {
    	Enumeration names = request.getHeaderNames();
    	while(names.hasMoreElements()){
    		name = names.nextElement().toString();
        	Enumeration values = request.getHeaders(name);
        	while(values.hasMoreElements()){
        		if (result.length()>0)
        			result += "\n";
        		result += name.toLowerCase() + ": " + values.nextElement();
        	}
    	}
    }
%><%=result%>