<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<jsp:useBean id="frm" class="jbossews.FormBean"></jsp:useBean>
<jsp:setProperty property="*" name="frm"/>
<form action="frm.jsp" method="post">
<textarea rows="3" cols="30" name="symbLst"></textarea>
<input type="submit"/>
</form>
${frm.symbLst }
</body>
</html>