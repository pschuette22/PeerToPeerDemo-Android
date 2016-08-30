# PeerToPeerDemo-Android
Android application that utilizes Wi-Fi direct peer to peer functionality. At least two instances of this application must be deployed and connected via Wi-Fi direct in order for this to work

The purpose of this application is to establish a direct connection between two Android devices over Wi-Fi network. To do this, two Android devices connect via Wi-Fi Direct connection and one acts as a 'server' application. Connected devices push and poll messages to and from this server device to facilitate communication. If you are not satisfied with the latency, open com.zeppatech.p2pdemo.p2p.threads.*MessagesRunnable.java and adjust sleep/ socket wait times.

This application keeps multiple resident threads running in the background. It could have significant effect on battery life, I suggest keeping your devices plugged in.

## Setup Instructions

1. Clone repository to local machine and import into Android Studio
2. Get two physical Android devices running Android 4.0+ with WiFi-Direct capability
3. Enable WiFi-Direct on devices by navigating to Settings > Wireless & Networks > Wi-Fi > Wi-Fi Direct (often in 'Advanced' or 'more' depending on device)
4. Establish Wi-Fi direct connections to one or more Android devices
5. Run PeerToPeerDemo 'app' through Android Studio onto your devices

## Interface Explanation
This is not the Mona Lisa of Android applications. It's purpose is to demonstate one of the nifty things you can do with Android

1. Devices
	Button to attempt to update peer devices, list of connected peer devices

2. Send
	Send your name and birthday to a list of selected devices.

3. Messages
	List of messages that have been sent to this device displaying sender ip address, sender's entered name, sender's age and birthday.


## Note
This build is not bullet proof at the moment. Wi-Fi Direct is not the more reliable network and may cut out without warning. There is much to be desired in notifying the user of changes to the application state


