package com.simtechdata.arduinoserial.settings;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Clear {

	private final Preferences prefs = LABEL.prefs;

	public void screenWidth() {
		prefs.remove(LABEL.WIDTH.Name());
	}

	public void screenHeight() {
		prefs.remove(LABEL.HEIGHT.Name());
	}

	public void filterWindowWidth() {
		prefs.remove(LABEL.FILTER_WIDTH.Name());
	}

	public void filterWindowHeight() {
		prefs.remove(LABEL.FILTER_HEIGHT.Name());
	}

	public void filterLists() {
		prefs.remove(LABEL.FILTER_LISTS.Name());
	}

	public void lastVersion() {
		prefs.remove(LABEL.LAST_VERSION.Name());
	}

	public void thisVersion() {
		prefs.remove(LABEL.THIS_VERSION.Name());
	}



	public void masterReset() {
		try {
			prefs.clear();
		}
		catch (BackingStoreException e) {
			throw new RuntimeException(e);
		}
	}

}
