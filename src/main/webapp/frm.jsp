<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CC Screener</title>
</head>
<body>
<jsp:useBean id="frm" class="jbossews.FormBean" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="frm"/>


<form action="frm.jsp" method="post">
<table>

<tr><td>
Hide strike price below current:<input type="checkbox" name="noStrikeBelowCurrent" value="Y">
Show only the first quote:<input type="checkbox" name="unique" value="Y">
</td></tr>
<tr><td>
Expary month (YYYYMM)<input type="text" name="expMonth">
</td></tr>
<tr><td>
<textarea rows="3" cols="30" name="symbLst"></textarea>
</td></tr>
<tr><td>
<input type="submit"/>
</td></tr>
</table>
</form>
<c:if test="${frm.ready}">
	<a href="result.jsp">Download Worksheet</a>
</c:if>
<p/>
<a href="logout.jsp">Logout</a>
${frm.msg }
</body>
</html>