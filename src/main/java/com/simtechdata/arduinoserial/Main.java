package com.simtechdata.arduinoserial;

import com.simtechdata.arduinoserial.ui.MainUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	@Override public void start(Stage primaryStage) {
		new MainUI();
	}
}
