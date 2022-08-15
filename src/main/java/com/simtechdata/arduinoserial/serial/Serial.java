package com.simtechdata.arduinoserial.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.simtechdata.arduinoserial.settings.AppSettings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class Serial {

	public Serial(BooleanProperty clearOnNew) {
		this.clearOnNew.bind(clearOnNew);
	}

	private final BooleanProperty        clearOnNew  = new SimpleBooleanProperty(false);
	private       ObservableList<String> filterList;
	private       boolean                checkFilter = false;

	public ObservableList<String> getSerialPorts() {
		String                 os          = System.getProperty("os.name");
		SerialPort[]           serialPorts = SerialPort.getCommPorts();
		ObservableList<String> list        = FXCollections.observableArrayList();
		for (SerialPort port : serialPorts) {
			String portName    = port.getSystemPortName().toLowerCase();
			String description = "";
			if (os.toLowerCase().contains("mac")) {
				if (portName.contains("cu.usb")) {
					description = port.getDescriptivePortName().trim();
					if (description.contains("USB")) description = description + " " + port.getSystemPortName().replaceAll("\\D", "");
					list.add(description);
				}
			}
			else {
				description = port.getDescriptivePortName().trim();
				list.add(description);
			}
			PortMap.put(description, port);
		}
		list.sort(Comparator.comparing(String::toString));
		return list;
	}

	public BooleanBinding disableWhenClosedProperty(String activePort) {
		return PortMap.notOpenProperty(activePort);
	}

	public BooleanProperty disableWhenOpenProperty(String activePort) {
		return PortMap.openProperty(activePort);
	}

	public boolean wasOpen(String serialPortDescription) {
		return PortMap.wasOpen(serialPortDescription);
	}

	public void send(String serialPortDescription, String text) {
		PortMap.send(serialPortDescription, text);
	}

	public boolean isClosed(String description) {
		return PortMap.isClosed(description);
	}

	public boolean isOpen(String description) {
		return PortMap.isOpen(description);
	}

	public void setFilterList(ObservableList<String> filterList) {
		this.filterList = filterList;
	}

	public boolean openPort(String description, StringProperty serialData) {
		if (PortMap.isClosed(description)) {
			if (PortMap.open(description)) {
				listen(description, serialData);
				return true;
			}
		}
		return false;
	}

	public void setCheckFilter(boolean checkFilter) {
		this.checkFilter = checkFilter;
	}

	public String closePort(String serialPortDescription) {
		if (PortMap.have(serialPortDescription)) {
			if (PortMap.isOpen(serialPortDescription)) {
				if (PortMap.close(serialPortDescription)) {
					return "Closed";
				}
				else {
					return "Failed";
				}
			}
			else {return "Closed";}
		}
		return "Port Does Not Exist";
	}

	private void listen(String description, StringProperty serialData) {
		if (PortMap.have(description)) {
			if (PortMap.isOpen(description)) {
				new Thread(() -> {
					SerialPort    serialPort = PortMap.get(description);
					StringBuilder sb         = new StringBuilder();
					if (AppSettings.get().saveFilter()) {
						String   list      = AppSettings.get().getFilterList();
						String[] listItems = list.split("");
						filterList = FXCollections.observableArrayList(listItems);
					}
					while (serialPort.isOpen()) {
						serialPort.flushIOBuffers();
						while (serialPort.isOpen() && (serialPort.bytesAvailable() == 0)) {
							sleep(100);
						}
						if (serialPort.isOpen()) {
							byte[] readBuffer = new byte[serialPort.bytesAvailable()];
							serialPort.readBytes(readBuffer, readBuffer.length);
							String dataIn = new String(readBuffer);
							if (checkFilter) {
								for (String filter : filterList) {
									if (dataIn.contains(filter)) {
										sb.append(dataIn);
										break;
									}
								}
							}
							else {
								if (clearOnNew.getValue().equals(true)) {
									sb = new StringBuilder(dataIn);
								}
								else {
									sb.append(dataIn);
								}
							}
							serialData.setValue(sb.toString());
						}
					}
				}).start();
			}
		}
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
