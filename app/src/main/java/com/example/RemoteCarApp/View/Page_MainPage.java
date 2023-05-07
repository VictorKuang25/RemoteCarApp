package com.example.RemoteCarApp.View;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.RemoteCarApp.CarViewModel;
import com.example.RemoteCarApp.ConnectionStatus;
import com.example.RemoteCarApp.R;
import com.example.RemoteCarApp.View.CustomView.View_ConnectionPage;
import com.example.RemoteCarApp.View.CustomView.View_Dashboard;
import com.example.RemoteCarApp.View.CustomView.View_Joystick;

public class Page_MainPage extends Fragment {
    //-------------------
    ImageView btn_connectionPage;
    ImageView iv_sending,iv_receiving;
    View_Joystick joystick;
    View_ConnectionPage connectionPage;
    View_Dashboard dashboard;
    TextView tv_laserF, tv_laserB,tv_laserL,tv_laserR;
    //-------------------
    CarViewModel carViewModel;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_mainpage, container, false);

        btn_connectionPage = view.findViewById(R.id.btn_connectionPage);
        joystick = view.findViewById(R.id.joystick_main);
        dashboard = view.findViewById(R.id.view_dashboard);
        iv_sending = view.findViewById(R.id.iv_sending);
        iv_receiving = view.findViewById(R.id.iv_receiving);

        btn_connectionPage.setOnClickListener(v -> connectionPage.show());

        tv_laserF = view.findViewById(R.id.tv_laserF);
        tv_laserB = view.findViewById(R.id.tv_laserB);
        tv_laserL = view.findViewById(R.id.tv_laserL);
        tv_laserR = view.findViewById(R.id.tv_laserR);
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
            btn_connectionPage.setImageTintList(ColorStateList.valueOf(status == ConnectionStatus.CONNECTED ? Color.GREEN : Color.GRAY));
            if(status == ConnectionStatus.CONNECTED) onConnected();
            else if(status == ConnectionStatus.DISCONNECTED) onDisconnect();
        });
        carViewModel.getCarSpeed().observe(getViewLifecycleOwner(), aFloat -> {
            dashboard.setSpeed(aFloat * carViewModel.getMaxSpeed().getValue());
            dashboard.setCarReversingSing(aFloat < 0);
        });
        carViewModel.getCarAngle().observe(getViewLifecycleOwner(), aFloat -> {
            dashboard.setAngle(aFloat);
        });

        carViewModel.isTransmitting().observe(getViewLifecycleOwner(), aBoolean -> iv_sending.setImageTintList(ColorStateList.valueOf(aBoolean ? Color.GREEN : Color.GRAY)));
        carViewModel.isReceivingData().observe(getViewLifecycleOwner(), aBoolean -> iv_receiving.setImageTintList(ColorStateList.valueOf(aBoolean ? Color.RED : Color.GRAY)));

        carViewModel.getRx_laserF().observe(getViewLifecycleOwner(), aFloat -> {
            if(aFloat == null) {
                dashboard.setWarningSing(View_Dashboard.CAR_FRONT,false);
                tv_laserF.setText("null");
                return;
            }
            tv_laserF.setText("" + Math.round(aFloat*100)/100 + "cm");
            dashboard.setWarningSing(View_Dashboard.CAR_FRONT,aFloat < carViewModel.getWarningDistance().getValue());
        });
        carViewModel.getRx_laserB().observe(getViewLifecycleOwner(), aFloat -> {
            if(aFloat == null) {
                dashboard.setWarningSing(View_Dashboard.CAR_BACK,false);
                tv_laserB.setText("null");
                return;
            }
            tv_laserB.setText("" + Math.round(aFloat*100)/100 + "cm");
            dashboard.setWarningSing(View_Dashboard.CAR_BACK,aFloat < carViewModel.getWarningDistance().getValue());
        });
        carViewModel.getRx_laserL().observe(getViewLifecycleOwner(), aFloat -> {
            if(aFloat == null) {
                dashboard.setWarningSing(View_Dashboard.CAR_LEFT,false);
                tv_laserL.setText("null");
                return;
            }
            tv_laserL.setText("" + Math.round(aFloat*100)/100 + "cm");
            dashboard.setWarningSing(View_Dashboard.CAR_LEFT,aFloat < carViewModel.getWarningDistance().getValue());
        });
        carViewModel.getRx_laserR().observe(getViewLifecycleOwner(), aFloat -> {
            if(aFloat == null) {
                dashboard.setWarningSing(View_Dashboard.CAR_RIGHT,false);
                tv_laserR.setText("null");
                return;
            }
            tv_laserR.setText("" + Math.round(aFloat*100)/100 + "cm");
            dashboard.setWarningSing(View_Dashboard.CAR_RIGHT,aFloat < carViewModel.getWarningDistance().getValue());
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

        return  view;
    }

    public void onConnected(){
        carViewModel.transmissionStart();
    }

    public void onDisconnect(){
        carViewModel.transmissionEnd();
    }


    public void toast(String s){
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
    }
}
