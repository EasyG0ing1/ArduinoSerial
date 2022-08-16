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

	public boolean close(String caller) {
		wasOpen = serialPort.closePort();
		System.out.println("Port Closed by " + caller);
		return wasOpen;
	}

	public void sendText(String text) {
		String writeString = text + "\n";
		byte[] bytes       = writeString.getBytes();
		if (serialPort.isOpen()) {serialPort.writeBytes(bytes, bytes.length);}
	}

	public void applyPortSettings(PortSetting portSetting) {
		boolean portOpen = serialPort.isOpen();
		boolean applySettings = true;
		if (portOpen) {
			applySettings = serialPort.closePort();
		}
		if (applySettings) {
			int baud = portSetting.baud();
			int dataBits = portSetting.dataBits();
			int stopBits = portSetting.stopBits();
			int parity = portSetting.parity();
			serialPort.setComPortParameters(baud,dataBits,stopBits,parity);
			if(portOpen){
				serialPort.openPort(1000);
			}
		}
	}

	public Boolean isOpen() {
		return serialPort.isOpen();
	}
}
