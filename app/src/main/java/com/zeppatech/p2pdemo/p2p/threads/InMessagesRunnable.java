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
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by PSchuette on 8/30/16.
 *
 * Runnable listening on socket for messages being sent to the server device that will be polled by client
 *
 */
public class InMessagesRunnable implements Runnable {

    private static final String TAG = InMessagesRunnable.class.getSimpleName();

    int port;
    boolean isGroupOwner;
    String ipAddress;
    Activity activity;

    public InMessagesRunnable(Activity activity, boolean isGroupOwner, int port, String ipAddress){
        this.activity = activity;
        this.isGroupOwner = isGroupOwner;
        this.port = port;
        this.ipAddress = ipAddress;
    }


    @Override
    public void run() {
        // Infinite loop trying to read messages
        while(true) {

            try {
                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */
                ServerSocket serverSocket = new ServerSocket(port);
                Socket client = serverSocket.accept();

                /**
                 * If this code is reached, a client has connected and transferred data
                 * Save the input stream from the client as a file
                 */

                // Read the response
                InputStream inputstream = client.getInputStream();
                byte[] respBytes = new byte[1024];
                inputstream.read(respBytes);
                // Convert input into json string and add to
                final String resp = Utils.bytesToString(respBytes);

                try {
                    JSONObject json = new JSONObject(resp);
                    final String recipientIp = json.getString("to");
                    if(recipientIp!=null && !recipientIp.isEmpty()){
                        if(recipientIp.equalsIgnoreCase(ipAddress)){
                            // This message was intended for server device
                            // Hop onto ui thread and add this message to the singleton
                            final Message msg = new Message(json);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DataSingleton.getInstance().addMessage(msg);
                                    // Display a quick toast so we know a message was received
                                    Toast.makeText(activity, "new Message from + " + msg.getSenderAddress(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Valid recipient, add it to the message map
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DataSingleton.getInstance().putMessage(recipientIp, resp);
                                }
                            });
                        }
                    }

                } catch (JSONException e){
                    //
                    e.printStackTrace();
                }


                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

        }
    }
}
