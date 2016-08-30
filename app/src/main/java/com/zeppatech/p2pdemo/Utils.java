package com.zeppatech.p2pdemo;

import android.text.format.DateFormat;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by PSchuette on 8/28/16.
 */
public class Utils {

    private static long YEAR_IN_MILLIS = 365*24*60*60*1000l;

    /**
     * get the number of years between two dates
     *
     * potential for error because of leap year
     *
     * @param lowerDate
     * @param upperDate
     * @return number of years as an int
     */
    public static long getYearsBetweenDates(Date lowerDate, Date upperDate){
        if(lowerDate==null || upperDate==null){
            return -1;
        }
        long lowerTime = lowerDate.getTime();
        long upperTime = upperDate.getTime();
        long diffTime = upperTime-lowerTime;

        return diffTime/YEAR_IN_MILLIS;
    }

    /**
     * Format a date
     * @param date
     * @return
     */
    public static String formatDate(Date date){
        DateFormat df = new DateFormat();
        CharSequence cs = df.format("MM/dd/yyyy", date);
        return cs.toString();
    }


    /**
     * Convert a byte array into a string
     *
     * @param bytes to be coverted to string
     * @return resulting string conversion
     */
    public static String bytesToString(byte[] bytes) {
        int length = 0;
        for(int i =0; i < bytes.length; i++){
            if(bytes[i]==0)
                break;
            else
                length++;
        }
        byte[] trimmedBytes = new byte[length];
        for(int i=0; i< length; i++){
            trimmedBytes[i] = bytes[i];
        }
        return new String(trimmedBytes, Charset.forName("UTF-8"));
    }


    /*
    *
    * Stackoverflow's "Whome' gets all the credits for the two methods below
    *
    * http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
    *
     */

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    private Utils(){
        throw new RuntimeException("Should never instantiate utils");
    }
}
