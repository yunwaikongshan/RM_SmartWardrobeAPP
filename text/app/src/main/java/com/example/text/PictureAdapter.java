package com.example.text;


import java.util.ArrayList;

import java.util.List;



import android.content.Context;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.ImageView;

import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.text.Picture;
import com.example.text.ViewHolder;
import com.example.text.ui.notifications.NotificationsFragment;


//自定义适配器

public class PictureAdapter extends BaseAdapter {

    public static final String SD_APP_DIR_NAME = "TestDir"; //存储程序在外部SD卡上的根目录的名字
    public static final String PHOTO_DIR_NAME = "photo";    //存储照片在根目录下的文件夹名字


    private LayoutInflater inflater;

    private List<Picture> pictures;

    private String[] _titles;



    public PictureAdapter(String[] titles, String[] images, Context  context) {

        super();

        _titles=titles;
        pictures = new ArrayList<Picture>();

        inflater = LayoutInflater.from(context);

        for (int i = 0; i < images.length; i++) {

            Picture picture = new Picture(titles[i], images[i]);

            pictures.add(picture);

        }

    }



    @Override

    public int getCount() {

        if (null != pictures) {

            return pictures.size();

        } else {

            return 0;

        }

    }



    @Override

    public Object getItem(int position) {

        return pictures.get(position);

    }



    @Override

    public long getItemId(int position) {

        return position;

    }



    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.gridview_item, null);

            viewHolder = new ViewHolder();

            viewHolder.title = (TextView) convertView.findViewById(R.id.title);

            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);

            viewHolder.chahao=(ImageView) convertView.findViewById(R.id.photochoice);



            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

//        if(!FileUtil2.ishave(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + _titles[position])){
//            Resources resources = convertView.getResources();
//            Drawable imageDrawable = resources.getDrawable(R.drawable.chahao);
//
////                viewHolder.chahao.setBackgroundDrawable(imageDrawable);
//            viewHolder.chahao.setImageBitmap(BitmapFactory.decodeFile(pictures.get(
//
//                    position).getImageId()));
//        }

        viewHolder.title.setText(pictures.get(position).getTitle());

        viewHolder.image.setImageBitmap(BitmapFactory.decodeFile(pictures.get(

                position).getImageId()));



//        viewHolder.chahao.setImageBitmap(BitmapFactory.decodeFile(pictures.get(
//
//                position).getImageId()));

        if(!FileUtil2.ishave(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/" + _titles[position])){
            Resources resources = convertView.getResources();
            Drawable imageDrawable = resources.getDrawable(R.drawable.chahao);

                viewHolder.chahao.setBackgroundDrawable(imageDrawable);
                /*************/
            viewHolder.chahao.setImageBitmap(BitmapFactory.decodeFile(pictures.get(

                    position).getImageId()));
            /*********************/
        }


        return convertView;

    }



}



