package com.hzh.frame.ui;


import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.hzh.frame.R;
import com.hzh.frame.ui.activity.BaseUI;

public class WelcomeUI extends BaseUI{

    @Override
    public boolean setTitleIsShow() {
        return true;
    }
    
	@Override
	protected void onCreateBase() {
		setContentView(R.layout.base_ui_welcome);
		final TextView welcome = (TextView) findViewById(R.id.welcome);

		Animation ani = new AlphaAnimation(0f, 1f);
		ani.setDuration(1500);
		ani.setRepeatMode(Animation.REVERSE);
		ani.setRepeatCount(Animation.INFINITE);
		welcome.startAnimation(ani);
	}
}
