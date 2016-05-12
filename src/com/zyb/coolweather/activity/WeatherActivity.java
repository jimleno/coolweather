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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.zyb.coolweather.R;
import com.zyb.coolweather.service.AutoUpdateService;
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
	//一键分享
	private ImageButton shareimg;
	//天气图标
	private ImageView img;
	
	private String sunny = "晴";
	private String overcast = "阴";
	private String cloudy = "多云";
	private String rain = "雨";
	private String snow = "雪";
	private String fog = "霾";
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		initView();
	}
	
	private void showShare() {
		 ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 
		 
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle(getString(R.string.share));
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl("http://sharesdk.cn");
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText("我是分享文本");
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		 // url仅在微信（包括好友和朋友圈）中使用
		 oks.setUrl("http://sharesdk.cn");
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
		 oks.setComment("我是测试评论文本");
		 // site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite(getString(R.string.app_name));
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		 oks.setSiteUrl("http://sharesdk.cn");
		 
		// 启动分享GUI
		 oks.show(this);
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
		img = (ImageView) this.findViewById(R.id.show_weather);
		shareimg = (ImageButton) this.findViewById(R.id.onekey_share);
		
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
		shareimg.setOnClickListener(this);
	}

	/**
	 * 获得sharedpreferences的天气信息显示到界面上
	 */
	private void showWeather() {
		SharedPreferences pfs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(pfs.getString("city_name",""));
		temp1Text.setText(pfs.getString("temp1","").trim());
		temp2Text.setText(pfs.getString("temp2","").trim());
		weatherDespText.setText(pfs.getString("weather_desp", ""));
		publishText.setText("今天"+pfs.getString("publish_time", "")+"发布");
		currentDateText.setText(pfs.getString("current_date",""));
		weatherLayoutInfo.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		showImgView(pfs.getString("weather_desp",""));
		Intent in = new Intent(this,AutoUpdateService.class);
		startService(in);
	}

	private void showImgView(String info) {
		if(info.endsWith(sunny)){
			img.setImageResource(R.drawable.sunny);
			return;
		}else if(info.endsWith(cloudy)){
			img.setImageResource(R.drawable.cloudy);
			return;
		}else if(info.endsWith(overcast)){
			img.setImageResource(R.drawable.overcast);
			return;
		}else if(info.endsWith(fog)){
			img.setImageResource(R.drawable.fog);
			return;
		}else if(info.endsWith(rain)){
			img.setImageResource(R.drawable.rain);
			return;
		}else if(info.endsWith(snow)){
			img.setImageResource(R.drawable.snow);
			return;
		}
		
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
		HttpUtils.sendHttpResponse(WeatherActivity.this,address, new HttpCallBackListener(){

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
						Toast.makeText(WeatherActivity.this,"网络连接失败,请检查网络",Toast.LENGTH_LONG).show();
					}
					
				});
			}
			
		});
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.swich_city:
			SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = share.edit();
            edit.clear();//清空sharedpreferences文件里的内容
            edit.commit();//清空完必须要提交一下，不然不会生效
			Intent intent = new Intent(this,ChooseAreaActivity.class);
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
		case R.id.onekey_share:
			showShare();
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
