<%@page trimDirectiveWhitespaces="true"
            contentType="text/html; charset=utf-8"
            import="java.io.IOException,
                    java.io.ByteArrayOutputStream,
        java.io.OutputStream,
        javax.servlet.ServletContext,
        javax.servlet.http.HttpServlet,
        javax.servlet.http.HttpServletRequest,
        javax.servlet.http.HttpServletResponse"
 %>
<jsp:useBean id="frm" class="jbossews.FormBean" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="frm"/>
<%  
//Set the headers.
response.setContentType("application/x-download"); 
response.setHeader("Content-Disposition", "attachment; filename=data.csv");

ServletOutputStream sos=response.getOutputStream();
ByteArrayOutputStream outstream=frm.getOut();
byte[] buffer=outstream.toByteArray();
for (int i=buffer.length; i > 0; ) {
    int n=Math.min(i,buffer.length);
    i-=n;
    sos.write(buffer,0,n);
  }


sos.flush();
sos.close();
outstream.flush();
outstream.close();

%>