package com.simtechdata.arduinoserial.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.simtechdata.arduinoserial.settings.AppSettings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import org.hildan.fxgson.FxGson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PortMap {

	public static final Map<String, AppSerialPort> map      = new HashMap<>();
	private static      Settings                   settings = new Settings();

	public static void put(String comPort, SerialPort serialPort) {
		if (!map.containsKey(comPort)) {
			PortSetting portSetting = settings.getPortSetting(comPort);
			int baud = portSetting.baud();
			int dataBits = portSetting.dataBits();
			int stopBits = portSetting.stopBits();
			int parity = portSetting.parity();
			settings.setPortSetting(comPort, portSetting);
			serialPort.setComPortParameters(baud,dataBits,stopBits,parity);
			map.put(comPort, new AppSerialPort(serialPort));
			settings.newOpenProperty(comPort);
		}
	}

	public static SerialPort get(String comPort) {
		return map.getOrDefault(comPort, null).getSerialPort();
	}

	public static boolean open(String comPort) {
		boolean open = false;
		if (have(comPort)) {
			open = map.get(comPort).open(1000);
		}
		settings.setOpen(comPort, open);
		return open;
	}

	public static boolean close(String comPort) {
		boolean closed = false;
		if (have(comPort)) {
			closed = map.get(comPort).close();
			settings.setOpen(comPort, !closed);
		}
		return closed;
	}

	public static boolean isOpen(String comPort) {
		if (have(comPort)) {
			return map.get(comPort).isOpen();
		}
		return false;
	}

	public static boolean isClosed(String comPort) {
		if (have(comPort)) {
			return !map.get(comPort).isOpen();
		}
		return true;
	}

	public static void send(String comPort, String text) {
		if (have(comPort)) {
			map.get(comPort).sendText(text);
		}
	}

	public static void loadSettings(String json) {
		GsonBuilder g    = new GsonBuilder();
		Gson        gson = FxGson.addFxSupport(g).setPrettyPrinting().create();
		settings = gson.fromJson(json, Settings.class);
		for(String comPort : map.keySet()) {
			map.get(comPort).applyPortSettings(settings.getPortSetting(comPort));
		}
	}

	public static List<String> getFilterList(String comPort) {
		return settings.getFilterList(comPort);
	}

	public static void setFilterList(String comPort, List<String> filterList) {
		settings.setFilterList(comPort, filterList);
		save();
	}

	public static String getJson() {
		GsonBuilder g    = new GsonBuilder();
		Gson        gson = FxGson.addFxSupport(g).enableComplexMapKeySerialization().setPrettyPrinting().create();
		return gson.toJson(settings);
	}

	public static Boolean hasFilterList(String comPort) {
		return settings.hasFilterList(comPort);
	}

	public static void clearFilterList(String comPort) {
		settings.clearFilterList(comPort);
		save();
	}

	public static Boolean clearOnNew(String comPort) {
		return settings.clearOnNew(comPort);
	}

	public static void setClearOnNew(String comPort, boolean value) {
		settings.setClearOnNew(comPort, value);
		save();
	}

	public static Boolean keepOpen(String comPort) {
		return settings.keepOpen(comPort);
	}

	public static void setKeepOpen(String comPort, boolean value) {
		settings.setKeepOpen(comPort, value);
		save();
	}

	public static BooleanBinding notOpenProperty(String comPort) {
		return settings.notOpenProperty(comPort);
	}

	public static BooleanProperty openProperty(String comPort) {
		return settings.openProperty(comPort);
	}

	public static boolean have(String comPort) {
		return map.containsKey(comPort);
	}

	public static PortSetting getPortSetting(String comPort) {
		return settings.getPortSetting(comPort);
	}

	public static void setPortSetting(String comPort, PortSetting portSetting) {
		settings.setPortSetting(comPort, portSetting);
		if(map.containsKey(comPort)) {
			map.get(comPort).applyPortSettings(portSetting);
			settings.setOpen(comPort, map.get(comPort).isOpen());
		}
		save();
	}

	public static Integer getBaudRate(String comPort) {
		return map.get(comPort).getBaudRate();
	}

	public static void resetOpenState(String comPort) {
		settings.resetOpenState(comPort);
	}

	private static void save() {
		AppSettings.set().appSettings(getJson());
	}

}
