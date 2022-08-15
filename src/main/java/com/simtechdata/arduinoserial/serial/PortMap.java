package com.simtechdata.arduinoserial.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import org.hildan.fxgson.FxGson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PortMap {

	public static final Map<String, AppSerialPort> map         = new HashMap<>();
	private static      FilterLists                filterLists = new FilterLists();

	public static void put(String comPort, SerialPort serialPort) {
		if (!map.containsKey(comPort)) {
			serialPort.setComPortParameters(115200, 8, 1, 0);
			map.put(comPort, new AppSerialPort(serialPort));
			filterLists.newOpenProperty(comPort);
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
		filterLists.setOpen(comPort, open);
		return open;
	}

	public static boolean close(String comPort) {
		boolean closed = false;
		if (have(comPort)) {
			closed = map.get(comPort).close();
			filterLists.setOpen(comPort, !closed);
		}
		return closed;
	}

	public static boolean isOpen(String comPort) {
		if (have(comPort)) {
			return map.get(comPort).getSerialPort().isOpen();
		}
		return false;
	}

	public static boolean isClosed(String comPort) {
		if (have(comPort)) {
			return !map.get(comPort).getSerialPort().isOpen();
		}
		return true;
	}

	public static void send(String comPort, String text) {
		if (have(comPort)) {
			map.get(comPort).sendText(text);
		}
	}

	public static void setFilterLists(String json) {
		GsonBuilder g    = new GsonBuilder();
		Gson        gson = FxGson.addFxSupport(g).setPrettyPrinting().create();
		filterLists = gson.fromJson(json, FilterLists.class);
	}

	public static List<String> getFilterList(String comPort) {
		return filterLists.getFilterList(comPort);
	}

	public static void setFilterList(String comPort, List<String> filterList) {
		filterLists.setFilterList(comPort, filterList);
	}

	public static String getJsonFilterLists() {
		GsonBuilder g    = new GsonBuilder();
		Gson        gson = FxGson.addFxSupport(g).enableComplexMapKeySerialization().setPrettyPrinting().create();
		return gson.toJson(filterLists);
	}

	public static Boolean hasFilterList(String comPort) {
		return filterLists.hasFilterList(comPort);
	}

	public static void clearFilterList(String comPort) {
		filterLists.clearFilterList(comPort);
	}

	public static Boolean clearOnNew(String comPort) {
		return filterLists.clearOnNew(comPort);
	}

	public static void setClearOnNew(String comPort, boolean value) {
		filterLists.setClearOnNew(comPort, value);
	}

	public static Boolean keepOpen(String comPort) {
		return filterLists.keepOpen(comPort);
	}

	public static void setKeepOpen(String comPort, boolean value) {
		filterLists.setKeepOpen(comPort, value);
	}

	public static BooleanBinding notOpenProperty(String comPort) {
		return filterLists.notOpenProperty(comPort);
	}

	public static BooleanProperty openProperty(String comPort) {
		return filterLists.openProperty(comPort);
	}

	public static boolean have(String comPort) {
		return map.containsKey(comPort);
	}

}
