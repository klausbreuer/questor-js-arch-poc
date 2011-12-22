<script>

var Renderer = new Object;

Renderer.QuizStation = function (pQuestion) {    
	this.question = pQuestion;
	runtime.showQuizStation(pQuestion);
};


Renderer.QuizStation.prototype.onSubmit = function (pAnswer) {
	showToast (this.question + " = " + pAnswer);
};



Renderer.QuizStationHtml = function () {    

	this.dom = document.implementation.createHTMLDocument('');
	this.serializer = new XMLSerializer();
	this.question = '';
	
	var content;
	content = '<html><body>'; 
	content += '<div id="divQuestion"></div>';
	content += '<div id="divInput"><input id="inputAnswer"></div>';
	content += '<div id="divButton"><input id="btnAnswer" type="button" value="Antwort einloggen" ></div>';
	content += '</body></html>'; 
	
	this.dom.write(content);
	 /* alert (this.serializer.serializeToString(this.dom)); */  
};


Renderer.QuizStationHtml.prototype.setQuestion = function (pQuestion) {    

	this.question = pQuestion;
	this.dom.getElementById('divQuestion').appendChild(this.dom.createTextNode(this.question)); 
	 
	/* alert (this.serializer.serializeToString(this.dom)); */ 
};


Renderer.QuizStationHtml.prototype.setButtonText = function (pText) {    

	this.dom.getElementById('btnAnswer').setAttribute("value", pText); 
	 
};



Renderer.QuizStationHtml.prototype.show = function () {
	
	runtime.showHtmlStation(this.serializer.serializeToString(this.dom));
	
};





function showToast(toast) {
	runtime.showToast(toast);
};




</script>