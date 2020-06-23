package com.example.text.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.text.FileUtil;
import com.example.text.MainActivity;
import com.example.text.PictureAdapter;
import com.example.text.device_listActivity;

import com.example.text.R;
import com.example.text.weather.JSonInfoUtil;
import com.example.text.weather.TQYBInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.example.text.ui.dashboard.DashboardFragment.s;
import static com.example.text.ui.notifications.NotificationsFragment.PHOTO_DIR_NAME;

//************************************************************
//l蓝牙协议：提取温湿度0x0a，0x00
//           取衣服 0x00,0x+位置（春夏秋冬0-16）
//           放衣服 0x01,0x+位置（春夏秋冬0-16）
//************************************************************

public class HomeFragment extends Fragment {




    private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄

    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号

    private InputStream is;    //输入流，用来接收蓝牙数据

    private String smsg = "";    //显示用数据缓存
    private String fmsg = "";    //保存用数据缓存

    public String filename = ""; //用来保存存储的文件名
    private BluetoothDevice _device = null;     //蓝牙设备
    public static BluetoothSocket _socket = null;      //蓝牙通信socket
    boolean _discoveryFinished = false;
    private boolean bRun = true;
    private boolean bThread = false;

    private TextView tq;
    private TextView wd;
    TextView dizhi;
    private String jijie = "x.jpg";             //当前推荐图片默认文件名，保证不为null，有显示

    final String SD_APP_DIR_NAME = "TestDir"; //存储程序在外部SD卡上的根目录的名字
    final String PHOTO_DIR_NAME = "photo";    //存储照片在根目录下的文件夹名字

    public static String cityName;   //城市名


    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备


    private HomeViewModel homeViewModel;

