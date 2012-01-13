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
	
	this.attendees = new AttendeeList(null, null);
};

QuizStation.prototype.onEnter = function(session) {
	var obj = {
			question: this.question,
			buttonText: this.buttonText
	};
	
	simulator.sendCreateMessage(session, "QuizStationHtml", obj);

	for ( var i in this.attendees.entries) {
		var a = this.attendees.entries[i];
		var list = new Array();
		if (a != null) {
			list.push(a.session.playerId);
		
			// Tells each existing player about the new one.
			var obj = {
					type: "newPlayer",
					playerId: session.playerId
			};
			simulator.sendStationMessage(a.session, obj);
		}
		
		// Tells the new player about the existing ones.
		if (list.length > 0) {
			var obj = {
					type: "attendingPlayers",
					playerIds: list
			};
			simulator.sendStationMessage(session, obj);
		}
	}
	this.attendees.add(session);
};

QuizStation.prototype.onLeave = function(session) {
	this.attendees.remove(session);
	for ( var i in this.attendees.entries) {
		var a = this.attendees.entries[i];
		var list = new Array();
		if (a != null) {
			list.push(a.session.playerId);
		
			// Tells each existing player about the new one.
			var obj = {
					type: "advancedPlayer",
					playerId: session.playerId
			};
			simulator.sendStationMessage(a.session, obj);
		}
	}
};

QuizStation.prototype.onMessage = function(session, data) {
	if (this.answer == data) {
		simulator.performTransition(session, this.stationSuccess);
	} else {
		simulator.performTransition(session, this.stationFail);
	}
};
