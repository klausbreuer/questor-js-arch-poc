Renderer = function(implementationId) {
	this.implementationId = implementationId;
	
	this.station = null;
	this.apiLevel = 1;
	
	this.sessionId = null;
};

Renderer.prototype.join = function(playerId) {
	var obj = {
			type: "join",
			playerId: playerId,
			implementationId: this.implementationId,
			apiLevel: this.apiLevel
	};
	
	runtime.sendReplyInternal(null, JSON.stringify(obj));
}

Renderer.prototype.onMessage = function(sessionId, msg) {
	this.sessionId = sessionId;
	
	logger.i("Renderer.onMessage('{0}', '{1}'}".format(sessionId, msg));

	var msgObj = null;

	try {
		msgObj = JSON.parse(msg);
	} catch (e) {
		logger.e("Parsing message failed: " + e);
		return;
	}

	switch (msgObj.type) {
	case 'joinResponse':
		logger.i("Connection attempt to Simulator: '{0}'".format(msgObj.implementationId));
		
		if (msgObj.success) {
			logger.e("Simulator accepted us.");
			runtime.sendReplyInternal(sessionId, JSON.stringify({ type: "start" }));
		} else {
			logger.e("Simulator did not accept us.");
			// Provokes disconnect
			runtime.sendReplyInternal(sessionId, null);
		}
		
		break;
	case 'create':
		// a create message
		if (this.station != null) {
			this.station.onLeave();
		}

		var station = null;
		try {
			station = eval("new " + msgObj.stationClass);
		} catch (e) {
			logger.e("Execution of station create code failed. Check the syntax!");
			logger.e("Problem was: " + e);
		}
		station.init(msgObj.data);
		
		station.show();
		
		this.station = station;

		break;
	case 'station':
		if (this.station == null) {
			logger.e("Message of type '{0}' received but no station instance exists."
							.format(msgObj.type));
			return;
		}
		this.station.onMessage(msgObj.data);

		break;
	default:
		logger.e("Unknown message type: " + msgObj.type);
		break;
	}

};

Renderer.prototype.sendReply = function(data) {
	var obj = {
		type : "reply",
		data : data
	};

	runtime.sendReplyInternal(this.sessionId, JSON.stringify(obj));
}
