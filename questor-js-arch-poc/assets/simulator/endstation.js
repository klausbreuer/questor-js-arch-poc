EndStation = function(returnCode) {
	this.returnCode = returnCode;
};

EndStation.prototype.onEnter = function(session) {
	logger.i("endstation: " + session.stationId);
	runtime.exit(this.returnCode);
};
