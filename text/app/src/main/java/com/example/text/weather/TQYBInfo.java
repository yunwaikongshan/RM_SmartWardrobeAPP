package com.example.text.weather;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TQYBInfo {
    String city;		    //城市名称
    public String tqms;		    //天气描述
    public String wd;		    //温度
    String flfx;		    //风力风向
    byte[] picData;		//图片数据
    String date;        //日期

    //将天气预报信息对象转化为字节数组
    public static byte[] fromObjToBytes(TQYBInfo obj)
    {
        byte[] result=null;

        ByteArrayOutputStream bout=new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream oout = new ObjectOutputStream(bout);
            oout.writeObject(obj);
            oout.flush();
            result=bout.toByteArray();
            oout.close();
            bout.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    //将字节数组转化为天气预报信息对象
    public static TQYBInfo fromBytesToObj(byte[] data)
    {
        TQYBInfo result=null;

        ByteArrayInputStream bin=new ByteArrayInputStream(data);
        try
        {
            ObjectInputStream oin = new ObjectInputStream(bin);
            result=(TQYBInfo)oin.readObject();
            bin.close();
            oin.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

}
