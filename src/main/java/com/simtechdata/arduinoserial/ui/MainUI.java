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
	}

	private final Map<Tab, StringProperty> serialProperties   = new HashMap<>();
	private       Serial                   serial;
	private       String                   activePort         = "";
	private final String                   sceneId            = "ArduinoSerial";
	private       double                   width              = AppSettings.get().screenWidth();
	private       double                   height             = AppSettings.get().screenHeight();
	private       double                   newWidth;
	private       double                   newHeight;
	private final AnchorPane               ap                 = ap();
	private final Filter                   filter             = new Filter();
	private       CChoiceBox<String>       serialPorts;
	private       CLabel                   lblOpenClosed;
	private       CLabel                   lblFiltered;
	private       CCheckBox                checkClear;
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
				for(Tab tab : tabPane.getTabs()) {
					String port = tab.getText();
					lostFocusMap.put(port, serial.isOpen(port));
				}
				closePorts();
			}).start();
		}
		else {
			new Thread(() -> {
				for(String port : lostFocusMap.keySet()) {
					boolean openPort = lostFocusMap.get(port);
					if(openPort) openPort(port);
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
		btnFilter     = new Button.Builder(ap, "Set Filter").leftTop(130, 43.5).width(65).build();
		lblFiltered   = new CLabel.Builder(ap).leftTop(205, 49).build();
		serialPorts   = new CChoiceBox.Builder<String>(ap, 15, -1, 15, -1).size(180, 25).build();
		tfCommand     = new CTextField.Builder(ap, 15, 15, 75, -1).height(25).alignment(Pos.CENTER_LEFT).disabled().build();
		tabPane       = new CTabPane.Builder(ap).bounds(15, 15, 105, 50).build();
		btnClose      = new Button.Builder(ap, "Close Port").leftBottom((width / 2) - 100, 10).size(75, 25).build();
		btnOpen       = new Button.Builder(ap, "Open Port").rightBottom((width / 2) - 100, 10).size(75, 25).build();
		serial        = new Serial(checkClear.selectedProperty());
		serialList    = serial.getSerialPorts();
		serialPorts.setItems(serialList);
		lastSerialListSize = serialList.size();
	}

	private void setControlActions() {
		tfCommand.setOnAction(e -> {
			if (serial.isClosed(activePort)) {
				e.consume();
			}
			else {
				serial.send(activePort, tfCommand.getText());
				tfCommand.setText("");
				tfCommand.requestFocus();
			}
		});
		serialPorts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			activePort = newValue;
			ObservableList<Tab> tabList = tabPane.getTabs();
			boolean             addTab  = true;
			for (Tab tab : tabList) {
				if (tab.getText().equals(activePort)) {
					addTab = false;
					break;
				}
			}
			if (addTab) {
				Tab       tab = new Tab(activePort);
				CTextArea ta  = new CTextArea.Builder().editable(false).size(width, height).build();
				serialProperties.put(tab, ta.textProperty());
				AnchorPane newAp = new AnchorPane.Builder().build();
				newAp.addNode(ta, 0, 0, 0, 0);
				tab.setContent(newAp);
				tab.selectedProperty().addListener((observable1, wasSelected, isSelected) -> {
					if (isSelected) {
						activePort = tab.getText();
						if (serial.isOpen(activePort)) {
							lblOpenClosed.change("Open");
							lblOpenClosed.setTextFill(Color.color(0, .6, 0));
						}
						else {
							lblOpenClosed.change("Closed");
							lblOpenClosed.setTextFill(Color.color(.5, 0, 0));
						}
						btnOpen.disableProperty().unbind();
						btnOpen.disableProperty().bind(serial.disableWhenOpenProperty(activePort));
						btnClose.disableProperty().unbind();
						btnClose.disableProperty().bind(serial.disableWhenClosedProperty(activePort));
						tfCommand.disableProperty().unbind();
						tfCommand.disableProperty().bind(serial.disableWhenClosedProperty(activePort));
						new Thread(() -> {
							try {
								Thread.sleep(150);
								Platform.runLater(() -> tfCommand.requestFocus());
							}
							catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						}).start();
					}
				});
				tabPane.getTabs().add(tab);
				tabPane.getSelectionModel().select(tab);
			}
		});
		btnClose.setOnAction(e -> closePort());
		btnOpen.setOnAction(e -> openPort());
		btnFilter.setOnAction(e -> serial.setFilterList(filter.getFilterList()));
		lblOpenClosed.visibleProperty().bind(serialPorts.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0));
		btnClose.setDisable(true);
		btnOpen.setDisable(true);
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		Tooltip.install(checkClear, new Tooltip("This will clear the window every time new data comes in from the serial port, preventing the need to scroll history."));
		Tooltip.install(btnFilter, new Tooltip("Text coming from the serial port will only be displayed if any single line of text contains any word in your filter list.\nAn empty filter list will show ALL text coming from the serial port"));
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
					width = newWidth;
					if (newWidth > 0) {
						AppSettings.set().screenWidth(width);
					}
				}
				if (height != newHeight) {
					height = newHeight;
					if (newHeight > 0) {
						AppSettings.set().screenHeight(height);
					}
				}
				if (AppSettings.get().saveFilter() && AppSettings.get().getFilterList().length() > 0) {
					Platform.runLater(() -> {
						lblFiltered.change("Filter Active");
						serial.setCheckFilter(true);
						lblFiltered.setTextFill(Color.color(0, .6, 0));
					});
				}
				else {
					Platform.runLater(() -> {
						lblFiltered.change("No Filter");
						serial.setCheckFilter(false);
						lblFiltered.setTextFill(Color.color(.5, 0, 0));
					});
				}
			}
		};
	}

	private void closePorts() {
		for (Tab tab : tabPane.getTabs()) {
			Platform.runLater(() -> {
				lblOpenClosed.change(serial.closePort(tab.getText()));
				lblOpenClosed.setTextFill(Color.color(.5, 0, 0));
			});
		}
	}

	private void closePort() {
		lblOpenClosed.change(serial.closePort(activePort));
		lblOpenClosed.setTextFill(Color.color(.5, 0, 0));
	}

	private void openPort(String port) {
		Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
		for(Tab tab : tabPane.getTabs()) {
			if (tab.getText().equals(port)) {
				boolean success = serial.openPort(port, serialProperties.get(tab));
				if(tab.equals(activeTab)) {
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
		if (serial.openPort(activePort, serialProperties.get(tab))) {
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
