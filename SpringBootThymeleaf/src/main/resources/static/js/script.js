
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
			drawMessage(data.text, new Date(data.date), "left");
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

function sendMessage() {
	
	// Preparing data
	let text = $("#message_input_value").val();
	let date = new Date();
	let sender = $("#userName").text();
	let reciever = $("#contactName").text();
	let message = {"queryType": "message_sent", "text": text, "sender": sender, "date": date, "reciever": reciever};
	let url = "/";
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

function scrollDown() {
	$("#messages").scrollTop($("#messages")[0].scrollHeight);
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
					
		// Changing styles and content
		contacts = $(".contact-active");
		contacts.removeClass();
		contacts.toggleClass("contact");
		$(this).removeClass();
		$(this).toggleClass("contact-active");
		$("#messages").html("");
		
		// Hide tip table
		tipTable = $(".tdcenter");
		tipTable.removeClass();
		tipTable.html("");
		
		let contactName = $(this).text();
		$("#contactName").text(contactName);

		// Filling data
		let userData = {"queryType": "contact_clicked", "contact": contactName};
        let url = "/";
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

            	console.log("data: " + data);
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
	});
	
	// Changing login type
	$(".h2-login").click(function() {
		
		let h2login = $(".h2-login");
		h2login.removeClass("active");
		h2login.addClass("inactive");

		$(this).removeClass("inactive");
		$(this).addClass("active");

		$("#login_button").val($(this).text());
		
	});
	
});