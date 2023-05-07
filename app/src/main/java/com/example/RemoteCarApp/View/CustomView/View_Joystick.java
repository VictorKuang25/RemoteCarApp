package com.example.RemoteCarApp.View.CustomView;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.RemoteCarApp.R;


//          --Environment settings--
//  Put joystick_back and joystick_front in @drawable
//  Put them in a layout xml name joystick.xml in @layout
//  Add Joystick.java(this) to Java Class
//                                      * Sample code at the end of this .java
//          --Callable method--
//  # setSize(int) : Joystick(View) can set the size(pixel) by calling Joystick.setSize(int)
//  # setMode(Boolean) : Joystick(View) can set mode by calling Joystick.setMode(Boolean)
//                          setMode(true) will set the starting point in the center of this View
//                          setMode(false) will set the starting point form where the fingers touch down
//  # getX : get X value range in 0~1
//  # getY : get Y value range in 0~1
//  # getDegree : get angle value range in 0~+-180 degree
//  # getDistance : get distance from starting point to current point

public class View_Joystick extends ConstraintLayout {

    private ImageView jsf,jsb,pointer;
    private ConstraintLayout bg;
    private float jsfR,jsbR,bgH,bgW;    //onSetSize
    private float startPointX,startPointY;    //onTouchDown
    private float distances,x,y;              //onTouchMove
    private Boolean mode = false;
    private Context c;
    private int size = 500;
    LayoutParams params1,params2;

    public View_Joystick(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        c = context;

        View inflate = LayoutInflater.from(context).inflate(R.layout.joystick,this,true);
        bg = inflate.findViewById(R.id.bg);
        jsb = inflate.findViewById(R.id.jsb);
        jsf = inflate.findViewById(R.id.jsf);
        pointer = inflate.findViewById(R.id.imageView2);

        setSize(500);
    }

    //-------------------------onMoveListener------------------------
    public interface OnMoveListener{
        void onMove(float x, float y, float distance, float degree);
    }
    private OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMove(float x, float y, float distance, float degree) {
        }
    };
    public void setOnMoveListener(OnMoveListener onMoveListener){
        this.onMoveListener = onMoveListener;
    }
    //---------------------------------------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch ((event.getAction())){
            case MotionEvent.ACTION_DOWN:
                bgH = bg.getHeight();
                bgW = bg.getWidth();
                jsbR = jsb.getHeight()/2;
                jsfR = jsf.getHeight()/2;
                if(mode){
                    startPointX = bgW/2;
                    startPointY = bgH/2;
                }else {
                    startPointX = event.getX();
                    startPointY = event.getY();
                }
                jsb.setX(startPointX-jsbR);    jsb.setY(startPointY-jsbR);
                jsf.setX(startPointX-jsfR);    jsf.setY(startPointY-jsfR);
                if(pointerMode != 0){
                    pointer.setX(startPointX-jsbR);    pointer.setY(startPointY-jsbR);
                }
                X = ( (jsf.getX()+jsfR)-startPointX ) / jsbR;
                Y = ( startPointY-(jsf.getY()+jsfR) ) /jsbR;
                break;
            case MotionEvent.ACTION_MOVE:
                x =  event.getX() - startPointX ;
                y =  event.getY() - startPointY ;
                distances = (float) Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
                if(distances < jsbR){
                    jsf.setX(event.getX()-jsfR);
                    jsf.setY(event.getY()-jsfR);
                }else {
                    float a = jsbR / distances ;
                    jsf.setX( x*a + startPointX - jsfR );
                    jsf.setY( y*a + startPointY - jsfR );
                }

                if(pointerMode != 0){
                    if( (pointerMode==1 ? Y:X) == 0 ){
                        pointer.setVisibility(INVISIBLE);
                    }else if( (pointerMode==1 ? Y:X) > 0 ){
                        pointer.setRotation(pointerMode==1 ? 0:90);
                        pointer.setVisibility(VISIBLE);
                    }else {
                        pointer.setRotation(pointerMode==1 ? 180:270);
                        pointer.setVisibility(VISIBLE);
                    }
                }
                X = ( (jsf.getX()+jsfR)-startPointX ) / jsbR;
                Y = ( startPointY-(jsf.getY()+jsfR) ) /jsbR;
                break;
            case MotionEvent.ACTION_UP:
                jsf.setX(startPointX-jsfR);
                jsf.setY(startPointY-jsfR);
                if(pointerMode != 0){
                    pointer.setVisibility(INVISIBLE);
                }
                X = ( (jsf.getX()+jsfR)-startPointX ) / jsbR;
                Y = ( startPointY-(jsf.getY()+jsfR) ) /jsbR;
                break;
        }
        onMoveListener.onMove(getX(),getY(),getDistance(),getDegree());
        return true;
    }

    public void setSize(int size){
        this.size = size;
        params1 = new LayoutParams(size,size);
        params2 = new LayoutParams((int)(size*0.382),(int)(size*0.382));
        params1.topToTop = LayoutParams.PARENT_ID;          //LayoutParams.PARENT_ID = 0
        params1.leftToLeft = LayoutParams.PARENT_ID;
        params1.rightToRight = LayoutParams.PARENT_ID;
        params1.bottomToBottom = LayoutParams.PARENT_ID;
        params2.topToTop = LayoutParams.PARENT_ID;
        params2.leftToLeft = LayoutParams.PARENT_ID;
        params2.rightToRight = LayoutParams.PARENT_ID;
        params2.bottomToBottom = LayoutParams.PARENT_ID;
        jsb.setLayoutParams(params1);
        jsf.setLayoutParams(params2);
        pointer.setLayoutParams(params1);
    }

    public void setMode(Boolean mode){
        this.mode = mode;
        jsb.setX(bgW/2-jsbR);    jsb.setY(bgH/2-jsbR);
        jsf.setX(bgW/2-jsfR);    jsf.setY(bgH/2-jsfR);
        pointer.setX(bgW/2-jsbR);    pointer.setY(bgH/2-jsbR);
    }

    private float X,Y,Distance,Degree;

    public float getX(){
        return X;
    }

    public float getY(){
        return Y;
    }

    public Float getDistance() {
        Distance = (float) Math.sqrt(Math.pow(getX(),2)+Math.pow(getY(),2));
        return Distance;
    }

    public Float getDegree(){
//        Degree = (float) Math.toDegrees(Math.acos(getX()/getDistance())) * (getY()>0 ? 1:-1);
        Degree = (float) Math.toDegrees(Math.acos(getX()/getDistance()));
        return getY() > 0 ? Degree : 360 - Degree;
    }

    public static final int Off = 0;
    public static final int UpDown = 1;
    public static final int LeftRight = 2;
    private int pointerMode = 0 ;

    public void setPointer(int mode){
        pointerMode = mode;
    }
}


