package com.example.text.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class JSonInfoUtil {
    //从网络上获取指定编号城市天气预报jSon字符串的方法
    public static String getJSonStr(String cityName) throws Exception
    {
        System.out.println("tb-"+cityName);

        String jSonStr="";
        StringBuilder sb=new StringBuilder("http://api.map.baidu.com/telematics/v3/weather?location=");
        String str= URLEncoder.encode(cityName, "UTF-8");
        sb.append(str);
        sb.append("&output=json&ak=c5Q60GdE4UTORFE6k5V6Pr0R");
        String urlStr=null;
        urlStr=new String(sb.toString().getBytes());
        System.out.println("url "+urlStr);
        URL url=new URL(urlStr);
        URLConnection uc=url.openConnection();

        uc.setRequestProperty("accept-language", "zh_CN");
        InputStream in=uc.getInputStream();
        int ch=0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while((ch=in.read())!=-1)
        {
            baos.write(ch);
        }
        byte[] bb=baos.toByteArray();
        baos.close();
        in.close();
        jSonStr=new String(bb);
        return jSonStr;
    }

    //解析JSon字符串，得到应用程序需要的城市名称、天气描述、
    //最高温度、最低温度、风向、风力、图片
    public static TQYBInfo parseJSon(String jSonStr) throws JSONException
    {
        TQYBInfo result=new TQYBInfo();
        JSONObject json = new JSONObject(jSonStr);
        JSONArray obj = json.getJSONArray("results");
        JSONObject temp = new JSONObject(obj.getString(0));
        result.city=temp.getString("currentCity");
        JSONArray obj3 = temp.getJSONArray("weather_data");
        JSONObject temp2 = new JSONObject(obj3.getString(0));
        result.date=temp2.getString("date");
        result.tqms=temp2.getString("weather");
        result.wd=temp2.getString("temperature");
        result.flfx=temp2.getString("wind");
        result.picData=getPicData(temp2.getString("dayPictureUrl"));
        return result;
    }

    // 根据图片的url得到要显示的图片
    public static byte[] getPicData(String img)
    {
        byte[] bb=null;
        try
        {
            StringBuilder sb=new StringBuilder(img);
            String urlStr=null;
            urlStr=new String(sb.toString().getBytes());
            URL iconurl = new URL(urlStr);
            URLConnection conn = iconurl.openConnection();
            conn.connect();
            InputStream in = conn.getInputStream();
            int ch=0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((ch=in.read())!=-1)
            {
                baos.write(ch);
            }
            bb=baos.toByteArray();
            baos.close();
            in.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return bb;
    }
}
