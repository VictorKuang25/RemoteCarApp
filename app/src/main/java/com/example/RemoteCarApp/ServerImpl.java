package com.example.RemoteCarApp;

public class ServerImpl {
    private SocketIO socketIO;

    public ServerImpl() {
    }

    public void register(IpAddress ipAddress) {
        this.socketIO = new SocketIO(ipAddress.getIp(), ipAddress.getPort());
        socketIO.setOnGetEventListener(this::connectionStatusChange);
    }

    public void connect() {
        socketIO.connect();
    }
    public void connect(int readBytes,SocketIO.OnGetInputListener onGetInputListener) {
        socketIO.connect(readBytes,onGetInputListener);
    }

    public void disconnect() {
        socketIO.disconnect();
    }

    public boolean send(byte[] bytes) {
        return socketIO.send(bytes);
    }

    public boolean isConnected(){
        if(socketIO == null) return false;
        return socketIO.isConnect();
    }

//--------------------
    public interface OnConnectionStatusChangeListener {
        void onStatusChange(ConnectionStatus connectionStatus);
    }
    private OnConnectionStatusChangeListener onConnectionStatusChangeListener = null;
    public void setOnConnectionStatusChangeListener(OnConnectionStatusChangeListener onConnectionStatusChangeListener) {
        this.onConnectionStatusChangeListener = onConnectionStatusChangeListener;
    }
    private void connectionStatusChange(ConnectionStatus connectionStatus){
        if(onConnectionStatusChangeListener == null) return;
        onConnectionStatusChangeListener.onStatusChange(connectionStatus);
    }
//--------------------
}
