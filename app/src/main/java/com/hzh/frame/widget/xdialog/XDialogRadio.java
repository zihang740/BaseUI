package com.hzh.frame.widget.xdialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hzh.frame.R;
import com.hzh.frame.comn.callback.CallBack;
import com.hzh.frame.comn.model.BaseRadio;
import com.hzh.frame.util.AndroidUtil;
import com.hzh.frame.util.Util;
import com.hzh.frame.widget.toast.BaseToast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;

/**
 * 单选按钮组Dialog
 */
public class XDialogRadio<T extends BaseRadio> extends DialogFragment {

    public static final String TAG = "XDialogRadio";
    private View layout;
    private RadioGroup radioGroup;

    private List<T> datas;//所有单选项
    private String title = "";//标题
    private String okName = "";//确认名称
    private String noName = "";//取消名称
    private double radioButtonMinWidth= AndroidUtil.getWindowWith() / 10.0 * 7;
    private double radioButtonMinHeight=0;
    private CallBack<T> callBack;
    public Disposable disposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.TranslucentNoTitle);//设置全屏无弹窗
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.base_xdialog_radio, container);
        radioGroup = layout.findViewById(R.id.radioGroup);
        bindView(layout);
        return layout;
    }

    protected void bindView(View layout) {
        radioButtonMinHeight=getResources().getDimension(R.dimen.dp_48);
        for (int i = 0; i < datas.size(); i++) {
            T model = datas.get(i);
            RadioButton radioView = (RadioButton) getLayoutInflater().inflate(R.layout.base_item_xdialog_radio, null);
            radioView.setId(i);//解决默认选中无法取消
            radioView.setMinWidth((int) radioButtonMinWidth);
            radioView.setMinHeight((int) radioButtonMinHeight);
            radioView.setText(model.getName());//显示
            radioView.setChecked(model.getChecked());//选中
            radioView.setTag(model);
            radioView.setOnClickListener(buttonView -> {
                if (((RadioButton)buttonView).isChecked()) {
                    if(disposable!=null){
                        disposable.dispose();
                    }
                    Flowable<T> flowable1=Flowable.just((T) buttonView.getTag());
                    Flowable<Long> flowable2=Flowable.timer(1, TimeUnit.SECONDS);
                    disposable = Flowable
                            .zip(flowable1, flowable2, new BiFunction<T, Long, T>() {
                                @Override
                                public T apply(T t, Long aLong) throws Exception {
                                    return t;
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext(t -> {
                                if(callBack!=null){
                                    callBack.onSuccess(t);
                                    dismiss();
                                }
                            }).doOnComplete(() -> {
                                disposable.dispose();
                            }).subscribe();
                }
            });
            radioGroup.addView(radioView);
        }
        if (Util.isEmpty(title)) {
            layout.findViewById(R.id.title).setVisibility(View.GONE);
            layout.findViewById(R.id.titleLine).setVisibility(View.GONE);
        } else {
            layout.findViewById(R.id.title).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.titleLine).setVisibility(View.VISIBLE);
            ((TextView) layout.findViewById(R.id.title)).setText(title);
        }
        layout.findViewById(R.id.close).setOnClickListener(view -> dismiss());
    }

    public void show(FragmentManager manager) {

        if (datas == null || datas.size() == 0) {
            BaseToast.getInstance().setMsg("选项不能为空").show();
            return;
        }
        if (getDialog() == null) {
            super.show(manager, TAG);
        } else if (getDialog().isShowing()) {
            super.dismiss();
            super.show(manager, TAG);
        } else {
            super.show(manager, TAG);
        }
    }

    public XDialogRadio<T> setCallBack(CallBack<T> callBack) {
        this.callBack = callBack;
        return this;
    }

    public XDialogRadio<T> setData(List<T> datas) {
        if (datas == null) {
            this.datas = new ArrayList<>();
        } else {
            this.datas = datas;
        }
        return this;
    }

    public XDialogRadio<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * @param okName 确定按钮名称
     * @param noName 取消按钮名称
     */
    public XDialogRadio<T> setOkAndNoName(String okName, String noName) {
        this.okName = okName;
        this.noName = noName;
        return this;
    }

    public double getRadioButtonMinWidth() {
        return radioButtonMinWidth;
    }

    public XDialogRadio<T> setRadioButtonMinWidth(double radioButtonMinWidth) {
        this.radioButtonMinWidth = radioButtonMinWidth;
        return this;
    }

    public double getRadioButtonMinHeight() {
        return radioButtonMinHeight;
    }

    public XDialogRadio<T> setRadioButtonMinHeight(double radioButtonMinHeight) {
        this.radioButtonMinHeight = radioButtonMinHeight;
        return this;
    }
}
