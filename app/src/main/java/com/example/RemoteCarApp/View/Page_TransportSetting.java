package com.example.RemoteCarApp.View;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.RemoteCarApp.CarViewModel;
import com.example.RemoteCarApp.ConnectionStatus;
import com.example.RemoteCarApp.R;
import com.example.RemoteCarApp.View.CustomView.View_ConnectionPage;
import com.example.RemoteCarApp.View.CustomView.View_Joystick;

public class Page_TransportSetting extends Fragment {

    ImageView btcConnection;
    View_ConnectionPage connectionPage;
    View_Joystick joystick;
    TextView tv_speed, tv_angle, tv_maxSpeed, tv_warningDistance, tv_brakingDistance;
    SeekBar bar_maxSpeed,bar_warningDistance,bar_brakingDistance,bar_maxAngle;
    TextView tv_Tx,tv_Rx;
    ImageView btn_Tx;
    SeekBar bar_transmissionRate;
    TextView tv_transmissionRate;
    TextView tv_rx_01,tv_rx_02,tv_rx_03,tv_rx_04,tv_rx_05;

    CarViewModel carViewModel;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_setting,container,false);

        btcConnection = view.findViewById(R.id.btcConnection);
        joystick = view.findViewById(R.id.joystick);
        tv_speed = view.findViewById(R.id.speedSet);
        tv_angle = view.findViewById(R.id.angleSet);
        tv_maxSpeed = view.findViewById(R.id.maxSpeedSet);
        tv_warningDistance = view.findViewById(R.id.warningDistanceSet);
        tv_brakingDistance = view.findViewById(R.id.brakingDistanceSet);
        bar_maxSpeed = view.findViewById(R.id.bar_maxSpeed);
        bar_warningDistance = view.findViewById(R.id.bar_warningDistance);
        bar_brakingDistance = view.findViewById(R.id.bar_brakingDistance);
        tv_Tx = view.findViewById(R.id.Tx);
        tv_Rx = view.findViewById(R.id.Rx);
        btn_Tx = view.findViewById(R.id.btn_Tx);
        bar_transmissionRate = view.findViewById(R.id.bar_transferRate);
        tv_transmissionRate = view.findViewById(R.id.tv_transferRate);
        bar_maxAngle = view.findViewById(R.id.bar_maxAngle);
        tv_rx_01 = view.findViewById(R.id.tv_rx_01);
        tv_rx_02 = view.findViewById(R.id.tv_rx_02);
        tv_rx_03 = view.findViewById(R.id.tv_rx_03);
        tv_rx_04 = view.findViewById(R.id.tv_rx_04);
        tv_rx_05 = view.findViewById(R.id.tv_rx_05);

        btcConnection.setOnClickListener(v -> connectionPage.show());

        //---------------------- INIT -----------------------------
        carViewModel = new ViewModelProvider(requireActivity()).get(CarViewModel.class);
        connectionPage = new View_ConnectionPage(requireActivity());

        //---------------------- UI update -----------------------------
        carViewModel.getIpAddress().observe(getViewLifecycleOwner(), ipAddress -> connectionPage.setAddress(ipAddress.getIp(),ipAddress.getPort()));
        carViewModel.getConnectionStatus().observe(getViewLifecycleOwner(), status -> {
            switch (status) {
                case ERROR_UNKNOWN:
                    connectionPage.setPage(View_ConnectionPage.PAGE_CONNECTION_FAIL);
                    toast("Unknown Error");
                    break;
                case ERROR_TIMEOUT:
                    connectionPage.setPage(View_ConnectionPage.PAGE_CONNECTION_FAIL);
                    toast("Timeout");
                    break;
                case ERROR_SERVER_DISCONNECTION:
                    connectionPage.setPage(View_ConnectionPage.PAGE_CONNECTION_FAIL);
                    toast("Server Disconnect");
                    break;
                case DISCONNECTED:
                    connectionPage.setPage(View_ConnectionPage.PAGE_DISCONNECTED);
                    break;
                case CONNECTING:
                    connectionPage.setPage(View_ConnectionPage.PAGE_CONNECTING);
                    break;
                case CONNECTED:
                    connectionPage.setPage(View_ConnectionPage.PAGE_CONNECTED);
                    break;
            }
            btcConnection.setImageTintList(ColorStateList.valueOf(status == ConnectionStatus.CONNECTED ? Color.GREEN : Color.GRAY));
        });
        carViewModel.getTransmissionPeriod().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                float max = 200;
                double delta = (1000 / aFloat * 10)/max;
                double pi = Math.acos(1 - (delta * 2));
                int progress = (int) (pi / Math.PI * max);
                bar_transmissionRate.setProgress(progress);

                tv_transmissionRate.setText( Math.round(10000/aFloat)/10f + " /s");
            }
        });
        carViewModel.getCarSpeed().observe(getViewLifecycleOwner(), aFloat -> tv_speed.setText("Speed : " + aFloat));
        carViewModel.getCarAngle().observe(getViewLifecycleOwner(), integer -> tv_angle.setText("Angle ( ± " + carViewModel.getMaxAngle().getValue() + "° )  : " + integer + "°"));
        carViewModel.getMaxSpeed().observe(getViewLifecycleOwner(), integer -> {
            tv_maxSpeed.setText("MaxSpeed : " + integer + " km/h");
            bar_maxSpeed.setProgress(integer);
        });
        carViewModel.getWarningDistance().observe(getViewLifecycleOwner(), integer -> {
            tv_warningDistance.setText("WarningDistance : " + integer + " cm");
            bar_warningDistance.setProgress(integer);
        });
        carViewModel.getBrakingDistance().observe(getViewLifecycleOwner(), integer -> {
            tv_brakingDistance.setText("BrakingDistance : " + integer + " cm");
            bar_brakingDistance.setProgress(integer);
        });
        carViewModel.isTransmitting().observe(getViewLifecycleOwner(), aBoolean -> btn_Tx.setImageTintList(ColorStateList.valueOf(aBoolean ? Color.GREEN : Color.GRAY)));
        carViewModel.getMaxAngle().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                tv_angle.setText("Angle ( ± " + integer + "° )  : " + carViewModel.getCarAngle().getValue() + "°");
                bar_maxAngle.setProgress(integer);
            }
        });

        carViewModel.getRx_laserF().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                tv_rx_01.setText("Laser_Front : " + (aFloat != null ? Math.round(aFloat*100)/100f + "cm" : "null") );
            }
        });
        carViewModel.getRx_laserB().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                tv_rx_02.setText("Laser_Back : " + (aFloat != null ? Math.round(aFloat*100)/100f + "cm" : "null") );
            }
        });
        carViewModel.getRx_laserL().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                tv_rx_03.setText("Laser_Left : " + (aFloat != null ? Math.round(aFloat*100)/100f + "cm" : "null") );
            }
        });
        carViewModel.getRx_laserR().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                tv_rx_04.setText("Laser_Right : " + (aFloat != null ? Math.round(aFloat*100)/100f + "cm" : "null") );
            }
        });
        //---------------------- user control -----------------------------
        connectionPage.setOnGetEventListener((event, ip, port) -> {
            switch (event) {
                case View_ConnectionPage.EVENT_CONNECT:
                    carViewModel.connect();
                    break;
                case View_ConnectionPage.EVENT_DISCONNECT:
                    carViewModel.disconnect();
                    break;
                case View_ConnectionPage.EVENT_SAVE_IP:
                    carViewModel.saveIp(ip, port);
                    break;
            }
        });
        btn_Tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carViewModel.isTransmitting().getValue()) carViewModel.transmissionEnd();
                else carViewModel.transmissionStart();

            }
        });
        joystick.setOnMoveListener((x, y, distance, degree) -> {
            float speed = Math.round(distance * 100) / 100f;
            speed = y >= 0 ? speed : -speed;
            int angle;
            angle = (int) Math.toDegrees(Math.asin(x / distance));
            angle = Math.abs(angle);
            angle = y >= 0 ? angle : 180 - angle;
            angle = x >= 0 ? angle : -angle;
            angle = Math.round(angle * 100) / 100;

            carViewModel.setCarSpeed(speed);
            carViewModel.setCarAngle(angle);
        });
        bar_maxSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                carViewModel.setMaxSpeed(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        bar_warningDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                carViewModel.setWarningDistance(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        bar_brakingDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                carViewModel.setBrakingDistance(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        bar_transmissionRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float max = 200;
                double pi = progress / max * Math.PI;
                double delta = (1 - Math.cos(pi)) / 2;
                float transmissionPeriod = (float) (1000 / (max * delta / 10));
                carViewModel.setTransmissionPeriod(transmissionPeriod);
                carViewModel.transmissionEnd();
                carViewModel.transmissionStart();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        bar_maxAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                carViewModel.setMaxAngle(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

//
//        //----------- update connection ui -----------
//


//
//        //----------- data transport -----------
//        tv_Tx.setOnClickListener(v -> {
//            if(transporting) transportEnd();
//            else transportStart();
//            updateUi();
//        });
//        btn_Tx.setOnClickListener(v -> {
//            if(transporting) transportEnd();
//            else transportStart();
//            updateUi();
//        });
//        bar_transferRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                float max = 200;
//                double pi = progress / max * Math.PI;
//                double delta = (1 - Math.cos(pi)) / 2;
//                transferPeriod = (float) (1000 / (max * delta / 10));
//                updateUi();
//                if(transporting) transportStart();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        return view;
    }


    //----------- display -----------
    public void toast(String s){
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
    }

}
