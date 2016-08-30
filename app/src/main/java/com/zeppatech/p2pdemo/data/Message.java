package com.zeppatech.p2pdemo.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by PSchuette on 8/25/16.
 */
public class Message implements Comparable<Message> {

    private String senderAddress;
    private String name;
    private Date birthday;
    private Date received;

    /**
     * Construct a message object from explicit parameters
     * @param senderAddress
     * @param name
     * @param birthday
     * @param received
     */
    public Message(String senderAddress, String name, Date birthday, Date received) {
        this.senderAddress = senderAddress;
        this.name = name;
        this.birthday = birthday;
        this.received = received;
    }

    /**
     * Construct a message object from json
     * @param json
     * @throws JSONException
     */
    public Message(JSONObject json) throws JSONException {
        senderAddress = json.getString("from");
        name = json.getString("name");
        long birthdayInMillis = json.getLong("date");
        birthday = new Date(birthdayInMillis);
        received = new Date();
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public String getName() {
        return name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public Date getReceived() {
        return received;
    }

    @Override
    public int compareTo(Message message) {
        return received.compareTo(message.getReceived());
    }
}
