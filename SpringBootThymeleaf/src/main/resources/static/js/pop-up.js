let checkDelay = 1000;
let hideDelay = 5000;
let popUpActive = false;

function showPopUp(text) {

  if (popUpActive) {
    setTimeout(showPopUp, checkDelay);
    return;
  }

  popUpActive = true;
  
  $(".pop-up .text").text(text);
  $(".pop-up").addClass("show");
  setTimeout(hidePopUp, hideDelay);
}

function hidePopUp() {
  $(".pop-up").removeClass("show");
  popUpActive = false;
}