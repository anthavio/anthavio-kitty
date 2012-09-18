<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<t:insertDefinition name="authenticated">

<t:putAttribute name="HtmlTitle" value="Kitty Report"/>
<t:putAttribute name="PageTitle" value="Kitty Report"/>

<script type="text/javascript" src="<c:url value='/res/jquery-1.7.2.js'/>"></script>
<script type="text/javascript" src="<c:url value='/res/jquery-ui/jquery.ui.core.js'/>"></script>
<script type="text/javascript" src="<c:url value='/res/jquery-ui/jquery.ui.datepicker.js'/>"></script>

<link rel="stylesheet" href="<c:url value='/res/jquery-ui/themes/base/jquery.ui.core.css'/>" type="text/css" media="screen" />
<link rel="stylesheet" href="<c:url value='/res/jquery-ui/themes/base/jquery.ui.theme.css'/>" type="text/css" media="screen" />
<link rel="stylesheet" href="<c:url value='/res/jquery-ui/themes/base/jquery.ui.datepicker.css'/>" type="text/css" media="screen" />


<t:putAttribute name="body">
<script type="text/javascript">
	var $j = jQuery.noConflict();
	$j(document).ready(function() {
		$j.datepicker.setDefaults(
				$j.extend({showMonthAfterYear: false, firstDay: 1, dateFormat: 'yy-mm-dd'})
		);
		$j("#startDate").datepicker();
		$j("#endDate").datepicker();
	});
</script>

<style TYPE="text/css">
.errormessage {
   color:red;
}
.successmessage {
	color:green;
}

fieldset {
	float: left;        
	//clear: both;
	border: none;        
}

fieldset legend {
  font-weight: bold;
}

fieldset ol li {
	list-style: none;
	float: left;  
	clear: left;  
	width: 100%;  
}

fieldset label {
	float: left;  
	width: 5em;
	margin-right: 1em;  
}

fieldset#Statistic label{
	width: 8em;
}

</style>

<sf:form commandName='ReportCriteria' method="post">
<fieldset>
<legend>Criteria</legend>
<ol>
	<li>
		<sf:label path="startDate" for="startDate">Start Date</sf:label>
		<sf:input id="startDate" path="startDate" />
		<sf:errors path="startDate" element="label"/>
	</li>
	
	<li>
		<sf:label path="endDate" for="endDate">End Date</sf:label>
		<sf:input id="endDate" path="endDate" />
		<sf:errors path="endDate" element="label"/>
	</li>
	
	<li>
		<sf:label path="exeState" for="exeState">State</sf:label>
		<sf:select path="exeState">
			<sf:options items='${ExeStateValues}'/>
		</sf:select>
		<sf:errors path="exeState" element="label"/>
	</li>
	
	<li>
		<sf:button name="Display" value="Display">Display</sf:button>
		<sf:button name="Excel" value="Excel">Excel</sf:button>
		<sf:button name="Delete" value="Delete">Delete</sf:button>
	</li>
</ol>	
</fieldset>
</sf:form>

<fieldset id="Statistic">
<legend>Statistic</legend>
<ol>
	<li>
		<label>Total executions</label>
		<span>${Stats.totalExecuted}</span>
	</li>
	<li>
		<label>Total failed</label>
		<span>${Stats.totalFailed}</span>
	</li>
	<li>
		<label>Today executions</label>
		<span>${Stats.todayExecuted}</span>
	</li>
	<li>
		<label>Today failed</label>
		<span>${Stats.todayFailed}</span>
	</li>
</ol>
</fieldset>

<table style="width: 100%">
<thead>
	<tr>
	<th>Started</th>
	<th>Ended</th>
	<th>Directory</th>
	<th>File</th>
	<th>Failed Step</th>
	</tr>
</thead>
<tbody>
<c:forEach items="${Executions}" var="item" varStatus="status">
<c:choose>
	<c:when test="${item.passed}">
		<c:set var="BgColor" value="adff2f"/>
	</c:when>
	<c:when test="${item.failed}">
		<c:set var="BgColor" value="f08080"/>
	</c:when>
	<c:when test="${item.running}">
		<c:set var="BgColor" value="ffd700"/>
	</c:when>
	<c:otherwise>
		<c:set var="BgColor" value="white"/>
	</c:otherwise>
</c:choose>
<tr bgcolor="${BgColor}">
	<td>${item.started}</td>
	<td>${item.ended}</td>
	<td title="${item.file.parent}"><a href="<c:url value='scenario/browse?goTo=${item.file.parent}'/>">${item.file.parentFile.name}</a></td>
	<td title="${item.file}"><a href="<c:url value='scenario/view?file=${item.file.absolutePath}'/>">${item.file.name}</a></td>
	<td title="${item.errorMessage}">${item.errorStep}</td>
</tr>
</c:forEach>
</tbody>
</table>

</t:putAttribute>

</t:insertDefinition>