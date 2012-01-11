Renderer = function() {
	this.station = null;
};

Renderer.prototype.onMessage = function(msg) {
	logger.i("Renderer.onMessage('{0}'}".format(msg));

	var msgObj = null;

	try {
		msgObj = JSON.parse(msg);
	} catch (e) {
		logger.e("Parsing message failed: " + e);
		return;
	}

	switch (msgObj.type) {
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

	runtime.sendReplyInternal(JSON.stringify(obj));
}
