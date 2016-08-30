package com.zeppatech.p2pdemo.p2p.threads;

import android.util.Log;

import com.zeppatech.p2pdemo.Utils;
import com.zeppatech.p2pdemo.data.DataSingleton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by PSchuette on 8/30/16.
 *
 * Server called that fetches messages that have been posted for this user
 */
public class OutMessagesRunnable implements Runnable {

    private static final String TAG = OutMessagesRunnable.class.getSimpleName();

    public OutMessagesRunnable(){

    }

    @Override
    public void run() {

        while(true){
            try {
                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */
                ServerSocket serverSocket = new ServerSocket(8985);
                Socket client = serverSocket.accept();

                /**
                 * If this code is reached, a client has connected and transferred data
                 */

                // Read the input stream as the ip address of connected device
                InputStream inputstream = client.getInputStream();
                // TODO:  optimize byte count for request
                byte[] resp = new byte[1024];
                inputstream.read(resp);
                String recipientIp = Utils.bytesToString(resp);

                // If this is a valid recipient
                if(recipientIp!=null && !recipientIp.isEmpty()){

                    // Grab the last message from this user
                    String messageJsonString = DataSingleton.getInstance().pollMessageQueue(recipientIp);

                    // write it to the calling client
                    OutputStream os = client.getOutputStream();
                    os.write(messageJsonString.getBytes());

                }

                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
