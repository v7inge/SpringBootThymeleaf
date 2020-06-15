

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
	        	scrollDown();
	    	}	
	    });
    }
}