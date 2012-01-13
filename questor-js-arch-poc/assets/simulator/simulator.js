/** Session class
 * Holds the player and his/her current station.
 */
Session = function(sessionId, playerId) {
	this.sessionId = sessionId;
	this.playerId = playerId;
}

/** Simulator class
 * Keeps track of the stations and the sessions.
 */
Simulator = function() {
	this.sessions = new Object();
	this.stations = null;
	
	// Sets the message that is to be send for 
	runtime.setInvalidationMessage('{"type":"invalidate"}');
};

Simulator.prototype.setGame = function(start, stations) {
	this.start = start;
	this.stations = stations;
}

/** This method handles the messages send from the renderer.
*/	
Simulator.prototype.onMessage = function(sessionId, msg) {
	logger.i("Simulator.onMessage('{0}', '{1}'}".format(sessionId, msg));
	
	var msgObj = null;
	try {
		msgObj = JSON.parse(msg);
	} catch (e) {
		logger.e("Unable to parse message: " + e);
		return;
	}
	
	switch (msgObj.type) {
		case 'join':
			var session = this.newSession(sessionId, msgObj.playerId);
			this.performTransition(session, this.start);
			break;
		case 'invalidate':
			var s = this.deleteSession(sessionId);
			if (!s) {
				logger.e("Nothing to do. Session already invalid: " + sessionId);
				return;
			}
			
			// Leave a potentially occupied station
			if (s.station) {
				// Actually we need some kind of onCancel() as the player is not
				// properly leaving this station.
				s.station.onLeave(s);
			}
			break;
		case 'reply':
			var s = this.toSession(sessionId);
			if (!s) {
				logger.e("Could not find session for id: " + sessionId);
				return;
			};
			
			s.station.onMessage(s, msgObj.data);
			
			break;
		default:
			logger.i("Unexpected message type: " + msgObj.type);
			break;
	}
	
};

Simulator.prototype.sendCreateMessage = function(session, stationClass, data) {
	var obj = {
			type: "create",
			stationClass: stationClass,
			data: data
	};

	this.sendMessageObject(session, obj);
};

Simulator.prototype.sendStationMessage = function(session, data) {
	// Suppressing quietly.
	if (session.sessionId == "fake")
		return;
	
	if (!this.sessionExists(session)) {
		logger.e("Session expired. Suppressing 'send' for {0} (player: {1})".format(session.sessionId, session.playerId));
		return;
	}
	
	var obj = {
		type: "station",
		data: data
	};
	
	this.sendMessageObject(session, obj);
};

/** Sends a fully prepared message object. The caller is supposed to set
 * the 'type' field which has to be understood by the Renderer instance.
 */
Simulator.prototype.sendMessageObject = function(session, obj) {
	var newMsg = JSON.stringify(obj);
	
	runtime.sendToRenderer(this.toSessionId(session), newMsg);
};

Simulator.prototype.performTransition = function(session, newStationId) {
	// Call leaving code.
	if (session.station) {
		session.station.onLeave(session);
	}
	
	// Update session data
	session.station = this.stations[newStationId];
	session.stationId = newStationId;
	
	// Call enter code.
	session.station.onEnter(session);
};

Simulator.prototype.newSession = function(sessionId, playerId) {
	s = new Session(sessionId, playerId);
	this.sessions[sessionId] = s;
	return s;
};

Simulator.prototype.deleteSession = function(sessionId) {
	s = this.sessions[sessionId];
	
	delete this.sessions[sessionId];
	
	return s;
};

Simulator.prototype.sessionExists = function(session) {
	return (this.sessions[session.sessionId] != null);
}

Simulator.prototype.toSessionId = function(session) {
	return session.sessionId;
};

Simulator.prototype.toSession = function(sessionId) {
	return this.sessions[sessionId];
};
	

