var stations = new Object();

// What follows is an actual 'game' configuration.
stations["1st_question"] = new QuizStation(
		"Wie toll ist Questor im Netzwerk?",
		"Antworten", "sehr", "2nd_question", "fail");

stations["2nd_question"] = new QuizStation("5 + 5 = ?", "Antworten", "10",
		"1st_html", "fail");

stations["1st_html"] = new HtmlStation(
		"<p>At the foot of the hill, the path splits into two directions, "
				+ "both leading into a large wood. "
				+ "You can take "
				+ "the <choice target=\"1st_question\">right</choice> "
				+ "or <choice target=\"2nd_question\">left</choice> "
				+ "or <choice target=\"1st_html\">up</choice> "
				+ "or <choice target=\"2nd_html\">down</choice> track into the wood.</p>");

stations["2nd_html"] = new HtmlStation(
		"<p>Hey!! Super! Du hast den Ausgang gefunden!!! "
				+ "<br><choice target=\"success\">Hier</choice> gehts raus...</p>");

stations["success"] = new EndStation(0);
stations["fail"] = new EndStation(1);

simulator.setGame("1st_question", stations);