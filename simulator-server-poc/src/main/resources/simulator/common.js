// Global variable to be initialized by the runtime.
var simulator;

String.prototype.format = function() {
	var formatted = this;
	for (arg = 0; arg < arguments.length; arg++) {
		formatted = formatted.replace("{" + arg + "}", arguments[arg]);
	}
	return formatted;
};

function checkSimulator() {
	if (logger == null) {
		// This function is not supposed to be implemented.
		LoggerIsNotAvailable_FixThisAndThisErrorWillGoAway();
	}

	if (runtime == null) {
		logger.e("runtime not available.");
	}
	
	if (simulator != null) {
		logger.i("simulator ready");
	} else {
		logger.e("simulator instance does not exist. Check constructor!"); 
	}
}
