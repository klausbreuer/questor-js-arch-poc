var Renderer = new Object;
var station;

Renderer.QuizStation = function (pQuestion) {    
	this.question = pQuestion;
	runtime.showQuizStation(pQuestion);
};

Renderer.QuizStationHtml = function () {
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
	content += '<div id="divButton"><input id="btnAnswer" type="button" value="Antwort einloggen" onClick="callback.onSubmit(document.getElementById(\'inputAnswer\').value);"></div>';
	content += '';
		 
	this.dom.write(content);

	 /* alert (this.serializer.serializeToString(this.dom)); */  
};


Renderer.QuizStationHtml.prototype.setQuestion = function (pQuestion) {    

	this.question = pQuestion;
	this.dom.getElementById('divQuestion').appendChild(this.dom.createTextNode(this.question)); 
	 
	/* alert (this.serializer.serializeToString(this.dom)); */ 
};


Renderer.QuizStationHtml.prototype.setButtonText = function (pText) {    

	this.dom.getElementById('btnAnswer').setAttribute("value", pText); 
	 
};

// Retrieves the text field's information.
Renderer.QuizStationHtml.prototype.getFieldText = function() {
	// This value is supposed to be set by the callback function.
	return this.answer; 
};

Renderer.QuizStationHtml.prototype.onSubmit = function () {
	showToast (this.getFieldText());
};

Renderer.QuizStationHtml.prototype.show = function () {
	
	runtime.showHtmlStation(this.serializer.serializeToString(this.dom));
	
};

function showToast(toast) {
	runtime.showToast(toast);
};

