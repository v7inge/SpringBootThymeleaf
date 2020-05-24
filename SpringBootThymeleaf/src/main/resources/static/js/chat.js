

function onLoad() {
	connect();
}


function connect() {
	
	let sender = $("#userName").text();
	var socket = new SockJS("/chat-messaging");
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function (frame) {
		
		stompClient.subscribe("/queue/" + sender, function (data) {
			
			var data = JSON.parse(data.body);
			
			let messageFrom = data.sender;
			let activeContact = $("#contactName").text();
			
			if (messageFrom == activeContact) {
				drawMessage(data.text, new Date(data.date), "left");
			} else {
				increaseCounter(messageFrom);
			}
			
		});
	});
}


function drawMessage(text, date, side) {

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


function getContactName(el) {
	return $(el).children(".name").text();	
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
	let date = new Date();
	let sender = $("#userName").text();
	let reciever = $("#contactName").text();
	let message = {"text": text, "sender": sender, "date": date, "reciever": reciever};
	let url = "/message-sent";
    let userJson = JSON.stringify(message);
	
    if ((reciever == "") || (text == "")) {
		return;
	}
    
	// Sending message
	drawMessage(text, date, "right");
	stompClient.send("/app/direct/" + sender + "/to/" + reciever, {}, userJson);
	$("#message_input_value").val("");
	
	// Sending for DB saving
    $.ajax
    ({
    	type: "POST",
        data: userJson,
        url: url,
        contentType: "application/json; charset=utf-8",
        scriptCharset: "utf-8"
    });
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
        error: function(e)
        {
        	console.log("Запрос не удался!");
        },
        success: function(data)
    	{

        	let side = "";
        	let mas = data.contactHistory;
        	for (let i = 0; i !== mas.length; i += 1) {
        		
        		if (mas[i].sender == $("#userName").text()) {
        			side = "right";
        		} else {
        			side = "left";
        		}
        		
        		drawMessage(mas[i].text, new Date(mas[i].date), side);
        	
        	}
    	}
    });
	
	scrollDown();
}


function scrollDown() {
	$("#messages").scrollTop($("#messages")[0].scrollHeight);
}


function contactInputChange() {

	let inputValue = $("#contact_input").val();
	if (inputValue == "") {
		$("#contacts").removeClass("invisible");
		$("#searching-tip").addClass("invisible");
	} else {
		$("#contacts").addClass("invisible");
		$("#searching-tip").removeClass("invisible");
		$("#searching-tip").text("Searching...");
	}
	
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

        	console.log("we got some data");
        	let mas = data.users;
        	for (let i = 0; i !== mas.length; i += 1) {
        		
        		console.log(mas[i]);
        	}
    	}
    });
	
}


function dropDown() {
  document.getElementById("myDropdown").classList.toggle("show");
}


window.onclick = function(event) {
  if (!event.target.matches(".dropbtn")) {
    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains("show")) {
        openDropdown.classList.remove("show");
      }
    }
  }
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

	// Click on contact name
	$(".contact").click(function() {
		contactClick($(this));	
	});
	
});