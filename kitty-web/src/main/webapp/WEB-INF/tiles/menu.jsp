<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<ul>
  <li><a href="<c:url value="/"/>">Cotty home</a></li>
  <li><a href="<c:url value='/ui/scenario/browse'/>">Scenario browser</a></li>
  <!--
  <li><a href="<c:url value="/jmx/"/>">JMX</a></li>
  -->
  <c:if test="${scenarioExecutor.batchRunning == true}">
  <li><a href="<c:url value='/ui/scenario/batchCancel'/>">Stop batch</a></li>
  </c:if>
  <li><a href="<c:url value='/ui/report'/>">Report</a></li>
  <li><a href="<c:url value="/ui/setting"/>">Setting</a></li>
  <jsp:include page="ext/menu_after.jsp"/>
</ul>