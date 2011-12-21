<script>

var Renderer = {};

Renderer.QuizStation = function (pQuestion) {    
	this.question = pQuestion;
	runtime.showQuizStation(pQuestion);
};


Renderer.QuizStation.prototype.onSubmit = function (pAnswer) {
	showToast (this.question + " = " + pAnswer);
};


function showToast(toast) {
	runtime.showToast(toast);
};




</script>