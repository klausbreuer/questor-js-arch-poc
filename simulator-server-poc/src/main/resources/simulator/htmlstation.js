/**
 * The HtmlStation is a station to simply show Html.
 *
 * There is one elements to customize:
 * - The content to be shown
 *
 * It can have lot of exits and must have at least one.
 * To implement an exit, the content must have <choice> tags, e.g. <choice target="1st_question">some text</choice>
 * If "some text" it tapped, the game goes to the station "1st_question".
 *
 * 
 */

HtmlStation = function(pContent) {
	station = this;
	this.content = pContent;
};

HtmlStation.prototype.onEnter = function(session) {
	simulator.sendCreateStation(session, this.generateJavascript());
};

HtmlStation.prototype.onMessage = function(session, msg) {
	simulator.performTransition(session, msg);
};

HtmlStation.prototype.generateJavascript = function() {
	var generatorCode = 
		("var h = new Renderer.HtmlStation ();"
		+ "h.setContent('{0}'); "
		+ "h.show();").format(this.content);
	return generatorCode;
};
