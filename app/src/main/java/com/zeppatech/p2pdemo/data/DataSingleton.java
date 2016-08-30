package com.zeppatech.p2pdemo.data;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Created by PSchuette on 8/25/16.
 *
 * Singleton for holding data
 */
public class DataSingleton {

    /**
     * Listener for when the device receives a message
     */
    public interface MessageReceivedListener {
        void onMessageReceived(Message message);
    }

    /**
     * interface for fragments listening for wifi P2p devices updated
     */
    public interface WiFiDevicesListener {
        void onWifiP2pDeviceListUpdated(List<DeviceInfo> devices);
    }


    // Singleton instance
    private static DataSingleton instance;

    // Linked list of messages this devices has received
    private List<Message> receivedMessages;

    // Linked list of listeners who want to know when a message is received
    private List<MessageReceivedListener> messageReceivedListeners;

    // Set of devices this device is connected to via wifi
    private Set<DeviceInfo> connectedDevices;

    // List of listeners that wish to be notified when devices are updated
    private List<WiFiDevicesListener> wiFiDevicesListeners;

    // Queue of messages mapped to recipient addresses
    // This is only utilized if the application is the server application
    private Map<String,Queue<String>> messageDispatchMap;

    // IP address of this device
    private String deviceIPAddress;

    // IP address of the wifi-direct group owner
    private String groupOwnerIPAddress;

    // True if the current device is the server
    private boolean isGroupOwner;

    /**
     * Instantiate the DataSingleton
     */
    private DataSingleton(){
        receivedMessages = new LinkedList<Message>();
        messageReceivedListeners = new LinkedList<MessageReceivedListener>();
        connectedDevices = new HashSet<DeviceInfo>();
        wiFiDevicesListeners = new LinkedList<WiFiDevicesListener>();
        messageDispatchMap = new HashMap<String, Queue<String>>();

    }

    /**
     * Add a message to list of received messages
     * @param message that was recieved
     */
    public void addMessage(Message message){
        receivedMessages.add(message);
        // Iterate through listeners notifying them a new message was received
        for(MessageReceivedListener listener: messageReceivedListeners){
            listener.onMessageReceived(message);
        }
    }

    /**
     * Get a list of the messages this device has recieved
     * @return receivedMessages
     */
    public List<Message> getReceivedMessages() {
        return receivedMessages;
    }

    /**
     * Register a listener to receive a callback when a message is received
     * @param listener that wishes to recieve callback
     */
    public void registerMessageReceivedListener(MessageReceivedListener listener){
        messageReceivedListeners.add(listener);
    }

    /**
     * Stop listening for message receive callback
     * @param listener to unregister
     * @return true if change was made to list of receivers
     */
    public boolean unregisterMessageReceivedListener(MessageReceivedListener listener){
        return messageReceivedListeners.remove(listener);
    }

    /**
     * Register a listener who wants to be notificed when device list updates
     * @param listener
     */
    public void registerWiFiDevicesListener(WiFiDevicesListener listener){
        this.wiFiDevicesListeners.add(listener);
    }

    /**
     * Unregister a listener who no longer wants to be notified when device list updates
     * @param listener
     * @return
     */
    public boolean unregisterWiFiDevicesListener(WiFiDevicesListener listener) {
        return this.wiFiDevicesListeners.remove(listener);
    }

    /**
     * Get a list of all the connected devices
     * @return
     */
    public List<DeviceInfo> getConnectedDevices(){
        List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
        if(!connectedDevices.isEmpty()) {
            Iterator<DeviceInfo> itr = connectedDevices.iterator();
            while(itr.hasNext())
                devices.add(itr.next());
        }
        return devices;
    }

    /**
     * Update the connected device list
     * @param deviceList
     */
    public void updateWifiP2pDeviceList(WifiP2pDeviceList deviceList){
        connectedDevices.clear();

        // Iterate through the list of devices adding device info
        for(WifiP2pDevice device: deviceList.getDeviceList()) {
            DeviceInfo info = new DeviceInfo(device.deviceAddress, device.deviceName);
            connectedDevices.add(info);
        }

        // now that the device set has been updated, notify interested parties
        if(!wiFiDevicesListeners.isEmpty()) {
            List<DeviceInfo> devices = getConnectedDevices();
            for (WiFiDevicesListener listener : wiFiDevicesListeners) {
                listener.onWifiP2pDeviceListUpdated(devices);
            }
        }

    }

    public String getGroupOwnerIPAddress() {
        return groupOwnerIPAddress;
    }

    public void setGroupOwnerIPAddress(String groupOwnerIPAddress) {
        this.groupOwnerIPAddress = groupOwnerIPAddress;
    }

    public boolean isGroupOwner() {
        return isGroupOwner;
    }

    public void setGroupOwner(boolean groupOwner) {
        isGroupOwner = groupOwner;
    }


    public void setDeviceIPAddress(String deviceIPAddress) {
        this.deviceIPAddress = deviceIPAddress;
    }

    public String getDeviceIPAddress() {
        return deviceIPAddress;
    }

    /**
     * Put a message into it's intended recipients message queue
     *
     * @param recipientIp
     * @param messageJsonString
     */
    public synchronized boolean putMessage(String recipientIp, String messageJsonString){
        // Make sure the dispatch map has a queue for this recipient
        if(!messageDispatchMap.containsKey(recipientIp)){
           messageDispatchMap.put(recipientIp, new LinkedList<String>());
        }

        return messageDispatchMap.get(recipientIp).add(messageJsonString);
    }

    /**
     * Poll the message queue for this recipient
     * @param recipientIp address mapped to device's message queue
     * @return messageJsonString or empty if queue is empty/ nonexistant
     */
    public synchronized String pollMessageQueue(String recipientIp){
        String messageJsonString = "empty";
        // If there is a queue for this recipient and there is a message for them, poll their queue
        if(messageDispatchMap.containsKey(recipientIp) && !messageDispatchMap.get(recipientIp).isEmpty()){
            messageJsonString = messageDispatchMap.get(recipientIp).poll();
        }

        return messageJsonString;
    }

    /**
     * Grab the singleton instance
     * @return instance
     */
    public static synchronized DataSingleton getInstance(){
        if(instance==null){
            instance = new DataSingleton();
        }
        return instance;
    }



}