    //获得地址
    LocationManager lm;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //页面各组件读取
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //各个组件的声明需在此之后
//        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);

            }
        });
        return root;
    }

    //位置改变监听
    private LocationListener ll = new LocationListener(){
        public void onLocationChanged(Location location){
            updateView(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateView(null);
        }

        @Override
        public void onProviderEnabled(String provider) {
            @SuppressLint("MissingPermission") Location l = lm.getLastKnownLocation(provider);
            updateView(l);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("MissingPermission")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);






//        /* 解决兼容性问题，6.0以上使用新的API*/
//        final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
//        final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_ACCESS_COARSE_LOCATION);
//                Log.e("11111","ACCESS_COARSE_LOCATION");
//            }
//            if(getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_ACCESS_FINE_LOCATION);
//                Log.e("11111","ACCESS_FINE_LOCATION");
//            }
//        }


        //如果打开本地蓝牙设备不成功，提示信息，结束程序
        if (_bluetooth == null) {
            Toast.makeText(getActivity(), "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
//            finish();
            return;
        }





        //初始化变量
        wd = (TextView) getActivity().findViewById(R.id.h_temp);
        tq = (TextView) getActivity().findViewById(R.id.weather);
        dizhi = (TextView) getActivity().findViewById(R.id.cityname1);

//        dizhi.setText(cityName);

        //获取地址

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
        }else{
            //开始定位
            Toast.makeText(getContext(),"已开启定位权限",Toast.LENGTH_LONG).show();
        }


        if(lm==null){
            lm = (LocationManager)this.getContext().getSystemService(Context.LOCATION_SERVICE);
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        }

        //定位相关设置
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setSpeedRequired(false);
        c.setCostAllowed(false);
        c.setBearingRequired(false);
        c.setAltitudeRequired(false);
        c.setPowerRequirement(Criteria.POWER_LOW);

        String bestProvider = lm.getBestProvider(c, true);

        Location l = lm.getLastKnownLocation(bestProvider);
        //第一次很可能得到Location为null
        if(l==null){
            //为null时再进行一次
            bestProvider = lm.getBestProvider(c,false);
            l = lm.getLastKnownLocation(bestProvider);
            if(l!=null){
                updateView(l);
            }
            //设置监听
            lm.requestLocationUpdates(bestProvider, 5000, 8, ll);
        }else{
            //更新数据
            updateView(l);
        }
        //设置ui
        dizhi.setText(cityName);

        Button btn_send = (Button) getActivity().findViewById(R.id.Blutooth);   //蓝牙连接按钮
        btn_send.setOnClickListener(new View.OnClickListener() {

            //按钮监听

            public void onClick(View v) {
                if (!_bluetooth.isEnabled()) {  //如果蓝牙服务不可用则提示
                    _bluetooth.enable();
                    Toast.makeText(getActivity(), " 打开蓝牙中...", Toast.LENGTH_LONG).show();
                    return;
                }
                //如未连接设备则打开DeviceListActivity进行设备搜索
                Button btn = (Button) getActivity().findViewById(R.id.Blutooth);
                if (_socket == null) {
                    //当前未连接
                    Intent serverIntent = new Intent(getContext(), device_listActivity.class); //跳转程序设置
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
//                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
                } else {
                    //当前已连接,断开连接
                    //关闭连接socket
                    try {
                        bRun = false;
                        Thread.sleep(2000);

                        is.close();
                        _socket.close();
                        _socket = null;

                        btn.setText("扫描蓝牙设备");
                    } catch (IOException ignored) {
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Button send = (Button) getActivity().findViewById(R.id.send);       //蓝牙发送取温湿度的按钮

        //设置照片墙

        //检查是否存在路径,不存在则创建
        MainActivity.isFolderExists(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");
        //搜索路径下所有图片
        final String[] titles = FileUtil.getImageNames(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");
        int item = 0;

        for (int i = 0; i < titles.length; i++) {

            //遍历寻找与"jijie"同名的图片
            if(titles[i].equals(jijie)){
                item++;
//                imagePaths[item++] = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + titles[i];
            }

        }
        //将图片路径存入imagePaths
        String[] imagePaths = new String[item];
        item = 0;
        for (int i = 0; i < titles.length; i++) {

            if(titles[i].equals(jijie)){

                imagePaths[item++] = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + titles[i];
            }

        }

        GridView gridView = (GridView) getActivity().findViewById(R.id.xihao_cloth);            //照片墙


        PictureAdapter adapter = new PictureAdapter(titles, imagePaths, getContext());          //利用PictureAdapter存入
        gridView.setAdapter(adapter);                                                           //设置内容


        //蓝牙发送按钮监听
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int i = 0;
                int n = 0;
                if (_socket == null) {              //当前未连接
                    Toast.makeText(getActivity(), "请先连接HC模块", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {

                    OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流
                    byte[] bos = {0x0a, 0x00};                      //发送首位为0x0a时代表取温湿度
//                    for (i = 0; i < bos.length; i++) {
//                        if (bos[i] == 0x0a) n++;
//                    }
//                    byte[] bos_new = new byte[bos.length + n];
//                    n = 0;
//                    for (i = 0; i < bos.length; i++) { //手机中换行为0a,将其改为0d 0a后再发送
//                        if (bos[i] == 0x0a) {
//                            bos_new[n] = 0x0d;
//                            n++;
//                            bos_new[n] = 0x0a;
//                        } else {
//                            bos_new[n] = bos[i];
//                        }
//                        n++;
//                    }

                    os.write(bos);
                } catch (IOException ignored) {
                }


            }
        });


        // 设置设备可以被搜索
//        new Thread(){
//            public void run(){
//                if(!_bluetooth.isEnabled()){
//                    _bluetooth.enable();
//                }
//            }
//        }.start();



        //获取天气线程
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void run() {
                try {
                    //获取选中城市的名称
                    String CN = "北京";
                    if(cityName!=null){
                        CN = cityName;
                    }


                    //请求网络权限
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET)
                            != PackageManager.PERMISSION_GRANTED){//未开启定位权限
                        //开启定位权限,200是标识码
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.INTERNET},200);
                    }

                    //获取指定城市的天气预报JSon字符串
                    String jStr = JSonInfoUtil.getJSonStr(CN);
                    //解析指定城市的天气预报JSon字符串
                    TQYBInfo tqybInfo = JSonInfoUtil.parseJSon(jStr);


                    flushDetail(tqybInfo);

                } catch (Exception eq) {
                    eq.printStackTrace();
                    String msgStr = "当前此城市天气信息不可用！\n您可以选择相近的城市查询！";
                    //解决在子线程中调用Toast的异常情况处理
                    Looper.prepare();
                    Toast.makeText(getContext(), msgStr, Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        }.start();


    }


    //推荐图片读取
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void initLayout() {


        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            return ;
        } else {

        }
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
        }

        boolean sdCardExist = Environment.getExternalStorageDirectory()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在

//        MainActivity.isFolderExists(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");

        String[] titles = FileUtil.getImageNames(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");
        StringBuilder imagePaths = new StringBuilder();

        StringBuilder _titles = new StringBuilder();

//        if(titles.length==0){
//            return;
//        }

        for (int i = 0; i < titles.length; i++) {

            if (titles[i].intern() == jijie.intern()) {
                imagePaths.append(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + titles[i]);

                _titles.append(titles[i]);

            }
//            imagePaths[i] = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + titles[i];

        }
        String[] arr_imagePaths = new String(imagePaths).split(",");
        String[] arr_titles = new String(_titles).split(",");


        if (this.getActivity().isFinishing()) {
            Log.d("wisely", "activity is finishing");
            return ;
        }
        GridView gridView = (GridView) getActivity().findViewById(R.id.xihao_cloth);

        PictureAdapter adapter = new PictureAdapter(arr_titles, arr_imagePaths, this.getContext());
        try {

            gridView.setAdapter(adapter);

        } catch (Exception e) {
//            e.printStackTrace();
        }
//        gridView.setAdapter(adapter);
    }

    //接收活动结果，响应startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONNECT_DEVICE) {     //连接结果，由DeviceListActivity设置返回
            // 响应返回结果
            if (resultCode == Activity.RESULT_OK) {   //连接成功，由DeviceListActivity设置返回
                // MAC地址，由DeviceListActivity设置返回
                String address = data.getExtras()
                        .getString(device_listActivity.EXTRA_DEVICE_ADDRESS);
                // 得到蓝牙设备句柄
                _device = _bluetooth.getRemoteDevice(address);

                // 用服务号得到socket
                try {
                    _socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "连接失败！", Toast.LENGTH_SHORT).show();
                }
                //连接socket
                Button btn = (Button) getActivity().findViewById(R.id.Blutooth);
                try {
                    _socket.connect();
                    Toast.makeText(getActivity(), "连接" + _device.getName() + "成功！", Toast.LENGTH_SHORT).show();
                    btn.setText("断开");
                } catch (IOException e) {
                    try {
                        Toast.makeText(getActivity(), "连接失败！", Toast.LENGTH_SHORT).show();
                        _socket.close();
                        _socket = null;
                    } catch (IOException ee) {
                        Toast.makeText(getActivity(), "连接失败！", Toast.LENGTH_SHORT).show();
                    }

                    return;
                }

                //打开接收线程
                try {
                    is = _socket.getInputStream();   //得到蓝牙数据输入流
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "接收数据失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bThread == false) {
                    //接收数据线程
                    Thread readThread = new Thread() {

                        public void run() {
                            int num = 0;
                            byte[] buffer = new byte[1024];
                            byte[] buffer_new = new byte[1024];
                            int i = 0;
                            int n = 0;
                            bRun = true;
                            //接收线程
                            while (true) {
                                try {
                                    while (is.available() == 0) {
                                        while (bRun == false) {
                                        }
                                    }
                                    while (true) {
                                        if (!bThread)//跳出循环
                                            return;

                                        num = is.read(buffer);         //读入数据
                                        n = 0;

                                        String s0 = new String(buffer, 0, num);
//                        String s0 = new String(buffer);

//                        TextView sd=(TextView) getActivity().findViewById(R.id.shiduing) ;
//                        sd.setText(s0);

//                        Toast.makeText(getContext(), s0, Toast.LENGTH_SHORT).show();


                                        String stmp = "";
                                        StringBuilder sb = new StringBuilder("");
                                        fmsg += s0;    //保存收到数据
                                        for (i = 0; i < num; i++) {
                                            if ((buffer[i] == 0x0d) && (buffer[i + 1] == 0x0a)) {
                                                buffer_new[n] = 0x0a;
                                                i++;
                                            } else {
                                                buffer_new[n] = buffer[i];
                                            }
                                            stmp = Integer.toHexString(buffer_new[n]);
                                            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
                                            n++;
                                        }
                                        String[] _scare = new String(sb).split(",");
                                        String s = new String(buffer_new, 0, n);
                                        TextView sd = (TextView) getActivity().findViewById(R.id.shiduing);
                                        TextView wd = (TextView) getActivity().findViewById(R.id.wenduing);
//                        sd.setText("123");
                                        try {

                                            //设置城市名称及天气描述
                                            sd.setText(Integer.toHexString(buffer_new[0]));
                                            wd.setText(Integer.toHexString(buffer_new[1]));

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
//                        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                                        smsg += s;   //写入接收缓存
                                        if (is.available() == 0) break;  //短时间没有数据才跳出进行显示
                                    }
                                    Log.d("debug001", smsg);
                                    //发送显示消息，进行显示刷新
                                    handler.sendMessage(handler.obtainMessage());
                                } catch (IOException ignored) {
                                }
                            }
                        }
                    };
                    bThread = true;
                } else {
                    bRun = true;
                }
            }
        }
    }

//    //接收数据线程
//    Thread readThread=new Thread(){
//
//        public void run(){
//            int num = 0;
//            byte[] buffer = new byte[1024];
//            byte[] buffer_new = new byte[1024];
//            int i = 0;
//            int n = 0;
//            bRun = true;
//            //接收线程
//            while(true){
//                try{
//                    while(is.available()==0){
//                        while(bRun == false){}
//                    }
//                    while(true){
//                        if(!bThread)//跳出循环
//                            return;
//
//                        num = is.read(buffer);         //读入数据
//                        n=0;
//
//                        String s0 = new String(buffer,0,num);
////                        String s0 = new String(buffer);
//
////                        TextView sd=(TextView) getActivity().findViewById(R.id.shiduing) ;
////                        sd.setText(s0);
//
////                        Toast.makeText(getContext(), s0, Toast.LENGTH_SHORT).show();
//
//
//                        String stmp="";
//                        StringBuilder sb = new StringBuilder("");
//                        fmsg+=s0;    //保存收到数据
//                        for(i=0;i<num;i++){
//                            if((buffer[i] == 0x0d)&&(buffer[i+1]==0x0a)){
//                                buffer_new[n] = 0x0a;
//                                i++;
//                            }else{
//                                buffer_new[n] = buffer[i];
//                            }
//                            stmp = Integer.toHexString(buffer_new[n]);
//                            sb.append((stmp.length()==1)? "0"+stmp : stmp);
//                            n++;
//                        }
//                        String[] _scare=new String(sb).split(",");
//                        String s = new String(buffer_new,0,n);
//                         TextView sd=(TextView) getActivity().findViewById(R.id.shiduing) ;
//                        TextView wd=(TextView) getActivity().findViewById(R.id.wenduing) ;
////                        sd.setText("123");
//                        try
//                        {
//
//                            //设置城市名称及天气描述
//                            sd.setText(Integer.toHexString(buffer_new[0]));
//                            wd.setText(Integer.toHexString(buffer_new[1]));
//
//                        }
//                        catch(Exception e)
//                        {
//                            e.printStackTrace();
//                        }
////                        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
//                        smsg+=s;   //写入接收缓存
//                        if(is.available()==0)break;  //短时间没有数据才跳出进行显示
//                    }
//                    Log.d("debug001",smsg);
//                    //发送显示消息，进行显示刷新
//                    handler.sendMessage(handler.obtainMessage());
//                }catch(IOException ignored){
//                }
//            }
//        }
//    };


    @SuppressLint("HandlerLeak")
    private
    Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            tv_in.setText(smsg);   //显示数据
//            sv.scrollTo(0,tv_in.getMeasuredHeight()); //跳至数据最后一页
            if(msg.what == 0){
                //设置温度
                Bundle data = msg.getData();
                String val = data.getString("value");
                wd.setText(val);
//                initLayout();
            }else if(msg.what == 1){
                //设置天气
                Bundle data = msg.getData();
                String val = data.getString("value");
                tq.setText(val);
            }
        }
    };


    //    final EditText dizhi=(EditText) getActivity().findViewById(R.id.cityname1) ;
