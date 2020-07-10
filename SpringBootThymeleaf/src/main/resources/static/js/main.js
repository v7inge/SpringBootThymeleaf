
let imagePlaceholderSource = "https://aconsultant.ru/wp-content/uploads/2020/06/img_148071.png";
let fileIconSource = "https://aconsultant.ru/wp-content/uploads/2020/06/attach-file.png";
let uploadingPlaceholderText = "Uploading...";
let downloadingPlaceholderText = "Downloading...";

let userName;
let contactName;


class Message {
	
	constructor(sender, receiver, code, text, fileName = null) {
	  
		let milliseconds = Date.now();  
		  
		this.sender = sender;
		this.receiver = receiver;
		this.code = code;
		this.milliseconds = milliseconds;
		this.id = "" + sender[0] + receiver[0] + milliseconds;
		this.text = text;
		this.fileName = fileName;
		
		if (fileName == null) {
			this.filePath = null;
		} else {
			this.filePath = this.id + " " + fileName;
		}
	}
}


class Contact {
	
	constructor(name, base64, classLetter) {
	  
		this.name = name;
		this.base64 = base64;
		this.classLetter = classLetter;
	}
}


function onLoad() {
	
	userName = $("#userName").text();
	connect();
}


function disconnect(){
	stompClient.disconnect();
}


function connect() {
	
	var socket = new SockJS("/chat-messaging");
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function (frame) {
		
		stompClient.subscribe("/user/queue/reply", function (data) {
			
			var message = JSON.parse(data.body);
			broadcastMessage(message);
		});
	});
}


function broadcastMessage(message) {

	let blockName;
	if (message.sender == userName) {
		blockName = message.receiver;
	} else {
		blockName = message.sender;
	}
	
	if (messagesBlockIsEmpty(blockName)) {
		
		if (message.code != 1 && message.code != 4) {
			loadMessageHistory(blockName, message);
		}
		
	} else {
		broadcastMessageToExistingBlock(message);
	}
}


function broadcastMessageToExistingBlock(message) {
	
	if (message.code == 0 || message.code == null) {
		// We got some plain text
		
		if (message.sender != userName) {
			drawMessage(message);
		}
	
	} else if (message.code == 2) {
		// Image is ready

		updateMessageImages();

		if (message.sender == userName) {
			updateMessageDate(message);
		}

	} else if (message.code == 6) {
		// User was added as a contact
				
		let contact = new Contact(message.sender, message.text, message.filePath);
		createContact(contact, null, false, false);
		showPopUp("" + message.sender + " added you as a contact.");
				
	} else if (message.code == 5) {
		// File is ready

		updateMessageDate(message);

	} else if (message.code == 3) {
		// Plain text is delivered
					
		let messageContainer = $("#" + message.id);
		if (messageContainer.prop("id") == null) {
			drawMessage(message);
		} else {
			updateMessageDate(message);
		}

	} else if (message.code == 1 || message.code == 4) {
		// Check if we need to draw a message placeholder
					
		let messageContainer = $("#" + message.id);
		if (messageContainer.prop("id") == null) {
			drawMessage(message, uploadingPlaceholderText);
		}
	}

	///////////////////////////////////////////////////////////////
	// Check if we need to increase counter
	increaseCounterIfNecessary(message);
}


