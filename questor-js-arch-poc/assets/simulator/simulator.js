var station;
	
QuizStation = function(pQuestion, pButtonText, pAnswer, pStationSuccess, pStationFail) {
	station = this;
	this.question = pQuestion;
	this.buttonText = pButtonText;
	this.answer = pAnswer;
	this.stationSuccess = pStationSuccess;
	this.stationFail = pStationFail;
};

QuizStation.prototype.onEnter = function() {
	sim.sendCreateStation(this.generateJavascript());
};

QuizStation.prototype.onMessage = function(msg) {
	if (this.answer == msg) {
		sim.performTransition(this.stationSuccess);
	} else {
		sim.performTransition(this.stationFail);
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

sim.finished();