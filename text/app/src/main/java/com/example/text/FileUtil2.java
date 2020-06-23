package com.example.text;

import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtil2 {
    public static boolean ishave(String path){
        boolean b=true;
        try{
            int t = path.length() - 4;
            //从后面删除字符串
            String substring = path.substring(0, t);
            File infile = new File( substring+"2"+".txt");
            if (!infile.exists()) {
                // 创建新的空文件
                infile.createNewFile();
                FileOutputStream outStream=new FileOutputStream(infile);
                outStream.write("1".getBytes());
                outStream.close();

            }else{
                FileInputStream inputStream=new FileInputStream(infile);
                byte temp[] = new byte[1024];
                StringBuilder sb = new StringBuilder("");
                int len = 0;
                while ((len = inputStream.read(temp)) > 0){
                    sb.append(new String(temp, 0, len));
                }
                inputStream.close();
                String[] _scare;
                 _scare=new String(sb).split(",");
                int scare=0;
                scare=Integer.parseInt(_scare[0].substring(0,1));
                if(scare==0){
                    b=false;
                }
            }
        }catch (Exception e){

        }
        return b;
    }

    public static void qu(String path){
        try{
            int t = path.length() - 4;
            //从后面删除字符串
            String substring = path.substring(0, t );
            File infile = new File( substring+"2"+".txt");
            if (!infile.exists()) {
                // 创建新的空文件
                infile.createNewFile();
                FileOutputStream outStream=new FileOutputStream(infile);
                outStream.write("0".getBytes());
                outStream.close();

            }else{
                FileInputStream inputStream=new FileInputStream(infile);
                byte temp[] = new byte[1024];
                StringBuilder sb = new StringBuilder("");
                int len = 0;
                while ((len = inputStream.read(temp)) > 0){
                    sb.append(new String(temp, 0, len));
                }
                inputStream.close();
                String[] _scare=new String(sb).split(",");
                int scare=0;
                scare=Integer.parseInt(_scare[0].substring(0,1));
                if(scare==1){
                    infile.delete();
                    infile.createNewFile();

                    FileOutputStream out=new FileOutputStream(infile);
                    out.write("0".getBytes());


                    out.close();
                }
            }
        }catch (Exception e){

        }
    }

    public static void fang(String path){
        try{
            int t = path.length() - 4;
            //从后面删除字符串
            String substring = path.substring(0, t);
            File infile = new File( substring+"2"+".txt");
            if (!infile.exists()) {
                // 创建新的空文件
                infile.createNewFile();
                FileOutputStream outStream=new FileOutputStream(infile);
                outStream.write("1".getBytes());
                outStream.close();

            }else{
                FileInputStream inputStream=new FileInputStream(infile);
                byte temp[] = new byte[1024];
                StringBuilder sb = new StringBuilder("");
                int len = 0;
                while ((len = inputStream.read(temp)) > 0){
                    sb.append(new String(temp, 0, len));
                }
                inputStream.close();
                String[] _scare=new String(sb).split(",");
                int scare=0;
                scare=Integer.parseInt(_scare[0].substring(0,1));
                if(scare==0){
                    infile.delete();
                    infile.createNewFile();

                    FileOutputStream out=new FileOutputStream(infile);
                    out.write("1".getBytes());

                    out.close();
                }
            }
        }catch (Exception e){

        }
    }

    public static void add( String path){
        try{
            // 创建指定路径的文件
            int t = path.length() - 4;
            //从后面删除字符串
            String substring = path.substring(0, t );
            File infile = new File( substring+".txt");
            // 如果文件不存在
            if (!infile.exists()) {
                // 创建新的空文件
                infile.createNewFile();
                FileOutputStream outStream=new FileOutputStream(infile);
                outStream.write("20".getBytes());
                outStream.close();

            }else {

                FileInputStream inputStream=new FileInputStream(infile);
                byte temp[] = new byte[1024];
                StringBuilder sb = new StringBuilder("");
                int len = 0;
                while ((len = inputStream.read(temp)) > 0){
                    sb.append(new String(temp, 0, len));
                }
                inputStream.close();
                String[] _scare=new String(sb).split(",");
                int scare=0;
                scare=Integer.parseInt(_scare[0].substring(0,1));
                if(scare<50){
                    scare+=10;
                }

                infile.delete();
                infile.createNewFile();

                FileOutputStream out=new FileOutputStream(infile);
                out.write(scare);

                out.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deletefile(String fileName) {
        try {
            int t = fileName.length() - 4;
            //从后面删除字符串
            String substring = fileName.substring(0, t );
            File infile = new File( substring+".txt");
            // 找到文件所在的路径并删除该文件
            File file = new File( fileName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getnum(String fileName){
        int scare=0;
        try {
            int t = fileName.length() - 4;
            //从后面删除字符串
            String substring = fileName.substring(0, t);
            File file = new File(substring+".txt");
            FileInputStream inputStream=new FileInputStream(file);
            byte temp[] = new byte[1024];
            StringBuilder sb = new StringBuilder("");
            int len = 0;
            while ((len = inputStream.read(temp)) > 0){
                sb.append(new String(temp, 0, len));
            }
            inputStream.close();
            String[] _scare=new String(sb).split(",");
            scare=Integer.parseInt(_scare[0].substring(0,1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scare;
    }

    public static int getweizhi(String fileName){
        int scare=0;
        try {
            int t = fileName.length() - 4;
            //从后面删除字符串
            String substring = fileName.substring(0, t );
            if(substring.intern()=="s".intern()){
                scare=1;
            }else if (substring.intern()=="x".intern()){
                scare=2;
            }else if (substring.intern()=="q".intern()){
                scare=3;
            }else if (substring.intern()=="d".intern()){
                scare=4;
            }else if (substring.intern()=="s4".intern()){
                scare=5;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scare;
    }

    public static String zhuanhua(String name){
        String substring="s.txt";
        try {
            int t = name.length() - 4;
            //从后面删除字符串
            substring = name.substring(0, t );
            substring+=".txt";
        }catch (Exception e){
            e.printStackTrace();
        }
        return substring;
    }

}