function drawMessage(message, datePlaceholder = null) {
	
	let side = getMessageSide(message);
	
	// Define messages block
	let messagesBlockName;
	if (message.sender == userName) {
		messagesBlockName = message.receiver;
	} else {
		messagesBlockName = message.sender;
	}
	let messages = document.getElementById("messages-" + messagesBlockName);
	
	// #refactor
	// If there is no messages block, we don't draw it. Maybe this check is not needed.
	/*if (messages == null) {
		return;
	}*/
	
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
	if (message.code == 0 || message.code == 3 || message.code == null) {
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


function sendMessage() {
	
	let text = $("#message_input_value").val();
	
	if ((contactName == "") || (text == "")) {
		return;
	}
	
	// Build and draw it
	let message = new Message(userName, contactName, 0, text);
	drawMessage(message, "Sending...");
	
	// Send message
	let userJson = JSON.stringify(message);
	stompClient.send("/app/message-flow", {}, userJson);
	$("#message_input_value").val("");
}


function sendFile() {
	
	let file = document.getElementById("file-input").files[0];
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
		let message = new Message(userName, contactName, code, "", filename);
		drawMessage(message, uploadingPlaceholderText);
		
		// Set uploaded image as a source
		if (code == 1) {
			updateMessageImageByFile(message.id, file);
		}
		
		// Send to the server
		let data = new FormData();
		data.append("file", file);
		data.append("contact", contactName);
		data.append("id", message.id);
		data.append("code", message.code);
		data.append("milliseconds", message.milliseconds);
		
		$.ajax({
	        type: "POST",
	        url: "/send-file",
	        data: data,
	        processData: false,
	        contentType: false,
	        cache: false,
	        dataType: "json",
	        timeout: 1000000
	    });		
	}
	
	document.getElementById("file-input").value = "";
}


function outputMessageHistory(data) {
	
	let side = "";
	let mas = data.contactHistory;
	for (let i = 0; i !== mas.length; i += 1) {
	
		if (mas[i].code == 1) {
			drawMessage(mas[i], "Loading...");
		} else {
			drawMessage(mas[i]);
		}
	}
	
	updateMessageImages();
}


function contactClick(contactElement) {

	let contactCounter = getContactCounter(contactElement);
	contactName = getContactName(contactElement);

	// Update contact list
	if (contactCounter != 0) {
		resetCounter(contactName);
	}
	contacts = $(".contact-active");
	contacts.removeClass();
	contacts.toggleClass("contact");
	contactElement.removeClass();
	contactElement.toggleClass("contact-active");
	
	// Hide tip table
	tipTable = $(".starting-tip");
	tipTable.removeClass();
	tipTable.html("");
	
	// Hide current message list
	let elems = $(".contact");
    let elemsTotal = elems.length;
    for (let i = 0; i < elemsTotal; ++i) {
		let cName = getContactName(elems[i]);
		$("#messages-"+cName).addClass("invisible");
	}

	// Show messages
    $("#messages-" + contactName).removeClass("invisible");

	// Show bottom panel
	$(".bottom_wrapper").removeClass("invisible");

	// Load message history
	loadMessageHistory(contactName);
}


function loadMessageHistory(contact, messageToBroadcast = null) {
	
	if (messagesBlockIsEmpty(contact)) {
		
		$(".chat-tip").removeClass("invisible");
		
		let userData = {"contact": contact};
	    let url = "/get-history";
	    let userJson = JSON.stringify(userData);
		
		$.ajax
	    ({
	        type: "POST",
	        data: userJson,
	        url: url,
	        contentType: "application/json; charset=utf-8",
	        success: function(data)
	    	{
	        	outputMessageHistory(data);
	        	
	        	if (messageToBroadcast) {
	        		
	        		// We don't know if the new message was already loaded. It depends on the connection speed. 
	        		// If it wasn't, we broadcast it. Else we just increase the counter. 
	        		if ($("#" + messageToBroadcast.id).length == 0) { 
	        			broadcastMessageToExistingBlock(messageToBroadcast);
	        		} else {
	        			increaseCounterIfNecessary(messageToBroadcast);
	        		}
	        	}
	        	
	        	scrollDown();
	        	$(".chat-tip").addClass("invisible");
	    	}
	    });
	}
}


function updateAvatar() {
	
	let file = document.getElementById("avatar_input").files[0];
	let ext = file.name.split(".").pop().toLowerCase();
	
	if(jQuery.inArray(ext, ["png","jpg","jpeg"]) == -1) {
		showPopUp("Sorry, only .jpg and .png files are accepted.");
	} else {
		
		showPopUp("Got it, your picture will be updated soon!");
		let data = new FormData();
		data.append("file", file);
		
		$.ajax({
	        type: "POST",
	        url: "/set-profile-picture",
	        data: data,
	        processData: false,
	        contentType: false,
	        cache: false,
	        dataType: "json",
	        timeout: 1000000,
	        success: function(response) {
	        	
	        	$("#menu-profile-img").removeClass("invisible");
	        	$("#contact-profile-img").removeClass("invisible");
	        	$("#menu-profile-letter").addClass("invisible");
	        	$("#contact-profile-placeholder").addClass("invisible");
	        	
	        	$("#menu-profile-img").attr("src", "data:image/png;base64," + response.base64String);
	        	$("#contact-profile-img").attr("src", "data:image/png;base64," + response.base64String);
	        }
	    });
	}
	
	document.getElementById("avatar_input").value = "";
}


function test() {
	sendTestQuery();
}


function sendTestQuery() {
	
	$.ajax({
        type: "POST",
        url: "/test",
        processData: false,
        contentType: false,
        cache: false,
        timeout: 1000000
    });
}


$(document).ready(function() {
	
	$(document).keypress(function (e) {
	    if (e.which == 13 && $("#message_input_value").is(":focus")) {
	    	sendMessage();
	    }
	});
	
	// Click on "send" button
	$("#send_button").click(function() {
		sendMessage();
    });

	// Click on contact
	$("body").on("click", ".contact", function () {
		if (!$(this).hasClass("found")) {
			contactClick($(this));
		}
	});

	// Click on found contact
	$("body").on("click", ".found", function () {
		moveContactToTheList($(this));
	});
	
	// Click on profile
	$("#profile").click(function() {
		$(".menu").addClass("active");
	});

	// Click on avatar in menu
	$(".shadowRound").click(function() {
		$("#avatar_input").click();
	});

	// Click on attach icon
	$("#attach").click(function() {
		chooseFile();
	});
	
	// Click on the file
	$("body").on("click", ".file-message .message-box", function () {
		
		let messageContainer = $(this).parent();
		let currentDate = messageContainer.children(".date").text();
		if (currentDate == uploadingPlaceholderText || currentDate == downloadingPlaceholderText) {
			
			showPopUp("Please wait until file is downloaded");
		
		} else {
			
			setMessageDateText(messageContainer, downloadingPlaceholderText);
			let path = messageContainer.children(".path-text").text();
			let filename = messageContainer.children(".message-box").children(".filename").text();
			getFile(path, filename, messageContainer);
		}
	});
	
	// Click on any space
	$(document).click(function(event) {
    if ($(event.target).closest(".menu").length
		|| $(event.target).closest(".profile").length
		|| $(event.target).closest("#avatar_input").length) {
			return;
		}
		$(".menu").removeClass("active");
	});
	
	// Avatar file chosen
	$(document).on("change", "#avatar_input", function() {
		updateAvatar();		
	});
	
	// File chosen
	$(document).on("change", "#file-input", function() {
		sendFile();		
	});
	
	// Click on message image
	$(document).on("click", ".image-message img", function() {
		
		showLightbox($(this));
	});
	
	// Click to hide a lightbox
	$(document).on("click", "#overlay", function() {

		event.preventDefault();
		hideLightbox();
	});
	
});