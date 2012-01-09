package de.questor.poc.jsarch.renderer.compass;

import de.questor.poc.jsarch.renderer.OnChoiceListener;


public interface StationView {
	public void addOnChoiceListener(OnChoiceListener listener);
	public void removeOnChoiceListener(OnChoiceListener listener);
}
