var station;
	
function test() {
	logger.i("test");
}
	
QuizStation = function(pQuestion, pButtonText, pAnswer, pStationsSuccess, pStationFail) {
	station = this;
	logger.i("station: " + station);
	 
	this.question = pQuestion;
	logger.i(question);
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
		sim.performTransition(stationSuccess);
	} else {
		sim.performTransition(stationFail);
	}
};

QuizStation.prototype.generateJavascript = function() {
	var submitCode = "runtime.sendReply(this.getFieldText()); ";
		
	var generatorCode = 
		"var q = new Renderer.QuizStationHtml ();"
		+ "q.setQuestion('{0}'); "
		+ "q.setButtonText('{1}'); "
		+ "q.onSubmit = function() { {2} };"
		+ "q.show();".format(this.question, this.buttonText, this.submitCode);
		
	return generatorCode;
};
