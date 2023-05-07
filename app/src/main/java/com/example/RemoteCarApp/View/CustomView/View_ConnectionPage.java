package com.example.RemoteCarApp.View.CustomView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.RemoteCarApp.R;

public class View_ConnectionPage extends ConstraintLayout {

    private View view;
    private Context context;

    private ConstraintLayout connectionPage;
    private TextView title,divider,hint;
    private ImageView wifi,loadingRing,retry,setting,back;
    private LinearLayout settingPage;
    private EditText ipSetting,portSetting;
    private Button saveSetting;

    private static final int STATUS_CONNECTION_FAIL = -1;
    private static final int STATUS_DISCONNECTED = 0;
    private static final int STATUS_CONNECTED = 1;
    private static final int STATUS_CONNECTING = 2;
    private int connectionStatus = STATUS_DISCONNECTED;
    private String IP = "192.168.0.0";
    private int PORT = 5050;

    public View_ConnectionPage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.connection_page,this,true);
        context = this.context;
        setDialogSize(900,600);

        connectionPage = view.findViewById(R.id.connectionPage);
        divider = view.findViewById(R.id.divider);
        title = view.findViewById(R.id.title);
        hint = view.findViewById(R.id.hint);
        wifi = view.findViewById(R.id.wifi);
        loadingRing = view.findViewById(R.id.loadingring);
        retry = view.findViewById(R.id.retry);
        setting = view.findViewById(R.id.setting);
        back = view.findViewById(R.id.back);
        settingPage = view.findViewById(R.id.settingPage);
        ipSetting = view.findViewById(R.id.ipSetting);
        portSetting = view.findViewById(R.id.portSetting);
        saveSetting = view.findViewById(R.id.saveSetting);

        setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setPage(PAGE_SETTING);
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (connectionStatus){
                    case STATUS_CONNECTION_FAIL:
                        setPage(PAGE_CONNECTION_FAIL); break;
                    case STATUS_DISCONNECTED:
                        setPage(PAGE_DISCONNECTED); break;
                    case STATUS_CONNECTED:
                        setPage(PAGE_CONNECTED); break;
                    case STATUS_CONNECTING:
                        setPage(PAGE_CONNECTING); break;
                }
            }
        });
    }

    public View_ConnectionPage(@NonNull Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.connection_page,this,true);
        context = this.context;
        setDialogSize(900,600);

        connectionPage = view.findViewById(R.id.connectionPage);
        divider = view.findViewById(R.id.divider);
        title = view.findViewById(R.id.title);
        hint = view.findViewById(R.id.hint);
        wifi = view.findViewById(R.id.wifi);
        loadingRing = view.findViewById(R.id.loadingring);
        retry = view.findViewById(R.id.retry);
        setting = view.findViewById(R.id.setting);
        back = view.findViewById(R.id.back);
        settingPage = view.findViewById(R.id.settingPage);
        ipSetting = view.findViewById(R.id.ipSetting);
        portSetting = view.findViewById(R.id.portSetting);
        saveSetting = view.findViewById(R.id.saveSetting);

        setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setPage(PAGE_SETTING);
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (connectionStatus){
                    case STATUS_CONNECTION_FAIL:
                        setPage(PAGE_CONNECTION_FAIL); break;
                    case STATUS_DISCONNECTED:
                        setPage(PAGE_DISCONNECTED); break;
                    case STATUS_CONNECTED:
                        setPage(PAGE_CONNECTED); break;
                    case STATUS_CONNECTING:
                        setPage(PAGE_CONNECTING); break;
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        textResize();
    }

    //-------------------------onGetEventListener------------------------
    public static final int EVENT_CONNECT = 0;
    public static final int EVENT_DISCONNECT = 1;
    public static final int EVENT_SAVE_IP = 2;
    public interface OnGetEventListener{
        public abstract void onGetEvent(int event, String ip, int port);
    }
    private OnGetEventListener onGetEventListener = new OnGetEventListener() {
        @Override
        public void onGetEvent(int event, String ip, int port) {
            switch (event){
                case EVENT_CONNECT:
                    Log.i("onGetEventListener","EVENT_CONNECT");
                    break;
                case EVENT_DISCONNECT:
                    Log.i("onGetEventListener","EVENT_DISCONNECT");
                    break;
                case EVENT_SAVE_IP:
                    Log.i("onGetEventListener","EVENT_SAVE_IP");
                    Log.i("IP",ip);
                    Log.i("PORT",port+"");
                    break;
            }
        }
    };
    public void setOnGetEventListener(OnGetEventListener onGetEventListener){
        this.onGetEventListener = onGetEventListener;
    }
    //-------------------------------------------------------------------


    public static final int PAGE_CONNECTION_FAIL = -1;
    public static final int PAGE_DISCONNECTED = 0;
    public static final int PAGE_CONNECTED = 1;
    public static final int PAGE_CONNECTING = 2;
    public static final int PAGE_SETTING = 3;

    @SuppressLint("SetTextI18n")
    @MainThread
    public void setPage(int page){
        hideSoftInputFromWindow();
        hideAllChildView(connectionPage);
        title.setVisibility(VISIBLE);
        divider.setVisibility(VISIBLE);
        loadingAnimeEnd();
        wifi.setOnClickListener(null);
        switch (page){
            case PAGE_DISCONNECTED:
                setting.setVisibility(VISIBLE);
                wifi.setVisibility(VISIBLE);
                hint.setVisibility(VISIBLE);
                title.setText("Disconnected");
                hint.setText("Tap to connect");
                wifi.setImageDrawable(AppCompatResources.getDrawable(this.getContext(),R.drawable.connection_page_wifi_off));
                wifi.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGetEventListener.onGetEvent(EVENT_CONNECT,IP,PORT);
                    }
                });
                connectionStatus = STATUS_DISCONNECTED;
                break;
            case PAGE_CONNECTED:
                setting.setVisibility(VISIBLE);
                wifi.setVisibility(VISIBLE);
                hint.setVisibility(VISIBLE);
                title.setText("Connected");
                hint.setText("Tap to disconnect");
                wifi.setImageDrawable(AppCompatResources.getDrawable(this.getContext(),R.drawable.connection_page_wifi));
                wifi.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGetEventListener.onGetEvent(EVENT_DISCONNECT,IP,PORT);
                    }
                });
                connectionStatus = STATUS_CONNECTED;
                break;
            case PAGE_CONNECTING:
                setting.setVisibility(VISIBLE);
                wifi.setVisibility(VISIBLE);
                loadingRing.setVisibility(VISIBLE);
                title.setText("Connecting");
                wifi.setImageDrawable(AppCompatResources.getDrawable(this.getContext(),R.drawable.connection_page_wifi));
                loadingAnimeStart();
                connectionStatus = STATUS_CONNECTING;
                break;
            case PAGE_CONNECTION_FAIL:
                setting.setVisibility(VISIBLE);
                retry.setVisibility(VISIBLE);
                hint.setVisibility(VISIBLE);
                title.setText("Connection Failed");
                hint.setText("Tap to retry");
                retry.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGetEventListener.onGetEvent(EVENT_CONNECT,IP,PORT);
                    }
                });
                connectionStatus = STATUS_CONNECTION_FAIL;
                break;
            case PAGE_SETTING:
