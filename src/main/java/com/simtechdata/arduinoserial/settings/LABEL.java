package com.simtechdata.arduinoserial.settings;

import java.util.prefs.Preferences;

public enum LABEL {
	WIDTH,
	HEIGHT,
	FILTER_WIDTH,
	FILTER_HEIGHT,
	FILTER_LISTS,
	LAST_VERSION,
	THIS_VERSION
	;

	public String Name(LABEL this) {
		return switch(this){
			case WIDTH -> "ScreenWidth";
			case HEIGHT -> "ScreenHeight";
			case FILTER_WIDTH -> "FilterWindowWidth";
			case FILTER_HEIGHT -> "FilterWindowHeight";
			case FILTER_LISTS -> "FilterLists";
			case LAST_VERSION -> "LastVersion";
			case THIS_VERSION -> "ThisVersion";
		};
	}

	public static final Preferences prefs = Preferences.userNodeForPackage(LABEL.class);

}
