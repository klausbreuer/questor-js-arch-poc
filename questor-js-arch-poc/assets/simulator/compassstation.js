/**
 * The CompassStation
 *
 * Elements to customize:
 * - a list of POIs to be shown as crosses in the compass
 * - the positions of the other players
 * - a station for success and a station for fail 
 * 
 */


CompassStation = function(pStationSuccess, pStationFail) {
	station = this;
	this.stationSuccess = pStationSuccess;
	this.stationFail = pStationFail;
	
	// Keeps the player sessions and their locations
	this.attendees = new Object();
	
	this.updateCount = 0;

	var helper = this;
	
	this.interval = setInterval (
			function() {
				if (helper.updateCount > 3)
					{
						helper.sendAttendeePositions();
					}

				// TEST CODE: Send random positions to all players.
				for (var i in helper.attendees) {
					var a = helper.attendees[i];
					if (a != null) {
						helper.sendPoiPosition(a.session);
					}
				}
			}, 5000 );	
};


CompassStation.prototype.onEnter = function(session) {
	simulator.sendCreateStation(session, this.generateJavascript());
	
	this.addAttendee(session);
};

CompassStation.prototype.sendPoiPosition = function(session) {
	// testing: sends random positions for up to four POIs:
	
	var msg;
	var id, lat, lon, col;
	
	//helping function to generate a random number:
	function randomFromTo(from, to){
		return Math.floor(Math.random() * (to - from + 1) + from);
	}	
	
	id  = randomFromTo(0, 3);
	lat = randomFromTo(45644768, 56365250);
	lon = randomFromTo(4921875, 15732422);
	col = "FFFF0000";
	
	msg = id + "," + lat + "," + lon + "," + col;
	simulator.sendMessage("poiPos", session, msg);
};


CompassStation.prototype.onMessage = function(session, msg) {
	if (msg == "playerPos") {
		updateAttendee(session, 5, 10);
		
	} else if (msg = "cheatOut") {
		
		removeAttendee(session);
		clearInterval(this.interval);
		simulator.performTransition(session, msg);
	}
};

CompassStation.prototype.generateJavascript = function() {
	var generatorCode = 
		"var c = new Renderer.CompassStation ();"
		+ "c.show();";
	return generatorCode;
};

CompassStation.prototype.sendAttendeePositions = function() {
	var h = this;
	var helper = function(attendees, current) {
		for (i in attendees) {
			a = attendees[i];
			if (a != null && a != current) {
				
				msg = a.session.playerId + "," + a.lat + "," + a.lon;
				simulator.sendMessage("playerPos", current.session, msg);
			}
		}
	}
	
	for (i in this.attendees) {
		a = this.attendees[i];
		
		if (a != null) {
			helper(this.attendees, a);
		}
	}
};

CompassStation.prototype.addAttendee = function(session) {
	if (this.attendees[session.playerId] == null) {
		logger.i("new player attending compassstation '{0}'".format(session.stationId));
	} else {
		logger.i("new player attending compassstation '{0}' but was already in attendee list".format(session.stationId));
	}
	
	var newattendee = this.attendees[session.playerId] = new Object();
	newattendee.session = session;
	newattendee.lon = null;
	newattendee.lat = null;
};

CompassStation.prototype.updateAttendee = function(session, lat, lon) {
	if (this.attendees[session.playerId] == null) {
		logger.i("updating player attending compass station '{0}' but did not enter before".format(session.stationId));
		return;
	} else {
		logger.i("updating player attending compass station '{0}'".format(session.stationId));
	}
	
	var attendee = this.attendees[session.playerId];
	attendee.lat = lat;
	attendee.lon = lon;
};

CompassStation.prototype.removeAttendee = function(session, lat, lon) {
	if (this.attendees[session.playerId] == null) {
		logger.i("removing player attending compass station '{0}' but did not enter before".format(session.sessionId));
		return;
	} else {
		logger.i("removing player attending compass station '{0}'".format(session.sessionId));
	}
	
	this.attendees[session.playerId] = null;
};
