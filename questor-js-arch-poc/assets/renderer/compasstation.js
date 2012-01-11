/*******************************************************************************
 * CompassStation
 * 
 * example for an implementation of a station as a native activity
 * 
 */

CompassStation = function() {
	if (compassDelegate == null) {
		logger
				.e("CompassDelegate not initialized. CompassStation *WILL NOT* work!");
	}

	// Implementation note: Assume this has been loaded from some data file.
	this.playerColor = 0x0000FF;
	this.poiColor = 0xFFFF0000;
};

CompassStation.prototype.init = function(obj) {
	// Intentionally empty.
};

CompassStation.prototype.onLocationChanged = function(lon, lat) {
	logger.i("CompassStation::onLocationChanged({0}, {1})".format(lon, lat));

	var pos = {
		type: "playerPos",
		lon: lon,
		lat: lat
	};

	renderer.sendReply(pos);
}

CompassStation.prototype.show = function() {
	locationService.addTarget("renderer.station");

	compassDelegate.show();
};

CompassStation.prototype.onMessage = function(data) {

	var updateFunction = null;
	switch (data.type) {
	case 'playerPos':
		updateFunction = function(station, pos, lon, lat) {
			compassDelegate.updatePlayerPosition(pos, lon, lat,
					renderer.station.playerColor);
		};
		break;
	case 'poiPos':
		updateFunction = function(station, pos, lon, lat) {
			compassDelegate.updatePoiPosition(pos, lon, lat, renderer.station.poiColor);
		};
		break;
	default:
		logger.e("Unknown message type: " + data.type);
		return;
	}

	// As the format for player and poi position is identical we can use the
	// same
	// parsing code.
	try {
		var list = data.list;
		for ( var i in list) {
			var pos = list[i];

			updateFunction(station, pos.id, pos.lon, pos.lat);
		}
	} catch (e) {
		logger.e("error parsing message to object: " + e);
	}

}

CompassStation.prototype.onLeave = function() {
	// Nothing to do yet.
}
