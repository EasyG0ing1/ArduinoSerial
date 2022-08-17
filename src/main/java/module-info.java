module com.simtechdata.arduinoserial {
    requires javafx.controls;
    requires com.simtechdata.sceneonefx;
	//requires SceneOneFX.TEST;
	requires org.apache.commons.codec;
    requires EasyFXControls;
    requires java.datatransfer;
    requires java.desktop;
    requires com.fazecast.jSerialComm;
	requires java.prefs;
	requires com.google.gson;
	requires org.hildan.fxgson;
	exports com.simtechdata.arduinoserial;
	exports com.simtechdata.arduinoserial.serial to com.google.gson;
	opens com.simtechdata.arduinoserial.serial to com.google.gson;
}
