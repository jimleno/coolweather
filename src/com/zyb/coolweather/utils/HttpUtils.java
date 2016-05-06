package com.zyb.coolweather.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

	public static void sendHttpResponse(final String address,final HttpCallBackListener listener){
		new Thread(new Runnable(){

			@Override
			public void run() {
				URL url;
				HttpURLConnection connection = null;
				try {
					url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(8000);
					if(200 == connection.getResponseCode()){
						InputStream in = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						StringBuilder response = new StringBuilder();
						String line = null;
						while((line = reader.readLine())!=null){
							response.append(line);
						}
						if(listener != null){
							listener.onFinish(response.toString());
						}
						in.close();
						reader.close();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(listener != null){
						listener.onError(e);
					}
					e.printStackTrace();
				}finally{
					
					connection.disconnect();
				}
				
			}
			
		}).start();
	}
}
