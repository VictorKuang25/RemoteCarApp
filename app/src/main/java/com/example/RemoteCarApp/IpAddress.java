package com.example.RemoteCarApp;

import androidx.annotation.NonNull;

import java.util.Objects;

public class IpAddress {
    private final String ip;
    private final int port;

    public IpAddress(String ip, int port) {
        if( !isLegalIp(ip) ) ip = "127.0.0.1";
        if( !isLegalPort(port) ) port = 5050;
        this.ip = ip;
        this.port = port;
    }
    public IpAddress(IpAddress ipAddress) {
        this.ip = ipAddress.getIp();
        this.port = ipAddress.getPort();
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpAddress ipAddress = (IpAddress) o;
        return port == ipAddress.port && ip.equals(ipAddress.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    @NonNull
    @Override
    public String toString() {
        return ip + ":" + port;
    }

    //-------------------------------------------------------
    private Boolean isLegalIp(String ip){
        if(ip == null) return false;

        String[] ipArray = ip.split("\\.");
        if(ipArray.length != 4) return false;

        for ( String s : ipArray ) {
            int num;
            try { num = Integer.parseInt(s); }
            catch (Exception e) { return false; }
            if (num < 0 || num > 255) return false;
        }
        return true;
    }
    private Boolean isLegalPort(int port){
        return port >= 0 && port <= 65535;
    }

    //-------------------------------------------------------
    public static class IllegalIpException extends Exception {
        IllegalIpException(String ip){
            super("\"" + ip + "\" Not Legal Ip");
        }
    }
    public static class IllegalPortException extends Exception {
        IllegalPortException(int port){
            super("\"" + port + "\" Not Legal Port");
        }
    }
}
