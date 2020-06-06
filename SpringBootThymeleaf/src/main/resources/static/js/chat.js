
function test() {
	

	
}



function onLoad() {
	connect();
}


function connect() {
	
	let sender = $("#userName").text();
	var socket = new SockJS("/chat-messaging");
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function (frame) {
		
		stompClient.subscribe("/user/queue/reply", function (data) {
			
			var message = JSON.parse(data.body);
			
			let activeContact = $("#contactName").text();
			let username = $("#userName").text();
			
			// Broadcast incoming message
			if (message.sender == username && message.reciever == activeContact || message.sender == activeContact) {
				
				// Active dialogue
				if (message.code == 2) {
					updateMessageImages();
				} else {
					drawMessage(message);
				}
				
			} else if (message.sender != username && message.code != 1) {
				
				// Inactive dialogue
				increaseCounter(message.sender);
			}
			
		});
	});
}


function disconnect(){
	stompClient.disconnect();
}


function formatDate(date) {

	  let day = date.getDate();
	  if (day < 10) day = "0" + day;

	  let month = date.getMonth() + 1;
	  if (month < 10) month = "0" + month;

	  let year = date.getFullYear();

	  let hour = date.getHours();
	  
	  let minute = date.getMinutes();
	  if (minute < 10) minute = "0" + minute;
	  
	  return day + "." + month + "." + year + " " + hour + ":" + minute;
}


function resetCounter(str) {

	let el = findContact(str);
	if (el != null) {
	
		let counterElement = $(el).children(".counter");

		if(counterElement.hasClass("invisible") == false) {
			counterElement.toggleClass("invisible");
		}

		counterElement.text("");

	};
}


function findContact(str) {

	let contact = null;

	$(".contact").each(function (index, el) {

		if (getContactName(el) == str) {
			contact = el;	
			return false;
		}

	});

	return contact;
}


function getCurrentContactName() {
	
}


function getContactName(el) {
	return $(el).children(".contact-name").text();	
}


function getContactCounter(el) {
	let counterElement = $(el).children(".counter");
	let counterNumber = Number(counterElement.text());
	return counterNumber;
}


function increaseCounter(str, num = 1) {

	let el = findContact(str);
	if (el != null) {
	
		let counterElement = $(el).children(".counter");

		if(counterElement.hasClass("invisible")) {
			counterElement.toggleClass("invisible");
		}

		let counterNumber = Number(counterElement.text());
		counterElement.text(counterNumber + num);

	};
}


function sendMessage() {
	
	// Preparing data
	let text = $("#message_input_value").val();
	let sender = $("#userName").text();
	let reciever = $("#contactName").text();
	let message = {"text": text, "sender": sender, "reciever": reciever, "milliseconds" : Date.now()};
    let userJson = JSON.stringify(message);
	
    if ((reciever == "") || (text == "")) {
		return;
	}
    
	// Sending message
	//drawMessage(message);
	stompClient.send("/app/message-flow", {}, userJson);
	$("#message_input_value").val("");
}


function drawMessage(message) {
	
	let side = "";
	if (message.sender == $("#userName").text()) {
		side = "right";
	} else {
		side = "left";
	}
	
	let date = new Date(message.milliseconds);
	
	if (message.code == 1) {
		drawImageMessage(message.filePath, side, date);
	} else {
		drawTextMessage(message.text, side, date);
	}
}


function drawTextMessage(text, side, date) {
	
	let messages = document.getElementById("messages");
	let li = document.createElement("li");
	
	let m = document.createElement("div");
	m.classList.add("m");
	m.classList.add("m-" + side);
	m.appendChild(document.createTextNode(text));
	li.appendChild(m);
	
	let d = document.createElement("div");
	d.classList.add("date");
	d.classList.add(side);
	d.appendChild(document.createTextNode(formatDate(date)));
	li.appendChild(d);
	
	messages.appendChild(li);
	scrollDown();
}


function drawImageMessage(path, side, date) {
	
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
	
	// Store image path
	let pathText = document.createElement("div");
	pathText.classList.add("invisible");
	pathText.classList.add("path_text");
	pathText.appendChild(document.createTextNode(path));
	messageContainer.appendChild(pathText);
	
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


function outputMessageHistory(data) {
	
	let side = "";
	let mas = data.contactHistory;
	for (let i = 0; i !== mas.length; i += 1) {
		
		if (mas[i].sender == $("#userName").text()) {
			side = "right";
		} else {
			side = "left";
		}
		
		drawMessage(mas[i]);
	
	}
	
	updateMessageImages();
}


function updateMessageImages() {
	
	// Loop image messages
	let needToLoad = false;
	let pathData = {};
	$(".image_loading_message").each(function() {
		
		// Check if we still need to load it
		if ($(this).children(".image_loading_text").contents().last()[0].textContent != "") {
			needToLoad = true;
			let path = $(this).children(".path_text").text();
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
	        	$(".image_loading_message").each(function(e) {
	        		
	        		// Check if we need to update
	        		let textPlaceholder = $(this).children(".image_loading_text");
	        		if (textPlaceholder.contents().last()[0].textContent != "") {
	        			
	        			// Update image path
	        			path = $(this).children(".path_text").text();
	        			img = $(this).children("img");
	        			img.prop("src", "data:image/png;base64," + response[path]);
	        			img.removeClass("invisible");
	        			
	        			// Update text placeholder
	        			textPlaceholder.contents().last()[0].textContent = "";
	        		}
	        	});
	    	}	
	    });
    }
    scrollDown();
}


function chooseImage() {
	console.log("chooseImage");
	$("#image_input").click();
} 


