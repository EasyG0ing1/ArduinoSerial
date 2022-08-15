package com.simtechdata.arduinoserial.ui;

import com.simtechdata.arduinoserial.serial.Serial;
import com.simtechdata.arduinoserial.settings.AppSettings;
import com.simtechdata.easyfxcontrols.containers.AnchorPane;
import com.simtechdata.easyfxcontrols.controls.Button;
import com.simtechdata.easyfxcontrols.controls.CLabel;
import com.simtechdata.easyfxcontrols.controls.CListView;
import com.simtechdata.easyfxcontrols.controls.CTextField;
import com.simtechdata.sceneonefx.SceneOne;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.text.TextAlignment;

import java.util.*;

import static com.simtechdata.easyfxcontrols.enums.Placement.LEFT;

public class Filter {

	public Filter(Serial serial) {
		this.serial = serial;
		makeControls();
		setControlActions();
		loadData();
		windowTimer.scheduleAtFixedRate(setWindow(), 3000, 500);
	}

	private final Serial serial;
	private final String sceneId       = "ArduinoSerialFilter";
	private       String            activeComPort = "";
	private       double            width         = AppSettings.get().filterWindowWidth();
	private       double            height        = AppSettings.get().filterWindowHeight();
	private       double            newWidth;
	private       double            newHeight;
	private final AnchorPane        ap            = ap();
	private       CTextField        tfWord;
	private       CListView<String> lvWordList;
	private CLabel lblComPort;
	private       Button            btnRemove;
	private       Button            btnClose;
	private       Button            btnClear;
	private final Timer             windowTimer   = new Timer();

	private AnchorPane ap() {
		return new AnchorPane.Builder(width, height).build();
	}

	private void makeControls() {
		tfWord     = new CTextField.Builder(ap, 80, 15, 15, -1).size(250, 25).addLabel("Word(s)", TextAlignment.RIGHT, LEFT, 45, 25).build();
		lblComPort = new CLabel.Builder(ap,"Com Port",300,25).alignment(Pos.CENTER_LEFT).textAlignment(TextAlignment.LEFT).leftTop(15, 50).build();
		lvWordList = new CListView.Builder<String>(ap).bounds(15, 15, 80, 60).build();
		btnRemove  = new Button.Builder(ap, "Remove", 65, 25).bounds((width / 4) - 32.5, -1, -1, 15).build();
		btnClear   = new Button.Builder(ap, "Clear", 65, 25).bounds((width / 2) - 32.5, -1, -1, 15).build();
		btnClose   = new Button.Builder(ap, "Close", 55, 25).bounds(-1, (width / 4) - 27.5, -1, 15).build();
	}

	private void setControlActions() {
		tfWord.setOnAction(e -> {
			lvWordList.getItems().add(tfWord.getText());
			tfWord.setText("");
			tfWord.requestFocus();
			saveData();
		});
		btnRemove.setOnAction(e -> {
			String item = lvWordList.getSelectionModel().getSelectedItem();
			if (!item.isEmpty()) {
				lvWordList.getItems().remove(item);
				lvWordList.getSelectionModel().clearSelection();
			}
			tfWord.requestFocus();
			saveData();
		});
		btnClose.setOnAction(e -> SceneOne.close(sceneId));
		btnClear.setOnAction(e -> {
			ObservableList<String> emptyList = FXCollections.observableArrayList();
			lvWordList.setItems(emptyList);
			saveData();
		});
		Tooltip.install(tfWord, new Tooltip("Type in a word and press enter to add it to the list"));
		Tooltip.install(btnRemove, new Tooltip("Select a word from the list and click this button to remove it"));
		Tooltip.install(btnClear, new Tooltip("Wipe out the entire list"));
		btnRemove.disableProperty().bind(lvWordList.getSelectionModel().selectedIndexProperty().lessThan(0));
	}

	private void loadData() {
		String json = AppSettings.get().filterLists();
		if (!json.isEmpty()) {
			serial.setFilterLists(json);
		}
	}

	private void loadWordList() {
		lvWordList.getItems().clear();
		if (serial.hasFilterList(activeComPort)) {
			List<String> wordList = serial.getFilterList(activeComPort);
			if (wordList.size() > 0) {
				wordList.sort(Comparator.comparing(String::toString));
				lvWordList.setItems(FXCollections.observableArrayList(wordList));
			}
		}
	}

	public void saveData() {
		List<String> wordList = new ArrayList<>(lvWordList.getItems());
		if (!wordList.isEmpty()) {
			wordList.sort(Comparator.comparing(String::toString));
			lvWordList.setItems(FXCollections.observableArrayList(wordList)); //Because this method is called whenever a new word is entered
			serial.setFilterList(activeComPort, wordList);
			AppSettings.set().filterLists(serial.getJsonFilterLists());
		}
		else {serial.clearFilterList(activeComPort);}
	}

	private TimerTask setWindow() {
		return new TimerTask() {
			@Override public void run() {
				if (width != newWidth) {
					if (newWidth > 0) {
						width = newWidth;
						AppSettings.set().filterWindowWidth(width);
					}
				}
				if (height != newHeight) {
					if (newHeight > 0) {
						height = newHeight;
						AppSettings.set().filterWindowHeight(height);
					}
				}
			}
		};
	}

	public void editFilterList(String comPort) {
		activeComPort = comPort;
		Platform.runLater(() -> lblComPort.change("Port: " + comPort));
		if (!SceneOne.sceneExists(sceneId)) {
			SceneOne.set(sceneId, ap, width, height).centered().build();
			SceneOne.getScene(sceneId).widthProperty().addListener((observable, oldValue, newValue) -> newWidth = (Double) newValue);
			SceneOne.getScene(sceneId).heightProperty().addListener((observable, oldValue, newValue) -> newHeight = (Double) newValue);
		}
		loadWordList();
		SceneOne.show(sceneId);
	}
}
