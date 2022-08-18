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
			if(!description.isEmpty())
				PortMap.put(description, port);
		}
		list.sort(Comparator.comparing(String::toString));
		return list;
	}

	public BooleanBinding disableWhenClosedProperty(String comPort) {
		if(comPort.isEmpty()) return null;
		return PortMap.notOpenProperty(comPort);
	}

	public BooleanProperty disableWhenOpenProperty(String comPort) {
		if(comPort.isEmpty()) return null;
		return PortMap.openProperty(comPort);
	}

	public boolean keepOpen(String comPort) {
		if(comPort.isEmpty()) return false;
		return PortMap.keepOpen(comPort);
	}

	public void setKeepOpen(String comPort, boolean keepOpen) {
		if(comPort.isEmpty()) return;
		PortMap.setKeepOpen(comPort, keepOpen);
	}

	public void send(String comPort, String text) {
		PortMap.send(comPort, text);
	}

	public boolean isClosed(String comPort) {
		if(comPort.isEmpty()) return true;
		return PortMap.isClosed(comPort);
	}

	public boolean isOpen(String comPort) {
		if(comPort.isEmpty()) return false;
		return PortMap.isOpen(comPort);
	}

	public boolean openPort(String comPort, StringProperty serialData) {
		if(comPort.isEmpty()) return false;
		if (PortMap.isClosed(comPort)) {
			if (PortMap.open(comPort)) {
				listen(comPort, serialData);
				return true;
			}
		}
		return false;
	}

	public String closePort(String comPort) {
		if(comPort.isEmpty()) return "Closed";
		if (PortMap.have(comPort)) {
			if (PortMap.isOpen(comPort)) {
				if (PortMap.close(comPort)) {
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
		if(comPort.isEmpty()) return;
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
		if(comPort.isEmpty()) return null;
		return PortMap.hasFilterList(comPort);
	}

	public Boolean clearOnNew(String comPort) {
		if(comPort.isEmpty()) return null;
		return PortMap.clearOnNew(comPort);
	}

	public void setClearOnNew(String comPort, boolean value) {
		if(comPort.isEmpty()) return;
		PortMap.setClearOnNew(comPort, value);
	}

	public void setFilterList(String comPort, List<String> filterList) {
		if(comPort.isEmpty()) return;
		PortMap.setFilterList(comPort, filterList);
	}

	public void loadSettings(String json) {
		PortMap.loadSettings(json);
	}

	public List<String> getFilterList(String comPort) {
		return PortMap.getFilterList(comPort);
	}

	public void clearFilterList(String comPort) {
		PortMap.clearFilterList(comPort);
	}

	public PortSetting getPortSetting(String comPort) {
		return PortMap.getPortSetting(comPort);
	}

	public void setPortSetting(String comPort, PortSetting portSetting) {
		if(comPort.isEmpty()) return;
		PortMap.setPortSetting(comPort,portSetting);
	}

	public Integer getBaudRate(String comPort) {
		return PortMap.getBaudRate(comPort);
	}

	public void resetOpenState(String comPort) {
		PortMap.resetOpenState(comPort);
	}
}
