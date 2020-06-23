package com.example.text.ui.notifications;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

//import com.example.text.MainActivity;
import com.example.text.FileUtil2;
import com.example.text.MainActivity;
import com.example.text.R;
import com.example.text.FileUtil;
import com.example.text.PictureAdapter;
import com.example.text.ui.home.HomeFragment;
import com.example.text.weather.JSonInfoUtil;
import com.example.text.weather.TQYBInfo;

import java.io.IOException;
import java.io.OutputStream;

import static com.example.text.FileUtil2.fang;
import static com.example.text.FileUtil2.getnum;
import static com.example.text.ui.home.HomeFragment._socket;


public class NotificationsFragment extends Fragment {

    public static final String SD_APP_DIR_NAME = "TestDir"; //存储程序在外部SD卡上的根目录的名字
    public static final String PHOTO_DIR_NAME = "photo";    //存储照片在根目录下的文件夹名字

    int w;


    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        return root;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        try {

        }catch (Exception e){
            e.printStackTrace();
        }
        MainActivity.isFolderExists(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");
        final String[] titles = FileUtil.getImageNames(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");
        String[] imagePaths = new String[titles.length];

        for (int i = 0; i < titles.length; i++) {

            imagePaths[i] = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + titles[i];

        }

        Button sent = (Button) getActivity().findViewById(R.id.qyf);

        GridView gridView = (GridView) getActivity().findViewById(R.id.allclothes);


        PictureAdapter adapter = new PictureAdapter(titles, imagePaths, getContext());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onItemClick(AdapterView<?> parent, View v,

                                    int position, long id) {

                String weizhi = null;
                w=position;

                TextView wei_zgi = (TextView) getActivity().findViewById(R.id.selectxuhao);
                if (titles[position].intern() == "s.jpg") {
                    weizhi = "春";
                } else if (titles[position].intern() == "q.jpg") {
                    weizhi = "秋";
                } else if (titles[position].intern() == "d.jpg") {
                    weizhi = "冬";
                } else if (titles[position].intern() == "s4.jpg") {
                    weizhi = "春四";
                } else if (titles[position].intern() == "x.jpg") {
                    weizhi = "夏";
                }

                if (wei_zgi != null) {
                    wei_zgi.setText(weizhi);
                }

//                parent.getAdapter().toString();
                RatingBar ratingBar = (RatingBar)getActivity().findViewById(R.id.ratingbar);
                int Bar=FileUtil2.getnum(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + titles[position]);

                ratingBar.setRating((float)((float)Bar/(float)2));

                String num= Integer.toString((int)((float)Bar/(float)2));

                Toast.makeText(getActivity(), "你点击了" + num,

                        Toast.LENGTH_SHORT).show();

            }
        });


//        gridView.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//
//            }
//        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View v,

                                           int position, long id) {

                Toast.makeText(getActivity(), "你长按了" + id,

                        Toast.LENGTH_SHORT).show();

                return true;

                /*

                 * 这里需要true

                 * 因为:OnItemLongClick事件中：down事件返回值标记此次事

                 * 件是否为点击事件（返回false，是点击事件；返回true，不记为点击事件），

                 * 而up事件标记此次事件结束时间，也就是判断是否为长按。

                 */


            }


        });
        sent.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                MainActivity.isFolderExists(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");
                final String[] titles = FileUtil.getImageNames(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");

                if(FileUtil2.ishave(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/"+titles[w])){

                    int i=0;
                    int n=0;
                    if(_socket==null){
                        Toast.makeText(getActivity(), "请先连接HC模块", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try{

                        OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流
                        int weizhi=FileUtil2.getweizhi(titles[w]);
                        byte[] bos ={0x02,(byte)weizhi};
                        for(i=0;i<bos.length;i++){
                            if(bos[i]==0x0a)n++;
                        }
                        byte[] bos_new = new byte[bos.length+n];
                        n=0;
                        for(i=0;i<bos.length;i++){ //手机中换行为0a,将其改为0d 0a后再发送
                            if(bos[i]==0x0a){
                                bos_new[n]=0x0d;
                                n++;
                                bos_new[n]=0x0a;
                            }else{
                                bos_new[n]=bos[i];
                            }
                            n++;
                        }


                        os.write(bos_new);
                        int t = titles[w].length() - 4;
                        //从后面删除字符串
                        String substring = titles[w].substring(0, t );
                        substring+=".txt";
                        FileUtil2.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/"+substring);
                        FileUtil2.qu(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/"+substring);
                    }catch(IOException e){
                    }
                }else {
                    int i = 0;
                    int n = 0;
                    if (_socket == null) {
                        Toast.makeText(getActivity(), "请先连接HC模块", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {

                        OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流
                        int weizhi = FileUtil2.getweizhi(titles[w]);
                        byte[] bos = {0x01, (byte) weizhi};
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
                        int t = titles[w].length() - 4;
                        //从后面删除字符串
                        String substring = titles[w].substring(0, t );
                        substring += ".txt";
                        //FileUtil2.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/", substring);
                        FileUtil2.fang(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + substring);
                    } catch (IOException e) {
                    }
                }

            }

        });

        Button shanchu=(Button) getActivity().findViewById(R.id.chong_zhi);

        shanchu.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                MainActivity.isFolderExists(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");
                final String[] titles = FileUtil.getImageNames(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");
                FileUtil2.deletefile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + titles[w]);
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + titles[w];
//                final String[] titles = FileUtil.getImageNames(Environment.getDataDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");
//                FileUtil2.deletefile(Environment.getDataDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + titles[w]);
//                String path = Environment.getDataDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + titles[w];
                if (!TextUtils.isEmpty(path)) {
                    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver mContentResolver = getActivity().getContentResolver();
                    String where = MediaStore.Images.Media.DATA + "='" + path + "'";
//删除图片
                    mContentResolver.delete(uri, where, null);
                }


                refush();

            }
        });
    }

//    @Override
////    public void onResume() {
////        int id = getActivity().getIntent().getIntExtra("id", 0);
////        if(id==2){
//////            getContext().setCurrentItem(2);
////        }
////        super.onResume();
////    }



void refush(){
    Intent intent = new Intent(getActivity(),MainActivity.class);
    intent.putExtra("id", 1);
    startActivity(intent);
}


}