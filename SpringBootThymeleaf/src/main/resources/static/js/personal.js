$(document).ready(function() {
 
    $("#submitButton").click(function(event) {
 
        event.preventDefault();
        submitAvatarUpload();
    });
 
});
 
function submitAvatarUpload() {
 
    var form = $('#fileUploadForm')[0];
    var data = new FormData(form);

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/avatar-upload",
        data: data,
 
        processData: false,
        contentType: false,
        cache: false,
        timeout: 1000000
        /*,
        success: function(data, textStatus, jqXHR) {
 
        	$('#fileUploadForm')[0].reset();
        },
        error: function(jqXHR, textStatus, errorThrown) {  
 
        	$('#fileUploadForm')[0].reset();
 
        }*/
    });
    
    $('#fileUploadForm')[0].reset();
 
}

function test() {
	
	$.ajax({
	    url: "/testload",
	    async: true,
	    type: "POST",
	    dataType: "json",
	    contentType: "application/json; charset=utf-8",
	    success: function (response) {
	        $("#imgsrv").attr("src", "data:image/png;base64," + response.base64String);
	    }
	});
	
}