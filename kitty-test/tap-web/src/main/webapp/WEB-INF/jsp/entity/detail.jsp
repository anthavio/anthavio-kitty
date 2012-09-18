<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<tiles:insertDefinition name="anonymous">
<sp:message code='example.message' var="title"/>
<tiles:putAttribute name="title" value="${title}"/>
<tiles:putAttribute name="body">

<h1>Detail Entity</h1>

<a href="<c:url value='/ui/entity/list'/>">Zpět na seznam</a>
	
<table>
<tr>
	<th>ID</th>
	<td>${entity.id}</td>
</tr>
<tr>
	<th>Datum</th>
	<td>${entity.datum}</td>
</tr>
<tr>
	<th>Text</th>
	<td>
		${fn:replace(entity.text, newLineChar, "<br/>")}
	</td>
</tr>
<tr>
	<td style="text-align: center;">
		<a href="<c:url value='/ui/entity/delete/${entity.id}'/>">Smazat</a>
	</td>
	<td style="text-align: center;">
		<a href="<c:url value='/ui/entity/update/${entity.id}'/>">Editovat</a>
	</td>
</tr>
</table>

</tiles:putAttribute>
</tiles:insertDefinition>