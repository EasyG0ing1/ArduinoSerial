package com.simtechdata.arduinoserial.settings;

import java.util.prefs.Preferences;

public class Set {

	private final Preferences prefs = LABEL.prefs;

	public void screenWidth(Double value) {
		if (value > 500){
			AppSettings.clear().screenWidth();
			prefs.putDouble(LABEL.WIDTH.Name(), value);
		}
	}

	public void screenHeight(Double value) {
		if (value > 300) {
			AppSettings.clear().screenHeight();
			prefs.putDouble(LABEL.HEIGHT.Name(), value);
		}
	}

	public void filterWindowWidth(double value) {
		if (value > 400) {
			AppSettings.clear().filterWindowWidth();
			prefs.putDouble(LABEL.FILTER_WIDTH.Name(),value);
		}
	}

	public void filterWindowHeight(double value) {
		if (value > 500) {
			AppSettings.clear().filterWindowHeight();
			prefs.putDouble(LABEL.FILTER_HEIGHT.Name(),value);
		}
	}

	public void appSettings(String value) {
		AppSettings.clear().appSettings();
		prefs.put(LABEL.APP_SETTINGS.Name(), value);
	}

	public void lastVersion(String value) {
		AppSettings.clear().lastVersion();
		prefs.put(LABEL.LAST_VERSION.Name(), value);
	}

	public void thisVersion(String value) {
		AppSettings.clear().thisVersion();
		prefs.put(LABEL.THIS_VERSION.Name(), value);
	}
}
