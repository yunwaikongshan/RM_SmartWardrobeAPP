package com.example.text.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.text.FileUtil;
import com.example.text.FileUtil2;
import com.example.text.MainActivity;
import com.example.text.R;
import com.example.text.ui.photo;

public class DashboardFragment extends Fragment {

    public static final String SD_APP_DIR_NAME = "TestDir"; //存储程序在外部SD卡上的根目录的名字
    public static final String PHOTO_DIR_NAME = "photo";    //存储照片在根目录下的文件夹名字

    public  static boolean s=true;
    public  static boolean s_4=true;

    public  static boolean q=true;
    public  static boolean x=true;
    public  static boolean d=true;

    public static boolean cun = true;
    public static boolean xia = true;
    public static boolean qiu = true;
    public static boolean dong = true;
    public static boolean cun4 = true;




    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
                try {
                    MainActivity.isFolderExists(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");
                    final String[] titles = FileUtil.getImageNames(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_APP_DIR_NAME + "/" + PHOTO_DIR_NAME + "/");

                    for (int i=0;i<titles.length;i++){
                        if(titles[i].intern()=="s.jpg".intern()){

                            cun=false;

                        }
                        if(titles[i].intern()=="x.jpg".intern()){


                            xia=false;

                        }

                        if(titles[i].intern()=="q.jpg".intern()){


                            qiu=false;

                        }

                        if(titles[i].intern()=="d.jpg".intern()){


                            dong=false;

                        }

                        if(titles[i].intern()=="s4.jpg".intern()){


                            cun4=false;

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                Button x1 = (Button) getActivity().findViewById(R.id.x1);
                Button x2 = (Button) getActivity().findViewById(R.id.x2);
                Button x3 = (Button) getActivity().findViewById(R.id.x3);
                Button x4 = (Button) getActivity().findViewById(R.id.x4);

                Button q1 = (Button) getActivity().findViewById(R.id.q1);
                Button q2 = (Button) getActivity().findViewById(R.id.q2);
                Button q3 = (Button) getActivity().findViewById(R.id.q3);
                Button q4 = (Button) getActivity().findViewById(R.id.q4);


                Button d4 = (Button) getActivity().findViewById(R.id.d4);
                Button d3 = (Button) getActivity().findViewById(R.id.d3);
                Button d2 = (Button) getActivity().findViewById(R.id.d2);
                Button d1 = (Button) getActivity().findViewById(R.id.d1);


                Button s1 = (Button) getActivity().findViewById(R.id.s1);
                Button s2 = (Button) getActivity().findViewById(R.id.s2);
                Button s3 = (Button) getActivity().findViewById(R.id.s3);
                Button s4 = (Button) getActivity().findViewById(R.id.s4);

                s1.setActivated(cun);
                s2.setActivated(cun);
                s3.setActivated(cun);
                s4.setActivated(cun4);

                x1.setActivated(xia);
                x2.setActivated(xia);
                x3.setActivated(xia);
                x4.setActivated(xia);

                q1.setActivated(qiu);
                q2.setActivated(qiu);
                q3.setActivated(qiu);
                q4.setActivated(qiu);

                d1.setActivated(dong);
                d2.setActivated(dong);
                d3.setActivated(dong);
                d4.setActivated(dong);



            }

        });
        return root;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        {

            Button x1 = (Button) getActivity().findViewById(R.id.x1);
            Button x2 = (Button) getActivity().findViewById(R.id.x2);
            Button x3 = (Button) getActivity().findViewById(R.id.x3);
            Button x4 = (Button) getActivity().findViewById(R.id.x4);

            Button q1 = (Button) getActivity().findViewById(R.id.q1);
            Button q2 = (Button) getActivity().findViewById(R.id.q2);
            Button q3 = (Button) getActivity().findViewById(R.id.q3);
            Button q4 = (Button) getActivity().findViewById(R.id.q4);


            Button d4 = (Button) getActivity().findViewById(R.id.d4);
            Button d3 = (Button) getActivity().findViewById(R.id.d3);
            Button d2 = (Button) getActivity().findViewById(R.id.d2);
            Button d1 = (Button) getActivity().findViewById(R.id.d1);


            Button s1 = (Button) getActivity().findViewById(R.id.s1);
            Button s2 = (Button) getActivity().findViewById(R.id.s2);
            Button s3 = (Button) getActivity().findViewById(R.id.s3);
            Button s4 = (Button) getActivity().findViewById(R.id.s4);

            s1.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!s){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    s=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "s");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            s3.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!s){
                        Toast.makeText(getContext(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    s=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "s");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            s2.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!s){
                        Toast.makeText(getContext(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    s=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "s");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            s4.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!s_4){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    s_4=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "s4");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            x1.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!x){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    x=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "x");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            x2.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!x){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    x=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "x");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            x3.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!x){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    x=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "x");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            x4.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!x){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    x=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "x");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            q1.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!q){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    q=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "q");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            q2.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!q){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    q=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "q");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            q3.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!q){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    q=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "q");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            q4.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!q){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    q=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "q");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            d1.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!d){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    d=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "d");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            d2.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!d){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    d=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "d");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            d3.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!d){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    d=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "d");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });

            d4.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(!d){
                        Toast.makeText(getActivity(), " 此处已有衣物", Toast.LENGTH_LONG).show();
                        return;
                    }
                    d=false;
                    Intent intent = new Intent(); // Activity1
                    intent.putExtra("one", "d");
                    intent.setClass(getContext(), photo.class);
                    startActivity(intent);
                }
            });


        }
    }
}