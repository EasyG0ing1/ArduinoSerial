package com.simtechdata.arduinoserial.settings;

import java.util.prefs.Preferences;

public enum LABEL {
	WIDTH,
	HEIGHT,
	FILTER_LIST,
	FILTER_WIDTH,
	FILTER_HEIGHT,
	SAVE_FILTER
	;

	public String Name(LABEL this) {
		return switch(this){
			case WIDTH -> "ScreenWidth";
			case HEIGHT -> "ScreenHeight";
			case FILTER_LIST -> "FilterList";
			case FILTER_WIDTH -> "FilterWindowWidth";
			case FILTER_HEIGHT -> "FilterWindowHeight";
			case SAVE_FILTER -> "SaveFilterList";
		};
	}

	public static final Preferences prefs = Preferences.userNodeForPackage(LABEL.class);

}
