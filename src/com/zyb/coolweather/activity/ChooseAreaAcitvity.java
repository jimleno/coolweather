package com.zyb.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zyb.coolweather.R;
import com.zyb.coolweather.db.CoolWeatherDB;
import com.zyb.coolweather.model.City;
import com.zyb.coolweather.model.County;
import com.zyb.coolweather.model.Province;
import com.zyb.coolweather.utils.HttpCallBackListener;
import com.zyb.coolweather.utils.HttpUtils;
import com.zyb.coolweather.utils.Utility;

public class ChooseAreaAcitvity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private CoolWeatherDB db;
	private TextView textview;
	private ListView listview;
	private ProgressDialog mdialog ;
	private ArrayAdapter<String> adapter;
	private List<String> datalist = new ArrayList<String>();
	
	private List<Province> provincelist;
	
	private List<City> citylist;
	
	private List<County> countylist;
	
	private Province selectprovince;
	private City selectcity;
	private County selectcounty;
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		initview();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,datalist);
		listview.setAdapter(adapter);
		db = CoolWeatherDB.getInstance(this);
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				switch(currentLevel){
					case LEVEL_PROVINCE:
						selectprovince = provincelist.get(index);
						queryCities();
						break;
					case LEVEL_CITY:
						selectcity = citylist.get(index);
						queryCounties();
						break;
				}
				queryProvinces();//加载省级数据
			}	
				});
				
			}


			protected void queryCounties() {
				countylist = db.loadCounty(selectcity.getId());
				if(countylist != null){
					datalist.clear();
					for(County county :countylist){
						datalist.add(county.getCountyName());
					}
					adapter.notifyDataSetChanged();
					listview.setSelection(0);
					textview.setText(selectcity.getCityName());
					currentLevel = LEVEL_COUNTY;
				}else{
					queryFromServer(selectcity.getCityCode(),"county");
				}
		
	}


			protected void queryCities() {
				citylist = db.loadCitys(selectprovince.getId());
				if(citylist != null){
					datalist.clear();
					for(City city : citylist){
						datalist.add(city.getCityName());
					}
					adapter.notifyDataSetChanged();
					listview.setSelection(0);
					textview.setText(selectprovince.getProvinceName());
					currentLevel = LEVEL_CITY;
				}else{
					queryFromServer(selectprovince.getProvinceCode(),"city");
				}
		
	}

			/**
			 * 查询全国的省，如果数据库没有就去服务器查询
			 */
			protected void queryProvinces() {
				provincelist = db.loadProvinces();
				if(provincelist != null){
					datalist.clear();
					for(Province province : provincelist){
						datalist.add(province.getProvinceName());
					}
					adapter.notifyDataSetChanged();
					listview.setSelection(0);
					textview.setText("中国");
					currentLevel = LEVEL_PROVINCE;
				}else{
					queryFromServer(null,"province");
				}
	}


			private void showProgressDialog() {
				// TODO Auto-generated method stub
				if(mdialog == null){
					mdialog = new ProgressDialog(ChooseAreaAcitvity.this);
					mdialog.setMessage("正在加载...");
					mdialog.setCanceledOnTouchOutside(false);
				}
				mdialog.show();
			}
			
			private void closeProgressDialog() {
				if(mdialog != null){
					mdialog.dismiss();
				}
				
			}
	

	private void initview() {
		textview = (TextView) this.findViewById(R.id.text_title);
		listview = (ListView) this.findViewById(R.id.show_listview);
		
	}

	
	
	
	private void queryFromServer(final String code,final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address ="http://www.weather.com.cn/data/list3/city.xml";
			
		}
		showProgressDialog();
		HttpUtils.sendHttpResponse(address, new HttpCallBackListener(){



			@Override
			public void onError(Exception error) {
				// TODO Auto-generated method stub
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					closeProgressDialog();
					Toast.makeText(ChooseAreaAcitvity.this,"加载失败",Toast.LENGTH_LONG).show();
				}
				
			});
			}
			
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				switch(type){
				case "province":
					result = Utility.handleProvinceResponse(db, response);
					break;
				case "city":
					result = Utility.handleCityResponse(db, response, selectprovince.getId());
					break;
				case "county":
					result = Utility.handleCountyResponse(db, response, selectcity.getId());
				}
				if(result){
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}

						
						
					});
				}
				
				
			}
		//
		});
				}


	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
	
	
		}
