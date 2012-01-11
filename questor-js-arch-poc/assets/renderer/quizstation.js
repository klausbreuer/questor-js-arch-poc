/*****************************************************
 * QuizStationHtml
 * 
 * example for an implementation of a station in pure HTML.
 * 
 */


QuizStationHtml = function () {
	if (quizDelegate == null) {
		logger.e("QuizDelegate not initialized. QuizStation *WILL NOT* work!");
	}

	this.question = '';
	this.buttonText = '';
	
	// Implementation note: This could either be read from a data file or could have
	// been set by the simulator.
	this.native = true;
};

QuizStationHtml.prototype.setNative = function (native) {    
	this.native = native;
};

QuizStationHtml.prototype.setQuestion = function (pQuestion) {    
	this.question = pQuestion;
};

QuizStationHtml.prototype.setButtonText = function (pButtonText) {    
	this.buttonText = pButtonText;
};

QuizStationHtml.prototype.show = function () {
	// Demonstration of a JavaScript implemented station which supports considerably
	// different user-interface logic.
	// However a better showcase would be a station which has different delegate implementations
	// that are chosen by the environment.
	if (this.native) {
		makeCurrent(this);
		quizDelegate.showNative(this.question);
	} else {
		var content;
		content  = '<div id="divQuestion">' + this.question + '</div>';
		content += '<div id="divInput"><input id="inputAnswer"></div>';
		
		content += '<div id="divButton">';
		content += '<input id="btnAnswer" type="button" value="' + this.buttonText + '"';
		content += ' onClick="runtime.sendReply(document.getElementById(\'inputAnswer\').value);">';
		content += '</div>';
		
		makeCurrent(this);
		quizDelegate.showHtml(content);
	}

};

QuizStationHtml.prototype.onLeave = function() {
	// Intentionally empty.
}
