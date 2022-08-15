package com.simtechdata.arduinoserial.ui;

import com.simtechdata.arduinoserial.settings.AppSettings;
import com.simtechdata.easyfxcontrols.containers.AnchorPane;
import com.simtechdata.easyfxcontrols.controls.Button;
import com.simtechdata.easyfxcontrols.controls.CCheckBox;
import com.simtechdata.easyfxcontrols.controls.CListView;
import com.simtechdata.easyfxcontrols.controls.CTextField;
import com.simtechdata.sceneonefx.SceneOne;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tooltip;
import javafx.scene.text.TextAlignment;

import java.util.*;

import static com.simtechdata.easyfxcontrols.enums.Placement.LEFT;

public class Filter {

	public Filter() {
		makeControls();
		setControlActions();
		loadData();
		windowTimer.scheduleAtFixedRate(setWindow(),3000,500);
	}

	private final String            sceneId = "ArduinoSerialFilter";
	private       double            width   = AppSettings.get().filterWindowWidth();
	private       double            height  = AppSettings.get().filterWindowHeight();
	private       double                   newWidth;
	private       double                   newHeight;
	private final AnchorPane        ap      = ap();
	private       CTextField        tfWord;
	private       CListView<String> lvWordList;
	private       Button            btnRemove;
	private       Button            btnClose;
	private       Button            btnClear;
	private       CCheckBox         checkSave;
	private final Timer windowTimer = new Timer();

	private AnchorPane ap() {
		return new AnchorPane.Builder(width, height).build();
	}

	private void makeControls() {
		tfWord     = new CTextField.Builder(ap, 80, 100, 15, -1).size(150, 25).addLabel("Filter Word", TextAlignment.RIGHT, LEFT, 65, 25).build();
		checkSave  = new CCheckBox.Builder(ap, "Save List").bounds(-1, 15, 18, -1).build();
		lvWordList = new CListView.Builder<String>(ap).bounds(15, 15, 50, 60).build();
		btnRemove  = new Button.Builder(ap, "Remove", 65, 25).bounds((width / 4) - 32.5, -1, -1, 15).build();
		btnClear   = new Button.Builder(ap, "Clear", 65, 25).bounds((width / 2) - 32.5, -1, -1, 15).build();
		btnClose   = new Button.Builder(ap, "Close", 55, 25).bounds(-1, (width / 4) - 27.5, -1, 15).build();
	}

	private void setControlActions() {
		tfWord.setOnAction(e -> {
			lvWordList.getItems().add(tfWord.getText());
			saveData();
			tfWord.setText("");
			tfWord.requestFocus();
		});
		btnRemove.setOnAction(e -> {
			String item = lvWordList.getSelectionModel().getSelectedItem();
			if (!item.isEmpty()) {
				lvWordList.getItems().remove(item);
				lvWordList.getSelectionModel().clearSelection();
			}
			tfWord.requestFocus();
		});
		btnClose.setOnAction(e -> SceneOne.close(sceneId));
		btnClear.setOnAction(e -> {
			ObservableList<String> emptyList = FXCollections.observableArrayList();
			lvWordList.setItems(emptyList);
			AppSettings.clear().filterList();
		});
		checkSave.setSelected(AppSettings.get().saveFilter());
		checkSave.selectedProperty().addListener((observable, oldValue, newValue) -> {
			AppSettings.set().saveFilter(newValue);
			if (!newValue) {AppSettings.clear().filterList();}
		});
		Tooltip.install(tfWord, new Tooltip("Type in a word and press enter to add it to the list"));
		Tooltip.install(btnRemove, new Tooltip("Select a word from the list and click this button to remove it"));
		btnRemove.disableProperty().bind(lvWordList.getSelectionModel().selectedIndexProperty().lessThan(0));
	}

	private void loadData() {
		if (AppSettings.get().saveFilter()) {
			String   list      = AppSettings.get().getFilterList();
			String[] listItems = list.split("");
			List<String> newListItems = new ArrayList<>();
			for (String listItem : listItems) {
				if (!listItem.isEmpty())
					newListItems.add(listItem);
			}
			lvWordList.setItems(FXCollections.observableArrayList(newListItems));
		}
	}

	public void saveData() {
		ObservableList<String> list = lvWordList.getItems();
		if (!list.isEmpty()) {
			list.sort(Comparator.comparing(String::toString));
			lvWordList.setItems(list);
			StringBuilder sb = new StringBuilder();
			for (String word : list) {
				if(!word.isEmpty())
					sb.append(word).append("");
			}
			String finalList = sb.toString();
			int    len       = finalList.length();
			finalList = finalList.substring(0, len - 1);
			if (checkSave.isSelected()) {
				AppSettings.set().filterList(finalList);
			}
		}
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

	public ObservableList<String> getFilterList() {
		if (!SceneOne.sceneExists(sceneId)) {
			SceneOne.set(sceneId, ap, width, height).centered().build();
			SceneOne.getScene(sceneId).widthProperty().addListener((observable, oldValue, newValue) -> newWidth = (Double) newValue);
			SceneOne.getScene(sceneId).heightProperty().addListener((observable, oldValue, newValue) -> newHeight = (Double) newValue);
		}
		SceneOne.showAndWait(sceneId);
		saveData();
		return lvWordList.getItems();
	}
}
