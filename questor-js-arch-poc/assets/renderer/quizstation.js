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
 * QuizStationHtml
 * 
 * example for an implementation of a station in pure HTML.
 * 
 */


Renderer.QuizStationHtml = function () {
	this.question = '';
	this.buttonText = '';
};

Renderer.QuizStationHtml.prototype.setQuestion = function (pQuestion) {    
	this.question = pQuestion;
};

Renderer.QuizStationHtml.prototype.setButtonText = function (pButtonText) {    
	this.buttonText = pButtonText;
};

Renderer.QuizStationHtml.prototype.show = function () {
	var content;
	content  = '<div id="divQuestion">' + this.question + '</div>';
	content += '<div id="divInput"><input id="inputAnswer"></div>';
	
	content += '<div id="divButton">';
	content += '<input id="btnAnswer" type="button" value="' + this.buttonText + '"';
	content += ' onClick="runtime.sendReply(document.getElementById(\'inputAnswer\').value);">';
	content += '</div>';
	
	makeCurrent(this);
	runtime.showHtmlStation(content);

};

