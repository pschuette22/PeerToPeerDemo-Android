package com.zeppatech.p2pdemo.p2p;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.zeppatech.p2pdemo.data.DataSingleton;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by PSchuette on 8/26/16.
 */
public class MessageSenderService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    private static final String TAG = MessageSenderService.class.getSimpleName();
    public static final String ACTION_SEND_MESSAGE = "com.zeppatech.p2pdemo.SEND_MESSAGE";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    public static final String EXTRAS_RECIPIENT_IP = "recipient_ip";
    public static final String EXTRAS_JSON = "json_payload";

    public static final int MESSAGE_SUCCESS = 200;

    public MessageSenderService(String name) {
        super(name);
    }

    public MessageSenderService(){
        super(MessageSenderService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(intent.getAction().contentEquals(ACTION_SEND_MESSAGE)){
            // Send a message
            String jsonString = intent.getExtras().getString(EXTRAS_JSON);

            if(DataSingleton.getInstance().isGroupOwner()) {
                // If this device is the group owner, just add to the device map
                String recipient_ip = intent.getExtras().getString(EXTRAS_RECIPIENT_IP);
                DataSingleton.getInstance().putMessage(recipient_ip, jsonString);
            } else {
                String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
                Socket socket = new Socket();
                int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

                try {
                    Log.d(TAG, "Opening client socket - ");
                    socket.bind(null);
                    socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                    Log.d(TAG, "Client socket - " + socket.isConnected());
//                InputStream is = socket.getInputStream();
                    // Read the input byte stream

                    OutputStream os = socket.getOutputStream();
                    os.write(jsonString.getBytes());

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
            }

        }


    }
}
