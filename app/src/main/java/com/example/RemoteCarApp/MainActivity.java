package com.example.RemoteCarApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView drawer;
    FragmentContainerView fragmentContainerView;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        drawer = findViewById(R.id.drawer);
        fragmentContainerView = findViewById(R.id.fragmentContainerView);

//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawer.getMenu().findItem(R.id.Page_DataPacketSetting).setVisible(false);

        drawer.setNavigationItemSelectedListener(item -> {
            if(Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.Page_MainPage) {
                navController.popBackStack();
            } else navController.navigateUp();

            navController.navigate(item.getItemId());
            drawerLayout.close();
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        navController = Navigation.findNavController(fragmentContainerView);
    }


    //--------------------------------------------
    static long exitTime = 0;
    int i = 0;
    int clickTimes = 21;
    @Override
    public void onBackPressed() {
        if (drawerLayout.isOpen()) {
            drawerLayout.close();
            return;
        }
        if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.Page_MainPage) {
//            if(System.currentTimeMillis() - exitTime > 2000) {
//                exitTime = System.currentTimeMillis();
//                exitToast();
//                return;
//            }
            if(i<clickTimes){
                toast("Click " + (clickTimes - i) +" times to leave");
                i++;
                if(i == clickTimes) clickTimes = 999999;
                return;
            }
        }
        super.onBackPressed();
    }

    Toast toast;
    TextView ToastText;
    public void toast(String s) {
        if (null == toast) {
            toast = new Toast(this);
            ToastText = new TextView(this);
            ToastText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            toast.setView(ToastText);
        }
        ToastText.setText(s);
        toast.show();
    }

    Toast exitToast;
    TextView exitText;
    public void exitToast() {
        if (null == exitToast) {
            exitToast = new Toast(this);
            exitText = new TextView(this);
            exitText.setText("Press again to exit");
            exitText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            exitToast.setView(exitText);
        }
        exitToast.show();
    }
}