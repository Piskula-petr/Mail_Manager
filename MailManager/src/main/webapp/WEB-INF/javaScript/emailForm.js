$(function() {
	
	// Vymazání kopie při odpovědi na email
	if ($("#emailReply").text() == "Odpověď") {
		
		$("#emailForm #cc").val("");
		
	// Upravení stylů
	} else if ($("#emailReply").text() == "Odpověď všem") {
	
		$("#emailReply").css({"padding": "10px 10px",
							  "margin-left": "6px"});
		
	// Vymazání příjemce a kopie při přeposílání emailu
	} else if ($("#emailReply").text() == "Přeposlat") {
		
		$("#emailForm #to").val("");
		$("#emailForm #cc").val("");
	}
	
// Změna rozměrů obsahu, podle množství souborů /////////////////////////////////////////////////////
	
	$.fn.resizeContent = function() {
		
		// Jeden řádek
		if ($("#emailForm #files").height() == 94) {
			
			$("#emailForm #content").css("height", "62.5vh");
			
		// Dva řádky ("přidat soubor" na začátku)
		} else if ($("#emailForm #files").height() == 155) {
			
			$("#emailForm #content").css("height", "57.7vh");
			
		// Dva řádky
		} else if ($("#emailForm #files").height() == 188) {
			
			$("#emailForm #content").css("height", "55.1vh");
			
		// Default
		} else $("#emailForm #content").css("height", "65vh");
	}
	
// Získání celkové velikosti souborů ////////////////////////////////////////////////////////////////
	
	$.fn.getTotalSize = function() {
		
		var totalSize = 0;
		$("#files #fileSize").each(function() {
			
			totalSize = totalSize + parseInt($(this).text());
		});
		
		// Velikost v MB
		totalSize = totalSize / 1024;
		
		$("#emailForm #totalSize").empty();
		$("#emailForm #totalSize").append(totalSize.toFixed(1) + " MB");
		
		// Omezení velikosti na 25 MB
		if (totalSize > 25) {
			
			$("#emailForm #totalSize").css("color", "#B22222");
			$("#emailForm #sendEmail").css("display", "none");
			
		} else {
			
			$("#emailForm #totalSize").css("color", "#EEE");
			$("#emailForm #sendEmail").css("display", "block");
		}
	}
	
// Změna vybraného souboru //////////////////////////////////////////////////////////////////////////
	
	var fileIndex = 1;
	$(document).on("change", "#files label", function() {
		
		var input = $(this).find("input");
		
		// Smazání předchozího souboru
		$(this).find("span").remove();
		$(this).find("table").remove();
		
		if (input.val()) {
			
			// Název souboru
			var fileName = input[0].files[0].name;
			var path = "";
			
			// Vybrání miniatury podle koncovky souboru
			if (fileName.includes(".png")) {
				path = "image//png.png";
			
			} else if (fileName.includes(".jpg")) {
				path = "image//jpg.png";
			
			} else if (fileName.includes(".pdf")) {
				path = "image//pdf.png";
				
			} else if (fileName.includes(".gif")) {
				path = "image//gif.png";
				
			} else if (fileName.includes(".txt")) {
				path = "image//txt.png";
				
			} else if (fileName.includes(".doc")) {
				path = "image//doc.png";
				
			} else if (fileName.includes(".xls")) {
				path = "image//xls.png";
				
			} else if (fileName.includes(".ppt")) {
				path = "image//ppt.png";
				
			} else if (fileName.includes(".zip")) {
				path = "image//zip.png";
				
			} else path = "image//file.png"
			
			// Velikost souboru
			var fileSize = Math.round(input[0].files[0].size / 1024);
			
			// Přidání jednotky
			if (fileSize < 1000) {
				
				fileSize = fileSize + " kB";
				
			} else fileSize = Math.round(fileSize / 1024) + " MB";
			
			// Vytvoření tabůlky souboru
			$(this).append("<table>" +
					
						   		"<tr>" +
						   			"<td rowspan='2'>" +
						   				"<img src=" + path + ">" +
						   			"</td>" +
						   			"<td>" +
					   					"<div>" + fileName + "</div>" +
					   				"</td>" +
					   				"<td rowspan='2'>" +
					   					"<img id='remove' src='image//remove.png'>" +
					   				"</td>" +
						   		"</tr>" +
						   		
						   		"<tr>" +
						   			"<td>" +
						   				"<div id='fileSize'>" + fileSize + "</div>" +
						   			"<td>" +
						   		"</tr>" +
						   		
						   	"</table>");
			
		} else $(this).append("<span>+ Přidat soubor</span>");
		
		// Přidání nového inputu
		if (!$("#files label:last").text().includes("+ Přidat soubor")) {
			
			fileIndex++;
			$("#emailForm #files").append("<label id='fileLabel' for='file" + fileIndex + "'>" +
										  		"<span>+ Přidat soubor</span>" +
										  		"<input id='file" + fileIndex + "' type='file' name='files'" +
										  "</label>");
		}
		
		// Smazání souboru
		$(this).find("#remove").on("click", function() {
			
			$(this).closest("label").remove();
			
			// Získání celkové velikosti souborů
			$.fn.getTotalSize();
			
			// Změna rozměrů obsahu, podle množství souborů
			$.fn.resizeContent();
		});
		
		// Získání celkové velikosti souborů
		$.fn.getTotalSize();
		
		// Změna rozměrů obsahu, podle množství souborů
		$.fn.resizeContent();
	});
	
// Nastavení hodnoty inputu při odeslání formuláře //////////////////////////////////////////////////
	
	$("#emailForm #sendEmail").on("click", function() {
		
		$("#emailForm #emailTextarea").val($("#emailForm #content").html());
	})
	
// Přesunutí emailu do složky rozepsané, při kliknutí na navigační odkazy ///////////////////////////
	
	$("#navigation a").on("click", function() {
		
		// URL pro více requestů (/novy, /odpoved, /odpoved-vsem, /preposlat)
		var url = location.pathname.split("/")[2] + "/rozepsany";
		
		// Rozdílné umístění obsahu
		var content;
		
		// tag <form:textarea>
		if (url.includes("novy")) {
			
			content = $("#emailForm #content").val();
			
		// tag <div>
		} else content = $("#emailForm #content").html();
		
		// Ajax objekt
		var email = {
			"from": 			$("#emailForm #from").val(),
			"recipientsTO": 	$("#emailForm #to").val(),
			"recipientsCC": 	$("#emailForm #copy").val(),
			"subject": 			$("#emailForm #subject").val(),
			"content": 			content
		};
		
		$.ajax({
			
			url: url,
			type: "POST",
			data: email,
			dataType: "json",
			complete: function() {
				
				alert("Email byl přesunut do složky rozepsané.");
			}
		})
	});
	
});