package com.zeppatech.p2pdemo.data;

/**
 * Created by Pschuette on 8/29/16.
 */
public class DeviceInfo implements  Comparable<DeviceInfo>{

    // Ip address of this device
    private String ip;

    // Name of this device
    private String name;

    /**
     * Construct
     * @param ip
     * @param name
     */
    public DeviceInfo(String ip, String name){
        this.ip = ip;
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        // Devices are equal if their IP addresses are the same
        if(obj instanceof DeviceInfo){
            return ((DeviceInfo) obj).getIp().equalsIgnoreCase(ip);
        } else return false;
    }

    @Override
    public int compareTo(DeviceInfo deviceInfo) {
        return deviceInfo.getName().compareTo(name);
    }
}
