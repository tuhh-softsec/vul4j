<%@page import="java.util.Enumeration"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%
	response.addCookie(new Cookie("test1", "value11"));
%>
<%
	response.addCookie(new Cookie("test2", "value2"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Page containing blocks</title>
</head>
<body style="background-color: aqua">
	<div>Begin page</div>
	<div style="border: 1px solid red">

		<!--$beginblock$myblock$-->
		<div style="background-color: aqua">

			Cookies for aggregated2 : <br />
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
		</div>

		<script type="text/javascript">
		<!--
			// Javascript-set cookie
			document.cookie = "testjs=testJs;Path=/";
		//-->
		</script>
		<!--$endblock$myblock$-->
	</div>
</body>
</html>