<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="cs-cz">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		
		<link rel="stylesheet" type="text/css" href="style//loginPage.css"/>
		
		<link rel="shortcut icon" href="image//favicon.png" type="image/png"/>
		<title>Mail Manager</title>
	</head>
	
	<body>
	
		<!-- Přihlašovací formulář -->
		<form:form method="post" id="loginForm" action="dorucene" modelAttribute="user">
			
			<!-- Logo -->
			<div id="logo">
				<div id="rhombus" class="backgroundColor"></div>
				<img src="image//logo.png">
				
				<div id="rectangle"></div>
				<div id="triangle"></div>
				<span>Mail Manager</span>
			</div>
			
			<!-- Seznam Link -->
			<a href="https://www.seznam.cz" id="seznam">
				<img src="image//seznam.png">
			</a>
			
			<div id="login">Přihlášení: </div>
			
			<table>
				<tr>
					<td>Email:</td>
					
					<td>
						<form:input path="email" autofocus="autofocus"/>
					</td>
					
					<td>
						<div id="emailServer">@seznam.cz</div>
						<form:input path="emailServer" value="@seznam.cz" hidden="true"/>
					</td>
				</tr>
				
				<tr>
					<td>Heslo:</td>
					
					<td>
						<form:password path="password" />
					</td>
				</tr>
			</table>
			
			<button type="submit">Přihlásit</button>
		</form:form>
	
	</body>
</html>