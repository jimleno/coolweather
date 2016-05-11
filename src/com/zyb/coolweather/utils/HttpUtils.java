package com.zyb.coolweather.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpUtils {

	public static void sendHttpResponse(Context context,final String address,final HttpCallBackListener listener){
		
		if(listener != null &&!netCheck(context)){
			listener.onError(null);
		}
		
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
	
	/**
	 * 检查网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean netCheck(Context context){
		boolean isWifiConnected = checkWifiConnect(context);
		boolean isMobilConnected = checkMobilConnect(context);
		if(isWifiConnected == false && isMobilConnected == false){
			return false;
		}
		
		return true;
	}

	/**
	 * 检查是否使用3G网络
	 * @param context
	 * @return
	 */
	private static boolean checkMobilConnect(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(info != null && info.isConnected()){
			return true;
		}
		return false;
	}

    /**检查是否使用wifi网络
     * @param context
     * @return
     */
	private static boolean checkWifiConnect(Context context) {
		ConnectivityManager managers = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = managers.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(info != null && info.isConnected()){
			return true;
		}
		return false;
	}
}
