package com.simtechdata.arduinoserial.serial;

import com.fazecast.jSerialComm.SerialPort;


public class AppSerialPort {

	public AppSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}

	private final SerialPort serialPort;
	private       boolean    open;
	private       boolean wasOpen  = false;

	public SerialPort getSerialPort() {
		return serialPort;
	}

	public boolean open(int safetySleepTime) {
		open    = serialPort.openPort(safetySleepTime);
		wasOpen = false;
		return open;
	}

	public boolean close() {
		wasOpen = serialPort.closePort();
		return wasOpen;
	}

	public void sendText(String text) {
		String writeString = text + "\n";
		byte[] bytes       = writeString.getBytes();
		if (serialPort.isOpen()) {serialPort.writeBytes(bytes, bytes.length);}
	}
}
