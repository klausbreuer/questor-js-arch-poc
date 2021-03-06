/*****************************************************
 * HtmlStation
 * 
 * The normal, simple HtmlStation 
 * <choice> tags are replaced by calls to runtime.sendReply()
 * 
 */

HtmlStation = function () {
	if (htmlDelegate == null) {
		logger.e("HtmlDelegate not initialized. HtmlStation *WILL NOT* work!");
	}
	
	this.content = "";
};

HtmlStation.prototype.init = function (obj) {    
	this.content = obj.content;
};

HtmlStation.prototype.show = function () {
	
	// We replace the <choice target="xx"> tags by a call to runtime.sendReply
	// not finished yet...
	var dom;
	dom = document.implementation.createHTMLDocument('');
	dom.write(this.content);
	
	//alert (new XMLSerializer().serializeToString(dom));   

	var choiceNode, choiceVal, choiceTxt, newA, parent;

	var arrChoices = dom.getElementsByTagName("choice");
	//for( var i=0; i < arrChoices.length; i++ ) {
	while (arrChoices.length > 0) {
		
		/* Klaus: Found a strange behavior about getElementsByTagName here!
		 * I assumed arrChoices is a simple array, containing a list of references to <choice> nodes.
		 * But when I processed them in for-loop and removed them at the end of the loop, the arrChoices array was updated!!
		 * In each loop, one element was removed from my array!
		 * getElementsByTagName returns a "live NodeList" (https://developer.mozilla.org/en/DOM/document.getElementsByTagName)
		 * So my for-loop always only processed half of the <choice> nodes!
		 * Using "while (arrChoices.length > 0)" instead of the for-loop solved the problem.
		 */
		
		//choiceNode = arrChoices[i];
		choiceNode = arrChoices[0];
		
		// 1. get the target and the text of the <choice>:
		choiceVal = choiceNode.getAttribute("target");
		choiceTxt = choiceNode.firstChild.nodeValue;

		// 2. create a new <a>, fill the href and the onClick attributes and add a textnode with the text:
		newA = dom.createElement("a");
		newA.setAttribute("href","#");
		newA.setAttribute("onClick","runtime.sendReply('" + choiceVal  + "'); return false;");
		newA.appendChild(dom.createTextNode(choiceTxt));
		
		// 3. replace the <choice> with the new <a>:
		parent = choiceNode.parentNode;
		parent.replaceChild(newA, choiceNode);
		
	}

	htmlDelegate.show(new XMLSerializer().serializeToString(dom));
};

HtmlStation.prototype.onMessage = function(data) {
	// Intentionally empty.
}

HtmlStation.prototype.onLeave = function() {
	// Intentionally empty.
}
