package com.zyb.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyb.coolweather.ChooseAreaAcitvity;
import com.zyb.coolweather.R;
import com.zyb.coolweather.utils.HttpCallBackListener;
import com.zyb.coolweather.utils.HttpUtils;
import com.zyb.coolweather.utils.Utility;

public class WeatherActivity extends Activity implements OnClickListener {

	private LinearLayout weatherLayoutInfo;
	
	//城市名称
	private TextView cityNameText;
	//发布时间
	private TextView publishText;
	//天气描述
	private TextView weatherDespText;
	//气温
	private TextView temp1Text;
	//气温
	private TextView temp2Text;
	//当前日期
	private TextView currentDateText;
	//切换城市
	private Button switchcity;
	//刷新天气信息
	private Button refreshweather;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		initView();
	}

	private void initView() {
		weatherLayoutInfo = (LinearLayout) this.findViewById(R.id.weather_info);
		cityNameText = (TextView) this.findViewById(R.id.city_name);
		publishText = (TextView) this.findViewById(R.id.publish_text);
		weatherDespText = (TextView) this.findViewById(R.id.weather_desp);
		temp1Text = (TextView) this.findViewById(R.id.temp1);
		temp2Text = (TextView) this.findViewById(R.id.temp2);
		currentDateText = (TextView) this.findViewById(R.id.current_date);
		switchcity = (Button) this.findViewById(R.id.swich_city);
		refreshweather = (Button) this.findViewById(R.id.refresh_info);
		
		String countyCode = getIntent().getStringExtra("county_code");
		//有县级信息就查询
		if(!TextUtils.isEmpty(countyCode)){
			publishText.setText("同步中...");
			weatherLayoutInfo.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}
		else{
			//没有就显示本地信息
			showWeather();
		}
		switchcity.setOnClickListener(this);
		refreshweather.setOnClickListener(this);
	}

	/**
	 * 获得sharedpreferences的天气信息显示到界面上
	 */
	private void showWeather() {
		SharedPreferences pfs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(pfs.getString("city_name",""));
		temp1Text.setText(pfs.getString("temp1",""));
		temp2Text.setText(pfs.getString("temp2",""));
		weatherDespText.setText(pfs.getString("weather_desp", ""));
		publishText.setText("今天"+pfs.getString("publish_time", "")+"发布");
		currentDateText.setText(pfs.getString("current_date",""));
		weatherLayoutInfo.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
	}

	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromService(address,"countyCode");
		
	}

	/**
	 * 根据传入的地址和类型去服务器获取天气信息
	 * @param address
	 * @param countyCode
	 */
	private void queryFromService(String address, final String type) {
		HttpUtils.sendHttpResponse(address, new HttpCallBackListener(){

			@Override
			public void onFinish(String response) {
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] array = response.split("\\|");
						//从服务器返回的数据中解析天气代号
						if(array != null&& array.length == 2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
				Utility.handleWeatherResponse(WeatherActivity.this, response);
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						showWeather();
						
					}
					
				});
				}
				
			}

			@Override
			public void onError(Exception error) {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						publishText.setText("同步失败...");
					}
					
				});
			}
			
		});
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.swich_city:
			Intent intent = new Intent(this,ChooseAreaAcitvity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_info:
			publishText.setText("同步中...");
			SharedPreferences prs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
			queryWeatherInfo(weatherCode);
			}
			break;
			default:
				break;
		}

	}

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromService(address,"weatherCode");
	}

}
