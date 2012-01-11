/**
 * The QuizStation is a station which poses a question which needs to be answered correctly.
 *
 * There are three elements to customize:
 * - The question text that is shown in the renderer.
 * - The text for the button.
 * - The right answer. This value stays in the simulator only and is never transferred to the
 *   renderer.
 *
 * The station has two exits. The successful exit is taken when the question was answered right
 * the failed exit is taken when the answer was wrong.
 *
 * TODOs for a real implementation:
 * The actual texts shown should be resource keys which are then send to the renderer. The renderer
 * will look up the resource on its side and display the underlying content.
 */

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
		("var q = new QuizStationHtml ();"
		+ "q.setQuestion(\"{0}\"); "
		+ "q.setButtonText(\"{1}\"); "
		+ "q.show();").format(this.question, this.buttonText);
	return generatorCode;
};
