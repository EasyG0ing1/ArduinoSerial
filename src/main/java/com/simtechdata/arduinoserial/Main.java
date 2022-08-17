package com.simtechdata.arduinoserial;

import com.simtechdata.arduinoserial.settings.AppSettings;
import com.simtechdata.arduinoserial.ui.MainUI;
import com.simtechdata.sceneonefx.SceneOne;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	private static boolean development = false;

	@Override public void start(Stage primaryStage) {
		if (!development) {
			AppSettings.set().thisVersion("1.0.3");
			cleanMetadata();
		}
		SceneOne.disableNotice();
		new MainUI();
	}

	private void cleanMetadata() {
		String lastVersion = AppSettings.get().lastVersion();
		String thisVersion = AppSettings.get().thisVersion();
		if (!lastVersion.equals(thisVersion)) {
			double screenWidth        = AppSettings.get().screenWidth();
			double screenHeight       = AppSettings.get().screenHeight();
			double filterWindowWidth  = AppSettings.get().filterWindowWidth();
			double filterWindowHeight = AppSettings.get().filterWindowHeight();
			String appSettings        = AppSettings.get().appSettings();
			AppSettings.clear().masterReset();
			AppSettings.set().lastVersion(thisVersion);
			AppSettings.set().screenWidth(screenWidth);
			AppSettings.set().screenHeight(screenHeight);
			AppSettings.set().filterWindowWidth(filterWindowWidth);
			AppSettings.set().filterWindowHeight(filterWindowHeight);
			AppSettings.set().appSettings(appSettings);
		}
	}

	public static void main(String[] args) {
		for(String arg : args) {
			if (arg.toLowerCase().contains("dev")) {
				development = true;
			}
			if(arg.contains("json")) {
				System.out.println(AppSettings.get().appSettings());
				System.exit(0);
			}
			if(arg.contains("wipe")) {
				AppSettings.clear().masterReset();
				System.exit(0);
			}
		}
		launch(args);
	}

}
