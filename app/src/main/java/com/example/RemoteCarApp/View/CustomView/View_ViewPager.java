package com.example.RemoteCarApp.View.CustomView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.RemoteCarApp.R;

import java.util.ArrayList;

public class View_ViewPager extends ConstraintLayout {

    private int num;
    private ArrayList pageList = new ArrayList();
    private ArrayList<Boolean> pageListDisallowIntercept = new ArrayList<Boolean>();
    private LinearLayout linearLayout;
    private FragmentStateAdapter fragmentStateAdapter;
    private ViewPager2 viewPager2;
    private ImageView[] dot;


    public View_ViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager,this,true);
        viewPager2 = view.findViewById(R.id.viewPager2);
        linearLayout = view.findViewById(R.id.LinearLayout);

        fragmentStateAdapter = new FragmentAdapter((Activity)context);
        viewPager2.setAdapter(fragmentStateAdapter);
        viewPager2.setPageTransformer(new TransferPage());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int nowIndex = viewPager2.getCurrentItem();
        if(pageListDisallowIntercept.size() > 0 && pageListDisallowIntercept.get(nowIndex)){
            ViewGroup v = (ViewGroup) pageList.get(nowIndex);
            if(ev.getAction() == MotionEvent.ACTION_MOVE){
                v.requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void addPage(View view){
        pageList.add(view);
        pageListDisallowIntercept.add(false);
        setNum();
    }
    public void addPage(int index,View view){
        if(pageList.size()<index){
            while (pageList.size()<index){
                pageList.add(null);
                pageListDisallowIntercept.add(false);
            }
            pageList.add(view);
            pageListDisallowIntercept.add(false);
        }else {
            pageList.add(index,view);
            pageListDisallowIntercept.add(index,false);
        }
        setNum();
    }
    public void addPage(ViewGroup view,Boolean disallowIntercept){
        pageList.add(view);
        pageListDisallowIntercept.add(disallowIntercept);
        setNum();
    }
    public void addPage(int index,ViewGroup view,Boolean disallowIntercept){
        if(pageList.size()<index){
            while (pageList.size()<index){
                pageList.add(null);
                pageListDisallowIntercept.add(false);
            }
            pageList.add(view);
            pageListDisallowIntercept.add(disallowIntercept);
        }else {
            pageList.add(index,view);
            pageListDisallowIntercept.add(index,disallowIntercept);
        }
        setNum();
    }
    public void addPage(int layoutResource){
        pageList.add(layoutResource);
        pageListDisallowIntercept.add(false);
        setNum();
    }
    public void addPage(int index,int layoutResource){
        if(pageList.size()<index){
            while (pageList.size()<index){
                pageList.add(null);
                pageListDisallowIntercept.add(false);
            }
            pageList.add(layoutResource);
            pageListDisallowIntercept.add(false);
        }else {
            pageList.add(index,layoutResource);
            pageListDisallowIntercept.add(index,false);
        }
        setNum();
    }
    public void removePage(){
        pageList.remove(pageList.size()-1);
        pageListDisallowIntercept.remove(pageList.size()-1);
        setNum();
    }
    public void removePage(int index){
        pageList.remove(index);
        pageListDisallowIntercept.remove(index);
        setNum();
    }

    private void setNum(){
        linearLayout.removeAllViews();
        this.num = pageList.size();
        dot = new ImageView[num];
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.leftMargin = 5;
        params.rightMargin = 5;

        for(int i = 0;i<=num-1;i++){
            dot[i] = new ImageView(getContext());
            dot[i].setImageDrawable(AppCompatResources.getDrawable(getContext(),R.drawable.viewpager_dot_model_00));
            dot[i].setLayoutParams(params);
            linearLayout.addView(dot[i]);
        }
    }

    private void setDot(int i,Boolean change){
        if(change){
            dot[i].setImageDrawable(AppCompatResources.getDrawable(getContext(),R.drawable.viewpager_dot_model_01));
        }else {
            dot[i].setImageDrawable(AppCompatResources.getDrawable(getContext(),R.drawable.viewpager_dot_model_00));
        }
    }

    //-------------------------------------------------------------------------
    private class FragmentAdapter extends FragmentStateAdapter{
        public FragmentAdapter(Activity activity) {
            super((FragmentActivity) activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(pageList.get(position) instanceof View){
                return new FragmentCreator((View)pageList.get(position));
            }else if(pageList.get(position) instanceof Integer){
                return new FragmentCreator((Integer) pageList.get(position));
            }else {
                return new FragmentCreator();
            }
        }

        @Override
        public int getItemCount() {
            return num;
        }
    }

    public static class FragmentCreator extends Fragment {
        private int mode = 0;
        private int resource;
        private View view;

        public FragmentCreator(){
            mode = 0;
            resource = -1;
        }

        public FragmentCreator(int resource){
            mode = 1;
            this.resource = resource;
        }

        public FragmentCreator(View view){
            mode = 2;
            this.view = view;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            switch (mode){
                case 0 :
                    return new View(getContext());
                case 1 :
                    return inflater.inflate(resource,container,false);
                case 2 :
                    return view;
                default:
                    return null;
            }
        }
    }

    private class TransferPage implements ViewPager2.PageTransformer {
        @Override
        public void transformPage(@NonNull View page, float position) {
            int i = viewPager2.getCurrentItem();
            setDot(i,true);
            try {
                setDot(i + 1, false);
            } catch(Exception e){ }
            try {
                setDot(i - 1, false);
            } catch(Exception e){ }
            onTransformPageListener.onTransformPage(i);
        }
    }

    public interface onTransformPageListener{
        void onTransformPage(int currentItem);
    }
    private onTransformPageListener onTransformPageListener;
    public void setOnTransformPageListener(onTransformPageListener onTransformPageListener){
        this.onTransformPageListener = onTransformPageListener;
    }
}
