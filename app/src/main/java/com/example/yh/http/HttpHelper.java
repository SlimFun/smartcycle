package com.example.yh.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yh on 2015/2/16.
 */
public class HttpHelper {
    public static String sendPost(String url, String contents){
        InputStreamReader inr = null;
        try {
            URL serverUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.addRequestProperty("Accept-Encoding","gzip,deflate");
            conn.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
            conn.addRequestProperty("Connection", "keep-alive");
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            if(contents != null) {
                conn.getOutputStream().write(contents.getBytes());
            }
            InputStream ins = conn.getInputStream();
            inr = new InputStreamReader(ins, "UTF-8");
            BufferedReader bfr = new BufferedReader(inr);

            String line = "";
            StringBuffer res = new StringBuffer();

            do {
                res.append(line);
                line = bfr.readLine();
            } while (line != null);
            return res.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }   finally{
            if(inr!=null){
                try {
                    inr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    inr =null;
                }

            }

        }
    }
}
