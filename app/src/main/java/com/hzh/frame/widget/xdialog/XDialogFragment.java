package com.hzh.frame.widget.xdialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.hzh.frame.R;
import com.hzh.frame.util.AndroidUtil;

/**
 * 全屏无弹窗DialogFragment
 * */
public abstract class XDialogFragment extends DialogFragment {

    public static final String TAG="x_dialog_fragment";
    private View layout;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout( AndroidUtil.getWindowWith(), getDialog().getWindow().getAttributes().height);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.TranslucentNoTitle);//设置全屏无弹窗
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        setWindowAttr();
        layout=inflater.inflate(getLayoutResour(),container);
        bindView(layout);
        return layout;
    }

    public void setWindowAttr(){
        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        window.setAttributes(lp);
        //进入结束动画
        getDialog().getWindow().getAttributes().windowAnimations=R.style.FragmentDialogAnimation;
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        if(getDialog()==null){
            super.show(manager, tag);
        } else
        if(getDialog().isShowing()){
            super.dismiss();
            super.show(manager, tag);
        }else{ 
            super.show(manager, tag);
        }
    }

    protected void bindView(View layout){};
    
    //获取布局文件
    protected abstract int getLayoutResour();

    public View getLayout() {
        return layout;
    }
    
    public <T extends View> T findViewById(int id) {
        return layout.findViewById(id);
    }
}
