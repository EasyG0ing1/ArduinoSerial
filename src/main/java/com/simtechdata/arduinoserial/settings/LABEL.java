package com.simtechdata.arduinoserial.settings;

import java.util.prefs.Preferences;

public enum LABEL {
	WIDTH,
	HEIGHT,
	FILTER_WIDTH,
	FILTER_HEIGHT,
	APP_SETTINGS,
	LAST_VERSION,
	THIS_VERSION
	;

	public String Name(LABEL this) {
		return switch(this){
			case WIDTH -> "ScreenWidth";
			case HEIGHT -> "ScreenHeight";
			case FILTER_WIDTH -> "FilterWindowWidth";
			case FILTER_HEIGHT -> "FilterWindowHeight";
			case APP_SETTINGS -> "Settings";
			case LAST_VERSION -> "LastVersion";
			case THIS_VERSION -> "ThisVersion";
		};
	}

	public static final Preferences prefs = Preferences.userNodeForPackage(LABEL.class);

}
