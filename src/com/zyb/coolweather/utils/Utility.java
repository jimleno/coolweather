package com.zyb.coolweather.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.zyb.coolweather.db.CoolWeatherDB;
import com.zyb.coolweather.model.City;
import com.zyb.coolweather.model.County;
import com.zyb.coolweather.model.Province;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
	 * @param db
	 * @param province
	 * @return
	 */
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB db,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allprovinces = response.split(",");
			if(allprovinces != null && allprovinces.length > 0){
				for(String res : allprovinces){
					String[] array = res.split("\\|");
					Province provinces = new Province();
					provinces.setProvinceCode(array[0]);
					provinces.setProvinceName(array[1]);
					db.saveProvince(provinces);
				}
				return true;
			}
			
		}
		
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 * @param db
	 * @param province
	 * @return
	 */
	public synchronized static boolean handleCityResponse(CoolWeatherDB db,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allcity = response.split(",");
			if(allcity != null && allcity.length > 0){
				for(String city : allcity){
					String[] array = city.split("\\|");
					City citys = new City();
					citys.setCityCode(array[0]);
					citys.setCityName(array[1]);
					citys.setProvinceId(provinceId);
					db.saveCity(citys);
				}
				return true;
			}
		}
		
		
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
	 * @param db
	 * @param province
	 * @return
	 */
	public synchronized static boolean handleCountyResponse(CoolWeatherDB db,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allcounty = response.split(",");
			if(allcounty != null && allcounty.length > 0){
				for(String county :allcounty){
					String[] array = county.split("\\|");
					County countys = new County();
					countys.setCountyCode(array[0]);
					countys.setCountyName(array[1]);
					countys.setCityId(cityId);
					db.saveCounty(countys);
				}
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 解析服务器返回的JSON数据，并存储至本地
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject object = new JSONObject(response);
			JSONObject weatherinfo = object.getJSONObject("weatherinfo");
			String cityName = weatherinfo.getString("city");
			String weathercode = weatherinfo.getString("cityid");
			String temp1 = weatherinfo.getString("temp1");
			String temp2 = weatherinfo.getString("temp2");
			String weatherdesp = weatherinfo.getString("weather");
			String publishTime = weatherinfo.getString("ptime");
			saveWeatherInfo(context,cityName,weathercode,temp1,temp2,weatherdesp,publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将解析出来的信息保存至sharedpreferences中
	 * @param context
	 * @param cityName
	 * @param weathercode
	 * @param temp1
	 * @param temp2
	 * @param weatherdesp
	 * @param publishTime
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weathercode, String temp1, String temp2, String weatherdesp,
			String publishTime) {
		SimpleDateFormat date = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name",cityName);
		editor.putString("weather_code", weathercode);
		editor.putString("temp1",temp1);
		editor.putString("temp2",temp2);
		editor.putString("publish_time",publishTime);
		editor.putString("weather_desp",weatherdesp);
		editor.putString("current_date",date.format(new Date()));
		editor.commit();
	}
	
}
