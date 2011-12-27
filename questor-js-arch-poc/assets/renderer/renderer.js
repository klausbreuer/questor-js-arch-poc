var Renderer = new Object;
var station;


/*****************************************************
 * QuizStation
 * 
 * example for an implementation of a station as a native activity
 * 
 */

Renderer.QuizStation = function (pQuestion) {    
	this.question = pQuestion;
	runtime.showQuizStation(pQuestion);
};


/*****************************************************
 * QuizStationHtmlOLD
 * 
 * example for an implementation of a station in pure HTML
 * 
 */

Renderer.QuizStationHtmlOLD = function () {
	station = this;    
	this.dom = document.implementation.createHTMLDocument('');
	this.serializer = new XMLSerializer();
	this.question = '';
	
	var content;
	content = '<div id="divQuestion"></div>';
	content += '<div id="divInput"><input id="inputAnswer"></div>';
	
	// The receiving station knows a 'callback' element that allows calling back into this WebView instance. As the station.onSubmit() code needs
	// the field value this is extracted here and set in the callback method's implementation.
	// Not very clean ... 
	//content += '<div id="divButton"><input id="btnAnswer" type="button" value="Antwort einloggen" onClick="callback.onSubmit(document.getElementById(\'inputAnswer\').value);"></div>';

	content += '<div id="divButton"><input id="btnAnswer" type="button" value="Antwort einloggen" onClick="runtime.sendReply(document.getElementById(\'inputAnswer\').value);"></div>';
	content += '';
		 
	this.dom.write(content);

	 /* alert (this.serializer.serializeToString(this.dom)); */  
};


Renderer.QuizStationHtmlOLD.prototype.setQuestion = function (pQuestion) {    

	this.question = pQuestion;
	this.dom.getElementById('divQuestion').appendChild(this.dom.createTextNode(this.question)); 
	 
	/* alert (this.serializer.serializeToString(this.dom)); */ 
};


Renderer.QuizStationHtmlOLD.prototype.setButtonText = function (pText) {    

	this.dom.getElementById('btnAnswer').setAttribute("value", pText); 
	 
};

// Retrieves the text field's information.
Renderer.QuizStationHtmlOLD.prototype.getFieldText = function() {
	// This value is supposed to be set by the callback function.
	return this.answer; 
};

Renderer.QuizStationHtmlOLD.prototype.onSubmit = function () {
	showToast (this.getFieldText());
};

Renderer.QuizStationHtmlOLD.prototype.show = function () {
	
	runtime.showHtmlStation(this.serializer.serializeToString(this.dom));
	
};



/*****************************************************
 * QuizStationHtml
 * 
 * latest version of the QuizStation, an implementation of a station in pure HTML.
 * Simpler and clearer.
 * 
 */


Renderer.QuizStationHtml = function () {
	this.question = '';
	this.buttonText = '';
};

Renderer.QuizStationHtml.prototype.setQuestion = function (pQuestion) {    
	this.question = pQuestion;
}

Renderer.QuizStationHtml.prototype.setButtonText = function (pButtonText) {    
	this.buttonText = pButtonText;
}


Renderer.QuizStationHtml.prototype.show = function () {
	
	var content;
	content  = '<div id="divQuestion">' + this.question + '</div>';
	content += '<div id="divInput"><input id="inputAnswer"></div>';

	content += '<div id="divButton">';
	content += '<input id="btnAnswer" type="button" value="' + this.buttonText + '"';
	content += ' onClick="runtime.sendReply(document.getElementById(\'inputAnswer\').value);">';
	content += '</div>';

	runtime.showHtmlStation(content);
	
}



/*****************************************************
 * HtmlStation
 * 
 * The simple HtmlStation 
 * 
 */

Renderer.HtmlStation = function () {
	this.content = "";
};

Renderer.HtmlStation.prototype.setContent = function (pContent) {    
	this.content = pContent;
}

Renderer.HtmlStation.prototype.show = function () {
	
	// We replace the <choice target="xx"> tags by a call to runtime.sendReply
	// not finished yet...
	var dom;
	dom = document.implementation.createHTMLDocument('');
	dom.write(this.content);
	
	//alert (new XMLSerializer().serializeToString(dom));   

	var arrChoices = dom.getElementsByTagName("choice");
	for( var i=0; i < arrChoices.length; i++ ) {
		alert (arrChoices[i].getAttribute("target"));
		
	}
	

	
	
	
}



/*****************************************************
 * other functions, helpers, utilities....
 * 
 * - showToast(toast)
 * 
 */


function showToast(toast) {
	runtime.showToast(toast);
};

