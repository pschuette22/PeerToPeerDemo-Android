package com.zeppatech.p2pdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zeppatech.p2pdemo.R;
import com.zeppatech.p2pdemo.data.DataSingleton;
import com.zeppatech.p2pdemo.data.Message;
import com.zeppatech.p2pdemo.data.MessagesAdapter;

/**
 * Created by PSchuette on 8/25/16.
 */
public class MessagesFragment extends Fragment implements DataSingleton.MessageReceivedListener{

    MessagesAdapter messagesAdapter;

    public MessagesFragment() {
        super();
    }

    @Override
    public void onStart() {
        super.onStart();
        DataSingleton.getInstance().registerMessageReceivedListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_messages,container,false);

        // initialize the list adapter and display messages
        ListView lv = (ListView) v.findViewById(R.id.messages_list);
        messagesAdapter = new MessagesAdapter(getContext());
        lv.setAdapter(messagesAdapter);


        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        DataSingleton.getInstance().unregisterMessageReceivedListener(this);
    }

    @Override
    public void onMessageReceived(Message message) {
        if(messagesAdapter!=null){
            // add the new message
            messagesAdapter.addMessage(message);
        }

    }
}
