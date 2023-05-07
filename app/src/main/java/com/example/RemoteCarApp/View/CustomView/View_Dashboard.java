package com.example.RemoteCarApp.View.CustomView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.RemoteCarApp.R;

@SuppressLint("SetTextI18n")
public class View_Dashboard extends ConstraintLayout {

    TextView tv_speed,tv_angle;
    ImageView iv_car;
    ImageView iv_carReversingSing;
    ImageView warningSingF,warningSingB,warningSingL,warningSingR;
    float speed,angle;

    public View_Dashboard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View v = LayoutInflater.from(context).inflate(R.layout.dashboard,this,true);
        tv_speed = v.findViewById(R.id.dashboard_tv_speed);
        tv_angle = v.findViewById(R.id.dashboard_tv_angle);
        iv_car = v.findViewById(R.id.car2);
        iv_carReversingSing = v.findViewById(R.id.carBackSing);
        warningSingF = v.findViewById(R.id.warningSingF);
        warningSingB = v.findViewById(R.id.warningSingB);
        warningSingL = v.findViewById(R.id.warningSingL);
        warningSingR = v.findViewById(R.id.warningSingR);

        setSpeed(0);
        setAngle(0);
    }

    public void setSpeed(float speed){
        this.speed = speed;
        tv_speed.setText("速度 : " + Math.round(speed*10f)/10f + " km/h");
    }

    public void setAngle(int angle) {
        this.angle = angle;
        tv_angle.setText("偏航角 :  " + angle + "°");

//        int delta = Math.abs(angle) < 90 ? 0 : 180;
//        iv_car.setRotation(angle - delta);
    }

    ObjectAnimator a = null;
    public void setCarReversingSing(Boolean on){
        iv_carReversingSing.setVisibility(on ? VISIBLE:INVISIBLE);
        if(a == null) a = ObjectAnimator.ofFloat(iv_carReversingSing,"alpha",1,0).setDuration(1500);
        if(!on) {
            a.cancel();
            return;
        }
        if(!a.isRunning()) a.start();
    }


    public static final int HIDE_ALL = 0;
    public static final int CAR_FRONT = 1;
    public static final int CAR_BACK = 2;
    public static final int CAR_LEFT = 3;
    public static final int CAR_RIGHT = 4;
    private ObjectAnimator[] warningAnimator = new ObjectAnimator[5];
    private Boolean[] isWarning = new Boolean[5];
    public void setWarningSing(int direction, Boolean on){
        ImageView[] warningSing = {null,warningSingF,warningSingB,warningSingL,warningSingR};

        if(warningAnimator[direction] == null){
            warningAnimator[direction] = ObjectAnimator.ofFloat(warningSing[direction],"alpha",1,0);
            warningAnimator[direction].setDuration(500);
            warningAnimator[direction].setRepeatCount(ValueAnimator.INFINITE);
        }

        if(on){
            if(direction == 0){
                for(int i = 1;i<=4;i++){
                    warningSing[i].setVisibility(INVISIBLE);
                    try {
                        warningAnimator[i].cancel();
                    } catch (Exception e){ }
                    isWarning[i] = false;
                }
            }else {
                warningSing[direction].setVisibility(VISIBLE);
                warningAnimator[direction].start();
                isWarning[direction] = true;
            }
        } else {
            warningSing[direction].setVisibility(INVISIBLE);
            warningAnimator[direction].cancel();
            isWarning[direction] = false;
        }
    }
}
