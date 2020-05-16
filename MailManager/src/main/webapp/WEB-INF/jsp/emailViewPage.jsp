<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="cs-cz">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		
		<link rel="stylesheet" type="text/css" href="style//emailViewPage.css"/>
		
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    	<script src="javaScript//emailViewPage.js" type="text/javascript"></script>
		
		<link rel="shortcut icon" href="image//favicon.png" type="image/png"/>
		<title>Doručené - Seznam.cz</title>
	</head>

	<body id="mainBody">
	
		<!-- Navigační menu -->	
	    <div id="navigation">

	        <a href=" <c:url value="/novy" /> " id="newEmail">Nový email</a>
	
			<!-- Zobrazené emaily -->
			<div id="messageCountHidden">${messageCount}</div>
			<div id="messageCount"></div>
	
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
	
		<!-- Seznam emailů -->
	    <div id="emailList">
			
			<c:forEach var="email" items="${emails}">
				
				<!-- Kontejner emailu -->
				<div id="emailContainer">
					<table>
						<tr>
							<td>
								<div id="index"></div>
								<div id="isSeen">${email.isSeen}</div>
							</td>
							
							<td>
								<div id="sender">${email.from}</div>
								<div id="subject">${email.subject}</div>
								<div id="sentDate">${email.sentDate}</div>
								<div id="sentTime">${email.sentTime}</div>
							</td>
						</tr>
					</table>
				</div>
				
			</c:forEach>

			<!-- Načítání -->
			<div id="loaderContainer">
				<div id="loader"></div>
			</div>

	    </div>

		<!-- Obsah emailu -->
	    <div id="emailContent">
			<table>
	    		<tr>
	    			<td colspan="2">
	    				<div id="subject"></div>
	    			</td>
	    		</tr>
	    		
	    		<tr>
	    			<td id="sender">
	    				<div>Odesilatel:&nbsp;&nbsp;</div>
	    			</td>
	    			
	    			<td id="senderValue"></td>
	    		</tr>
	    		
	    		<tr>
	    			<td id="recipient">
	    				<div>Příjemce:&nbsp;&nbsp;</div>
	    			</td>
	    			
	    			<td id="recipientValue"></td>
	    		</tr>
	    		
	    		<tr>
	    			<td id="copy">
	    				<div>Kopie:&nbsp;&nbsp;</div>
	    			</td>
	    			
	    			<td id="copyValue"></td>
	    		</tr>
	    		
	    		<tr>
	    			<td id="sentDate">
	    				<div>Odesláno:&nbsp;&nbsp;</div>
	    			</td>
	    			
	    			<td id="sentDateValue"></td>
	    		</tr>
	    		
	    		<tr id="attachedFileRow">
	    			<td id="attachedFile">
	    				<div>Příloha:&nbsp;&nbsp;</div>
	    			</td>
	    			
	    			<td id="attachedFileValue"></td>
	    		</tr>
	    		
	    		<tr>
	    			<td colspan="2">
	    				<div id="buttonPanel">
	    					
	    					<!-- Přesunout -->
	    					<button id="move">Přesunout
	    						<img src="image//caret_down.png">
	    					</button>
	    					
	    					<div id="dropdown">
	    						<div>Doručené</div>
	    						<div>Odeslané</div>
	    						<div>Rozepsané</div>
	    						<div>Hromadné</div>
	    						<div>Archiv</div>
	    						<div>Spam</div>
	    						<div>Koš</div>
	    					</div>
	    					
	    					<!-- Smazat -->
	    					<button id="delete">Smazat</button>
	    					
	    					<!-- Odpověď -->
	    					<form id="replyForm" method="post" action="odpoved" hidden="hidden">
	    						<input id="detailIndex" name="detailIndex" hidden="hidden">
	    						<input id="folderName" name="folderName" hidden="hidden">
	    					</form>
	    					
	    					<button id="reply">Odpovědět</button>
	    					
	    					<!-- Odpověď všem -->
	    					<form id="replyAllForm" method="post" action="odpoved-vsem" hidden="hidden">
	    						<input id="detailIndex" name="detailIndex" hidden="hidden">
	    						<input id="folderName" name="folderName" hidden="hidden">
	    					</form>
	    					
	    					<button id="replyAll">Odpovědět všem</button>
	    					
	    					<!-- Pčeposlat -->
	    					<form id="resendForm" method="post" action="preposlat" hidden="hidden">
	    						<input id="detailIndex" name="detailIndex" hidden="hidden">
	    						<input id="folderName" name="folderName" hidden="hidden">
	    					</form>
	    					
	    					<button id="resend">Přeposlat</button>
	    					
	    				</div>
	    			</td>
	    		</tr>
	    	</table>
	    	
			<div id="content"></div>
			
			<!-- Tlačítko pro vyskrolování -->
			<div id="backToTop">
				<div id="arrowColor"></div>
				<img src="image//arrow_top.png">
			</div>
			
	    </div>
	    
	</body>
</html>