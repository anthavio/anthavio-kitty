<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!--
<a href="?locale=en">English</a> | <a href="?locale=cs">Česky</a> |
-->
<ss:authorize access="hasRole('ROLE_AUTHENTICATED')">
Uživatel <a href="<c:url value='/ui/user'/>"><ss:authentication property="name"/></a>
<a href="<c:url value='/ui/logout'/>">Odhlásit</a>
</ss:authorize>
<ss:authorize ifAllGranted="ROLE_ANONYMOUS">
<a href="<c:url value='/ui/login'/>">Přihlásit</a>
</ss:authorize>