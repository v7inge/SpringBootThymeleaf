
function drawImageMessage(path, side, date, id = null) {
	
	let messages = document.getElementById("messages");
	let li = document.createElement("li");
	
	// Create message
	let messageContainer = document.createElement("div");
	messageContainer.classList.add("m");
	messageContainer.classList.add("m-" + side);
	messageContainer.classList.add("image_loading_message");
	
	// Create "loading" text
	let imageLoadingText = document.createElement("div");
	imageLoadingText.classList.add("image_loading_text");
	imageLoadingText.appendChild(document.createTextNode("Image loading..."));
	messageContainer.appendChild(imageLoadingText);
	
	// Create image
	let img = document.createElement("img");
	img.classList.add("invisible");
	messageContainer.appendChild(img);
	
	// Store message id
	let idContainer = document.createElement("div");
	idContainer.classList.add("invisible");
	idContainer.classList.add("id_container");
	if (id != null) { 
		idContainer.appendChild(document.createTextNode(id)); 
	}
	messageContainer.appendChild(idContainer);
	
	// Store image path
	let pathContainer = document.createElement("div");
	pathContainer.classList.add("invisible");
	pathContainer.classList.add("path_text");
	pathContainer.appendChild(document.createTextNode(path));
	messageContainer.appendChild(pathContainer);
	
	li.appendChild(messageContainer);
	
	// Create date
	let dateContainer = document.createElement("div");
	dateContainer.classList.add("date");
	dateContainer.classList.add(side);
	dateContainer.appendChild(document.createTextNode(formatDate(date)));
	li.appendChild(dateContainer);
	
	messages.appendChild(li);
	scrollDown();
}


function updateMessageImageByBase64(id, base64) {
	
	let messageContainer = findImageMessageById(id);

	let textPlaceholder = messageContainer.children(".image_loading_text");
	if (getText(textPlaceholder) != "") {
	
		// Set source
		let img = messageContainer.children("img");
		img.prop("src", "data:image/png;base64," + base64);
		img.removeClass("invisible");
		
		// Clear placeholder text
		textPlaceholder.contents().last()[0].textContent = "";
	}
}


function findImageMessageById(id) {
	
	let messageContainer = null;
	$(".image_loading_message").each(function(e) {
		
		let idContainer = $(this).children(".id_container");
		let idText = getText(idContainer);
		
		if (idText == ("" + id)) {
			messageContainer = $(this);
		}
	});
	return messageContainer;
}


function updateMessageImageByFile(id, file) {
	
	let fileReader = new FileReader();
	if (fileReader) {
		fileReader.onload = function () {
			
			let messageContainer = findImageMessageById(id);
			let textPlaceholder = messageContainer.children(".image_loading_text");
			if (getText(textPlaceholder) != "") {
	
				// Set source
				let img = messageContainer.children("img");
				img.prop("src", fileReader.result);
				img.removeClass("invisible");
				
				// Clear placeholder text
				textPlaceholder.contents().last()[0].textContent = "";
			}
	    }
	    fileReader.readAsDataURL(file);
	}
}


function chooseImage() {
	
	if ($("#contactName").text()=="") {
		return;
	}
	
	$("#image_input").click();
} 


function sendImage() {
	
	let file = document.getElementById("image_input").files[0];
	let ext = file.name.split(".").pop().toLowerCase();
	
	if(jQuery.inArray(ext, ["png","jpg","jpeg"]) == -1) {
		showPopUp("Sorry, only .jpg and .png files are accepted.");
	} else {
		
		// Firstly draw it
		let milliseconds = Date.now();
		drawImageMessage("", "right", new Date(), "" + milliseconds);
		updateMessageImageByFile("" + milliseconds, file);

		// Send to the server
		let data = new FormData();
		data.append("file", file);
		data.append("contact", $("#contactName").text());
		data.append("milliseconds", milliseconds);
		
		$.ajax({
	        type: "POST",
	        url: "/send-image",
	        data: data,
	        processData: false,
	        contentType: false,
	        cache: false,
	        dataType: "json",
	        timeout: 1000000
	    });
	}
	
	document.getElementById("image_input").value = "";
}