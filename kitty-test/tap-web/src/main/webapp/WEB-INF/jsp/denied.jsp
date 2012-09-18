<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@ page import="org.springframework.security.core.Authentication"%>

<html>
<head>
<title>Access Denied</title>
</head>

<body>
<h1>Sorry, access is denied</h1>

<p><%=request.getAttribute("SPRING_SECURITY_403_EXCEPTION")%></p>

<p><%=request.getAttribute("SPRING_SECURITY_LAST_EXCEPTION")%></p>
<p>
<%
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	if (auth != null) {
%> Authentication object as a String: <%=auth.toString()%><BR>
<BR>
<%
	}
%>
</p>
</body>
</html>