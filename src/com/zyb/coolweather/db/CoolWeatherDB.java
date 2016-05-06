package com.zyb.coolweather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zyb.coolweather.model.City;
import com.zyb.coolweather.model.County;
import com.zyb.coolweather.model.Province;

public class CoolWeatherDB {

	/**
	 * ���ݿ���
	 */
	public static final String DB_NAME = "cool_weather";
	
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolweatherDB;
	
	private SQLiteDatabase db;
	
	/**
	 * ���췽��˽�л�
	 * @param context
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherHelper dbhelper = new CoolWeatherHelper(context,DB_NAME,null,VERSION);
		db = dbhelper.getWritableDatabase();
	}
	
	/**
	 * ��ȡCoolWeatherDB��ʵ��
	 * @param context
	 * @return
	 */
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolweatherDB == null){
			coolweatherDB = new CoolWeatherDB(context);
		}
		return coolweatherDB;
		
	}
	
	/**
	 * ��Provinceʵ���洢�����ݿ�
	 * @param province
	 */
	public void saveProvince(Province province){
		if(province != null){
			ContentValues value = new ContentValues();
			value.put("province_name",province.getProvinceName());
			value.put("province_code", province.getProvinceCode());
			db.insert("Province", null, value);
		}
		
	}
	
	/**
	 * �����ݿ��ȡʡ�ݵ���Ϣ
	 * @return
	 */
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
		province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
		province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
		list.add(province);
				
			}while(cursor.moveToNext());
		}
		return list;
		
	}
	
	/**
	 * ��cityʵ���洢�����ݿ�
	 * @param city
	 */
	public void saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code",city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("city", null, values);
		}
	}
	
	/**
	 * �����ݿ��ȡ���е���Ϣ
	 * @return
	 */
	public List<City> loadCitys(int provinceId){
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("city",null, "province_id =?",new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
			City city = new City();
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
			city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
			city.setProvinceId(provinceId);
			list.add(city);
				
			}while(cursor.moveToNext());
		}
		return list;
		
	}
	
	
	/**
	 * ��Countyʵ���洢�����ݿ�
	 * @param county
	 */
	public void saveCounty(County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name",county.getCountyName());
			values.put("city_id",county.getCityId());
			values.put("county_code", county.getCountyCode());
			db.insert("county", null, values);
		}
	}

	/**
	 * �����ݿ��ȡ�ص���Ϣ
	 * @return
	 */
	public List<County> loadCounty(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("county",null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null, null);
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			}while(cursor.moveToNext());
		}
		return list;
		
	}
	
}
