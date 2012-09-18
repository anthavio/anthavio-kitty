<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" session="false"%>
<%@ include file="taglibs.jsp" %>
 
<t:insertDefinition name="anonymous">
<t:putAttribute name="title" value="Dobrý den"/>
<t:putAttribute name="body">

	<ss:authorize access="hasRole('ROLE_AUTHENTICATED')">
	Přihlášený uživatel
	<br/>
	<a href="<c:url value='/ui/user'/>">Detail uživatele</a>
	<br/>
	<a href="<c:url value='/ui/logout'/>">Odhlásit</a>
	<br/>
	<a href="<c:url value='/ui/scenario/browse'/>">Browser</a>
	</ss:authorize>
	
	<ss:authorize access="hasRole('ROLE_ANONYMOUS')">
	Nepřihlášený uživatel
	<a href="<c:url value='/ui/login'/>">Přihlásit</a>
	</ss:authorize>

</t:putAttribute>
</t:insertDefinition>


