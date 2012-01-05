/*****************************************************
 * CompassStation
 * 
 * example for an implementation of a station as a native activity
 * 
 */

Renderer.CompassStation = function () {
};


Renderer.CompassStation.prototype.show = function () {
	runtime.showCompassStation();
}

Renderer.CompassStation.prototype.sendMessage = function (pType, pMsg) {
	runtime.sendMessageToCompassStation(pType, pMsg);
}

