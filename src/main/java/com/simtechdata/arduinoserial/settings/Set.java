package com.simtechdata.arduinoserial.settings;

import java.util.prefs.Preferences;

public class Set {

	private final Preferences prefs = LABEL.prefs;

	public void screenWidth(Double value) {
		AppSettings.clear().screenWidth();
		prefs.putDouble(LABEL.WIDTH.Name(), value);
	}

	public void screenHeight(Double value) {
		AppSettings.clear().screenHeight();
		prefs.putDouble(LABEL.HEIGHT.Name(), value);
	}

	public void filterList(String value) {
		AppSettings.clear().filterList();
		prefs.put(LABEL.FILTER_LIST.Name(), value);
	}

	public void filterWindowWidth(double value) {
		AppSettings.clear().filterWindowWidth();
		prefs.putDouble(LABEL.FILTER_WIDTH.Name(),value);
	}

	public void filterWindowHeight(double value) {
		AppSettings.clear().filterWindowHeight();
		prefs.putDouble(LABEL.FILTER_HEIGHT.Name(),value);
	}

	public void saveFilter(boolean value) {
		AppSettings.clear().saveFilter();
		prefs.putBoolean(LABEL.SAVE_FILTER.Name(), value);
	}
}
