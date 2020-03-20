package com.hzh.frame;

import android.app.Application;

import com.hzh.frame.core.BaseFrame;


public class BaseApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		BaseFrame.init(this);
    }

    
	@Override
    public void onTerminate() {
        super.onTerminate();
        BaseFrame.stop();
    }

}
