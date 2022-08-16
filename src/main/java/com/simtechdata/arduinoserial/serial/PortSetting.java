package com.simtechdata.arduinoserial.serial;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;

public class PortSetting {

	public PortSetting(int baud, int dataBits, int stopBits, int parity) {
		this.baud     = baud;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity   = parity;
	}

	private final int baud;
	private final int dataBits;
	private final int stopBits;
	private final int parity;

	public static ObservableList<Integer> getBaudRates() {
		return FXCollections.observableArrayList(Arrays.asList(9600,28800,38400,57600,115200,230400,460800));
	}

	public static PortSetting getDefault() {
		return new PortSetting(9600,8,1,0);
	}

	public int baud() {
		return baud;
	}

	public int dataBits() {
		return dataBits;
	}

	public int stopBits() {
		return stopBits;
	}

	public int parity() {
		return parity;
	}
}
