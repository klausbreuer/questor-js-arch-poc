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
	
	// Testcode: Hardcodes another 2 players who are in this station as well.
	try {
		var testsession = new Session("testsession1");
		testsession.station = this;
		testsession.stationId = "test";
		this.addAttendee(testsession);
		this.updateAttendee(testsession, 4921875, 45644768);

		testsession = new Session("testsession2");
		testsession.station = this;
		testsession.stationId = "test";
		this.addAttendee(testsession);
		this.updateAttendee(testsession, 4921875, 56365250);
	} catch (e) {
		logger.i("exception: " + e);
	}
	
	this.interval = setInterval (
			function() {
				helper.sendAttendeePositions();

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
	
	//helping function to generate a random number:
	function randomFromTo(from, to){
		return Math.floor(Math.random() * (to - from + 1) + from);
	}	
	
	var amount  = randomFromTo(1, 4);
	
	var list = new Array();
	for (var i = 0; i < amount; i++) {
		var lon = randomFromTo(4921875, 15732422);
		var lat = randomFromTo(45644768, 56365250);
		
		var obj = {
				id:i,
				lon:lon,
				lat:lat
		};
		
		list.push(obj);
	}
	
	if (list.length > 0) {
		var msg = JSON.stringify(list);
		simulator.sendMessage("poiPos", session, msg);
	}
	
};

CompassStation.prototype.onMessage = function(session, msg) {
	var msgObj = null;
	try {
		// Unpack the message string and make a JavaScript object
		// out of it before handing it over to the station
		msgObj = JSON.parse(msg);
	} catch (e) {
		logger.e("some problem: " + e);
		return;
	}
	
	if (msgObj.type == "cheatOut") {
		
		this.removeAttendee(session);
		clearInterval(this.interval);
		simulator.performTransition(session, this.stationSuccess);
		
	} else if(msgObj.type == "playerPos") {
		this.updateAttendee(session, msgObj.lon, msgObj.lat);
	}
	
};

CompassStation.prototype.generateJavascript = function() {
	var generatorCode = 
		"var c = new CompassStation ();"
		+ "c.show();";
	return generatorCode;
};

CompassStation.prototype.sendAttendeePositions = function() {
	var h = this;
	var helper = function(attendees, current) {
		var list = new Array();
		
		for (i in attendees) {
			a = attendees[i];
			if (a != null && a != current) {
				var obj = {
						id:a.session.playerId,
						lon:a.lon,
						lat:a.lat
				};
				
				list.push(obj);
			}
		}
		
		if (list.length > 0) {
			var msg = JSON.stringify(list);
			simulator.sendMessage("playerPos", current.session, msg);
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
		logger.i("new player attending compassstation '{0}': {1}".format(session.stationId, session.playerId));
	} else {
		logger.i("new player attending compassstation '{0}' but was already in attendee list".format(session.stationId));
	}
	
	var newattendee = this.attendees[session.playerId] = new Object();
	newattendee.session = session;
	newattendee.lon = null;
	newattendee.lat = null;
};

CompassStation.prototype.updateAttendee = function(session, lon, lat) {
	if (this.attendees[session.playerId] == null) {
		logger.i("updating player attending compass station '{0}' but did not enter before".format(session.stationId));
		return;
	} else {
		logger.i("updating player attending compass station '{0}'".format(session.stationId));
	}
	
	var attendee = this.attendees[session.playerId];
	attendee.lon = lon;
	attendee.lat = lat;
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
