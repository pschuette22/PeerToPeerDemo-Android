package com.zeppatech.p2pdemo.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.zeppatech.p2pdemo.MainActivity;
import com.zeppatech.p2pdemo.data.DataSingleton;
import com.zeppatech.p2pdemo.p2p.threads.InMessagesRunnable;
import com.zeppatech.p2pdemo.p2p.threads.OutMessagesRunnable;
import com.zeppatech.p2pdemo.p2p.threads.PollMessagesRunnable;
import com.zeppatech.p2pdemo.p2p.threads.ThreadManager;

/**
 * Created by PSchuette on 8/25/16.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {



    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;

    /**
     * Construct the receiver
     * @param mManager
     * @param mChannel
     * @param mActivity
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, MainActivity mActivity) {
        super();

        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = mActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                // Should this be handled?
                mActivity.setWifiP2pEnabled(true);
                updatePeers();
            } else {
                // Wi-Fi P2P is not enabled
                mActivity.setWifiP2pEnabled(false);
                mActivity.displayAlertMessage("WiFi p2p is not enabled", "For this application to work, WiFi p2p must be enabled. To go Settings > Wireless & Networks > more... > Wi-Fi Direct and ensure Wi-Fi Direct is enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, this);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP
                mManager.requestConnectionInfo(mChannel, this);
            } else {
                // It's a disconnect
                // TODO: notify the manager it needs to update it's connected device list
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            // Set the current device's ip address
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            DataSingleton.getInstance().setDeviceIPAddress(device.deviceAddress);
        }

    }

    /**
     * Call to discover a list of peers
     */
    public void updatePeers(){
        if(mManager!=null) {
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    // Successfully discovered peers
                    mManager.requestPeers(mChannel, WiFiDirectBroadcastReceiver.this);
                }

                @Override
                public void onFailure(int reasonCode) {
                    // Failed to discover peers. Handle?
                    mActivity.displayAlertMessage("Failed to update peers", "Failed to update WiFi p2p peers with reasonCode " + reasonCode);
                }
            });
        }
    }


//    /**
//     * obtain a device peer
//     * */
//    public void obtainPeer(final WifiP2pDevice device) {
//
//        // Obtain a peer on this channel. Provide a listener for success callback
//        WifiP2pConfig config = new WifiP2pConfig();
//        config.deviceAddress = device.deviceAddress;
//        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onFailure(int i) {
//
//            }
//        });
//    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        // Updated list of available peers
        DataSingleton.getInstance().updateWifiP2pDeviceList(wifiP2pDeviceList);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        if(wifiP2pInfo.groupFormed){

            // Set the group owner address
            DataSingleton.getInstance().setGroupOwnerIPAddress(wifiP2pInfo.groupOwnerAddress.getHostAddress());
            // maintain whether or not this device is the group owner/ messaging server
            DataSingleton.getInstance().setGroupOwner(wifiP2pInfo.isGroupOwner);

            // Make sure all the runners are dead and reboot them appropriately.
            // This is to handle (ungracefully) a change in host application

            if(wifiP2pInfo.isGroupOwner){
                // Group owner, spin up server threads
                ThreadManager.execute(new InMessagesRunnable(mActivity, wifiP2pInfo.isGroupOwner, 8988, DataSingleton.getInstance().getDeviceIPAddress()));
                ThreadManager.execute(new OutMessagesRunnable());
            } else {
                // Client application. Poll group owner
                ThreadManager.execute(new PollMessagesRunnable(mActivity, wifiP2pInfo.groupOwnerAddress.getHostAddress(), 8985));
            }

        }
    }


}
