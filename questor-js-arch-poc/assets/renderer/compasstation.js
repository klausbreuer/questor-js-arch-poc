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
}

Renderer.CompassStation.prototype.show = function () {
	makeCurrent(this);
	
	locationService.addTarget("station");
	
	runtime.showCompassStation();
};

Renderer.CompassStation.prototype.onMessage = function (type, msg) {
	if (type == "poiPos") {
		// tell poi positions to CompassView
		this.sendMessage(type, msg);
	} else if (type == "playerPos") {
		// tell player positions to CompassView
		this.sendMessage(type, msg);
	}
}

Renderer.CompassStation.prototype.sendMessage = function (pType, pMsg) {
	runtime.sendMessageToCompassStation(pType, pMsg);
};

