<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" session="false"%>
<%@ include file="taglibs.jsp" %>

<tiles:insertDefinition name="anonymous">
<tiles:putAttribute name="title" value="Přihlášení"/>
<tiles:putAttribute name="body">

<script type="text/javascript" src="<c:url value="/resources/jquery/1.4/jquery.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/jquery/validate/jquery.validate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/messages_cs.js"/>"></script>
<script type="text/javascript">

$(document).ready(function() {
	
	$("#LoginForm").validate({
		rules: {
			"j_username": {
				required: true,
				minlength: 5
			},
			"j_password": {
			  required: true,
			  minlength: 5
			}
		},
		errorPlacement: function(error, element) {
			if ( element.is(":radio") )
				error.appendTo( element.parent().next().next() );
			else if ( element.is(":checkbox") )
				error.appendTo ( element.next() );
			else
				error.appendTo( element.parent().prev() );
		}
	});
	
	$('input[name=j_username]').focus();
	
});

</script>


<style TYPE="text/css">
.errormessage {
   color:red;
}
.successmessage {
	color:green;
}

</style>
	

    <h1>Login</h1>

    <c:if test="${not empty param.authfailed}">
      <span id="infomessage" class="errormessage" >
				Login failed due to: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
    	</span>
    </c:if>
    
    <c:if test="${not empty param.errusername}">
      <span id="infomessage" class="errormessage" >
				Login failed due to incorrect username
    	</span>
    </c:if>
    
    <c:if test="${not empty param.errpassword}">
      <span id="infomessage" class="errormessage" >
				Login failed due to incorrect password
    	</span>
    </c:if>

		<c:if test="${not empty param.newpassword}">
		    <span id="infomessage" class="errormessage" >
		    Login failed due to expored password
		    </span>
		</c:if>
		<c:if test="${not empty param.acclocked}">
		    <span id="infomessage" class="errormessage" >
		    Login failed due to locked account
		    </span>
		</c:if>
		<c:if test="${not empty param.accdisabled}">
		    <span id="infomessage" class="errormessage" >
		    Login failed due to disabled account
		    </span>
		</c:if>
    
		<c:if test="${not empty param.loggedout}">
			<span id="infomessage" class="successmessage">
				You have been successfully logged out.
			</span>
		</c:if>

    <form id="LoginForm" name="LoginForm" action="<c:url value='/ui/j_spring_security_check'/>" method="POST">
    	<fieldset>
    		<legend>Authentication</legend>
    		<ol>
    			<li>
    				<label class="flabel" for="j_username">
    					<sp:message code='account.username'/>
    				</label>
    				<div class="fstatus"></div>
    				<div class="finput">
    					<input type="text" name="j_username" value="<c:if test='${not empty param.authfailed}'><c:out value='${SPRING_SECURITY_LAST_USERNAME}'/></c:if>"/>
    				</div>
    				
    			</li>
    			
    			<li>
    				<label class="flabel" for="j_password">
    					<sp:message code='account.password'/>
						</label>
						<div class="fstatus"></div>
    				<div class="finput">
    					<input type="password" name="j_password">
    				</div>
    			</li>
    			
    			<li>
    				<label class="flabel" style="width: 200px" for="_spring_security_remember_me">
    					<sp:message code='account.remember_me'/>
    				</label>
    				<input type="checkbox" name="_spring_security_remember_me">
    				<div class="finput">
    					
    				</div>
    			</li>
    		</ol>
    	</fieldset>
    	<fieldset class="fsubmit">
    		<input name="submit" type="submit" value="<sp:message code='login.label'/>">
    	</fieldset>
    </form>

</tiles:putAttribute>
</tiles:insertDefinition>