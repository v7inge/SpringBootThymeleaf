
function updateMessageDate(message) {
	
	let messageContainer = $("#" + message.id);
	dateText = formatDate(new Date(message.milliseconds));
	setMessageDateText(messageContainer, dateText);
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


function resetCounter(contact) {

	let el = findContact(contact);
	if (el != null) {
	
		let counterElement = $(el).children(".counter");

		if(counterElement.hasClass("invisible") == false) {
			counterElement.toggleClass("invisible");
		}

		counterElement.text("");
		
		// Notify the server
		let userData = {"contact": contact};
	    let url = "/reset-counter";
	    let userJson = JSON.stringify(userData);
		
		$.ajax
	    ({
	        type: "POST",
	        data: userJson,
	        url: url,
	        contentType: "application/json; charset=utf-8",
	        success: function(data) { }
	    });

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


function scrollDown() {
	if ($("#messages-"+contactName).length != 0) {
		$("#messages-"+contactName).scrollTop($("#messages-"+contactName)[0].scrollHeight);
	}
}


function getMessageSide(message) {
	
	let side = "";
	if (message.sender == userName) {
		side = "right";
	} else {
		side = "left";
	}
	return side;
}


function chooseFile() {
	
	if (contactName == "") {
		return;
	}
	
	$("#file-input").click();
}


function getText(el) {
	
	let text = el.contents().filter(function() {
		  return this.nodeType == Node.TEXT_NODE;
		}).text();
	return text;
}


function messagesBlockIsEmpty(contact) {
	
	let messagesBlock = $("#messages-" + contact);
	return (messagesBlock.children("li").length == 0);
}


function increaseCounterIfNecessary(message) {
	
	if (message.sender != contactName && message.code != 1 && message.code != 4 && message.code != 6) {

		increaseCounter(message.sender);
	}
}