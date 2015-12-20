<?xml version="1.0" encoding="ISO-8859-1" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Block example</title>
</head>
<body style="background-color: yellow">
Page from aggregated1
<br />
User: <%=request.getRemoteUser() %>
<br />
<!--$includeblock$aggregated2$block.jsp$myblock$--><!--$endincludeblock$-->
</body>
</html>