//          ------@drawable/joystick_front------
//<?xml version="1.0" encoding="utf-8"?>
//<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle" >
//<corners
//        android:radius="100dp"
//                />
//<solid
//        android:color="#4F4F4F"
//                />
//<size
//        android:width="92dp"
//                android:height="92dp"
//                />
//<stroke
//        android:width="3dp"
//                android:color="#878787"
//                />
//</shape>

//          ------@drawable/joystick_back------
//<?xml version="1.0" encoding="utf-8"?>
//<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle" >
//    <corners
//        android:radius="100dp"
//        />
//    <solid
//        android:color="#1F1F1F"
//        />
//    <size
//        android:width="150dp"
//        android:height="150dp"
//        />
//    <stroke
//        android:width="3dp"
//        android:color="#B5B5B5"
//        />
//</shape>

//          ------@layuot/joystick.xml------
//    <?xml version="1.0" encoding="utf-8"?>
//<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
//        xmlns:app="http://schemas.android.com/apk/res-auto"
//        xmlns:tools="http://schemas.android.com/tools"
//        android:id="@+id/bg"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        tools:context=".Joystick">
//
//<ImageView
//        android:id="@+id/jsb"
//                android:layout_width="wrap_content"
//                android:layout_height="wrap_content"
//                android:elevation="2dp"
//                android:src="@drawable/joystick_back"
//                app:layout_constraintBottom_toBottomOf="parent"
//                app:layout_constraintLeft_toLeftOf="parent"
//                app:layout_constraintRight_toRightOf="parent"
//                app:layout_constraintTop_toTopOf="parent" />
//
//<ImageView
//        android:id="@+id/jsf"
//                android:layout_width="wrap_content"
//                android:layout_height="wrap_content"
//                android:elevation="3dp"
//                android:src="@drawable/joystick_front"
//                app:layout_constraintBottom_toBottomOf="parent"
//                app:layout_constraintLeft_toLeftOf="parent"
//                app:layout_constraintRight_toRightOf="parent"
//                app:layout_constraintTop_toTopOf="parent" />
//
//</androidx.constraintlayout.widget.ConstraintLayout>



