![1](./img/logo.png)

# A Better Serial interface for your Arduino needs.

### Benefits of ArduinoSerial over the IDE Serial interface:
*All settings are unique to each com port - none are global in scope.*
- Simultaneously open multiple serial ports
- Create a filter list where a line of text coming from your Arduino will only be shown if it has a word in the filter list
- Toggle feature to automatically clear the display for each incoming serial line (avoid scrolling)
- When you click on any other window, Arduino Serial will automatically close any open com ports, so that you don't have to manually close the ports before uploading your sketches.
  - When you click back on Arduino Serial, all previously closed com ports will be automatically re-opened.
  - This behavior can be changed by using the ```Keep Open``` check box, so you can have some com ports that remain open while you upload sketches to other com ports.
- Serial ports show up in tabs with a text field on top of the tabs. Type in your text and hit enter and Arduino Serial will send that text to that tabs serial port.
- All settings that you engage for a given com port will persist through app reloads.


## Discussion

I usually write my code for Arduino's in the CLion IDE with PlatformIO, because it has numerous benefits over the stock
Arduino IDE. This makes it really easy to work with multiple Arduinos at the same time so that I only need
to switch to different open projects which are each assigned to a different Arduino.

However, the serial interface, even in the CLion environment can be cumbersome to use, because you have
to manually close com ports before attempting to upload sketches and there are other "clumsy" issues with
the serial interface in the IDE.

So I wrote this program that makes is really easy to interface with the different Arduino's that I might be
working with, and I decided to publish it in case anyone else might benefit from it.

## Installation

Click on releases to the right and download your installer.

- Mac installer - simply open the DMG file and drag the app to your Applications folder.
- Windows - use the .exe for standard Windows installation.

The Java runtime is packaged inside the app and it will not expand any files out onto your hard drive.
It will remain as an isolated package. The only exception is the settings file, which is managed based
on the operating system. I have no control over that.

If you want to compile it yourself, there is a library in the Libraries folder that you will need to manually add to the project before it will compile properly.

## Contribution

If you wish to contribute to the app, I thoroughly encourage that. Submit pull requests or create incidents
as needed, or you may contact me at [sims.mike@gmail.com](mailto:sims.mike@gmail.com).
