<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<tiles:insertDefinition name="authenticated">
<sp:message code='example.message' var="title"/>
<tiles:putAttribute name="title" value="${title}"/>
<tiles:putAttribute name="body">

<h1>Home Page</h1>


<a href="<c:url value='/ui/user'/>">Detail uživatele</a>
<br/>
<a href="<c:url value='/ui/entity/list'/>">Seznam entit</a>

</tiles:putAttribute>
</tiles:insertDefinition>