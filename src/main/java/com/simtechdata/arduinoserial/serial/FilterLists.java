package com.simtechdata.arduinoserial.serial;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterLists {
	private final        Map<String, List<String>>    filterMap       = new HashMap<>();
	private final        Map<String, Boolean>         clearMap        = new HashMap<>();
	private final        Map<String, Boolean>         keepOpenMap     = new HashMap<>();
	private static final Map<String, BooleanProperty> openPropertyMap = new HashMap<>();

	public void setFilterList(String comPort, List<String> filterList) {
		if (filterMap.containsKey(comPort)) {
			filterMap.replace(comPort, filterList);
		}
		else {filterMap.put(comPort, filterList);}
	}

	public List<String> getFilterList(String comPort) {
		return filterMap.getOrDefault(comPort, null);
	}

	public void clearFilterList(String comPort) {
		filterMap.remove(comPort);
	}

	public Boolean hasFilterList(String comPort) {
		return filterMap.containsKey(comPort);
	}

	public Boolean clearOnNew(String comPort) {
		return clearMap.getOrDefault(comPort, false);
	}

	public void setClearOnNew(String comPort, boolean value) {
		if (clearMap.containsKey(comPort)) {
			clearMap.replace(comPort, value);
		}
		else {clearMap.put(comPort, value);}
	}

	public Boolean keepOpen(String comPort) {
		return keepOpenMap.getOrDefault(comPort, false);
	}

	public void setKeepOpen(String comPort, boolean value) {
		if (keepOpenMap.containsKey(comPort)) {
			keepOpenMap.replace(comPort, value);
		}
		else {keepOpenMap.put(comPort, value);}
	}

	public BooleanBinding notOpenProperty(String comPort) {
		if (openPropertyMap.containsKey(comPort)) {
			return openPropertyMap.get(comPort).not();
		}
		return null;
	}

	public BooleanProperty openProperty(String comPort) {
		if (openPropertyMap.containsKey(comPort)) {
			return openPropertyMap.get(comPort);
		}
		return null;
	}

	public void setOpen(String comPort, boolean value) {
		if (openPropertyMap.containsKey(comPort)) {
			openPropertyMap.get(comPort).setValue(value);
		}
	}

	public void newOpenProperty(String comPort) {
		openPropertyMap.putIfAbsent(comPort, new SimpleBooleanProperty(false));
	}
}
