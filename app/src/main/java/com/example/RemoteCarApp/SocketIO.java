package com.example.RemoteCarApp;

import androidx.annotation.NonNull;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class SocketIO {
    private final String ip;
    private final int port;
    private int timeout = 5000;
    private Socket socket = null;
    private BufferedInputStream inputStream = null;
    private BufferedOutputStream outputStream = null;

    private Boolean isConnect = false;

    public SocketIO(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }
    public int getPort() {
        return port;
    }
    public int getTimeout() {
        return timeout;
    }

    //    ------------------------------------------------------------------------------
    public interface OnGetEventListener {
        void onGetEvent(ConnectionStatus connectionStatus);
    }
    private OnGetEventListener onGetEventListener;
    private void callEventListener(ConnectionStatus connectionStatus) {
        if (this.onGetEventListener != null) this.onGetEventListener.onGetEvent(connectionStatus);
    }
    public void setOnGetEventListener(OnGetEventListener onGetEventListener) {
        this.onGetEventListener = onGetEventListener;
    }
    public interface OnGetInputListener {
        void onGetInput(byte[] inputByteArray, String inputString);
    }

    //------------------------------------------------------------------------------
    private Runnable connectRunnable;
    public void connect(int timeout){
        this.timeout = timeout;
        connect();
    }
    public void connect(){
        connect(0,null);
    }
    public void connect(int readBytes,OnGetInputListener onGetInputListener) {
        if(isConnect || connectRunnable != null) return;
        connectRunnable = () -> {
            try {
                SocketAddress address = new InetSocketAddress(ip, port);
                socket = new Socket();
                socket.connect(address, timeout);
                inputStream = new BufferedInputStream(socket.getInputStream());
                outputStream = new BufferedOutputStream(socket.getOutputStream());
                isConnect = true;
                callEventListener(ConnectionStatus.CONNECTED);
                listenOnListenThread(readBytes, onGetInputListener);
            } catch (SocketTimeoutException e) {
                callEventListener(ConnectionStatus.ERROR_TIMEOUT);
            } catch (Exception e) {
                callEventListener(ConnectionStatus.ERROR_UNKNOWN);
            }
            connectRunnable = null;
        };
        callEventListener(ConnectionStatus.CONNECTING);
        new Thread(connectRunnable).start();
    }

    public void disconnect() {
        disconnect(true);
    }
    private void disconnect(Boolean activeDisconnect) {
        if (!isConnect) return;
        isConnect = false;
        callEventListener(activeDisconnect ? ConnectionStatus.DISCONNECTED : ConnectionStatus.ERROR_SERVER_DISCONNECTION);
        new Thread() {
            @Override
            public void run() {
                try {
                    if (socket != null) {
                        socket.close();
                        socket = null;
                    }
                    if (inputStream != null) {
                        inputStream.close();
                        inputStream = null;
                    }
                    if (outputStream != null) {
                        outputStream.close();
                        outputStream = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //---------------------------------------------
    public synchronized boolean send(byte[] outputBytes) {
        if (!isConnect) return false;

        new Thread() {
            @Override
            public void run() {
                try {
                    outputStream.write(outputBytes);
                    outputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    disconnect(true);
                }
            }
        }.start();
        return true;
    }
    public boolean send(@NonNull String outputString) {
        return send(outputString.getBytes(StandardCharsets.UTF_8));
    }

    //---------------------------------------------
    private Thread listen;
    private Runnable listenRunnable;
    private void readOnListenThread(int readByteSize, Boolean keepListen, OnGetInputListener onGetInputListener) {
        if (!isConnect) return;

        listenRunnable = () -> {
            listenRunnable = null;
            Boolean readFail = true;
            byte[] byteTemp;

            try {
                do {
                    readFail = inputStream.read(byteTemp = new byte[readByteSize]) == -1;
                    if(onGetInputListener != null) onGetInputListener.onGetInput(byteTemp,new String(byteTemp));
                } while (listenRunnable == null && !readFail && keepListen);
            } catch (IOException e) {
                disconnect(true);
            }

            if (isConnect && readFail) disconnect(false);
            if(listenRunnable != null) {
                listen = new Thread(listenRunnable);
                listen.start();
            }
        };

        if (listen != null && listen.isAlive()) return;
        listen = new Thread(listenRunnable);
        listen.start();
        //  notNull isAlive
        //  0       0       start
        //  0       1       start
        //  1       0       start
        //  1       1       return
    }
    private void read(int readByteSize, Boolean keepListen, OnGetInputListener onGetInputListener) {
        if (!isConnect) return;

        Boolean readFail = true;
        byte[] byteTemp;

        try {
            readFail = false;
            do {
                readFail = inputStream.read(byteTemp = new byte[readByteSize]) == -1;
                if(onGetInputListener != null) onGetInputListener.onGetInput(byteTemp,new String(byteTemp));
            } while (!readFail && keepListen);
        } catch (IOException e) {
            disconnect(true);
        }

        if (isConnect && readFail) disconnect(false);
    }

    //---------------------------------------------
    /** Warning : may cause delayed posting of real-time connection status*/
    @Deprecated
    public void readOnListenThread(int readByteSize, OnGetInputListener onGetInputListener) {
        readOnListenThread(readByteSize, false, onGetInputListener);
    }
    public void listenOnListenThread(int readByteSize, OnGetInputListener onGetInputListener) {
        readOnListenThread(readByteSize, true, onGetInputListener);
    }
    public void read(int readByteSize, OnGetInputListener onGetInputListener) {
        read(readByteSize, false, onGetInputListener);
    }
    public void listen(int readByteSize, OnGetInputListener onGetInputListener) {
        read(readByteSize, true, onGetInputListener);
    }

    public Boolean isConnect() {
        return isConnect;
    }
    @SuppressWarnings("UnusedReturnValue")
    public long clearInputBuffer(){
        long skipNum = 0;
        try {
            skipNum = inputStream.skip(inputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return skipNum;
    }
}
