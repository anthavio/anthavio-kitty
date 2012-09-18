<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<t:insertDefinition name="authenticated">

<t:putAttribute name="HtmlTitle" value="Kitty Setting"/>
<t:putAttribute name="PageTitle" value="Kitty Setting"/>

<t:putAttribute name="body">

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
	width: 8em;
	margin-right: 1em;  
}

</style>

<sf:form commandName='KittyOptions' method="post">
<sf:hidden path="initialDir"/>
<fieldset>
<legend>Kitty Options</legend>
<ol>
	<li>
		<sf:label path="scenarioPrefix" for="scenarioPrefix">Scenario prefix:</sf:label>
		<sf:input id="scenarioPrefix"  path="scenarioPrefix" size="14" />
		<sf:errors path="scenarioPrefix" element="label"/>
	</li>
	<li>
		<sf:label path="passedDir" for="passedDir">Passed directory:</sf:label>
		<sf:checkbox id="usePassedDir" path="usePassedDir" />
		<sf:errors path="usePassedDir" element="label"/>
		<sf:input id="passedDir"  path="passedDir" size="10" disabled="true"/>
		<sf:errors path="passedDir" element="label"/>
	</li>
	
	<li>
		<sf:label path="passedPause" for="passedPause">Passed pause:</sf:label>
		<sf:input id="passedPause"  path="passedPause" size="3" /> seconds
		<sf:errors path="passedPause" element="label"/>
	</li>
	
	<li>
		<sf:label path="failedDir" for="failedDir">Failed directory:</sf:label>
		<sf:checkbox id="useFailedDir" path="useFailedDir"/>
		<sf:errors path="useFailedDir" element="label"/>
		<sf:input id="failedDir"  path="failedDir"  size="10" disabled="true"/>
		<sf:errors path="failedDir" element="label"/>
	</li>
	
	<li>
		<sf:label path="failedPause" for="failedPause">Failed Pause:</sf:label>
		<sf:input id="failedPause"  path="failedPause" size="3" /> seconds
		<sf:errors path="failedPause" element="label"/>
	</li>
	
	<li>
		<sf:label path="saveExecs" for="saveExecs">Save executions:</sf:label>
		<sf:checkbox id="saveExecs" path="saveExecs" />
		<sf:errors path="saveExecs" element="label"/>
	</li>
	
	<li>
	<input type="submit" name="save" value="Save"/>
	</li>
</ol>
</fieldset>
</sf:form>

<fieldset title="${KittyTaskExecutor.class.name}">
<legend>Task Executor</legend>
<ol>
	<li><label>T.E. Core Size</label>${KittyTaskExecutor.corePoolSize}</li>
	<li><label>T.E. Max Size</label>${KittyTaskExecutor.maxPoolSize}</li>
	<li><label>T.E. Pool Size</label>${KittyTaskExecutor.poolSize}</li>
	<li><label>T.E. Active Count</label>${KittyTaskExecutor.activeCount}</li>
	<li><label>T.E. Queue</label>${KittyTaskExecutor.threadPoolExecutor.queue}</li>
</ol>
</fieldset>

</t:putAttribute>

</t:insertDefinition>