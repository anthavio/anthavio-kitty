<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<h1>User Page</h1>
<p>
This is a protected page. You can get to me if you've been remembered, or if you've authenticated this session.
</p>

<ss:authorize access="hasRole('ROLE_ADMIN')">
    You are a supervisor! You can therefore see the <a href="extreme/index.jsp">extremely secure page</a>.<br/><br/>
</ss:authorize>

<ss:authorize access="hasRole('ROLE_USER')">
    You are plain user in ROLE_USER role
</ss:authorize>

<h3>Properties obtained using &lt;sec:authentication /&gt; tag</h3>
<table border="1">
<tr><th>Tag</th><th>Value</th></tr>
<tr>
<td>&lt;sec:authentication property='name' /&gt;</td><td><ss:authentication property="name"/></td>
</tr>
<tr>
<td>&lt;sec:authentication property='principal.username' /&gt;</td><td><ss:authentication property="principal.username"/></td>
</tr>
<tr>
<td>&lt;sec:authentication property='principal.enabled' /&gt;</td><td><ss:authentication property="principal.enabled"/></td>
</tr>
<tr>
<td>&lt;sec:authentication property='principal.authorities' /&gt;</td><td><ss:authentication property="principal.authorities"/></td>
</tr>
<tr>
<td>&lt;sec:authentication property='principal.accountNonLocked' /&gt;</td><td><ss:authentication property="principal.accountNonLocked"/></td>
</tr>
</table>

<a href="<c:url value='/'/>">Home</a>
<a href="<c:url value='/ui/logout'/>">Logout</a>
