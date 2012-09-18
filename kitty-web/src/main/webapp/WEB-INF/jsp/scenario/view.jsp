<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<t:insertDefinition name="authenticated">

<t:putAttribute name="HtmlTitle" value="Kitty ${File.name}"/>
<t:putAttribute name="PageTitle" value="Kitty ${File.name}"/>

<t:putAttribute name="body">

<script src="<c:url value='/res/cm/codemirror.js'/>"></script>
<script src="<c:url value='/res/cm/xml.js'/>"></script>
<script src="<c:url value='/res/cm/htmlmixed.js'/>"></script>
<script src="<c:url value='/res/cm/closetag.js'/>"></script>
<script src="<c:url value='/res/cm/formatting.js'/>"></script>

<link rel="stylesheet" href="<c:url value='/res/cm/codemirror.css'/>">
<style type="text/css">
	.CodeMirror {
		border: 1px solid #eee;
		font-family: monospace;
		font-size: medium;
		cursor: auto;
	}
	.activeline {
		background: #c0ccff !important;
	}
	.CodeMirror-gutter {
		min-width: 2.6em;
		cursor: pointer;
	}
	.CodeMirror-scroll {
		height: auto;
		overflow-y: hidden;
		overflow-x: auto;
		width: 100%;
	}
</style>

<%--
<script src="<c:url value='/res/prettify/prettify.js'/>"></script>
<link rel="stylesheet" href="<c:url value='/res/prettify/prettify.css'/>">

<script type="text/javascript">
document.addEventListener('DOMContentLoaded',function() {
    prettyPrint();
});
</script>
--%>
<h1><c:out value='${File.name}'/></h1>

<c:forEach items="${BrowseParents}" var="item" varStatus="status">
	<a href="<c:url value='browse?path=${item.absolutePath}'/>"><c:out value="${item.name}"/></a> /
</c:forEach>
<br/>

<span style="color: red;">
<c:out value="${XmlError}"/>
</span>

<form action="save" method="post">
<input type="hidden" name="file" value="${File}">
<textarea id="ScenarioXml" name="scenarioXml">${ScenarioXml}</textarea>

<table>
  <tr>
    <td>
    	<input type="button" value="Format" onclick="javascript:autoFormatAll();">
    </td>
    <td>
    	<input type="button" value="Comment" onclick="javascript:commentSelection(true);">
    </td>
    <td>
    	<input type="button" value="Uncomment" onclick="javascript:commentSelection(false);">
    </td>
    <td>
      <input type="submit" name="save" value="Save">
      <input type="submit" name="cancel" value="Cancel">
    </td>
  </tr>
</table>
</form>
<!-- Eventuelne nahradit http://ace.ajax.org/ -->
<script>
	var editor = CodeMirror.fromTextArea(document.getElementById("ScenarioXml"), {
    //mode: {name: "xml", alignCDATA: false},
    mode: 'text/html',
    //mode: "xmlpure",
    indentWithTabs: true,
    lineNumbers: true,
    //lineWrapping: true,
    //wordWrapping: true,
    onCursorActivity: function() {
      editor.setLineClass(hlLine, null, null);
      hlLine = editor.setLineClass(editor.getCursor().line, null, "activeline");
    },
    closeTagEnabled: true,
    closeTagIndent: true,
		extraKeys: {
			"'>'": function(cm) { cm.closeTag(cm, '>'); },
			"'/'": function(cm) { cm.closeTag(cm, '/'); }
		}
	});
    

function getSelectedRange() {
  return { from: editor.getCursor(true), to: editor.getCursor(false) };
}

function autoFormatAll() {
	CodeMirror.commands["selectAll"](editor);
  var range = getSelectedRange();
  editor.autoFormatRange(range.from, range.to);
}

function commentSelection(isComment) {
  var range = getSelectedRange();
  editor.commentRange(isComment, range.from, range.to);
}

//var hlLine = editor.setLineClass(0, "activeline");
</script>

<table>
<thead>
	<tr class="headerRow">
		<td>Step name</td><td>Step fields</td>
	</tr>
</thead>
<c:set var="rowStyle" value="evenRow" />
<c:forEach items="${StepInfoList}" var="step" varStatus="status">
<c:set var="rowStyle" value="${rowStyle=='evenRow'?'oddRow':'evenRow'}" />
<tr class="${rowStyle}">
	<td>${step.name}</td>
	<td>
	<c:forEach items="${step.fields}" var="field" varStatus="status2">
	${field.name} ${field.type.name} <br/>
	</c:forEach>
	</td>
</tr>
</c:forEach>
</table>
</t:putAttribute>
</t:insertDefinition>