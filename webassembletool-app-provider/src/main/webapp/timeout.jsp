<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.sourceforge.net/webassembletool-helper"
	prefix="assemble"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Timeout test</title>
</head>
<body style="background-color: aqua">
<% 
	try {
		Thread.sleep(5000);
	} catch (InterruptedException e) {
		// Nothing to do
	}
%>
<div>If you read this, you must have been waiting 5 s</div>
<div style="border: 1px solid red"><assemble:block name="block1">
	<div style="background-color: aqua">Content Block</div>
</assemble:block></div>
</body>
</html>