package com.zeppatech.p2pdemo.p2p.threads;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.zeppatech.p2pdemo.Utils;
import com.zeppatech.p2pdemo.data.DataSingleton;
import com.zeppatech.p2pdemo.data.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by PSchuette on 8/30/16.
 */
public class PollMessagesRunnable implements Runnable {

    private static final String TAG = PollMessagesRunnable.class.getSimpleName();
    private static final int SOCKET_TIMEOUT = 5000;
    // sleep for a second between polls
    private static final int MILLIS_SLEEP_BETWEEN_POLLS = 1000;


    Activity activity;
    String go_host;
    int port;

    public PollMessagesRunnable(Activity mActivity, String go_host, int port){
        this.activity = mActivity;
        this.go_host = go_host;
        this.port = port;
    }

    @Override
    public void run() {
        String deviceIPAddress = DataSingleton.getInstance().getDeviceIPAddress();
        byte[] ipBytes = deviceIPAddress.getBytes();
        // Continuously poll server device for messages meant to be delivered here
        while(true){

            Socket socket = new Socket();
            boolean doSleep = true;
            try {
                socket.bind(null);
                socket.connect((new InetSocketAddress(go_host, port)), SOCKET_TIMEOUT);

                // Write the device ip address to the device owner
                OutputStream os = socket.getOutputStream();
                os.write(ipBytes);

                // Read the response message
                InputStream is = socket.getInputStream();
                // TODO: optimize for maximum payload size
                byte[] respBytes = new byte[128];
                is.read(respBytes);

                // Convert the byte array to string
                String resp = Utils.bytesToString(respBytes);

                if(!(doSleep = resp.equalsIgnoreCase("empty"))) {
                    // response is not empty, assume there is a json message
                    try {
                        // grab the response json and construct a message object from it
                        JSONObject json = new JSONObject(resp);
                        final Message msg = new Message(json);

                        // Jump back onto the UI thread to dispatch the polled message
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DataSingleton.getInstance().addMessage(msg);
                                // Display a quick toast so we know a message was received
                                Toast.makeText(activity, "new Message from + " + msg.getSenderAddress(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    } catch (JSONException e){
                        // didn't receive expected message
                        doSleep=true;
                    }
                }

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

            /*
            *
            * If no message was received, sleep and wait for another
            *
             */
            if(doSleep) {
                // thread should sleep before polling again
                try {
                    Thread.sleep(MILLIS_SLEEP_BETWEEN_POLLS);
                } catch (InterruptedException e){
                    // Eat this exception for now
                }
            }
        }
    }
}
