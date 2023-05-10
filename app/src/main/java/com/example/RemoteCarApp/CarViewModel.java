package com.example.RemoteCarApp;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class CarViewModel extends ViewModel {

    //--- connection ---
    private final MutableLiveData<IpAddress> ipAddress = new MutableLiveData<>( new IpAddress("192.168.212.180",5050));
    private final MutableLiveData<ConnectionStatus> connectionStatus = new MutableLiveData<>(ConnectionStatus.DISCONNECTED);
    private final ServerImpl server = new ServerImpl();

    //--- packet transmission ---
    private Timer timer;
    private TimerTask timerTask;
    private final InstantMutableLiveData<Boolean> isTransmitting = new InstantMutableLiveData<>(false);
    private final InstantMutableLiveData<Float> transmissionPeriod = new InstantMutableLiveData<>(200f);
    //speed[0], angle[1], maxSpeed[2], warningDistance[3], BrakingDistance[4]
    private final int[] transportData = {-1,-1,0,0,0};
    private static final int sendTimes = 1;
    private int sendCounter = 0;

    private final InstantMutableLiveData<Boolean> isReceivingData = new InstantMutableLiveData<>(false);
    //--- car data ---
    private final InstantMutableLiveData<Float> tx_speedSet = new InstantMutableLiveData<>(0.0f);
    private final InstantMutableLiveData<Integer> tx_angleSet = new InstantMutableLiveData<>(0);
    private final InstantMutableLiveData<Integer> tx_maxSpeedSet = new InstantMutableLiveData<>(10);
    private final InstantMutableLiveData<Integer> tx_warningDistanceSet = new InstantMutableLiveData<>(50);
    private final InstantMutableLiveData<Integer> tx_brakingDistanceSet = new InstantMutableLiveData<>(10);

    private final InstantMutableLiveData<Float> rx_laserF = new InstantMutableLiveData<>(null);
    private final InstantMutableLiveData<Float> rx_laserB = new InstantMutableLiveData<>(null);
    private final InstantMutableLiveData<Float> rx_laserL = new InstantMutableLiveData<>(null);
    private final InstantMutableLiveData<Float> rx_laserR = new InstantMutableLiveData<>(null);

    private final InstantMutableLiveData<String> rx_emergencyBreakEvent = new InstantMutableLiveData<>(null);

    private final InstantMutableLiveData<Float> rx_gx = new InstantMutableLiveData<>(null);
    private final InstantMutableLiveData<Float> rx_gy = new InstantMutableLiveData<>(null);
    private final InstantMutableLiveData<Float> rx_gz = new InstantMutableLiveData<>(null);
    private final InstantMutableLiveData<Float> rx_ax = new InstantMutableLiveData<>(null);
    private final InstantMutableLiveData<Float> rx_ay = new InstantMutableLiveData<>(null);
    private final InstantMutableLiveData<Float> rx_az = new InstantMutableLiveData<>(null);
    private final InstantMutableLiveData<Float> rx_temperature = new InstantMutableLiveData<>(null);
    private final InstantMutableLiveData<Float> rx_battery = new InstantMutableLiveData<>(null);
    //--- remote setup ---
    private final InstantMutableLiveData<Integer> maxAngle = new InstantMutableLiveData<>(35);

    public CarViewModel() {
        server.register(Objects.requireNonNull(ipAddress.getValue()));
        server.setOnConnectionStatusChangeListener(connectionStatus::postValue);
    }

    /*---------------------- display data -----------------------------*/
    //--- connection ---
    public LiveData<IpAddress> getIpAddress() {
        return ipAddress;
    }
    public LiveData<ConnectionStatus> getConnectionStatus() {
        return connectionStatus;
    }
    //--- transmission ---
    public LiveData<Boolean> isTransmitting() {
        return isTransmitting;
    }
    public LiveData<Float> getTransmissionPeriod() {
        return transmissionPeriod;
    }
    public LiveData<Boolean> isReceivingData() {
        return isReceivingData;
    }

    //--- car data ---
    public LiveData<Float> getCarSpeed() {
        return tx_speedSet;
    }
    public LiveData<Integer> getCarAngle() {
        return tx_angleSet;
    }
    public LiveData<Integer> getMaxSpeed() {
        return tx_maxSpeedSet;
    }
    public LiveData<Integer> getWarningDistance() {
        return tx_warningDistanceSet;
    }
    public LiveData<Integer> getBrakingDistance() {
        return tx_brakingDistanceSet;
    }

    public LiveData<Float> getRx_laserF() {
        return rx_laserF;
    }
    public LiveData<Float> getRx_laserB() {
        return rx_laserB;
    }
    public LiveData<Float> getRx_laserL() {
        return rx_laserL;
    }
    public LiveData<Float> getRx_laserR() {
        return rx_laserR;
    }

    public LiveData<String> getEmergencyBreakEvent() {
        return rx_emergencyBreakEvent;
    }

    public LiveData<Float> getRx_gx(){
        return rx_gx;
    }
    public LiveData<Float> getRx_gy(){
        return rx_gy;
    }
    public LiveData<Float> getRx_gz(){
        return rx_gz;
    }
    public LiveData<Float> getRx_ax(){
        return rx_ax;
    }
    public LiveData<Float> getRx_ay(){
        return rx_ay;
    }
    public LiveData<Float> getRx_az(){
        return rx_az;
    }
    public LiveData<Float> getRx_temperature(){
        return rx_temperature;
    }
    public LiveData<Float> getRx_battery(){
        return rx_battery;
    }
    //--- remote setup ---
    public LiveData<Integer> getMaxAngle() {
        return maxAngle;
    }

    /*---------------------- control -----------------------------*/
    //--- connection ---
    public void connect(){
        server.connect(9, (inputByteArray, inputString) -> {
            if(inputByteArray[2] == 0x31) {
                server.read(39 - 9, (inputByteArray1, inputString1) -> {
                    byte[] newBytes = new byte[39];
                    System.arraycopy(inputByteArray, 0, newBytes, 0, inputByteArray.length);
                    System.arraycopy(inputByteArray1, 0, newBytes, inputByteArray.length, inputByteArray1.length);
                    packetDecode2(newBytes);
                });
            }else {
                packetDecode(inputByteArray);
            }
            isReceivingData.postValue(true);
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isReceivingData.postValue(false);
            }).run();
        });
    }
    public void  disconnect(){
        server.disconnect();
    }
    public void saveIp(String ip,int port){
        server.disconnect();
        IpAddress newIp = new IpAddress(ip,port);
        this.ipAddress.postValue(newIp);
        server.register(newIp);
    }

    //--- transmission ---
    public void setTransmissionPeriod(float period){
        transmissionPeriod.postValue(period);
    }
    public boolean transmissionStart(){
        if (!server.isConnected()) return false;
        if (isTransmitting.getInstantValue()) return true;
        sendCounter = 0;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                int dataLen = 10;
                for ( int transportDatum : transportData )  dataLen += transportDatum != 0 ? 5 : 0;
                ByteBuffer dataPacket = ByteBuffer.allocate(dataLen);

                dataPacket.put((byte) 0x25).putInt(dataLen);
                if(transportData[0] != 0) dataPacket.put((byte) 0x13).putFloat(tx_speedSet.getInstantValue());
                if(transportData[1] != 0) dataPacket.put((byte) 0x14).putInt(tx_angleSet.getInstantValue());
                if(transportData[2] != 0) {
                    dataPacket.put((byte) 0x15).putInt(tx_maxSpeedSet.getInstantValue());
                    transportData[2]--;
                }
                if(transportData[3] != 0) {
                    dataPacket.put((byte) 0x16).putInt(tx_warningDistanceSet.getInstantValue());
                    transportData[3]--;
                }
                if(transportData[4] != 0) {
                    dataPacket.put((byte) 0x17).putInt(tx_brakingDistanceSet.getInstantValue());
                    transportData[4]--;
                }
                dataPacket.putInt(sendCounter).put((byte) 0x52);

                if(!server.send(dataPacket.array())) transmissionEnd();
                sendCounter++;
            }
        };
        timer.schedule(timerTask,0, transmissionPeriod.getInstantValue().longValue());
        isTransmitting.postValue(true);
        return true;
    }
    public void transmissionEnd(){
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
        isTransmitting.postValue(false);
    }

    private void packetDecode(byte[] bytes){
        if(!(bytes[0] == 0x13 && bytes[1] == 0x01 && bytes[7] == 0x01 && bytes[8] == 0x31)) return;
        byte[] bytes1 = {bytes[6], bytes[5], bytes[4], bytes[3]};
        Float value = ByteBuffer.wrap(bytes1).getFloat();
        value = value <= 0 ? 0 : value;
        value = value > 100 ? null : value;
        switch (bytes[2]){
            case 0x21 :
                rx_laserF.postValue(value);
                break;
            case 0x22 :
                rx_laserB.postValue(value);
                break;
            case 0x23 :
                rx_laserL.postValue(value);
                break;
            case 0x24 :
                rx_laserR.postValue(value);
                break;
            case 0x41 :
                rx_battery.postValue(value);
                break;
            case 0x11 :
            case 0x12 :
            case 0x13 :
            case 0x14 :
                if(bytes[3] == 0x53 && bytes[4] == 0x54 && bytes[5] == 0x4f && bytes[6] == 0x50)
                    rx_emergencyBreakEvent.postValue("Emergency_Break_Event : 0x" + bytes[2]);
            break;
        }
    }
    private void packetDecode2(byte[] bytes){
        if(!(bytes[0] == 0x13 && bytes[1] == 0x01 && bytes[37] == 0x01 && bytes[38] == 0x31)) return;
        for ( int i = 2; i < 37; i+=5 ) {
            byte[] bytes1 = {bytes[i+4], bytes[i+3], bytes[i+2], bytes[i+1]};
            float value = ByteBuffer.wrap(bytes1).getFloat();
            switch (bytes[i]) {
                case 0x31 :
                    rx_gx.postValue(Math.max(Math.min(value,20),-20)); break;
                case 0x32 :
                    rx_gy.postValue(Math.max(Math.min(value,20),-20)); break;
                case 0x33 :
                    rx_gz.postValue(Math.max(Math.min(value,20),-20)); break;
                case 0x34 :
                    rx_ax.postValue(Math.max(Math.min(value,500),-500)); break;
                case 0x35 :
                    rx_ay.postValue(Math.max(Math.min(value,500),-500)); break;
                case 0x36 :
                    rx_az.postValue(Math.max(Math.min(value,500),-500)); break;
                case 0x37 :
                    rx_temperature.postValue(value); break;
            }
        }

    }
    //--- car data ---
    public void setCarSpeed(Float speed){
        this.tx_speedSet.postValue(speed);
    }
    public void setCarAngle(Integer angle){
        int maxAngle = this.maxAngle.getInstantValue();
        if(Math.abs(angle) > 90) angle = (180 - Math.abs(angle) > maxAngle) ? ((angle > 0 ? 1 : -1) * (180 - maxAngle)) : angle;
        else angle = (Math.abs(angle) > maxAngle) ? (angle > 0 ? maxAngle : -maxAngle) : angle;

        this.tx_angleSet.postValue(angle);
    }
    public void setMaxSpeed(Integer maxSpeed){
        this.tx_maxSpeedSet.postValue(maxSpeed);
        transportData[2] = sendTimes;
    }
    public void setWarningDistance(Integer warningDistance){
        this.tx_warningDistanceSet.postValue(warningDistance);
        transportData[3] = sendTimes;
    }
    public void setBrakingDistance(Integer brakingDistance){
        this.tx_brakingDistanceSet.postValue(brakingDistance);
        transportData[4] = sendTimes;
    }

    //--- remote setup ---
    public void setMaxAngle(Integer maxAngle){
        this.maxAngle.postValue(maxAngle);
    }

    /*-----------------------------------------------------------*/

}
