<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>

<tiles:insertDefinition name="anonymous">
<sp:message code='example.message' var="title"/>
<tiles:putAttribute name="title" value="${title}"/>
<tiles:putAttribute name="body">

<h1>Seznam Entit</h1>

<a href="<c:url value='/ui/entity/create/'/>">Vytvořit novou Entitu</a>

<sf:form id="CriteriaForm" commandName='criteria' action="search" method='post'>

	<sf:errors />
	
<table>
<tr>
	<th><sf:label path="id">ID</sf:label></th>
	<td>
		<sf:input path="id" />
		<sf:errors path="id" />
	</td>
</tr>
<tr>
	<th><sf:label path="dateCriteria.after">Datum Od</sf:label></th>
	<td>
		<sf:input id="dateCriteria.after" path="dateCriteria.after"/>
		<sf:errors path="dateCriteria.after"/>
	</td>
</tr>
<tr>
	<th><sf:label path="dateCriteria.before">Datum Do</sf:label></th>
	<td>
		<sf:input id="dateCriteria.before" path="dateCriteria.before"/>
		<sf:errors path="dateCriteria.before"/>
	</td>
</tr>
<tr>
	<th><sf:label path="textCriteria.value">Text</sf:label></th>
	<td>
		<sf:input path="textCriteria.value" />
		<sf:errors path="textCriteria.value"/>
	</td>
</tr>
<tr>
	<td colspan="2" style="text-align: center;">
		<input type="submit" value="Hledat" />
		<input type="reset" value="Zpět" />
		<input type="button" value="Vymazat" />
	</td>
</tr>
</table>

</sf:form>

<table>
	<thead>
		<tr>
			<th>ID</th>
			<th>Datum</th>
			<th>Text</th>
		</tr>
	</thead>
	<c:forEach items="${result.results}" var="item" varStatus="status">
		<tr>
			<td><a href="<c:url value='/ui/entity/${item.id}'/>">Detail
			${item.id}</a></td>
			<td><fmt:formatDate pattern="d.M.yyyy" value="${item.datum}" />
			</td>
			<td>${fn:replace(item.text, newLineChar, '<br/>')}</td>
		</tr>
	</c:forEach>
	
</table>

<pg:pager items="${result.total}" url="" export="currentPageNumber=pageNumber">
	<pg:index>
	
	<pg:page export="firstItem, lastItem">
		<div class="resultInfo">
		Zobrazeny výsledky <strong>${firstItem} až ${lastItem}</strong> z celkových <strong>${result.total}</strong>
		</div>
	</pg:page>
 
		<pg:first>
			<a href="${pageUrl}">[ První ]</a>
		</pg:first>

		<pg:pages>
			<c:choose>
			<c:when test="${pageNumber == currentPageNumber}"> 
				<b>${pageNumber}</b>
			</c:when>
			<c:otherwise>
				<a href="${pageUrl}">${pageNumber}</a>
			</c:otherwise>
			</c:choose>
		</pg:pages>

		<pg:last>
			<a href="${pageUrl}">[ Poslední ]</a>
		</pg:last>

	</pg:index>
</pg:pager>
	
</tiles:putAttribute>
</tiles:insertDefinition>