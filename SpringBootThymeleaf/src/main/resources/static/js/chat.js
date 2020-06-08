
class Message {

  constructor(sender, reciever, code, text) {
	  
	let milliseconds = Date.now();  
	  
	this.sender = sender;
	this.reciever = reciever;
	this.id = null;
	this.code = code;
	this.milliseconds = milliseconds;
	this.id = "" + sender[0] + reciever[0] + milliseconds;
	this.text = text;
  }

  /*sayHi() {
    alert(this.name);
  }*/

}

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
			
			if (message.sender == activeContact) {
				// Message from an active contact
				
				if (message.code == 2) {
					updateMessageImages();
				} else {
					drawImageMessage(message);
				}
				
			} else if (message.sender == username && message.reciever == activeContact) {
				// Message from user himself
				
				if (message.code == 3) {
					// Plain text is delivered
					
					let messageContainer = $("#" + message.id);
					if (messageContainer.prop("id") == null) {
						drawImageMessage(message);
					} else {
						updateMessageDate(message);
					}
						
				} else if (message.code == 2) {
					// Image is uploaded
					
					updateMessageImages(); // Needed only in case there's no file reader in the browser
					updateMessageDate(message);
				
				} else if (message.code == 1) {
					// Check if we need to draw a message placeholder
					
					let messageContainer = $("#" + message.id);
					if (messageContainer.prop("id") == null) {
						drawImageMessage(message, "Loading...");
					}
				}
			}
		});
	});
}


function disconnect(){
	stompClient.disconnect();
}


function updateMessageDate(message) {
	
	let messageContainer = $("#" + message.id);
	dateText = formatDate(new Date(message.milliseconds));
	messageContainer.children(".date").text(dateText);
}


function setMessageDateText(messageContainer, dateText = null) {
	
	// Create date text from milliseconds
	if (dateText == null) {
		
		let ms = +messageContainer.children(".ms_container").text();
		dateText = formatDate(new Date(ms));
	}
	
	let dateContainer = messageContainer.children(".date");
	dateContainer.text(dateText);
}


function createMessageId(message, milliseconds) {

	let id = "" + message.sender[0] + message.reciever[0] + milliseconds;
	message.id = id;
	return id;
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
	
	let sender = $("#userName").text();
	let reciever = $("#contactName").text();
	let text = $("#message_input_value").val();
	
	if ((reciever == "") || (text == "")) {
		return;
	}
	
	// Build and draw it
	let message = new Message(sender, reciever, 0, text);
	drawImageMessage(message, "Sending...");
	
	// Preparing data
	//
	
	//let data = {"text": text, "sender": sender, "reciever": reciever, "milliseconds" : Date.now()};
    let userJson = JSON.stringify(message);
	
    
    
	// Sending message
	//drawMessage(message);
	stompClient.send("/app/message-flow", {}, userJson);
	$("#message_input_value").val("");
}


function drawMessage(message, datePlaceholder) {
	
	// Fill data
	let milliseconds = Date.now();
	if (message.id == null) {
		createMessageId(message, milliseconds);
	}
	if (message.milliseconds == null) {
		message.milliseconds = milliseconds;
	}
	
	// Define side
	let side = "";
	if (message.sender == $("#userName").text()) {
		side = "right";
	} else {
		side = "left";
	}
	
	let date = new Date(message.milliseconds);
	
	if (message.code == 1) {
		drawImageMessage(message, datePlaceholder); //.filePath, side, date, message.id);
	} else {
		drawTextMessage(message.text, side, date);
	}
}


function getMessageSide(message) {
	
	let side = "";
	if (message.sender == $("#userName").text()) {
		side = "right";
	} else {
		side = "left";
	}
	return side;
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


function outputMessageHistory(data) {
	
	let side = "";
	let mas = data.contactHistory;
	for (let i = 0; i !== mas.length; i += 1) {
	
		drawMessage(mas[i], "Loading...");
	}
	
	updateMessageImages();
}


function getText(el) {
	
	let text = el.contents().filter(function() {
		  return this.nodeType == Node.TEXT_NODE;
		}).text();
	return text;
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

	// Click on attach icon
	$("#attach").click(function() {
		chooseImage();
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