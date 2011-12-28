QuizStation = function(pQuestion, pButtonText, pAnswer, pStationSuccess, pStationFail) {
	station = this;
	this.question = pQuestion;
	this.buttonText = pButtonText;
	this.answer = pAnswer;
	this.stationSuccess = pStationSuccess;
	this.stationFail = pStationFail;
};

QuizStation.prototype.onEnter = function(session) {
	simulator.sendCreateStation(session, this.generateJavascript());
};

QuizStation.prototype.onMessage = function(session, msg) {
	if (this.answer == msg) {
		simulator.performTransition(session, this.stationSuccess);
	} else {
		simulator.performTransition(session, this.stationFail);
	}
};

QuizStation.prototype.generateJavascript = function() {
	var generatorCode = 
		("var q = new Renderer.QuizStationHtml ();"
		+ "q.setQuestion('{0}'); "
		+ "q.setButtonText('{1}'); "
		+ "q.show();").format(this.question, this.buttonText);
	return generatorCode;
};
