function createNewScript(newscript) {
    var ss = document.createElement("script");
	var scr = newscript;
    var tt = document.createTextNode(scr);
    ss.appendChild(tt);
    var hh = document.getElementsByTagName('head')[0];
    hh.appendChild(ss);
}

function Simulator () {
}

Simulator.prototype.onEnter = function(playerId, stationId) {
    logger.i("onEnter " + playerId + " - " + stationId);
};