//                retry.setVisibility(INVISIBLE);
                back.setVisibility(VISIBLE);
                settingPage.setVisibility(VISIBLE);
                ipSetting.setVisibility(VISIBLE);
                portSetting.setVisibility(VISIBLE);
                saveSetting.setVisibility(VISIBLE);
                title.setText("Connection Setting");
                ipSetting.setText(IP);
                portSetting.setText(String.valueOf(PORT));
                ipSetting.setBackgroundTintList(ColorStateList.valueOf(0x6C6C6C));
                portSetting.setBackgroundTintList(ColorStateList.valueOf(0x6C6C6C));
                saveSetting.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newIp = ipSetting.getText().toString();
                        int newPort = portSetting.getText().toString().isEmpty()? -1 : Integer.parseInt(portSetting.getText().toString());
                        ipSetting.setBackgroundTintList(ColorStateList.valueOf( isLegalIp(newIp)? 0x6C6C6C : Color.RED) );
                        portSetting.setBackgroundTintList(ColorStateList.valueOf( isLegalPort(newPort)? 0x6C6C6C : Color.RED) );
                        if( isLegalIp(newIp) && isLegalPort(newPort) ){
                            Toast.makeText(getContext(),"IP/PORT saved",Toast.LENGTH_SHORT).show();
                            IP = newIp;
                            PORT = newPort;
                            onGetEventListener.onGetEvent(EVENT_SAVE_IP,IP,PORT);
                            hideSoftInputFromWindow();
                        }
                    }
                });
                break;
        }
    }

    private void textResize(){
        ipSetting.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (ipSetting.getHeight()*0.4));
        portSetting.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (portSetting.getHeight()*0.4));
    }
    private void hideAllChildView(ViewGroup view){
        for (int i=0; i<view.getChildCount(); i++){
            view.getChildAt(i).setVisibility(GONE);
            if(view.getChildAt(i) instanceof ViewGroup){
                hideAllChildView((ViewGroup) view.getChildAt(i));
            }
        }
    }
    private ObjectAnimator loadingAnime;
    private void loadingAnimeStart(){
        loadingAnime = ObjectAnimator.ofFloat(loadingRing,"rotation",0,360).setDuration(3000);
        loadingAnime.setRepeatCount(ValueAnimator.INFINITE);
        loadingAnime.start();
    }
    private void loadingAnimeEnd(){
        if(loadingAnime != null) loadingAnime.cancel();
    }
    private Boolean isLegalIp(String ip){
        if(ip == null){
            return false;
        }
        String[] ipArray = ip.split("\\.");
        if(ipArray.length != 4) {
            return false;
        }
        int num;
        for ( String s : ipArray ) {
            try {
                num = Integer.parseInt(s);
            } catch (Exception e) {
                return false;
            }
            if (num < 0 || num > 255) {
                return false;
            }
        }
        return true;
    }
    private Boolean isLegalPort(int port){
        return port >= 0 && port <= 65535;
    }

    public void hideSoftInputFromWindow(){
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
    }



    private int dialogWidth,dialogHeight;
    private Dialog dialog = null;
    private Window window = null;

    public void setAddress(String ip, int port){
        this.IP = ip;
        this.PORT = port;
    }
    public void setDialogSize(int width, int height){
        this.dialogWidth = width;
        this.dialogHeight = height;
    }
    public void show(){
        if(dialog == null){
            dialog = new AlertDialog.Builder(this.getContext()).setView(this).create();
            window = dialog.getWindow();
            window.setBackgroundDrawable(AppCompatResources.getDrawable(this.getContext(),R.drawable.radius));
//            window.getAttributes().windowAnimations = R.style.Animation_AppCompat_Dialog;
//            window.getAttributes().windowAnimations = R.style.Animation_AppCompat_Tooltip;
            window.getAttributes().windowAnimations = R.style.dialog_animation;
        }
        dialog.show();

        window.setLayout(dialogWidth,dialogHeight);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = dialogWidth;
        params.height = dialogHeight;
    }
    public Boolean isShowing(){
        if (dialog == null) return false;
        return dialog.isShowing();
    }
}
