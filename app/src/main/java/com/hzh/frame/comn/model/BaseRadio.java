package com.hzh.frame.comn.model;


import com.hzh.frame.util.Util;

public class BaseRadio{

    private String id;
	private String name;
    private boolean checked;

    public String getName() {
        return name;
    }

    public BaseRadio setName(String name) {
        if(Util.isEmpty(name)){
            this.name = "暂无";
        }else{
            this.name = name;
        }
        return this;
    }

    public boolean getChecked() {
        return checked;
    }

    public BaseRadio setChecked(boolean checked) {
        this.checked = checked;
        return this;
    }

    public String getId() {
        return id;
    }

    public BaseRadio setId(String id) {
        this.id = id;
        return this;
    }
}
