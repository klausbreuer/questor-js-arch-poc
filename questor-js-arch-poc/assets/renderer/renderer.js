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
 * example for an implementation of a station in pure HTML.
 * 
 */

Renderer.QuizStationHtmlOLD = function () {
	station = this;    
	this.dom = document.implementation.createHTMLDocument('');
	this.serializer = new XMLSerializer();
	this.question = '';
	this.buttonText = '';
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


/*****************************************************
 * CompassStation
 * 
 * example for an implementation of a station as a native activity
 * 
 */

Renderer.CompassStation = function () {
};


Renderer.CompassStation.prototype.show = function () {
	runtime.showCompassStation();
}

Renderer.CompassStation.prototype.sendMessage = function (pType, pMsg) {
	runtime.sendMessageToCompassStation(pType, pMsg);
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
	
	// We replace the <choice target="xx"> tags by a call to 
	
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

