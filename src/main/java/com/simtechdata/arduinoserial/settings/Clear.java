package com.simtechdata.arduinoserial.settings;

import java.util.prefs.Preferences;

public class Clear {

	private final Preferences prefs = LABEL.prefs;

	public void screenWidth() {
		prefs.remove(LABEL.WIDTH.Name());
	}

	public void screenHeight() {
		prefs.remove(LABEL.HEIGHT.Name());
	}

	public void filterList() {
		prefs.remove(LABEL.FILTER_LIST.Name());
	}

	public void filterWindowWidth() {
		prefs.remove(LABEL.FILTER_WIDTH.Name());
	}

	public void filterWindowHeight() {
		prefs.remove(LABEL.FILTER_HEIGHT.Name());
	}

	public void saveFilter() {
		prefs.remove(LABEL.SAVE_FILTER.Name());
	}

}
