
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

	let inputValue = $("#reg_username").val();
	
	if (inputValue.length == null || inputValue.length < 3) {
		$("#reg_username_tip").text("Username should contain at least 3 symbols");
		usernameCorrect = false;
	} else {
		$("#reg_username_tip").text("");
		usernameCorrect = true;
	}

	checkRegInfo();
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
	
	if (tabName == "Log in") {
		$("#h2_login").addClass("active");
		$("#h2_login").removeClass("inactive");
		$("#h2_signup").addClass("inactive");
		$("#h2_signup").removeClass("active");
		$("#form_login").removeClass("invisible");
		$("#form_signup").addClass("invisible");
	} else {
		$("#h2_login").addClass("inactive");
		$("#h2_login").removeClass("active");
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