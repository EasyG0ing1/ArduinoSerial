package com.simtechdata.arduinoserial.serial;

import com.fazecast.jSerialComm.SerialPort;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.HashMap;
import java.util.Map;

public class PortMap {

	public static final  Map<String, AppSerialPort>   map             = new HashMap<>();
	private static final Map<String, BooleanProperty> openPropertyMap = new HashMap<>();

	public static void put(String description, SerialPort serialPort) {
		if (!map.containsKey(description)) {
			serialPort.setComPortParameters(115200, 8, 1, 0);
			map.put(description, new AppSerialPort(serialPort));
			openPropertyMap.put(description, new SimpleBooleanProperty(false));
		}
	}

	public static SerialPort get(String description) {
		return map.getOrDefault(description, null).getSerialPort();
	}

	public static boolean open(String description) {
		boolean open = false;
		if (have(description)) {
			open = map.get(description).open(1000);
		}
		openPropertyMap.get(description).setValue(open);
		return open;
	}

	public static boolean close(String description) {
		boolean closed = false;
		if (have(description)) {
			closed = map.get(description).close();
			openPropertyMap.get(description).setValue(!closed);
		}
		return closed;
	}

	public static boolean wasOpen(String description) {
		if (have(description)) {
			return map.get(description).wasOpen();
		}
		return false;
	}

	public static boolean isOpen(String description) {
		if (have(description)) {
			return map.get(description).getSerialPort().isOpen();
		}
		return false;
	}

	public static boolean isClosed(String description) {
		if (have(description)) {
			return !map.get(description).getSerialPort().isOpen();
		}
		return true;
	}

	public static void send(String description, String text) {
		if (have(description)) {
			map.get(description).sendText(text);
		}
	}

	public static BooleanBinding notOpenProperty(String description) {
		if (have(description)) {
			return openPropertyMap.get(description).not();
		}
		return null;
	}

	public static BooleanProperty openProperty(String description) {
		if (have(description)) {
			return openPropertyMap.get(description);
		}
		return null;
	}

	public static boolean have(String description) {
		return map.containsKey(description);
	}

}
