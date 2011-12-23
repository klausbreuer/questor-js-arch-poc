package de.questor.poc.jsarch.simulator;

/** Server sided QuizStation.
 * 
 * TODO: To be implemented in Javascript at some point.
 * 
 * @author rschus
 *
 */
public class QuizStation {

	private Simulator sim;

	private String question;

	private String buttonText;

	private String answer;

	private String stationSuccess;

	private String stationFail;

	QuizStation(Simulator sim, String question, String buttonText,
			String answer, String stationSuccess, String stationFail) {
		this.sim = sim;
		this.question = question;
		this.buttonText = buttonText;
		this.answer = answer;
		this.stationSuccess = stationSuccess;
		this.stationFail = stationFail;
	}

	public void onEnter(Simulator.Session session) {
		sim.sendCreateStation(session, generateJavascript());
	}

	public void onMessage(Simulator.Session session, String msg) {
		if (answer.equals(msg)) {
			sim.performTransition(session, stationSuccess);
		} else
		{
			sim.performTransition(session, stationFail);
		}
	}

	private String generateJavascript() {
		String submitCode = "runtime.sendReply(this.getFieldText()); ";
		
		String generatorCode = String.format(
				"var q = new Renderer.QuizStationHtml ();"
				+ "q.setQuestion('%s'); "
				+ "q.setButtonText('%s'); "
				+ "q.onSubmit = function() { %s };"
				+ "q.show();", question, buttonText, submitCode);
		
		return generatorCode;
	}
	
}
