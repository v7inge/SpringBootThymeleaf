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
	        			
	        			let contact = new Contact(mas[i].username, mas[i].base64Image);
	        			createContact(contact, null, true, false);
	            	}
	        		$("#searching-tip").addClass("invisible");
	        	}
	        	
	    	}
	    });
	}
	
}


//Creates a new contact or a new found contact from a class instance or dom element
function createContact(contact = null, contactElement = null, found = false, makeAClick = false) {

  // Prepare data
  let name, imgSrc;
  if (contact != null) {
    name = contact.name;
    imgSrc = "data:image/png;base64," + contact.base64;
  } else if (contactElement != null) {
    name = contactElement.text();
    imgSrc = contactElement.children("img").prop("src");
  }

  let classValue = (found) ? "contact found" : "contact";
  let listId = (found) ? "found_contacts" : "contacts";

  // Create element
  let newContact = $("<li>", {"class": classValue});
	
  newContact.append( $("<img>", {"class": "contact-img", src: imgSrc}) );
  newContact.append( $("<div>", {"class": "contact-name", text: name}) );
  
  if (!found) {
	  newContact.append( $("<div>", {"class": "counter invisible"}) );
  }
  
  $("#" + listId).append(newContact);
  
  if (makeAClick) {
	newContact.click();
  }
}


function moveContactToTheList(el) {
	
	$("#found_contacts").empty();
	createContact(null, el, false, true);
	$("#contacts").removeClass("invisible");
	$("#searching-tip").addClass("invisible");
	$("#found_contacts").addClass("invisible");
	$("#contact_input").val("");
	
	// Notify the server
	let userData = {"input": el.text()};
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