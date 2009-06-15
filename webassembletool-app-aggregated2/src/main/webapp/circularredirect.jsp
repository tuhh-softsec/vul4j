<%
int param=Integer.parseInt(request.getParameter("count"));
if(param > 0){
    response.sendRedirect("circularredirect.jsp?count="+(param-1));
}else{
%>
<!--$beginblock$myblock$-->Circular redirect OK<!--$endblock$myblock$-->
<%} %>
