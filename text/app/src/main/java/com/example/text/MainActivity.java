package com.example.text;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;


import com.example.text.ui.dashboard.DashboardFragment;
import com.example.text.ui.home.HomeFragment;
import com.example.text.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    public String abs_path;
    public String sdDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sdDir = getApplicationContext().getExternalCacheDir().getPath();
        sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        abs_path = getApplicationContext().getCacheDir().getPath();

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            setContentView(R.layout.activity_main);

        }catch (Exception e){
            e.printStackTrace();
        }
//        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (this.isFinishing()) {
            Log.d("wisely", "activity is finishing");
            return ;
        }
        int id = getIntent().getIntExtra("id", 0);
//        int _id = getIntent().getIntExtra("id", 0);
        if (id == 2) {
//            Fragment fragmen = new ThreeFragment();
//            FragmentManager fmanger = getSupportFragmentManager();
//            FragmentTransaction transaction = fmanger.beginTransaction();
//            transaction.replace(R.id.viewpager, fragmen);
//            transaction.commit();
//            mViewPager.setCurrentItem(2);//
            //帮助跳转到指定子fragment
            Intent i=new Intent();
            i.setClass(this, DashboardFragment.class);
            i.putExtra("id",2);
            startActivity(i);
        }

        if (id==1) {
            if (this.isFinishing()) {
                Log.d("wisely", "activity is finishing");
                return ;
            }
            BottomNavigationView navView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
//            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                    .build();
//            NavController navController = Navigation.findNavController(this, R.id.navigation_notifications);
//            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//            NavigationUI.setupWithNavController(navView, navController);
        }

        if(id == 0){

        }

    }

    public static boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return true;
            } else {
                return false;

            }
        }
        return true;

    }



}
