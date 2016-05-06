package com.zyb.coolweather.utils;

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
	
}
