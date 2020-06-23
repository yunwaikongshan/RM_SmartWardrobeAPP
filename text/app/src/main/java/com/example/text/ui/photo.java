package com.example.text.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.text.MainActivity;
import com.example.text.R;
import com.example.text.ui.dashboard.DashboardFragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class photo extends AppCompatActivity {

    private String fileNmae;

    Uri photoUri = null;

    String num1=null;

    private Uri mImageUri;                                  //指定的uri
    private String mImagePath;                   //用于存储最终目录，即根目录 / 要操作（存储文件）的文件夹


    private String mImageName;                              //保存的图片的名字
    private File mImageFile;                                //图片文件
    public static final int VOICE_RESULT_CODE = 101;        //标志符，音频的结果码，判断是哪一个Intent
    public static final int PHOTO_RESULT_CODE = 100;        //标志符，图片的结果码，判断是哪一个Intent
    public static final int VIDEO_RESULT_CODE = 102;        //标志符，视频的结果码，判断是哪一个Intent

    public static final String SD_APP_DIR_NAME = "TestDir"; //存储程序在外部SD卡上的根目录的名字
    public static final String PHOTO_DIR_NAME = "photo";    //存储照片在根目录下的文件夹名字
    public static final String VOICE_DIR_NAME = "voice";    //存储音频在根目录下的文件夹名字
    public static final String VIDEO_DIR_NAME = "video";    //存储视频在根目录下的文件夹名字


//    Intent intent = getIntent(); //Activity2
//    String num1 = intent.getStringExtra("one");


    private final static int REQUEST_CODE_camera = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);



//        Intent intent = getIntent(); //Activity2
//        String num1 = intent.getStringExtra("one");
//        String num2 = intent.getStringExtra("two");
//        int ret = Integer.parseInt(num1);
//        result.setText(ret+"");

//        fileNmae = Environment.getExternalStorageDirectory().toString()+ File.separator+"OCR/image/"+num1+".jpg";

        Button photo = (Button) findViewById(R.id.paizhao);

        photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(photo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //未获得，向用户请求
                    Log.d(getLocalClassName(), "无读写权限，开始请求权限。");

                    ActivityCompat.requestPermissions(photo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                } else {
                    Log.d(getLocalClassName(), "有读写权限，准备启动相机。");
                    //启动照相机
                    startCamera();
                }
            }

        });
    }

    private void startCamera() {
        Intent intent = new Intent();
        //指定动作，启动相机
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //创建文件
        createImageFile();
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //获取uri
        mImageUri = FileProvider.getUriForFile(this, "com.example.text.provider", mImageFile);
        //将uri加入到额外数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        //启动相机并要求返回结果
        startActivityForResult(intent, PHOTO_RESULT_CODE);

    }


    private void createImageFile() {
        //设置图片文件名（含后缀）
        Intent intent = getIntent(); //Activity2
        num1 = intent.getStringExtra("one");
        mImageName = num1 + ".jpg";
        mImageFile =new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/", mImageName) ;
        //将图片的绝对路径设置给mImagePath，后面会用到
        mImagePath = mImageFile.getAbsolutePath();
        //按设置好的目录层级创建
        mImageFile.getParentFile().mkdirs();
        //不加这句会报Read-only警告。且无法写入SD
        mImageFile.setWritable(true);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(getLocalClassName(), "拍摄结束。");
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case PHOTO_RESULT_CODE:{
                    Bitmap bitmap = null;
                    Bitmap resizedBitmap=null;
                    try {
                        //根据uri设置bitmap
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);

                        // 获取这个图片的宽和高

                        int width = bitmap.getWidth();

                        int height = bitmap.getHeight();

                        // 定义预转换成的图片的宽度和高度

                        int newWidth = 400;

                        int newHeight = 400;

                        // 计算缩放率，新尺寸除原始尺寸

                        float scaleWidth = ((float) newWidth) / width;

                        float scaleHeight = ((float) newHeight) / height;

                        // 创建操作图片用的matrix对象

                        Matrix matrix = new Matrix();

                        // 缩放图片动作

                        matrix.postScale(scaleWidth, scaleHeight);

                        // 创建新的图片
                        resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    //将图片保存到SD的指定位置
                    savePhotoToSD(resizedBitmap);
                    //更新系统图库
                    updateSystemGallery();
                    break;
                }
                case VOICE_RESULT_CODE:
                case VIDEO_RESULT_CODE: {
//                    saveVoiceToSD();
                    break;
                }
            }
        }
    }

    /**
     * 保存照片到SD卡的指定位置
     */
    private void savePhotoToSD(Bitmap bitmap){
        //创建输出流缓冲区
        BufferedOutputStream os = null;
        try{
            //设置输出流
            os = new BufferedOutputStream(new FileOutputStream(mImageFile));
            //压缩图片，100表示不压缩
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, os);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }finally {
            if (os != null){
                try{
                    //不管是否出现异常，都要关闭流
                    os.flush();
                    os.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * 更新系统图库
     */
    private void updateSystemGallery(){
        //把文件插入到系统图库
        try{
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    mImageFile.getAbsolutePath(), mImageName, null);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        String cun=new String("s");
        String qiu=new String("q");


        // 最后通知图库更新
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mImagePath)));
//        Toast.makeText(this,  num1, Toast.LENGTH_LONG).show();
        setContentView(R.layout.fragment_dashboard);
        if(num1.intern()==cun.intern()){
            DashboardFragment.s=false;

            DashboardFragment.cun=false;
        }else if(num1.intern()=="s4".intern()){
            DashboardFragment.s_4=false;
            DashboardFragment.cun4=false;
        }else if(num1.intern()=="q".intern()){
            DashboardFragment.q=false;
            DashboardFragment.qiu=false;
        }else if(num1.intern()=="d".intern()){
            DashboardFragment.d=false;
            DashboardFragment.dong=false;
        }else if(num1.intern()=="x".intern()){
            DashboardFragment.x=false;
            DashboardFragment.xia=false;
        }
        Toast.makeText(this, "拍摄成功!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("id", 2);
        startActivity(intent);

    }
}

