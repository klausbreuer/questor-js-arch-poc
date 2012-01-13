AttendeeList = function(whenZeroFunction, whenOneFunction) {
	this.entries = new Object();
	
	this.amount = 0;
	this.whenZeroFunction = whenZeroFunction;
	this.whenOneFunction = whenOneFunction;
	// Intentionally empty.
};

AttendeeList.prototype.add = function(session) {
	if (this.entries[session.playerId] == null) {
		logger.i("new player attending compassstation '{0}': {1}".format(session.stationId, session.playerId));
		this.amount++;
		if (this.amount == 1 && this.whenOneFunction) {
			this.whenOneFunction();
		}
	} else {
		logger.i("new player attending compassstation '{0}' but was already in attendee list".format(session.stationId));
	}
	
	var newattendee = this.entries[session.playerId] = new Object();
	newattendee.session = session;
	newattendee.data = null;
	
};

AttendeeList.prototype.update = function(session, data) {
	if (this.entries[session.playerId] == null) {
		logger.i("updating player attending compass station '{0}' but did not enter before".format(session.stationId));
		return;
	} else {
		logger.i("updating player attending compass station '{0}'".format(session.stationId));
	}
	
	var attendee = this.entries[session.playerId];
	attendee.data = data;
};

AttendeeList.prototype.remove = function(session) {
	if (this.entries[session.playerId] == null) {
		logger.i("removing player attending compass station '{0}' but did not enter before".format(session.sessionId));
		return;
	} else {
		logger.i("removing player attending compass station '{0}'".format(session.sessionId));
		this.amount--;
		if (this.amount == 0 && this.whenZeroFunction) {
			this.whenZeroFunction();
		}
	}
	
	delete this.entries[session.playerId];
};