//
//
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void flushDetail(TQYBInfo info) {
        int di_wd = 20;
        int gao_wd = 20;
        if (Objects.requireNonNull(getActivity()).isFinishing()) {
            Log.d("wisely", "activity is finishing");
            return ;
        }
        try {

            //设置温度
//            wd.setText(info.wd);

            //网络请求
            String string = info.wd;
            Message msg = new Message();
            Bundle data = new Bundle();

            //将获取到的String装载到msg中
            data.putString("value", string);
            msg.setData(data);
            msg.what = 0;
            //发消息到主线程
            handler.sendMessage(msg);


            //设置城市名称及天气描述
//            tq.setText(info.tqms);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            //设置城市名称及天气描述
//            tq.setText(info.tqms);
            //网络请求
            String string = info.tqms;
            Message msg = new Message();
            Bundle data = new Bundle();

            //将获取到的String装载到msg中
            data.putString("value", string);
            msg.setData(data);
            msg.what = 1;
            //发消息到主线程
            handler.sendMessage(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            di_wd = Integer.parseInt(info.wd.substring(0, 1));

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            gao_wd = Integer.parseInt(info.wd.substring(5, 6));

        } catch (Exception e) {
            e.printStackTrace();
        }
//        di_wd=Integer.parseInt(info.wd.substring(0,1));
//        gao_wd=Integer.parseInt(info.wd.substring(3,4));
        int wendu = (di_wd + gao_wd) / 2;
        wendu = 20;
        //***********/
//        if (wendu < 5) {
//            jijie = "d.jpg";
//        } else if (wendu >= 5 && wendu < 15) {
//            jijie = "s.jpg";
//        } else {
//            jijie = "x.jpg";
//        }
        //********************/

//        initLayout();
    }

    public void sent(byte[] bos) {
        int i = 0;
        int n = 0;
        if (this._socket == null) {
            Toast.makeText(getActivity(), "请先连接HC模块", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流
            for (i = 0; i < bos.length; i++) {
                if (bos[i] == 0x0a) n++;
            }
            byte[] bos_new = new byte[bos.length + n];
            n = 0;
            for (i = 0; i < bos.length; i++) { //手机中换行为0a,将其改为0d 0a后再发送
                if (bos[i] == 0x0a) {
                    bos_new[n] = 0x0d;
                    n++;
                    bos_new[n] = 0x0a;
                } else {
                    bos_new[n] = bos[i];
                }
                n++;
            }

            os.write(bos_new);
        } catch (IOException e) {
        }

    }

    //地址更新
    public void updateView(Location newLocation)
    {
        if(newLocation !=null){
            String latitude = String.valueOf(newLocation.getLatitude());
            String longitude = String.valueOf(newLocation.getLongitude());
            double Latitude = newLocation.getLatitude();
            double Longitude = newLocation.getLongitude();
            cityName = getAddressbyGeoPoint(Latitude,Longitude);
            if(cityName == null){
                cityName = "唐山市";
            }
            dizhi.setText(cityName);
        }
        else{
//            dizhi.clearComposingText();
        }
    }

    //从地址Geopoint取得Address
    public String getAddressbyGeoPoint(double Latitude, double Longitude)
    {
        String strReturn = "唐山市";
        try
        {
            /* 创建GeoPoint不等于null */
//	    if (gp != null)
//	    {
            /* 创建Geocoder对象，用于获得指定地点的地址 */
            Geocoder gc = new Geocoder(getContext(), Locale.getDefault());

            /* 取出地理坐标经纬度*/
            double geoLatitude = Latitude;
            double geoLongitude = Longitude;

            /* 自经纬度取得地址（可能有多行）*/
            List<Address> lstAddress = gc.getFromLocation(geoLatitude, geoLongitude, 1);
            StringBuilder sb = new StringBuilder();

            /* 判断地址是否为多行 */
            if (lstAddress.size() > 0)
            {
                Address adsLocation = lstAddress.get(0);

                for (int i = 0; i < adsLocation.getMaxAddressLineIndex(); i++)
                {
                    sb.append(adsLocation.getAddressLine(i)).append("\n");  //精确的地址和附近的代表建筑物
                }
                sb.append(adsLocation.getLocality()).append("\n");  //当前经纬度所在的城市（市）
//	        sb.append(adsLocation.getPostalCode()).append("\n");
//	        sb.append(adsLocation.getCountryName());
            }

            /* 将取得到的地址组合后放到stringbuilder对象中输出用 */
            strReturn = sb.toString();
//	    }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return strReturn;
    }


    /**
     *
     * @param s         传入的图片路径+文件名
     * @return          返回文件名
     */
    private String getS_SlashToEnd(String s){
        int Index = 0;
        int length = s.length();
        for(int i = 0; i < length; i++){
            if(s.charAt(i)=='/') {
                Index = i+1;
            }
        }
        return s.substring(Index);
    }






}