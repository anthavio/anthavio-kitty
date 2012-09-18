<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<t:insertDefinition name="authenticated">

<t:putAttribute name="HtmlTitle" value="Kitty ${BrowseDir.name}"/>
<t:putAttribute name="PageTitle" value="Kitty ${BrowseDir.name}"/>

<t:putAttribute name="body">

<h1>Test is running</h1>

Test is running. Started <fmt:formatDate value="${DirectoryModel.started}" pattern="yyyy-MM-dd HH:mm:ss"/>

<br/>
<c:forEach items="${BrowseParents}" var="item" varStatus="status">
	<a href="<c:url value='browse?path=${item.absolutePath}'/>"><c:out value="${item.name}"/></a> /
</c:forEach>
<br/>

Use passed directory: <sf:checkbox path="Kitty.options.usePassedDir" disabled="true"/>
Use failed directory: <sf:checkbox path="Kitty.options.useFailedDir" disabled="true"/>

<table>
<thead>
<tr>
	<th>x</th>
	<th>Name</th>
	<th>Started</th>
	<th>Finished</th>
	<th>Message</th>
</tr>
</thead>
<c:forEach items="${DirectoryModel.items}" var="item" varStatus="status">
<c:choose>
	<c:when test="${item.execution.passed}">
		<c:set var="BgColor" value="adff2f"/>
	</c:when>
	<c:when test="${item.execution.failed}">
		<c:set var="BgColor" value="f08080"/>
	</c:when>
	<c:when test="${item.execution.running}">
		<c:set var="BgColor" value="ffd700"/>
	</c:when>
	<c:otherwise>
		<c:set var="BgColor" value="white"/>
	</c:otherwise>
</c:choose>
<tr bgcolor="${BgColor}" >
	<td>
	<c:if test="${item.selected}">x</c:if>
	</td>
	<td>
	<c:if test="${item.file.directory}">
		[<a href="<c:url value='browse?index=${status.index}'/>"><c:out value="${item.file.name}"/></a>]
	</c:if>
	<c:if test="${item.file.file}">
		<a href="<c:url value='view?index=${status.index}'/>"><c:out value="${item.file.name}"/></a>
	</c:if>
	</td>
	<td><fmt:formatDate value="${item.execution.started}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	<td><fmt:formatDate value="${item.execution.ended}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	<td>${item.message}</td>
</tr>
</c:forEach>
<tr>
	<td colspan="3">
		<a href="batchStop">Stop Batch</a>
	</td>
</tr>
</table>
<jsp:include page="ext/running_after.jsp"/>

</t:putAttribute>
</t:insertDefinition>