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
	 * 数据库名
	 */
	public static final String DB_NAME = "cool_weather";
	
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolweatherDB;
	
	private SQLiteDatabase db;
	
	/**
	 * 构造方法私有化
	 * @param context
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherHelper dbhelper = new CoolWeatherHelper(context,DB_NAME,null,VERSION);
		db = dbhelper.getWritableDatabase();
	}
	
	/**
	 * 获取CoolWeatherDB的实例
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
	 * 将Province实例存储到数据库
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
	 * 从数据库获取省份的信息
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
	 * 将city实例存储到数据库
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
	 * 从数据库获取城市的信息
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
	 * 将County实例存储到数据库
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
	 * 从数据库获取县的信息
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
