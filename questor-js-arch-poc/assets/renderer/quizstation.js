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
	this.nativeImpl = false;
};

QuizStationHtml.prototype.init = function(obj) {
	this.question = obj.question;
	this.buttonText = obj.buttonText;
};

QuizStationHtml.prototype.setNative = function (nativeImpl) {    
	this.nativeImpl = nativeImpl;
};

QuizStationHtml.prototype.show = function () {
	// Demonstration of a JavaScript implemented station which supports considerably
	// different user-interface logic.
	// However a better showcase would be a station which has different delegate implementations
	// that are chosen by the environment.
	if (this.nativeImpl) {
		quizDelegate.showNative(this.question);
	} else {
		var content;
		content  = '<div id="divQuestion">' + this.question + '</div>';
		content += '<div id="divInput"><input id="inputAnswer"></div>';
		
		content += '<div id="divButton">';
		content += '<input id="btnAnswer" type="button" value="' + this.buttonText + '"';
		content += ' onClick="runtime.sendReply(document.getElementById(\'inputAnswer\').value);">';
		content += '</div>';
		
		quizDelegate.showHtml(content);
	}

};

QuizStationHtml.prototype.onMessage = function(data) {
	switch (data.type) {
	case 'newPlayer':
		logger.i("A new player entered the QuizStation: " + data.playerId);
		break;
	case 'advancedPlayer':
		logger.i("A player left the QuizStation: " + data.playerId);
		break;
	case 'attendingPlayers':
		logger.i("The following players are already here: ");
		for (var i in data.playerIds) {
			logger.i("player: " + data.playerIds[i]);
		}
		break;
	default:
		logger.e("Unknown message type: " + data.type);
		break;
	}
};

QuizStationHtml.prototype.onLeave = function() {
	// Intentionally empty.
};
