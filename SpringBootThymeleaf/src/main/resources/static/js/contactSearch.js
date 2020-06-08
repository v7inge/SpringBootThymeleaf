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