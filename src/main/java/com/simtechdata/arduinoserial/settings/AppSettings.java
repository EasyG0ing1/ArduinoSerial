package com.simtechdata.arduinoserial.settings;

public class AppSettings {

	private static final Get   getter  = new Get();
	private static final Set   setter = new Set();
	private static final Clear clear  = new Clear();

	public static Get get() {
		return getter;
	}

	public static Set set() {
		return setter;
	}

	public static Clear clear() {
		return clear;
	}

}
