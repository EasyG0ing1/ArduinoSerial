package com.simtechdata.arduinoserial.settings;

import java.util.prefs.Preferences;

public class Get {

	private final Preferences prefs = LABEL.prefs;

	public Double screenWidth() {
		double value = prefs.getDouble(LABEL.WIDTH.Name(), 500.0);
		return (value < 500) ? 500 : value;
	}

	public Double screenHeight() {
		double value =  prefs.getDouble(LABEL.HEIGHT.Name(), 300.0);
		return (value < 300) ? 300 : value;
	}

	public Double filterWindowWidth() {
		double value = prefs.getDouble(LABEL.FILTER_WIDTH.Name(),400);
		return (value < 400) ? 400 : value;
	}

	public Double filterWindowHeight() {
		double value = prefs.getDouble(LABEL.FILTER_HEIGHT.Name(),500);
		return (value < 500) ? 500 : value;
	}

	public String filterLists() {
		return prefs.get(LABEL.FILTER_LISTS.Name(), "");
	}

	public String thisVersion() {
		return prefs.get(LABEL.THIS_VERSION.Name(), "");
	}

	public String lastVersion() {
		return prefs.get(LABEL.LAST_VERSION.Name(), "0.0.0");
	}
}
