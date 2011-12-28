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
	var dom;
	dom = document.implementation.createHTMLDocument('');
	dom.write(this.content);

	var arrChoices = dom.getElementsByTagName("choice");
	var choiceNode, choiceVal, choiceTxt, newA, parent;
	for( var i=0; i < arrChoices.length; i++ ) {
		
		choiceNode = arrChoices[i];
		
		// 1. get the target and the text of the <choice>:
		choiceVal = choiceNode.getAttribute("target");
		choiceTxt = choiceNode.firstChild.nodeValue;
		alert (choiceTxt);

		// 2. create a new <a>, fill the href and the onClick attributes and add a textnode with the text ;-:
		newA = dom.createElement("a");
		newA.setAttribute("href","");
		newA.setAttribute("onClick","alert('kicher:' + " + choiceVal  + ");");
		newA.appendChild(dom.createTextNode(choiceTxt))
		
		
		// 3. put the new <a> in the dom and remove the <choice>:
		parent = choiceNode.parentNode;
		parent.appendChild(newA);
		//parent.removeChild (choiceNode);
		
		//alert (new XMLSerializer().serializeToString(dom));
		
	}
	

	runtime.showHtmlStation(new XMLSerializer().serializeToString(dom));
	
	
	
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

