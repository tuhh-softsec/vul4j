<%@page import="java.util.Enumeration"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%
	response.addCookie(new Cookie("test0", "value0"));
%>
<%
	response.addCookie(new Cookie("test1", "value1"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Cookies example</title>
</head>
<body style="background-color: yellow">
	<h1>Cookie test example</h1>
	
	Cookies for aggregated1 :
	<%
		Enumeration cookies = request.getHeaders("Cookie");
		boolean hasCookies = false;
		int nbHeaders = 0;
		if (cookies.hasMoreElements()) {
			hasCookies = true;
	%><ul>
		<%
			}
			while (cookies.hasMoreElements()) {
		%><li><%=cookies.nextElement().toString()%></li>
		<%
			nbHeaders++;
			}
			if (hasCookies) {
		%>
	</ul>
	<%
		}
	%>
	Number of Cookie headers :
	<%=nbHeaders%>
    <hr />

	<!--$includeblock$aggregated2$block-cookie.jsp$myblock$-->
	<!--$endincludeblock$-->
	<hr />
	Total 4 cookies :
	<ul>
       <li>test0 and test1 are passed to aggregated1</li>
       <li>test1, test2 and testjs are passed to aggregated2</li>
       <li>test0 and test1 are set by aggregated1</li>
       <li>test1 and test2 are set by aggregated2 (note that test1 has the last value set in the headers)</li>
       <li>testjs is set by the navigator</li>
       <li>Each application has its own JSESSIONID, which differ from aggregator</li>
	</ul>
</body>