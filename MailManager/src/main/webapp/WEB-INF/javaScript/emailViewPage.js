$(function() {
	
	var detailIndex;
	
	// Aktuální složka (dorucene, odeslane, rozepsane ...)
	var currentFolder = location.pathname.split('/')[2];
	
	if (!currentFolder) {
		
		currentFolder = "dorucene";
	}
	
	// Odstranění zvýrazněné složky
	$("#navigation table tr td").each(function() {
		
		$(this).removeClass("active");
	});
	
	// Nastavení nové zvýrazněné složky
	switch (currentFolder) {
	
		case "dorucene":
			$("#navigation #inbox").addClass("active");
			break;
			
		case "odeslane":
			$("#navigation #sent").addClass("active");
			break;
			
		case "rozepsane":
			$("#navigation #drafts").addClass("active");
			break;
			
		case "hromadne":
			$("#navigation #newsletters").addClass("active");
			break;
			
		case "archiv":
			$("#navigation #archive").addClass("active");
			break;
			
		case "spam":
			$("#navigation #spam").addClass("active");
			break;
			
		case "kos":
			$("#navigation #trash").addClass("active");
			break;
	}
	
// Nastavení indexu + zvýraznění nepřečtených zpráv /////////////////////////////////////////////////
	
	$.fn.completingEmailsList = function() {
		
		var index = 1;
	    $("div#emailContainer").each(function() {
	    	
	    	$(this).find("#index").empty();
	        $(this).find("#index").append(index + ".");
	        index++;
	        
	        // Zvýraznění nepřečtených emailů
	        if ($(this).find("#isSeen").text().includes("false")) {
	        	
	        	$(this).find("table").css("border-left-color", "#B22222");
	        }
	    });
	}  
	
	$.fn.completingEmailsList();
	
// Aktulní počet zobrazených emailů /////////////////////////////////////////////////////////////////
    
    $.fn.displayCurrentMessageCount = function() {
		
    	var lastIndex = parseInt($("div#emailContainer #index:last").html());
    	var messageCount = $("#messageCountHidden").text();
    	
    	// Ošetření nulové hodnoty
    	if (!lastIndex || !messageCount) {
    		
    		lastIndex = 0;
    		messageCount = 0;
    	}
    	
    	$("#messageCount").empty().append(lastIndex + " / " + messageCount);
        
        // Odstranění načítání, pokud je ve složce málo emailů
        if (lastIndex < 15) {

        	$("#loaderContainer").remove();
        }
	}
    
    $.fn.displayCurrentMessageCount();
    
// Přesunutí emailu do jiné složky //////////////////////////////////////////////////////////////////
    
    $("#buttonPanel #dropdown").children().on("click", function() {
		
    	var folder;
		switch ($(this).text()) {
			
			case "Doručené":
				folder = "INBOX";
				break;
			
			case "Odeslané":
				folder = "sent";
				break;
				
			case "Rozepsané":
				folder = "drafts";
				break;
				
			case "Hromadné":
				folder = "newsletters";
				break;
				
			case "Archiv":
				folder = "archive";
				break;
				
			case "Spam":
				folder = "spam";
				break;
				
			case "Koš":
				folder = "trash";
				break;
		}
		
		$.ajax({
			
			url: currentFolder + "/presun",
			type: "POST",
			data: {"detailIndex": detailIndex,
				   "folder": folder},
			dataType: "json",
			complete: function() {
				
				// Odstranění emailu ze seznamu
				var emailContainer = $("#emailList #emailContainer");
				emailContainer.eq(detailIndex).remove();
				
				// Skrytí obsahu emailu
				$("#emailContent").css("display", "none");
				
				// Dekrementace indexu emailů
				for (detailIndex; detailIndex < $("#emailList #emailContainer").length + 1; detailIndex++) {
					
					var emailIndex = parseInt(emailContainer.eq(detailIndex).find("#index").text());
					emailIndex--;
					
					emailContainer.eq(detailIndex).find("#index").text(emailIndex + ".");
				}
				
				// Snížení celkového počtu emailů
				var messageCount = $("#messageCountHidden").text();
				messageCount--;
				
				$("#messageCountHidden").text(messageCount);
				
				// Aktulní počet zobrazených emailů
				$.fn.displayCurrentMessageCount();
				
				var messageCount = $("#messageCountHidden").text();
				
				// Přidání načítání
				if (messageCount > 15 && !$("#emailList #loaderContainer").length) {
					
					$("#emailList").append("<div id='loaderContainer'>" +
					   					   		"<div id='loader'></div> " +
   					   					   "</div");
				}
			}
		});
	});
    
    // Rozbalení možností
    $("#buttonPanel #move").on("click", function(event) {
		
    	$("#buttonPanel #dropdown").toggle();
    	event.stopPropagation();
	})
	
	// Skryje možnosti, při kliknutí mimo
	$(document).on("click", function() {
		
		$("#buttonPanel #dropdown").hide();
	});
	
// Smazání emailu ///////////////////////////////////////////////////////////////////////////////////
    
	$("#buttonPanel #delete").on("click", function() {
		
		$.ajax({
			
			url: currentFolder + "/smazat",
			type: "POST",
			data: {"detailIndex": detailIndex},
			dataType: "json",
			complete: function() {
				
				// Odstranění emailu ze seznamu
				var emailContainer = $("#emailList #emailContainer");
				emailContainer.eq(detailIndex).remove();
				
				// Skrytí obsahu emailu
				$("#emailContent").css("display", "none");
				
				// Dekrementace indexu emailů
				for (detailIndex; detailIndex < $("#emailList #emailContainer").length + 1; detailIndex++) {
					
					var emailIndex = parseInt(emailContainer.eq(detailIndex).find("#index").text());
					emailIndex--;
					
					emailContainer.eq(detailIndex).find("#index").text(emailIndex + ".");
				}
				
				// Snížení celkového počtu emailů
				var messageCount = $("#messageCountHidden").text();
				messageCount--;
				
				$("#messageCountHidden").text(messageCount);
				
				// Aktulní počet zobrazených emailů
				$.fn.displayCurrentMessageCount();
				
				var messageCount = $("#messageCountHidden").text();
				
				// Přidání načítání
				if (messageCount > 15 && !$("#emailList #loaderContainer").length) {
					
					$("#emailList").append("<div id='loaderContainer'>" +
					   					   		"<div id='loader'></div> " +
   					   					   "</div");
				}
			}
		});
	});
	
// Nastavení názvu složky ///////////////////////////////////////////////////////////////////////////
	
	var folderName;
	
	$.fn.getFolderName = function() {
		
		switch (currentFolder) {
		
			case "dorucene":
				folderName = "INBOX";
				break;
				
			case "odeslane":
				folderName = "sent";
				break;
				
			case "rozepsane":
				folderName = "drafts";
				break;
				
			case "hromadne":
				folderName = "newsletters"
				break;
				
			case "archiv":
				folderName = "archive";
				break;
				
			case "spam":
				folderName = "spam";
				break;
				
			case "kos":
				folderName = "trash";
				break;
		}
	}
	
// Odpověď na email /////////////////////////////////////////////////////////////////////////////////

	$("#buttonPanel #reply").on("click", function() {
		
		$("#replyForm #detailIndex").val(detailIndex);
		
		// Nastavení názvu složky
		$.fn.getFolderName();
		
		$("#replyForm #folderName").val(folderName);
		
		$("#replyForm").submit();
	});
	
// Odpověď všem /////////////////////////////////////////////////////////////////////////////////////
	
	$("#buttonPanel #replyAll").on("click", function() {
		
		$("#replyAllForm #detailIndex").val(detailIndex);
		
		// Nastavení názvu složky
		$.fn.getFolderName();
		
		$("#replyAllForm #folderName").val(folderName);
		
		$("#replyAllForm").submit();
	});
	
// Přeposlat email //////////////////////////////////////////////////////////////////////////////////
	
	$("#buttonPanel #resend").on("click", function() {
		
		$("#resendForm #detailIndex").val(detailIndex);
		
		// Nastavení názvu složky
		$.fn.getFolderName();
		
		$("#resendForm #folderName").val(folderName);
		
		$("#resendForm").submit();
	});
	
// Zobrazení detailu emailu /////////////////////////////////////////////////////////////////////////
    
    var lastChoice;
	$(document).on("click", "div#emailContainer", function() {
		
		// Skrytí tlačítka smazat ve složce koš (seznam neumožňuje smazat email z koše)
		if (location.pathname.split('/')[2].includes("kos")) {
			
			$("#buttonPanel #delete").hide();
		}
		
		// Odstranění zvýraznění nepřečteného emailu
    	$(this).find("table").css("border-left-color", "transparent");
    	
    	// Skrytí předešlého obsahu
		$("#emailContent").css("display", "none");
		
		// Odstranění posledního zvýrazněného emailu
	    if (lastChoice) {
	    	
	  	  lastChoice.find("table").removeClass("activeEmail");
	    }
  
	    // Zvýraznění vybraného emailu
	    lastChoice = $(this);
	    $(this).find("table").addClass("activeEmail");
		
		// Vyprázdnění předchozího obsahu
		$("#emailContent #content").empty();
		$("#emailContent #attachedFileValue a").remove();
		
		// Index vybraného emailu
		detailIndex = $(this).find("#index").html();
		detailIndex--;
		
		$.ajax({
			
		    url : currentFolder + "/detail",
		    type : "POST",
		    data : {"detailIndex": detailIndex},
		    dataType: 'json',
			success: function(data) {
				
				// Nastavení hodnot detailu emailu
				$("#emailContent #senderValue").html(data.from);
				$("#emailContent #subject").html(data.subject);
				$("#emailContent #recipientValue").html(data.recipientsTO);
				$("#emailContent #copyValue").html(data.recipientsCC);
				$("#emailContent #sentDateValue").html(data.sentDate + "&nbsp;&nbsp;" + data.sentTime);
				
				if (data.attachedFiles != null) {
					
					// Zobrazí přílohu
					$("#emailContent #attachedFileRow").css("display", "table-row");
					
					for (var i = 0; i < data.attachedFiles.length; i++) {
						
						// Vytvoření odkazu na přiložené soubory
						$("#emailContent #attachedFileValue").append("<a target='_blank'></a>");
						$("#emailContent #attachedFileValue a:last").attr("href", "dorucene/priloha$" + i + "/" + data.attachedFiles[i].fileName);
						
						// Vytvoření obrázku podle formátu souboru
						$("#emailContent #attachedFileValue a:last").append("<img>");
						var path = "";
						
						if (data.attachedFiles[i].fileName.includes(".png")) {
							path = "image//png.png";
							
						} else if (data.attachedFiles[i].fileName.includes(".jpg")) {
							path = "image//jpg.png";
						
						} else if (data.attachedFiles[i].fileName.includes(".pdf")) {
							path = "image//pdf.png";
						
						} else if (data.attachedFiles[i].fileName.includes(".gif")) {
							path = "image//gif.png";
						
						} else if (data.attachedFiles[i].fileName.includes(".txt")) {
							path = "image//txt.png";
						
						} else if (data.attachedFiles[i].fileName.includes(".doc")) {
							path = "image//doc.png";
						
						} else if (data.attachedFiles[i].fileName.includes(".xls")) {
							path = "image//xls.png";
						
						} else if (data.attachedFiles[i].fileName.includes(".ppt")) {
							path = "image//ppt.png";
							
						} else if (data.attachedFiles[i].fileName.includes(".zip")) {
							path = "image//zip.png";
							
						} else path = "image//file.png";
						
						$("#emailContent #attachedFileValue img:last").attr("src", path);
						
						// Vytvoření názvu a velikosti souboru
						$("#emailContent #attachedFileValue a:last").append("<div>" + data.attachedFiles[i].fileName + "</div>");
						$("#emailContent #attachedFileValue a:last").append("<div>" + data.attachedFiles[i].fileSize + "</div>");
					}
					
				} else {
					
					// Skyje přílohu
					$("#emailContent #attachedFileRow").css("display", "none");
				} 
				
				// Zobrazení nového obsahu
				$("#emailContent #content").html(data.content);
				$("#emailContent").css("display", "block");
			}  
		});
	});
	
// Tlačítko pro přesunutí scrollbaru nahorů /////////////////////////////////////////////////////////
	
	$("#emailContent").scroll(function() {
		
		// Zobrazí / skryje scrollbar, podle jeho pozice
		if ($(this).scrollTop() > 300) {

			$(this).find("#backToTop").css("display", "block");
			
		} else $(this).find("#backToTop").css("display", "none");
	});
	
	// Animace vyscrollování nahorů
	$("#emailContent #backToTop").on("click", function() {
		
		$("#emailContent").animate({
			
			scrollTop: 0
			
		}, 500);
	});
	
// Načtení nových emailů ////////////////////////////////////////////////////////////////////////////
	
	$("#emailList").scroll(function() {
		
		if ($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight) {
			
			var lastIndex = parseInt($("div#emailContainer #index:last").html());
			
			$.ajax({
				
				url: currentFolder + "/nacteni",
				type: "POST",
				data: {"lastIndex": lastIndex},
				dataType: "json",
				success: function(data) {
					
					lastIndex++;
					
					// Odstranění načítání
					$("#loaderContainer").remove();
					
					// Vytvoření nových kontejnerů pro email
					for (var i = 0; i < data.length; i++) {
						
						var emailContainer = "<div id='emailContainer'>" +
											 	"<table>" + 
											 		"<tr>" + 
											 			"<td>" + 
											 				"<div id='index'>" + lastIndex++ + ".</div>" + 
											 				"<div id='isSeen'>" + data[i].isSeen + "</div>" + 
											 			"</td>" +
											 			
											 			"<td>" + 
											 				"<div id='sender'>" + data[i].from + "</div>" + 
											 				"<div id='subject'>" + data[i].subject + "</div>" + 
											 				"<div id='sentDate'>" + data[i].sentDate + "</div>" + 
											 				"<div id='sentTime'>" + data[i].sentTime + "</div>" + 
											 			"</td>" + 
											 		"</tr>" + 
											 	"</table>" + 
											 "</div>";
						
						$("#emailList").append(emailContainer);
					}
					
					// Zobrazí aktulní počet emailů
					$.fn.displayCurrentMessageCount();
					
			    	var messageCount = $("#messageCountHidden").text();
			    	lastIndex--;
			    	
			    	// Pokud nejsou načteny všechny emaily, zobrazí načítání
			    	if (lastIndex != messageCount) {
			    		
			    		$("#emailList").append("<div id='loaderContainer'>" +
			    									"<div id='loader'></div> " +
			    							   "</div");
			    	}
				}
			});
		}
	});
	
	
// Načtení nových emailu každou minutu //////////////////////////////////////////////////////////////
	
	setInterval(function() {
		
		// Datum doručení prvního emailu
		var sentDate = $("#emailContainer #sentDate:first").text() + " " + $("#emailContainer #sentTime:first").text();
		
		var messageCount = $("#messageCountHidden").text();
		
		// Ajax získání Listu nových emailů
		$.ajax({
			
			url: currentFolder + "/obnoveni",
			type: "POST",
			data: {"sentDate": sentDate,
				   "messageCount": messageCount},
			dataType: "json",
			success: function(data) {
				
				if (data.length > 0) {
					
					// Vytvoření nových kontejnerů pro email
					for (var i = 0; i < data.length; i++) {
						
						var emailContainer = "<div id='emailContainer'>" +
											 	 "<table>" + 
											 		 "<tr>" + 
											 			 "<td>" + 
											 				 "<div id='index'></div>" + 
											 				 "<div id='isSeen'>" + data[i].isSeen + "</div>" + 
											 			 "</td>" +
											 			
											 			 "<td>" + 
											 				 "<div id='sender'>" + data[i].from + "</div>" + 
											 				 "<div id='subject'>" + data[i].subject + "</div>" + 
											 				 "<div id='sentDate'>" + data[i].sentDate + "</div>" + 
											 				 "<div id='sentTime'>" + data[i].sentTime + "</div>" + 
											 			 "</td>" + 
											 		 "</tr>" + 
											 	 "</table>" + 
											  "</div>";
						
						$("#emailList").prepend(emailContainer);
					}
					
					// Nastavení indexu + zvýraznění nových emailů
					$.fn.completingEmailsList();
				}
			}
			
		});
		
	// Časový interval 1 minuta
	}, 60000); 
	
});