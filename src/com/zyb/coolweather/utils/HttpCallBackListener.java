package com.zyb.coolweather.utils;

public interface HttpCallBackListener {
 public void onFinish(String response);
 public void onError(Exception error);
}
