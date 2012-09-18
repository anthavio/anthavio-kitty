7CY7B-3M6YT-8F528<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<t:insertDefinition name="authenticated">
<sp:message code='example.message' var="title"/>
<t:putAttribute name="title" value="${title}"/>
<t:putAttribute name="body">

<h1>Home Page</h1>


<a href="<c:url value='/ui/user'/>">Detail uživatele</a>
<br/>
<a href="<c:url value='/ui/entity/list'/>">Seznam entit</a>

</t:putAttribute>
</t:insertDefinition>