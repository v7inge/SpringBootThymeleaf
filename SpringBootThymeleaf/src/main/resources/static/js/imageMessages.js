
let imagePlaceholderSource = "http://aconsultant.ru/wp-content/uploads/2020/06/img_148071.png";

function drawImageMessage(message, datePlaceholder = null) {
	
	let side = getMessageSide(message);
	let plaseholderText = "Placeholder text";
	
	let messages = document.getElementById("messages");
	
	// Create message container
	let messageContainer = document.createElement("li");
	messageContainer.id = message.id;
	messageContainer.classList.add("message-container");
	messageContainer.classList.add("image-message");
	
	// Store message milliseconds
	let msContainer = document.createElement("div");
	msContainer.classList.add("invisible");
	msContainer.classList.add("ms_container");
	msContainer.appendChild(document.createTextNode(message.milliseconds));
	messageContainer.appendChild(msContainer);
	
	// Store image path
	let pathContainer = document.createElement("div");
	pathContainer.classList.add("invisible");
	pathContainer.classList.add("path-text");
	pathContainer.appendChild(document.createTextNode(message.filePath));
	messageContainer.appendChild(pathContainer);
	
	// Create message box
	let messageBox = document.createElement("div");
	messageBox.classList.add("message-box");
	messageBox.classList.add("m-" + side);
	
	// Create image
	let img = document.createElement("img");
	img.src = imagePlaceholderSource;
	messageBox.appendChild(img);
	
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
	let ext = file.name.split(".").pop().toLowerCase();
	
	if(jQuery.inArray(ext, ["png","jpg","jpeg"]) == -1) {
		showPopUp("Sorry, only .jpg and .png files are accepted.");
	} else {
		
		// Create and draw message
		let message = new Message($("#userName").text(), $("#contactName").text(), 1);
		drawMessage(message, "Sending...");
		updateMessageImageByFile(message.id, file);

		// Send to the server
		let data = new FormData();
		data.append("file", file);
		data.append("contact", $("#contactName").text());
		data.append("id", message.id);
		data.append("milliseconds", message.milliseconds);
		//data.append("milliseconds", milliseconds);
		
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
	
    let url = "/get-images";
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
	        			
	        			// Update text placeholder
	        			//textPlaceholder.contents().last()[0].textContent = "";
	        		}
	        	});
	    	}	
	    });
    }
    scrollDown();
}