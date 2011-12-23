var Renderer = new Object;

Renderer.QuizStation = function (pQuestion) {    
	this.question = pQuestion;
	runtime.showQuizStation(pQuestion);
};

Renderer.QuizStationHtml = function () {    
	this.dom = document.implementation.createHTMLDocument('');
	this.serializer = new XMLSerializer();
	this.question = '';
	
	var content;
	content = '<div id="divQuestion"></div>';
	content += '<div id="divInput"><input id="inputAnswer"></div>';
	
	// TODO: The fact that this Javascript is being put into a completely different WebView instance gives us no possibility
	// to call back in onClick().
	// Possible solution: Why not have a known 'root' div which contains the station HTML. Everytime
	// a station is made visible we can remove the elements below the div and replace it
	// with new code. 
	content += '<div id="divButton"><input id="btnAnswer" type="button" value="Antwort einloggen" onClick="this.station.onSubmit();"></div>';
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
	return this.dom.getElementById('inputAnswer').getAttribute("value"); 
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