function sendImage() {
	
	let file = document.getElementById("image_input").files[0];
	let ext = file.name.split(".").pop().toLowerCase();
	
	if(jQuery.inArray(ext, ["png","jpg","jpeg"]) == -1) {
		console.log("Sorry, only .jpg and .png files are accepted.");
	} else {
		
		let data = new FormData();
		data.append("file", file);
		data.append("contact", $("#contactName").text());
		data.append("milliseconds", Date.now());
		
		$.ajax({
	        type: "POST",
	        url: "/send-image",
	        data: data,
	        processData: false,
	        contentType: false,
	        cache: false,
	        dataType: "json",
	        timeout: 1000000,
	        success: function(response) {
	        	/*console.log("Success. Response: " + response);
	        	$("#menu-profile-img").attr("src", "data:image/png;base64," + response.base64String);
	        	$("#contact-profile-img").attr("src", "data:image/png;base64," + response.base64String);*/
	        }
	    });
	}
	
	document.getElementById("image_input").value = "";
}


function contactClick(contactElement) {

	// Counter
	let contactCounter = getContactCounter(contactElement);
	let needToResetCounter = (contactCounter != 0);
	
	let contactName = getContactName(contactElement);
	$("#contactName").text(contactName);

	// Changing styles and content
	resetCounter(contactName);
	contacts = $(".contact-active");
	contacts.removeClass();
	contacts.toggleClass("contact");
	contactElement.removeClass();
	contactElement.toggleClass("contact-active");
	$("#messages").html("");

	// Hide tip table
	tipTable = $(".tdcenter");
	tipTable.removeClass();
	tipTable.html("");

	// Filling data
	let userData = {"contact": contactName, "needToResetCounter": needToResetCounter};
    let url = "/contact-clicked";
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
    	}
    });
	
	scrollDown();
}


function scrollDown() {
	$("#messages").scrollTop($("#messages")[0].scrollHeight);
}


function contactInputChange() {

	$("#found_contacts").empty();
	
	let inputValue = $("#contact_input").val();
	if (inputValue == "") {
		$("#contacts").removeClass("invisible");
		$("#searching-tip").addClass("invisible");
		$("#found_contacts").addClass("invisible");
	} else {
		
		$("#contacts").addClass("invisible");
		$("#searching-tip").removeClass("invisible");
		$("#searching-tip").text("Searching...");
		
		// Filling data
		let userData = {"input": inputValue};
	    let url = "/contact-search";
	    let userJson = JSON.stringify(userData);
		
		$.ajax
	    ({
	        type: "POST",
	        data: userJson,
	        url: url,
	        contentType: "application/json; charset=utf-8",
	        success: function(data)
	    	{
	        	let mas = data.users;
	        	
	        	if (mas.length == 0) {
	        		$("#searching-tip").text("Nothing found");
	        	} else {
	        		$("#found_contacts").removeClass("invisible");
	        		for (let i = 0; i !== mas.length; i += 1) {	
	        			addFoundContact(mas[i]);	
	            	}
	        		$("#searching-tip").addClass("invisible");
	        	}
	        	
	    	}
	    });
	}
	
}


function addFoundContact(text) {

	let contactHtml = 
	'<li class = "contact found">' +
	'<div class = "name">' + text + '</div>' +
	'</li>';

	$("#found_contacts").append(contactHtml);
}


function addContact(text) {

	let contactHtml = 
	'<li class = "contact">' +
	'<div class = "name">' + text + '</div> \n' +
	'<div class = "counter invisible"></div>' +
	'</li>';

	$("#contacts").append(contactHtml);
}


function moveContactToTheList(text) {
	
	$("#found_contacts").empty();
	addContact(text);
	$("#contacts").removeClass("invisible");
	$("#searching-tip").addClass("invisible");
	$("#found_contacts").addClass("invisible");
	$("#contact_input").val("");
	
	// Filling data
	let userData = {"input": text};
    let url = "/contact-add";
    let userJson = JSON.stringify(userData);
	
	$.ajax
    ({
        type: "POST",
        data: userJson,
        url: url,
        contentType: "application/json; charset=utf-8",
        success: function(data) { }
    });
}


function updateAvatar() {
	
	let file = document.getElementById("avatar_input").files[0];
	let ext = file.name.split(".").pop().toLowerCase();
	
	if(jQuery.inArray(ext, ["png","jpg","jpeg"]) == -1) {
		console.log("Sorry, only .jpg and .png files are accepted.");
	} else {
		
		let data = new FormData();
		data.append("file", file);
		
		$.ajax({
	        type: "POST",
	        url: "/profile-picture",
	        data: data,
	        processData: false,
	        contentType: false,
	        cache: false,
	        dataType: "json",
	        timeout: 1000000,
	        success: function(response) {
	        	$("#menu-profile-img").attr("src", "data:image/png;base64," + response.base64String);
	        	$("#contact-profile-img").attr("src", "data:image/png;base64," + response.base64String);
	        }
	    });
	}
	
	document.getElementById("avatar_input").value = "";
}


$(document).ready(function() {
	
	$(document).keypress(function (e) {
	    if (e.which == 13) {
	    	sendMessage();
	    }
	});
	
	// Click on "send" button
	$("#send_button").click(function() {
		sendMessage();
    });

	// Click on contact
	$("body").on("click", ".contact", function () {
		contactClick($(this));
	});

	// Click on found contact
	$("body").on("click", ".found", function () {
		moveContactToTheList($(this).text());
	});
	
	// Click on profile
	$("#profile").click(function() {
		$(".menu").addClass("active");
	});

	// Click on avatar in menu
	$(".shadowRound").click(function() {
		$("#avatar_input").click();
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
	
	// Image file chosen
	$(document).on("change", "#image_input", function() {
		sendImage();		
	});
	
});