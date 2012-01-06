/*****************************************************
 * CompassStation
 * 
 * example for an implementation of a station as a native activity
 * 
 */

Renderer.CompassStation = function () {
};


Renderer.CompassStation.prototype.show = function () {
	makeCurrent(this);
	runtime.showCompassStation();
};

Renderer.CompassStation.prototype.onMessage = function (type, msg) {
	logger.i("onMessage: " + type + ", " + msg);
}

Renderer.CompassStation.prototype.sendMessage = function (pType, pMsg) {
	runtime.sendMessageToCompassStation(pType, pMsg);
};

