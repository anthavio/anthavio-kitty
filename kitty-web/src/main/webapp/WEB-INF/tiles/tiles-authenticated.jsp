<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title><t:getAsString name="HtmlTitle"/></title>
	<link rel="stylesheet" href="<c:url value='/res/style/style.css'/>" type="text/css"/>
 	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Pragma" content="no-cache"/>
	<meta http-equiv="Cache-Control" content="no-cache"/>
	<meta http-equiv="expires" content="0"/>
</head>
<body>

<div id="header">
<%--<t:insertAttribute name="header" />--%>
</div>

<div id="sidebar">
<div id="sidebar-wrapper">

	<div class="s-logo">
		<p class="r-logo"><a href="<c:url value="/"/>">Cotty</a></p>
	</div>

	<div class="box-simple">

		<div class="box-simple-top"></div>
		<div id="mainLoginArea" class="box-simple-content">
			<h2>Menu</h2>
			<t:insertAttribute name="menu" />
		</div>
		<div class="box-simple-bottom"></div>
	</div>
	
	<div class="sidebar-bottom"></div>
</div>
</div>


<div id="clearer"></div>


<div id="content">
<div id="content-wrapper">
	
	<div class="box">

		<h2><t:getAsString name="PageTitle"/></h2>

 		<div class="top-corners">
 			<div></div>
 		</div>

	  <div class="box-content">
	  	<div class="box-content-2">
	  	<div class="box-content-5">
		  	<t:insertAttribute name="body" />
		  	<div class="box-bottom"></div>
		  </div>
		  </div>
	  	<div class="bottom-corners">
	  	<div></div>
			</div>
		</div>
		 
	</div>

</div>
</div>

<div class="clearer"></div>

<div id="footer">
	<%--<t:insertAttribute name="footer" />--%>
	<p>
		datum: <c:out value="${VersionInfo.BuildDate}" />
		verze: <c:out value="${VersionInfo.BuildVersion}" />

	</p>
</div>

</body>
</html>