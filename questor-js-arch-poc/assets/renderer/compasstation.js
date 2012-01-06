/*****************************************************
 * CompassStation
 * 
 * example for an implementation of a station as a native activity
 * 
 */

Renderer.CompassStation = function () {
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
	if (type == "poiPos") {
		// tell poi positions to CompassViews
		this.sendMessage(type, msg);
	} else if (type == "playerPos") {
		// tell player positions to CompassView
		//this.sendMessage(type, msg);
		try {
			var list = JSON.parse(msg);
			for (var i in list) {
				var pos = list[i];
				
				// TODO: Find a way to send to CompassStation directly, so that we do not
				// have to transform the values again into something
				var formatted = "{0},{1},{2}".format(pos.playerId, pos.lon, pos.lat);
				runtime.sendMessageToCompassStation("playerPos", formatted);
			}
		} catch (e) {
			logger.e("error parsing message to object: " + e);
		}
	}
}

Renderer.CompassStation.prototype.sendMessage = function (pType, pMsg) {
	runtime.sendMessageToCompassStation(pType, pMsg);
};

