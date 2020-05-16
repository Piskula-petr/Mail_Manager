<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html lang="cs-cz">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		
		<link rel="stylesheet" type="text/css" href="style//emailReply.css"/>
		
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    	<script src="javaScript//emailForm.js" type="text/javascript"></script>
		
		<link rel="shortcut icon" href="image//favicon.png" type="image/png"/>
		
		<title>${replyType} - Seznam.cz</title>
	</head>
	
	<body>
	
		<!-- Navigační menu -->	
	    <div id="navigation">

	        <div id="emailReply">${replyType}</div>
	
	        <table>
	            <tr>
	                <td id="inbox" class="active">
	                	<a href=" <c:url value="/dorucene" /> ">Doručené</a>
	                </td>
	            </tr>
	
	            <tr>
	                <td id="sent">
	                	<a href=" <c:url value="/odeslane" /> ">Odeslané</a>
	                </td>
	            </tr>
	
	            <tr>
	                <td id="drafts">
	                	<a href=" <c:url value="rozepsane" /> ">Rozepsané</a>
	                </td>
	            </tr>
	
	            <tr>
	                <td id="newsletters">
	                	<a href=" <c:url value="/hromadne" /> ">Hromadné</a>
	                </td>
	            </tr>
	
	            <tr>
	                <td id="archive">
	                	<a href=" <c:url value="/archiv" /> ">Archiv</a>
	                </td>
	            </tr>
	
	            <tr>
	                <td id="spam">
	                	<a href=" <c:url value="/spam" /> ">Spam</a>
	                </td>
	            </tr>
	
	            <tr>
	                <td id="trash">
	                	<a href=" <c:url value="/kos" /> ">Koš</a>
	                </td>
	            </tr>
	        </table>
	    </div>

		<!-- Formulář nového emailu -->
		<form:form id="emailForm" method="post" action="odeslat" modelAttribute="email" enctype="multipart/form-data">
		
			<table id="emailFormTabel">
				<tr>
					<td id="header">Odesilatel: </td>
					
					<td>
						<form:input id="from" path="from" type="text" value="${from};"/>
					</td>
				</tr>
				
				<tr>
					<td id="header">Příjemce: </td>
					
					<td>
						<form:input id="to" path="recipientsTO" type="text" required="required" value="${to}"/>
					</td>
				</tr>
				
				<tr>
					<td id="header">Kopie: </td>
					
					<td>
						<form:input id="cc" path="recipientsCC" type="text" value="${cc}"/>
					</td>
				</tr>
				
				<tr>
					<td id="header">Předmět: </td>
					
					<td>
						<form:input id="subject" path="subject" type="text" value="${subject}"/>
					</td>
				</tr>
				
				<tr>
					<td id="header">Příloha: 
						<div id="totalSize">0.0 MB</div>
					</td>
				
					<td id="files">

						<label id="fileLabel" for="file1">
							<span>+ Přidat soubor</span>
							<input id="file1" type="file" name="files">
						</label>
						
					</td>
				</tr>
				
				<tr>
					<td colspan="2">
						<form:textarea id="emailTextarea" path="content" hidden="hidden"/>
						<div id="content" contentEditable="true">${content}</div>
					</td>
				</tr>
				
				<tr>
					<td id="submitButton" colspan="2">
						<button id="sendEmail" type="submit">Odeslat email</button>
					</td>
				</tr>
			</table>

		</form:form>

	</body>
</html>