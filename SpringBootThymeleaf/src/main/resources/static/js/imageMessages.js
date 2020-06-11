
let imagePlaceholderSource = "http://aconsultant.ru/wp-content/uploads/2020/06/img_148071.png";
let fileIconSource = "http://aconsultant.ru/wp-content/uploads/2020/06/attach-file.png";
let loadingPlaceholderText = "Loading...";
let sendingPlaceholderText = "Sending...";
let uploadingPlaceholderText = "Uploading...";
let downloadingPlaceholderText = "Downloading...";

function drawMessage(message, datePlaceholder = null) {
	
	let side = getMessageSide(message);
	
	let messages = document.getElementById("messages");
	
	// Create message container
	let messageContainer = document.createElement("li");
	messageContainer.id = message.id;
	messageContainer.classList.add("message-container");
	
	// Define message category
	if (message.code == 1) {
		messageContainer.classList.add("image-message");
	} else if (message.code == 4) {
		messageContainer.classList.add("file-message");
	}
	
	// Store message milliseconds
	let msContainer = document.createElement("div");
	msContainer.classList.add("invisible");
	msContainer.classList.add("ms_container");
	msContainer.appendChild(document.createTextNode(message.milliseconds));
	messageContainer.appendChild(msContainer);
	
	// Store file path
	if (message.code == 1 || message.code == 4) {
		let pathContainer = document.createElement("div");
		pathContainer.classList.add("invisible");
		pathContainer.classList.add("path-text");
		pathContainer.appendChild(document.createTextNode(message.filePath));
		messageContainer.appendChild(pathContainer);
	}
	
	// Store file name
	/*if (message.code == 4) {
		let filenameContainer = document.createElement("div");
		filenameContainer.classList.add("invisible");
		filenameContainer.classList.add("filename");
		filenameContainer.appendChild(document.createTextNode(message.fileName));
		messageContainer.appendChild(filenameContainer);
	}*/
	
	// Create message box
	let messageBox = document.createElement("div");
	messageBox.classList.add("message-box");
	messageBox.classList.add("m-" + side);
	
	// Create image placeholder
	if (message.code == 1) {
		let img = document.createElement("img");
		img.src = imagePlaceholderSource;
		messageBox.appendChild(img);
	}
	
	// Create file icon
	if (message.code == 4) {
		let img = document.createElement("img");
		img.src = fileIconSource;
		messageBox.appendChild(img);
	}
	
	// Add message text
	if (message.code == 0 || message.code == null) {
		messageBox.appendChild(document.createTextNode(message.text));
	} else if (message.code == 4) {
		let filenameContainer = document.createElement("div");
		filenameContainer.classList.add("filename");
		filenameContainer.appendChild(document.createTextNode(message.fileName));
		messageBox.appendChild(filenameContainer);
	}
	
	// Append message box to a message container
	messageContainer.appendChild(messageBox);
	
	// Create date
	let dateText;
	if (datePlaceholder == null) {
		dateText = formatDate(new Date(message.milliseconds));
	} else {
		dateText = datePlaceholder;
	}
	let dateContainer = document.createElement("div");
	dateContainer.classList.add("date");
	dateContainer.classList.add(side);
	dateContainer.appendChild(document.createTextNode(dateText));
	messageContainer.appendChild(dateContainer);
	
	// Append to a message list
	messages.appendChild(messageContainer);
	scrollDown();
}


function updateMessageImageByBase64(id, base64) {
	
	let messageContainer = $("#" + id);
	messageBox = $(this).children(".message-box");
	img = messageBox.children("img");

	if (img.prop("src") == imagePlaceholderSource) {
	
		// Set source
		img.prop("src", "data:image/png;base64," + base64);
	}
}


function updateMessageImageByFile(id, file) {
	
	let fileReader = new FileReader();
	if (fileReader) {
		fileReader.onload = function () {
			
			let messageContainer = $("#" + id);
			messageBox = messageContainer.children(".message-box");
    		img = messageBox.children("img");
			
			if (img.prop("src") == imagePlaceholderSource) {
	
				// Set source
				img.prop("src", fileReader.result);
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
	let username = $("#userName").text();
	let contact = $("#contactName").text();
	let code;
	let filename = file.name;
	let ext = getFileExt(filename);
	
	if(jQuery.inArray(ext, ["exe","rar","zip"]) != -1) {
		// Not accepted
		
		showPopUp("Sorry, .exe and archives are not accepted.");
	
	} else {
		
		if (jQuery.inArray(ext, ["png","jpg","jpeg"]) != -1) {
			code = 1; // Send as image
		} else {
			code = 4; // Send as file
		}
		
		// Create and draw message
		let message = new Message(username, contact, code, "", filename);
		drawMessage(message, uploadingPlaceholderText);
		//return;
		// Set uploaded image as a source
		if (code == 1) {
			updateMessageImageByFile(message.id, file);
		}
		
		// Send to the server
		let data = new FormData();
		data.append("file", file);
		data.append("contact", contact);
		data.append("id", message.id);
		data.append("code", message.code);
		data.append("milliseconds", message.milliseconds);
		
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


function updateMessageImages() {
	
	// Loop image messages
	let messageBox;
	let img;
	let path;
	let needToLoad = false;
	let pathData = {};
	$(".image-message").each(function() {
		
		messageBox = $(this).children(".message-box");
		img = messageBox.children("img");
		
		// Check if we still need to load it
		if (img.prop("src") == imagePlaceholderSource) {
			needToLoad = true;
			path = $(this).children(".path-text").text();
			pathData[path] = path;
		}
	});
	
    let url = "/get-files";
    let userJson = JSON.stringify(pathData);
	
    // Send ajax query
    if (needToLoad) {
	    $.ajax
	    ({
	    	type: "POST",
	        data: userJson,
	        url: url,
	        contentType: "application/json; charset=utf-8",
	        success: function(response)
	    	{
	        	$(".image-message").each(function(e) {
	        		
	        		messageBox = $(this).children(".message-box");
	        		img = messageBox.children("img");
	        		
	        		// Check if we need to update
	        		if (img.prop("src") == imagePlaceholderSource) {
	        			
	        			// Update image path
	        			path = $(this).children(".path-text").text();
	        			img.prop("src", "data:image/png;base64," + response[path]);
	        			
	        			setMessageDateText($(this));
	        		}
	        	});
	    	}	
	    });
    }
    scrollDown();
}