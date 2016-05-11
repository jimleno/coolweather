package com.zyb.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.zyb.coolweather.receiver.AutoUpdateReceiver;
import com.zyb.coolweather.utils.HttpCallBackListener;
import com.zyb.coolweather.utils.HttpUtils;
import com.zyb.coolweather.utils.Utility;

public class AutoUpdateService extends Service {

	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				updateWeatherInfo();
				
			}

			/**
			 * 更新天气信息
			 */
			private void updateWeatherInfo() {
				SharedPreferences pfs = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
				String weatherCode = pfs.getString("weather_code","");
				String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
				HttpUtils.sendHttpResponse(AutoUpdateService.this,address, new HttpCallBackListener(){

					@Override
					public void onFinish(String response) {
					Utility.handleWeatherResponse(AutoUpdateService.this, response);
						
					}

					@Override
					public void onError(Exception error) {
						Log.e("Tag","error happend in updateWeatherInfo about sendHttpResponse");
						
					}
					
				});
			}
			
		}).start();
		
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		int atTime = 8*60*60*1000;
	    long startTime = SystemClock.elapsedRealtime()+atTime;
	    Intent intents = new Intent(this,AutoUpdateReceiver.class);
	    PendingIntent pi = PendingIntent.getBroadcast(this, 0, intents, 0);
	    manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, startTime, pi);
		
		return super.onStartCommand(intent, flags, startId);
	}

	
	
	
}
