package com.zeppatech.p2pdemo.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zeppatech.p2pdemo.R;
import com.zeppatech.p2pdemo.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by PSchuette on 8/28/16.
 *
 * Adapter used to display messages that have been received
 */
public class MessagesAdapter extends BaseAdapter {

    Context context;
    private List<Message> messages;

    public MessagesAdapter(Context context) {
        super();
        this.context = context;
        messages = new ArrayList<Message>();
        messages.addAll(DataSingleton.getInstance().getReceivedMessages());
        Collections.sort(messages);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_message, viewGroup, false);
        }

        // relevant views and objects
        TextView senderTextView = (TextView) view.findViewById(R.id.message_sender);
        TextView nameTextView = (TextView) view.findViewById(R.id.message_name);
        TextView birthdayTextView = (TextView) view.findViewById(R.id.message_birthday);
        Message msg = getItem(i);

        // Set text views
        senderTextView.setText(msg.getSenderAddress());
        nameTextView.setText(msg.getName());
        long years = Utils.getYearsBetweenDates(msg.getBirthday(), new Date());
        String birthdayString = Utils.formatDate(msg.getBirthday());
        birthdayTextView.setText(years + " years old, born " + birthdayString);


        return view;
    }

    /**
     * add a message to the list of messages this adapter is holding
     * @param message
     */
    public void addMessage(Message message){
        this.messages.add(message);
        Collections.sort(messages);
        notifyDataSetChanged();
    }

}
