Renderer = function() {
	this.station = null;
};

Renderer.prototype.onMessage = function(type, msg) {
	switch (type) {
		case 'create':
			// a create message 
			if (this.station != null) {
				this.station.onLeave();
			}
			
			try {
				eval(msg);
			} catch (e) {
				logger.e("Execution of station create code failed. Check the syntax!");
				logger.e("Problem was: " + e);
			}
			
			break;
		default:
			if (this.station == null) {
				logger.e("Message of type '{0}' received but no station instance exists.".format(type));
				return;
			}
			this.station.onMessage(type, msg);
			
			break;
	}
	
};
