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
	var obj = {
			content: this.content
	};
	
	simulator.sendCreateMessage(session, "HtmlStation", obj);
};

HtmlStation.prototype.onLeave = function(session) {
};

HtmlStation.prototype.onMessage = function(session, data) {
	simulator.performTransition(session, data);
};
