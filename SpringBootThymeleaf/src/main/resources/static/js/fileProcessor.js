
function getFile(path, filename) {
	
	let pathData = {};
	let ext = getFileExt(filename);
	pathData[path] = path;
	let url = "/get-files";
    let userJson = JSON.stringify(pathData);
	
	    $.ajax
	    ({
	    	type: "POST",
	        data: userJson,
	        url: url,
	        contentType: "application/json; charset=utf-8",
	        success: function(response)
	    	{
	        	
	        	let arrayBuffer = base64ToArrayBuffer(response[path]);
	        	saveByteArray(filename, arrayBuffer, ext);
	    	}
	    });
	
}

function base64ToArrayBuffer(base64) {
    
	var binaryString = window.atob(base64);
    var binaryLen = binaryString.length;
    var bytes = new Uint8Array(binaryLen);
    for (var i = 0; i < binaryLen; i++) {
       var ascii = binaryString.charCodeAt(i);
       bytes[i] = ascii;
    }
    return bytes;
}

function saveByteArray(filename, byte, ext) {
	
    var blob = new Blob([byte], {type: "application/" + ext});
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = filename;
    link.click();
};

function getFileExt(filename) {
	
	return filename.split(".").pop().toLowerCase();
}