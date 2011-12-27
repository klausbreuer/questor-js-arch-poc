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
	var submitCode = "runtime.sendReply(this.getFieldText()); ";
		
	var generatorCode = 
		("var q = new Renderer.QuizStationHtml ();"
		+ "q.setQuestion('{0}'); "
		+ "q.setButtonText('{1}'); "
		+ "q.onSubmit = function() { {2} };"
		+ "q.show();").format(this.question, this.buttonText, this.submitCode);
	return generatorCode;
};

sim.finished();