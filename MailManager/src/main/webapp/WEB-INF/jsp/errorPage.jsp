<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="cs-cz">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		
		<link rel="stylesheet" type="text/css" href="style//errorPage.css"/>
		
		<link rel="shortcut icon" href="image//favicon.png" type="image/png"/>
		<title>Stránka nenalezena</title>
	</head>
	
	<body>
	
		<div id="errorContainer">
			
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
			
			<!-- Google Link -->
			<a href="https://www.google.com" id="google">
				<img src="image//google.png">
			</a>
			
			<!-- Popis -->
			<div id="errorTitle">Chyba, stránka nenalezena</div>
			
			<div class="errorMessage">Zkontrolujte si správnost přihlašovacích údajů.</div>
			
			<div class="errorMessage">Při použítí Gmailu musíte mít aktivovaný přístup protokolu IMAP v nastavení účtu&nbsp;
				<a href="https://support.google.com/mail/answer/7126229?hl=cs">odkaz zde</a>
			</div>
			
			<div class="errorMessage">V případě že používáte dvoufázové ověřování, <br>
			 je nutné se do aplikace přihlašovat pomocí přístupového hesla.</div>
			
			<div class="errorMessage">Odkazy na vytvoření:&nbsp;
				<a href="https://napoveda.seznam.cz/cz/login/dvoufazove-overeni/postovni-programy-caldav-dvoufazove-overeni/">Seznam</a> &nbsp;
				<a href="https://support.google.com/mail/answer/185833?hl=cs">Google</a>
			</div>
			
			<!-- Odkaz pro znovupřihlášení -->
			<a href="<c:url value="/" />" id="backToLoginPage" >Opětovné přihlášení</a>
			
		</div>
	
	</body>
</html>