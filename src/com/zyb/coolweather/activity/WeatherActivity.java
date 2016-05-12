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
	
	
	//��������
	private TextView cityNameText;
	//����ʱ��
	private TextView publishText;
	//��������
	private TextView weatherDespText;
	//����
	private TextView temp1Text;
	//����
	private TextView temp2Text;
	//��ǰ����
	private TextView currentDateText;
	//�л�����
	private Button switchcity;
	//ˢ��������Ϣ
	private Button refreshweather;
	//һ������
	private ImageButton shareimg;
	//����ͼ��
	private ImageView img;
	
	private String sunny = "��";
	private String overcast = "��";
	private String cloudy = "����";
	private String rain = "��";
	private String snow = "ѩ";
	private String fog = "��";
	
	
	
	
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
		 //�ر�sso��Ȩ
		 oks.disableSSOWhenAuthorize(); 
		 
		// ����ʱNotification��ͼ�������  2.5.9�Ժ�İ汾�����ô˷���
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		 oks.setTitle(getString(R.string.share));
		 // titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
		 oks.setTitleUrl("http://sharesdk.cn");
		 // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		 oks.setText("���Ƿ����ı�");
		 // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		 //oks.setImagePath("/sdcard/test.jpg");//ȷ��SDcard������ڴ���ͼƬ
		 // url����΢�ţ��������Ѻ�����Ȧ����ʹ��
		 oks.setUrl("http://sharesdk.cn");
		 // comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
		 oks.setComment("���ǲ��������ı�");
		 // site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
		 oks.setSite(getString(R.string.app_name));
		 // siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
		 oks.setSiteUrl("http://sharesdk.cn");
		 
		// ��������GUI
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
		//���ؼ���Ϣ�Ͳ�ѯ
		if(!TextUtils.isEmpty(countyCode)){
			publishText.setText("ͬ����...");
			weatherLayoutInfo.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}
		else{
			//û�о���ʾ������Ϣ
			showWeather();
		}
		switchcity.setOnClickListener(this);
		refreshweather.setOnClickListener(this);
		shareimg.setOnClickListener(this);
	}

	/**
	 * ���sharedpreferences��������Ϣ��ʾ��������
	 */
	private void showWeather() {
		SharedPreferences pfs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(pfs.getString("city_name",""));
		temp1Text.setText(pfs.getString("temp1","").trim());
		temp2Text.setText(pfs.getString("temp2","").trim());
		weatherDespText.setText(pfs.getString("weather_desp", ""));
		publishText.setText("����"+pfs.getString("publish_time", "")+"����");
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
	 * ���ݴ���ĵ�ַ������ȥ��������ȡ������Ϣ
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
						//�ӷ��������ص������н�����������
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
						publishText.setText("ͬ��ʧ��...");
						Toast.makeText(WeatherActivity.this,"��������ʧ��,��������",Toast.LENGTH_LONG).show();
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
            edit.clear();//���sharedpreferences�ļ��������
            edit.commit();//��������Ҫ�ύһ�£���Ȼ������Ч
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_info:
			publishText.setText("ͬ����...");
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
