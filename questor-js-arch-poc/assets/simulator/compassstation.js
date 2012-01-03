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
};


CompassStation.prototype.onEnter = function(session) {
	simulator.sendCreateStation(session, this.generateJavascript());
	
	// for testing only: sending periodically positions of POIs to testspieler:
	var self = this;
	setInterval ( function(){self.sendPoiPosition(simulator.sessions['testspieler']);}, 5000 );	
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
	simulator.performTransition(session, msg);
};

CompassStation.prototype.generateJavascript = function() {
	var generatorCode = 
		"var c = new Renderer.CompassStation ();"
		+ "c.show();";
	return generatorCode;
};
