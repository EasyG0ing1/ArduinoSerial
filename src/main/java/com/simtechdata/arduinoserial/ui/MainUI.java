package com.simtechdata.arduinoserial.ui;

import com.simtechdata.arduinoserial.serial.Serial;
import com.simtechdata.arduinoserial.settings.AppSettings;
import com.simtechdata.easyfxcontrols.containers.AnchorPane;
import com.simtechdata.easyfxcontrols.controls.*;
import com.simtechdata.sceneonefx.SceneOne;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainUI {

	public MainUI() {
		makeControls();
		setControlActions();
		SceneOne.set(sceneId, ap).size(width, height).centered().onCloseEvent(e -> closeScene()).onLostFocus(lostFocusListener).show();
		tfCommand.requestFocus();
		serialListTimer.scheduleAtFixedRate(checkSerialPorts(), 3000, 500);
		sceneSizeTimer.scheduleAtFixedRate(setWindow(), 3000, 500);
		SceneOne.getScene(sceneId).widthProperty().addListener((observable, oldValue, newValue) -> newWidth = (Double) newValue);
		SceneOne.getScene(sceneId).heightProperty().addListener((observable, oldValue, newValue) -> newHeight = (Double) newValue);
		filter = new Filter(serial);
	}

	private final Map<Tab, StringProperty> serialProperties   = new HashMap<>();
	private       Serial                   serial;
	private       String                   activeComPort      = "";
	private final String                   sceneId            = "ArduinoSerial";
	private       double                   width              = AppSettings.get().screenWidth();
	private       double                   height             = AppSettings.get().screenHeight();
	private       double                   newWidth;
	private       double                   newHeight;
	private final AnchorPane               ap                 = ap();
	private final Filter                   filter;
	private       CChoiceBox<String>       serialPorts;
	private       CLabel                   lblOpenClosed;
	private       CLabel                   lblFiltered;
	private       CCheckBox                checkClear;
	private       CCheckBox                checkKeepOpen;
	private       CTextField               tfCommand;
	private       Button                   btnClose;
	private       Button                   btnOpen;
	private       Button                   btnFilter;
	private       CTabPane                 tabPane;
	private final Timer                    serialListTimer    = new Timer();
	private final Timer                    sceneSizeTimer     = new Timer();
	private       ObservableList<String>   serialList         = FXCollections.observableArrayList();
	private       int                      lastSerialListSize = 0;
	private final Map<String, Boolean>     lostFocusMap       = new HashMap<>();

	private final ChangeListener<Boolean> lostFocusListener = (observable, lostFocus, isFocused) -> {
		if (lostFocus) {
			new Thread(() -> {
				lostFocusMap.clear();
				for (Tab tab : tabPane.getTabs()) {
					String port = tab.getText();
					lostFocusMap.put(port, serial.isOpen(port));
				}
				closePorts();
			}).start();
		}
		else {
			new Thread(() -> {
				for (String port : lostFocusMap.keySet()) {
					boolean openPort = lostFocusMap.get(port);
					if (openPort && !serial.keepOpen(port)) openPort(port);
				}
			}).start();
		}
	};

	private AnchorPane ap() {
		return new AnchorPane.Builder(width, height).build();
	}

	private void makeControls() {
		lblOpenClosed = new CLabel.Builder(ap, "Closed").leftTop(210, 18.5).width(200).alignment(Pos.CENTER_LEFT).build();
		checkClear    = new CCheckBox.Builder(ap).text("Clear On New").leftTop(15, 47).checked().build();
		btnFilter     = new Button.Builder(ap, "Edit Filter").leftTop(120, 43.5).disabled().width(75).build();
		lblFiltered   = new CLabel.Builder(ap).leftTop(205, 49).build();
		serialPorts   = new CChoiceBox.Builder<String>(ap, 15, -1, 15, -1).size(180, 25).build();
		checkKeepOpen = new CCheckBox.Builder(ap, -1, 15, 15, -1).text("Keep Open").build();
		tfCommand     = new CTextField.Builder(ap, 15, 15, 75, -1).height(25).alignment(Pos.CENTER_LEFT).disabled().build();
		tabPane       = new CTabPane.Builder(ap).bounds(15, 15, 105, 50).build();
		btnClose      = new Button.Builder(ap, "Close Port").leftBottom((width / 2) - 100, 10).size(75, 25).build();
		btnOpen       = new Button.Builder(ap, "Open Port").rightBottom((width / 2) - 100, 10).size(75, 25).build();
		serial        = new Serial();
		serialList    = serial.getSerialPorts();
		serialPorts.setItems(serialList);
		lastSerialListSize = serialList.size();
	}

	private void setControlActions() {
		tfCommand.setOnAction(e -> {
			if (serial.isClosed(activeComPort)) {
				e.consume();
			}
			else {
				serial.send(activeComPort, tfCommand.getText());
				tfCommand.setText("");
				tfCommand.requestFocus();
			}
		});
		serialPorts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			activeComPort = newValue;
			ObservableList<Tab> tabList = tabPane.getTabs();
			boolean             addTab  = true;
			for (Tab tab : tabList) {
				if (tab.getText().equals(activeComPort)) {
					addTab = false;
					break;
				}
			}
			if (addTab) {
				Tab       tab = new Tab(activeComPort);
				CTextArea ta  = new CTextArea.Builder().editable(false).size(width, height).build();
				serialProperties.put(tab, ta.textProperty());
				AnchorPane newAp = new AnchorPane.Builder().build();
				newAp.addNode(ta, 0, 0, 0, 0);
				tab.setContent(newAp);
				tab.selectedProperty().addListener((observable1, wasSelected, isSelected) -> {
					if (isSelected) {
						activeComPort = tab.getText();
						if (serial.isOpen(activeComPort)) {
							lblOpenClosed.change("Open");
							lblOpenClosed.setTextFill(Color.color(0, .6, 0));
						}
						else {
							lblOpenClosed.change("Closed");
							lblOpenClosed.setTextFill(Color.color(.5, 0, 0));
						}
						btnOpen.disableProperty().unbind();
						btnOpen.disableProperty().bind(serial.disableWhenOpenProperty(activeComPort));
						btnClose.disableProperty().unbind();
						btnClose.disableProperty().bind(serial.disableWhenClosedProperty(activeComPort));
						tfCommand.disableProperty().unbind();
						tfCommand.disableProperty().bind(serial.disableWhenClosedProperty(activeComPort));
						new Thread(() -> {
							try {
								Thread.sleep(150);
								Platform.runLater(() -> tfCommand.requestFocus());
							}
							catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						}).start();
						checkKeepOpen.setSelected(serial.keepOpen(activeComPort));
						checkClear.setSelected(serial.clearOnNew(activeComPort));
						btnFilter.setDisable(false);
					}
				});
				tabPane.getTabs().add(tab);
				tabPane.getSelectionModel().select(tab);
			}
		});
		checkKeepOpen.setOnAction(e -> serial.setKeepOpen(activeComPort, checkKeepOpen.isSelected()));
		checkClear.setOnAction(e -> serial.setClearOnNew(activeComPort, checkClear.isSelected()));
		btnClose.setOnAction(e -> closePort());
		btnOpen.setOnAction(e -> openPort());
		btnFilter.setOnAction(e -> filter.editFilterList(activeComPort));
		lblOpenClosed.visibleProperty().bind(serialPorts.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));
		btnClose.setDisable(true);
		btnOpen.setDisable(true);
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		Tooltip.install(checkClear, new Tooltip("This will clear the window every time new data comes in from the serial port, preventing the need to scroll history."));
		Tooltip.install(btnFilter, new Tooltip("Text coming from the serial port will only be displayed if any single line of text contains any word in your filter list.\nAn empty filter list will show ALL text coming from the serial port"));
		Tooltip.install(checkKeepOpen, new Tooltip("If checked, the current com port will be kept open even when you change focus to a different program.\nHowever, if the com port is closed, it will remain closed."));
	}

	private TimerTask checkSerialPorts() {
		return new TimerTask() {
			@Override public void run() {
				serialList = serial.getSerialPorts();
				if (lastSerialListSize != serialList.size()) {
					Platform.runLater(() -> serialPorts.setItems(serialList));
					lastSerialListSize = serialList.size();
				}
			}
		};
	}

	private TimerTask setWindow() {
		return new TimerTask() {
			@Override public void run() {
				if (width != newWidth) {
					if (newWidth > 0) {
						AppSettings.set().screenWidth(newWidth);
					}
					width = newWidth;
				}
				if (height != newHeight) {
					if (newHeight > 0) {
						AppSettings.set().screenHeight(newHeight);
					}
					height = newHeight;
				}
				if (serial.hasFilterList(activeComPort)) {
					Platform.runLater(() -> {
						lblFiltered.change("Filter List Active");
						lblFiltered.setTextFill(Color.color(0, .6, 0));
					});
				}
				else {
					Platform.runLater(() -> {
						lblFiltered.change("No Filter List");
						lblFiltered.setTextFill(Color.color(.5, 0, 0));
					});
				}
			}
		};
	}

	private void closePorts() {
		Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
		for (Tab tab : tabPane.getTabs()) {
			String  comPort  = tab.getText();
			boolean keepOpen = serial.keepOpen(comPort);
			if (!keepOpen) {
				String closedResponse = serial.closePort(tab.getText());
				if (tab.equals(activeTab)) {
					Platform.runLater(() -> {
						lblOpenClosed.change(closedResponse);
						lblOpenClosed.setTextFill(Color.color(.5, 0, 0));
					});
				}
			}
		}
	}

	private void closePort() {
		lblOpenClosed.change(serial.closePort(activeComPort));
		lblOpenClosed.setTextFill(Color.color(.5, 0, 0));
	}

	private void openPort(String port) {
		Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
		for (Tab tab : tabPane.getTabs()) {
			if (tab.getText().equals(port)) {
				boolean success = serial.openPort(port, serialProperties.get(tab));
				if (tab.equals(activeTab)) {
					Platform.runLater(() -> {
						if (success) {
							lblOpenClosed.change("Open");
							lblOpenClosed.setTextFill(Color.color(0, .6, 0));
						}
						else {
							lblOpenClosed.change("Failed to open");
							lblOpenClosed.setTextFill(Color.color(.5, 0, 0));
						}
					});
				}
			}
		}
	}

	private void openPort() {
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		lblOpenClosed.change("Closing...");
		lblOpenClosed.setTextFill(Color.color(.6, .6, 0));
		if (serial.openPort(activeComPort, serialProperties.get(tab))) {
			lblOpenClosed.change("Open");
			lblOpenClosed.setTextFill(Color.color(0, .6, 0));
		}
		else {
			lblOpenClosed.change("Failed to open");
			lblOpenClosed.setTextFill(Color.color(.5, 0, 0));
		}
		tfCommand.requestFocus();
	}

	private void closeScene() {
		SceneOne.close(sceneId);
		for (Tab tab : tabPane.getTabs()) {
			serial.closePort(tab.getText());
		}
		System.exit(0);
	}
}
