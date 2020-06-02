
let regCorrect = false;
let usernameCorrect = false;
let passwordCorrect = false;


function onLoad() {
	
	let strGet = window.location.search.replace("?", "");
	
	if (strGet == "regerror") {
		setActiveTab("Sign up");
	}
}


function usernameInputChange() {

	usernameCorrect = true;
	let inputValue = $("#reg_username").val();
	
	// Check length
	if (inputValue.length == null || inputValue.length < 3) {
		$("#reg_username_tip").text("Username should contain at least 3 symbols");
		usernameCorrect = false;
	}

	// Check symbols
	if (usernameCorrect) {

		for(let i=0; i<inputValue.length; i++) {
		
			let symbol = inputValue[i];

			if (i==0) {
				if (!isLetter(symbol)) {
					usernameCorrect = false;
					$("#reg_username_tip").text("Username should start with a letter");
					break; 
				}
			} else {
				if (!isAppropriateSymbol(symbol)) {
					usernameCorrect = false;
					$("#reg_username_tip").text("Username should contain only letters and numbers");
					break;
				}
			}
		}
	}
	
	if (usernameCorrect) {
		
		// Check if username is free
		let userData = {"username": inputValue};
	    let url = "/username-check";
	    let userJson = JSON.stringify(userData);
		
		$.ajax
	    ({
	        type: "POST",
	        data: userJson,
	        url: url,
	        contentType: "application/json; charset=utf-8",
	        success: function(data)
	    	{
	
	        	if (data.free == true) {
	        		usernameCorrect = true;
	        		$("#reg_username_tip").text("Good, such username is free");
	        	} else {
	        		usernameCorrect = false;
	        		$("#reg_username_tip").text("Sorry, such username is already in use");
	        	}
	        	
	        	checkRegInfo();
	
	    	}
	    });
	}
	
}


function isAppropriateSymbol(symbol) {
	return isLetter(symbol) || isNumber(symbol)
}


function isLetter(symbol) {
	let letters = "abcdefghijklmnopqrstuvwxyzабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
	return letters.includes(symbol.toLowerCase());
}


function isNumber(symbol) {
	let numbers = "0123456789";
	return numbers.includes(symbol);
}


function passwordInputChange() {

	let pass = $("#reg_password").val();
	let passR = $("#reg_password_repeat").val();

	if (pass.length == 0 || passR.length == 0) {
		passwordCorrect = false;
		$("#reg_password_tip").text("");
	} else {
	
		if (pass == passR) {
					passwordCorrect = true;
					$("#reg_password_tip").text("");
				} else {
					passwordCorrect = false;
					$("#reg_password_tip").text("Please check passwords, they don't match");
				}

	}

	checkRegInfo();
}


function checkRegInfo() {

	regCorrect = (usernameCorrect && passwordCorrect);

	if (regCorrect) {
		$("#reg_button").removeClass("inactive-input");	
	} else {
		$("#reg_button").addClass("inactive-input");	
	}

}


function setActiveTab(tabName) {
	
	if (tabName == "Sign in") {
		$("#h2_signin").addClass("active");
		$("#h2_signin").removeClass("inactive");
		$("#h2_signup").addClass("inactive");
		$("#h2_signup").removeClass("active");
		$("#form_login").removeClass("invisible");
		$("#form_signup").addClass("invisible");
	} else {
		$("#h2_signin").addClass("inactive");
		$("#h2_signin").removeClass("active");
		$("#h2_signup").addClass("active");
		$("#h2_signup").removeClass("inactive");
		$("#form_login").addClass("invisible");
		$("#form_signup").removeClass("invisible");
	}	
}


$(document).ready(function() {
	
	// Changing login type
	$(".h2-login").click(function() {
		
		setActiveTab($(this).text());
	});
	
});