<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<t:insertDefinition name="authenticated">

<t:putAttribute name="HtmlTitle" value="Kitty ${BrowseDir.name}"/>
<t:putAttribute name="PageTitle" value="Kitty ${BrowseDir.name}"/>

<t:putAttribute name="body">

<script type="text/javascript" src="<c:url value='/res/jquery-1.7.2.js'/>"></script>
<!--
<script type="text/javascript" src="<c:url value='/res/jquery.bigframe.js'/>"></script>
<script type="text/javascript" src="<c:url value='/res/jquery.dimensions.js'/>"></script>
<script type="text/javascript" src="<c:url value='/res/jquery.tooltip.js'/>"></script>
-->

<script type="text/javascript">
$(document).ready(function(){
  $("#CheckAll").click(function(){
	  $("#Items input[type='checkbox']").attr('checked', $('#CheckAll').is(':checked'));   
  });
  /*
  $('#Items tr').tooltip({ 
	  delay: 0,
		showURL: false,
		left: 25
	});
  */
});
</script>

<h1>Browser</h1>

<c:forEach items="${BrowseParents}" var="item" varStatus="status">
	<a href="<c:url value='browse?path=${item.absolutePath}'/>"><c:out value="${item.name}"/></a> /
</c:forEach>

<h2>Scenarios</h2>

<form name="BrowseForm" action="tableAction" method="post">
<sf:hidden path="DirectoryModel.path"/>
<table id="Items">
<thead>
	<tr>
	<th></th>
	<th>Name</th>
	<th>Validate</th>
	<th>Execute</th>
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

<tr bgcolor="${BgColor}" title="<c:out value='${item.execution.errorMessage}'/>">
	<td>
	<sf:checkbox path="DirectoryModel.items[${status.index}].selected" />
	</td>
	<td>
	<c:if test="${item.file.directory}">
		[<a href="<c:url value='browse?index=${status.index}'/>"><c:out value="${item.file.name}"/></a>]
	</c:if>
	<c:if test="${item.file.file}">
		<a href="<c:url value='view?index=${status.index}'/>"><c:out value="${item.file.name}"/></a>
	</c:if>
	</td>
	<td>
		<a href="<c:url value='validate?index=${status.index}'/>">validate</a>
	</td>
	<td>
		<a href="<c:url value='execute?index=${status.index}'/>">execute</a>
	</td>
	<td><c:out value='${item.message}'/></td>
</tr>
</c:forEach>
<tr>
	<td><input type="checkbox" id="CheckAll"/></td>
	<td colspan="4">
		<input name="validate" type="submit" value="Validate Selected"/>
		<input name="execute" type="submit" value="Execute Selected"/>
		<input name="reset" type="submit" value="Reset All"/>
	</td>
</tr>
</table>
<jsp:include page="ext/browse_after.jsp"/>
</form>

</t:putAttribute>
</t:insertDefinition>