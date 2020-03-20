package com.hzh.frame.widget.xdialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.hzh.frame.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 复选框Dialog
 * */
public class XDialogCheck {

	private Dialog alertDialog;
	private List<HashMap<String, Object>> items;//展示选项集合
	private List<HashMap<String, Object>> selectedItems;//选择的选项集合
	private String[] itemNames;//名称
	private boolean[] isSelected;//是否选中
    Context mContext;
	
	/**
	 * 复选框
	 * @param context
	 * @param title 弹出框标题
	 * @param list 复选项{name名称  isSelected是否选中(不传默认未选中)}
	 * @param callback 复选框选择结果回调
	 * */
	public void show(Context context,String title,List<HashMap<String, Object>> list,final ICheckboxDialogCallBack callback) {
	    mContext=context;
		//防止网络延迟情况下多次点击弹出多个窗口
		dismiss();
		items=list;
		itemNames = new String[items.size()];
		isSelected = new boolean[items.size()];
		for(int i = 0;i<items.size();i++){
			itemNames[i]=(String) items.get(i).get("name");
			if(null==items.get(i).get("isSelected")){
			   isSelected[i]=false;//默认未选中
			}else{
			   isSelected[i]=(Boolean) items.get(i).get("isSelected");
			}
		}
        alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMultiChoiceItems(itemNames, isSelected, new DialogInterface.OnMultiChoiceClickListener() {   
                    @Override   
                    public void onClick(DialogInterface dialog, int index, boolean isChecked) {   
                    	items.get(index).put("isSelected", isChecked);
                    }   
                })
                .setPositiveButton(mContext.getString(R.string.base_confirm), new DialogInterface.OnClickListener() {   
                    @Override   
                    public void onClick(DialogInterface dialog, int which) {  
                    	selectedItems=new ArrayList<HashMap<String,Object>>();
                    	for(int i=0;i<items.size();i++){
                    		if((Boolean) items.get(i).get("isSelected")){
                    			selectedItems.add(items.get(i));
                    		}
                    	}
                    	callback.confirm(selectedItems);
                    }   
                })
                .setNegativeButton(mContext.getString(R.string.base_cancel), new DialogInterface.OnClickListener() {   
   
                    @Override   
                    public void onClick(DialogInterface dialog, int which) {   
                    	callback.cancel();
                    }   
                })
                .create();   
        alertDialog.show();
	}
	
	/**
	 * 关闭当前Dialog
	 * */
	public void dismiss(){
		if(alertDialog!=null && alertDialog.isShowing()){
			alertDialog.dismiss();
		}
	}
	
	/**
	 * 复选框选择结果回调
	 * */
	public interface ICheckboxDialogCallBack{
		  /**
		   * 确定
		   * @param selectedItems 返回选择的结果集
		   */
		  void confirm(List<HashMap<String, Object>> selectedItems);
		  
		  /**
		   * 取消
		   */
		  void cancel();
	}
}
