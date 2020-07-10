
let partV = 0.8;
let partH = 0.8;

function showLightbox(img) {
	
	let imageSrc = img.attr("src");
    
    $("body").append('<div id="overlay"></div><div id="magnify"><img src="' + imageSrc + '" /></div>');
    
    let docHeight = $(document).height();
    let docWidth = $(document).width();
    
    let maxHeight = docHeight * partV;
    let maxWidth = docWidth * partH;
    
    let originalHeight = img[0].naturalHeight;
    let originalWidth = img[0].naturalWidth;
    
    let height, width;
    
    if (originalHeight > originalWidth) {	
    	
    	height = originalHeight > maxHeight ? maxHeight : originalHeight;
    	width = originalWidth * height / originalHeight;
    
    	if (width > maxWidth) {
    		width = maxWidth;
    		height = originalHeight * width / originalWidth;
    	}
    	
    } else {
    	
    	width = originalWidth > maxWidth ? maxWidth : originalWidth;
    	height = originalHeight * width / originalWidth;
    	
    	if (height > maxHeight) {
    		height = maxHeight;
    		width = originalWidth * height / originalHeight;
    	}
    }
    
    $("#magnify").css({
    	left: (docWidth - width) / 2,
    	top: (docHeight - height) / 2,
    	height: height,
    	width: width
	});
    
    $("#magnify img").css({
    	height: height,
    	width: width
	});
    
    $("#overlay, #magnify").fadeIn("fast");
}


function hideLightbox() {
	
	$("#overlay, #magnify").fadeOut("fast", function() {
    	$("#magnify, #overlay").remove();
    });
}