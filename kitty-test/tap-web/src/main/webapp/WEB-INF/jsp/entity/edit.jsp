<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<tiles:insertDefinition name="anonymous">
<sp:message code='example.message' var="title"/>
<tiles:putAttribute name="title" value="${title}"/>
<tiles:putAttribute name="body">

<script type="text/javascript" src="<c:url value="/resources/jquery/1.4/jquery.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/jquery/validate/jquery.validate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/jquery/validate/messages_${pageContext.response.locale}.js"/>"></script>

<script type="text/javascript" src="<c:url value='/resources/jqueryui/1.8/jquery.ui.core.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/jqueryui/1.8/jquery.ui.datepicker.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/jqueryui/1.8/i18n/jquery.ui.datepicker-${pageContext.response.locale}.js'/>"></script>

<link rel="stylesheet" href="<c:url value='/resources/jqueryui/1.8/themes/base/jquery.ui.core.css'/>" type="text/css" media="screen" />
<link rel="stylesheet" href="<c:url value='/resources/jqueryui/1.8/themes/base/jquery.ui.theme.css'/>" type="text/css" media="screen" />
<link rel="stylesheet" href="<c:url value='/resources/jqueryui/1.8/themes/base/jquery.ui.datepicker.css'/>" type="text/css" media="screen" />

<script type="text/javascript">

$(document).ready(function() {

	$("#EntityForm").validate({
		rules: {
			"datum": {
				required: true,
			},
			"text": {
			  required: true,
			  rangelength: [5,1000]
			}
		}
	});
	
	$.datepicker.setDefaults($.datepicker.regional['${pageContext.response.locale}']);
	$("#datum").datepicker();
});
</script>

<c:choose>
<c:when test="${entity.id != null}">
<h1>Upravit Entitu</h1>
</c:when>
<c:otherwise>
<h1>Přidat Entitu</h1>
</c:otherwise>
</c:choose>

<a href="<c:url value='/ui/entity/list'/>">Zpět na seznam</a>
<c:if test="${entity.id != null}" >
<a href="<c:url value='/ui/entity/${entity.id}'/>">Zpět na detail</a>
</c:if>

<sf:form id="EntityForm" commandName='entity' method='post'>

	<sf:errors />
	
<table>
<c:if test="${entity.id != null}" >
<tr>
	<th><sf:label path="id">ID</sf:label></th>
	<td>
		<sf:input path="id" readonly="true" />
		<sf:errors path="id" />
	</td>
</tr>
</c:if>
<tr>
	<th><sf:label path="datum">Datum</sf:label></th>
	<td>
		<sf:input id="datum" path="datum"/>
		<sf:errors path="datum"/>
	</td>
</tr>
<tr>
	<th><sf:label path="text">Text</sf:label></th>
	<td>
		<sf:textarea path="text" cols="50" rows="5"/>
		<sf:errors path="text"/>
	</td>
</tr>
<tr>
	<td colspan="2" style="text-align: center;">
		<input type="submit" name="ulozit" value="Uložit" />
		<c:if test="${entity.id != null}" >
		<input type="submit" name="smazat" value="Smazat" />
		</c:if>
	</td>
</tr>
</table>

</sf:form>

</tiles:putAttribute>
</tiles:insertDefinition>