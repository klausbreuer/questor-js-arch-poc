/*****************************************************
 * CompassStation
 * 
 * example for an implementation of a station as a native activity
 * 
 */

Renderer.CompassStation = function () {
	if (compassDelegate == null) {
		logger.e("CompassDelegate not initialized. CompassStation *WILL NOT* work!");
	}
	
	// Implementation note: Assume this has been loaded from some data file.
	this.playerColor = 0x0000FF;
	this.poiColor = 0xFFFF0000;
};

Renderer.CompassStation.prototype.onLocationChanged = function(lon, lat) {
	logger.i("CompassStation::onLocationChanged({0}, {1})".format(lon, lat));
	
	var pos = {
			type:"playerPos",
			lon:lon,
			lat:lat
	};

	var msg = JSON.stringify(pos);
	runtime.sendReply(msg);
}

Renderer.CompassStation.prototype.show = function () {
	makeCurrent(this);
	
	locationService.addTarget("station");
	
	runtime.showCompassStation();
};

Renderer.CompassStation.prototype.onMessage = function (type, msg) {
	
	var updateFunction = null;
	if (type == "playerPos") {
		updateFunction = function(station, pos, lon, lat) {
			compassDelegate.updatePlayerPosition(pos, lon, lat, station.playerColor);
		};
	} else if (type == "poiPos") {
		updateFunction = function(station, pos, lon, lat) {
			compassDelegate.updatePoiPosition(pos, lon, lat, station.poiColor);
		};
	} else {
		logger.e("Unknown message type: " + type);
		return;
	}
	
	// As the format for player and poi position is identical we can use the same
	// parsing code.
	try {
		var list = JSON.parse(msg);
		for (var i in list) {
			var pos = list[i];
			
			updateFunction(station, pos.id, pos.lon, pos.lat);
		}
	} catch (e) {
		logger.e("error parsing message to object: " + e);
	}
	
}

Renderer.CompassStation.prototype.sendMessage = function (pType, pMsg) {
	runtime.sendMessageToCompassStation(pType, pMsg);
};

