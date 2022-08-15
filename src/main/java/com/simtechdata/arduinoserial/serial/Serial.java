package com.simtechdata.arduinoserial.serial;

import com.fazecast.jSerialComm.SerialPort;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.List;


public class Serial {

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

	public boolean keepOpen(String serialPort) {
		return PortMap.keepOpen(serialPort);
	}

	public void setKeepOpen(String comPort, boolean keepOpen) {
		PortMap.setKeepOpen(comPort, keepOpen);
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

	public boolean openPort(String description, StringProperty serialData) {
		if (PortMap.isClosed(description)) {
			if (PortMap.open(description)) {
				listen(description, serialData);
				return true;
			}
		}
		return false;
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

	private void listen(String comPort, StringProperty serialData) {
		if (PortMap.isOpen(comPort)) {
			new Thread(() -> {
				SerialPort    serialPort = PortMap.get(comPort);
				StringBuilder sb         = new StringBuilder();
				boolean       useFilter  = PortMap.hasFilterList(comPort);
				List<String>  filterList = (useFilter) ? PortMap.getFilterList(comPort) : null;
				while (serialPort.isOpen()) {
					serialPort.flushIOBuffers();
					while (serialPort.isOpen() && serialPort.bytesAvailable() == 0) {
						sleep(100);
					}
					if (serialPort.isOpen()) {
						byte[] readBuffer = new byte[serialPort.bytesAvailable()];
						serialPort.readBytes(readBuffer, readBuffer.length);
						String dataIn = new String(readBuffer);
						if (useFilter) {
							for (String filter : filterList) {
								if (dataIn.contains(filter)) {
									sb.append(dataIn);
									break;
								}
							}
						}
						else {
							if (PortMap.clearOnNew(comPort)) {
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

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public Boolean hasFilterList(String comPort) {
		return PortMap.hasFilterList(comPort);
	}

	public Boolean clearOnNew(String comPort) {
		return PortMap.clearOnNew(comPort);
	}

	public void setClearOnNew(String comPort, boolean value) {
		PortMap.setClearOnNew(comPort, value);
	}

	public void setFilterList(String comPort, List<String> filterList) {
		PortMap.setFilterList(comPort, filterList);
	}

	public void setFilterLists(String json) {
		PortMap.setFilterLists(json);
	}

	public List<String> getFilterList(String comPort) {
		return PortMap.getFilterList(comPort);
	}

	public String getJsonFilterLists() {
		return PortMap.getJsonFilterLists();
	}

	public void clearFilterList(String comPort) {
		PortMap.clearFilterList(comPort);
	}
}
