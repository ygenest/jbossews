<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<jsp:useBean id="frm" class="jbossews.FormBean" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="frm"/>
<c:if test="${frm.ready}">
	<c:redirect url="/result.jsp"></c:redirect>
</c:if>

<form action="frm.jsp" method="post">
<textarea rows="3" cols="30" name="symbLst"></textarea>
<input type="checkbox" name="noStrikeBelowCurrent">Show strike price below current
<input type="submit"/>
</form>
${frm.symbLst }
</body>
</html>