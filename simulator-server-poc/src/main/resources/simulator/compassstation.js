/**
 * The CompassStation
 * 
 * Elements to customize: - a list of POIs to be shown as crosses in the compass -
 * the positions of the other players - a station for success and a station for
 * fail
 * 
 */
CompassStation = function(pStationSuccess, pStationFail) {
	station = this;
	this.stationSuccess = pStationSuccess;
	this.stationFail = pStationFail;

	var helper = this;

	this.updateFunction = function() {
		try {
			helper.sendAttendeePositions();

			// TEST CODE: Send random positions to all players.
			for ( var i in helper.attendees.entries) {
				var a = helper.attendees.entries[i];
				if (a != null) {
					helper.sendPoiPosition(a.session);
				}
			}
		} catch (e) {
			logger.e("Failed update function: " + e);
		}
	};

	// Keeps the player sessions and their locations
	this.attendees = new AttendeeList(function() {
		try {
			clearInterval(helper.interval);
		} catch (e) {
			logger.e("when zero function failed: " + e);
		}
	}, function() {
		try {
			setInterval(helper.updateFunction, 5000);
		} catch (e) {
			logger.e("when one function failed: " + e);
		}
	});

};

CompassStation.prototype.onEnter = function(session) {
	var obj = {};
	simulator.sendCreateMessage(session, "CompassStation", obj);

	this.attendees.add(session);

	// Testcode: Hardcodes another 2 players who are in this station as well.
	try {
		var testsession = new Session("testsession1");
		testsession.station = this;
		testsession.stationId = "test";
		this.attendees.add(testsession);
		this.attendees.update(testsession, {
			lon : 4921875,
			lat : 45644768
		});

		testsession = new Session("testsession2");
		testsession.station = this;
		testsession.stationId = "test";
		this.attendees.add(testsession);
		this.attendees.update(testsession, {
			lon : 4921875,
			lat : 56365250
		});
	} catch (e) {
		logger.i("exception: " + e);
	}

};

CompassStation.prototype.sendPoiPosition = function(session) {
	// testing: sends random positions for up to four POIs:

	// helping function to generate a random number:
	function randomFromTo(from, to) {
		return Math.floor(Math.random() * (to - from + 1) + from);
	}

	var amount = randomFromTo(1, 4);

	var list = new Array();
	for ( var i = 0; i < amount; i++) {
		var lon = randomFromTo(4921875, 15732422);
		var lat = randomFromTo(45644768, 56365250);

		var obj = {
			id : i,
			lon : lon,
			lat : lat
		};

		list.push(obj);
	}

	if (list.length > 0) {
		var data = {
			type : "poiPos",
			list : list
		};

		simulator.sendStationMessage(session, data);
	}

};

CompassStation.prototype.onMessage = function(session, data) {
	switch (data.type) {
	case 'cheatOut':
		this.attendees.remove(session);
		simulator.performTransition(session, this.stationSuccess);
		break;
	case 'playerPos':
		this.attendees.update(session, {
			lon : data.lon,
			lat : data.lat
		});
		break;
	default:
		logger.e("Unknown message type: " + data.type);
		return;
	}

};

CompassStation.prototype.sendAttendeePositions = function() {
	var h = this;
	var helper = function(attendees, current) {
		var list = new Array();

		for ( var i in attendees.entries) {
			var a = attendees.entries[i];
			if (a != null && a != current && a.data != null) {
				var obj = {
					id : a.session.playerId,
					lon : a.data.lon,
					lat : a.data.lat
				};

				list.push(obj);
			}
		}

		if (list.length > 0) {
			var data = {
				type : "playerPos",
				list : list
			};

			simulator.sendStationMessage(current.session, data);
		}

	}

	try {
		for ( var i in this.attendees.entries) {
			var a = this.attendees.entries[i];

			if (a != null) {
				helper(this.attendees, a);
			}
		}
	} catch (e) {
		logger.i("error: " + e);
	}
};
