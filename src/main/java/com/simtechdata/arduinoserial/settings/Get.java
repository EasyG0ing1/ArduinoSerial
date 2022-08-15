package com.simtechdata.arduinoserial.settings;

import java.util.prefs.Preferences;

public class Get {

	private final Preferences prefs = LABEL.prefs;

	public Double screenWidth() {
		double value = prefs.getDouble(LABEL.WIDTH.Name(), 500.0);
		return ((value < 500) ? 500 : value);
	}

	public Double screenHeight() {
		double value =  prefs.getDouble(LABEL.HEIGHT.Name(), 300.0);
		return ((value < 300) ? 300 : value);
	}

	public String getFilterList() {
		return prefs.get(LABEL.FILTER_LIST.Name(), "");
	}

	public Double filterWindowWidth() {
		return prefs.getDouble(LABEL.FILTER_WIDTH.Name(),400);
	}

	public Double filterWindowHeight() {
		return prefs.getDouble(LABEL.FILTER_HEIGHT.Name(),500);
	}

	public boolean saveFilter() {
		return prefs.getBoolean(LABEL.SAVE_FILTER.Name(), false);
	}
}
