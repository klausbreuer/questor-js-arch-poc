/**
 * The EndStation is a logical station which allow notifying the runtime whether a game is lost or
 * won.
 *
 * Entering an EndStation ends the whole simulation!
 *
 * The EndStation has no renderer equivalent (yet).
 */
 
EndStation = function(returnCode) {
	this.returnCode = returnCode;
};

EndStation.prototype.onEnter = function(session) {
	logger.i("endstation: " + session.stationId);
	runtime.exit(this.returnCode);
};

EndStation.prototype.onLeave = function(session) {
};

