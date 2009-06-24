<%@page contentType="text/html; charset=UTF-8" import="java.net.URLDecoder"%> 
<%request.setCharacterEncoding("UTF-8"); %><%
// Some servers may process querystring as ISO-8859-1, it is safer to decode the querystring manually
String queryString = request.getQueryString();
String myField = "";
if (queryString != null) {
	myField = queryString.substring(queryString.indexOf("myField=") + 8);
	myField = myField.substring(0, myField.indexOf("&"));
	myField = URLDecoder.decode(myField, "UTF-8");
}
%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Form post example</title>
</head>
<body style="background-color: yellow">
Posted field value = <%=myField%>
<form method="get">
<input type="text" name="myField" />
<input type="submit" name="send" value="Post this form"/>
</form>
</body>
</html>