package com.hzh.frame.ui.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.hzh.frame.R;
import com.hzh.frame.comn.callback.HttpCallBack;
import com.hzh.frame.core.HttpFrame.BaseHttp;
import com.hzh.frame.util.Util;
import com.hzh.frame.widget.xlistview.XListView;
import com.hzh.frame.widget.xlistview.XListView.IXListViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存只做第一次显示数据用(其他加载及时响应)
 */
public abstract class AbsListViewUI<T extends Model> extends BaseUI implements IXListViewListener {

    private Class<T> modelClass;
    private XListView mListView;
    private LinearLayout mListViewBg;
    private XListViewAdapter mAdapter;
    private int[] pageInfo;//int[0]:起始页码;int[1]:当前页码 ;int[10]:每页显示数
    private boolean httpState = true;
    /**
     * 加载模式 <br />0:先加载本地再加载网络数据 <br />1:只加载本地数据 <br />2:只加载网络数据
     */
    private int loadPattern = 0;
    /**
     * 设置框架加载网络数据后是否更新本地数据库 <br />false:不更新 <br />true:更新
     */
    private boolean updLocalData = true;

    @Override
    protected void onCreateBase() {
        pageInfo = setPageInfo();
        //获取T.Class
        modelClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        setContentView(setLayoutView());
        //		showLoding("加载中",false);
        mListView = (XListView) findViewById(R.id.xListView);
        mListView.setMListViewListener(this);
        mListView.setPageInfo(pageInfo[0], pageInfo[1], pageInfo[2]);
        mListViewBg = (LinearLayout) findViewById(R.id.xlistViewBg);
        //显示背景图片
        mListViewBg.setVisibility(View.VISIBLE);
        bindView();
        switch (loadPattern) {
            case 0:
                loadLocalData();
                loadNetworkData(pageInfo[1], pageInfo[2], true);
                break;
            case 1:
                loadLocalData();
                break;
            case 2:
                loadNetworkData(pageInfo[1], pageInfo[2], true);
                break;
        }
    }

    // 加载本地缓存数据(这里只做第一次点击进来第一个页面的缓存)
    private void loadLocalData() {
        From from = new Select().from(modelClass);
        //获取查询条件
        from = setSqlParams(from);
        //得到缓存数据
        List<T> list = from.execute();
        if (list.size() > 0) {
            //有缓存数据
            mListView.setPullLoadEnable(false);
            mAdapter = new XListViewAdapter(AbsListViewUI.this, list);
            mListView.setAdapter(mAdapter);
            mListView.setPullLoadEnable(false);
            mListView.stopRefresh();
            mListView.stopLoadMore();
            //			dismissLoding();

            //去除背景图片
            mListViewBg.setVisibility(View.GONE);
        } else {
            //无缓存数据
            if (setNoDateIsRefresh()) {
                //可以下拉刷新
                mListView.setPullLoadEnable(false);
                mAdapter = new XListViewAdapter(AbsListViewUI.this, list);
                mListView.setAdapter(mAdapter);
                mListView.setPullLoadEnable(false);
                mListView.stopRefresh();
                mListView.stopLoadMore();
            }//else 不可以下拉刷新
        }
    }

    // 加载网络数据
    public void loadNetworkData(int page, int limit, boolean isRefresh) {
        if (loadPattern == 1) {
            loadLocalData();
        } else {
            JSONObject params = setHttpParams();
            try {
                params.put("page", page);
                params.put("limit", limit);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            BaseHttp.getInstance().query(setHttpPath(), params, new CallBack(page, limit, isRefresh));
        }
    }

    public class CallBack extends HttpCallBack {

        public int page;
        public int limit;
        public boolean isRefresh;

        public CallBack(int page, int limit, boolean isRefresh) {
            this.page = page;
            this.limit = limit;
            this.isRefresh = isRefresh;
        }

        @Override
        public void onSuccess(JSONObject response) {
            httpState = true;
            List<T> list = handleHttpData(response);
            if (httpState) {
                if (list != null && list.size() > 0) {
                    //接口返回正常
                    //放本地数据库缓存
                    if (updLocalData) {
                        new Delete().from(modelClass).execute();
                        for (T model : list) {
                            model.save();
                        }
                    }
                    if (list.size() < limit) {
                        mListView.setPullLoadEnable(false);
                    } else {
                        mListView.setPullLoadEnable(true);
                    }
                    if (isRefresh) {
                        //下拉刷新
                        mAdapter = new XListViewAdapter(AbsListViewUI.this, list);
                        mListView.setAdapter(mAdapter);
                        mListView.setRefreshTime(Util.getNewTime("yyyy-MM-dd HH:mm:ss"));
                    } else {
                        //加载更多
                        mAdapter.getList().addAll(list);
                        mAdapter.notifyDataSetChanged();
                    }
                    mListView.stopRefresh();
                    mListView.stopLoadMore();
                    //						dismissLoding();

                    //去除背景图片
                    mListViewBg.setVisibility(View.GONE);
                } else {
                    //接口返回无数据或错误
                    if (isRefresh) {
                        //下拉刷新
                        if (list == null) {
                            list = new ArrayList<T>();
                        }
                        mAdapter = new XListViewAdapter(AbsListViewUI.this, list);
                        mListView.setAdapter(mAdapter);
                        mListView.setRefreshTime(Util.getNewTime("yyyy-MM-dd HH:mm:ss"));
                        //显示背景图片
                        mListViewBg.setVisibility(View.VISIBLE);
                    }
                    mListView.setPullLoadEnable(false);
                    mListView.stopRefresh();
                    mListView.stopLoadMore();
                    //确认当前ListView还没有成功显示过时可以让用户点击重载
                    if (mAdapter == null) {
                        //							showLoding("点击重载",true);
                    }
                }
            } else {
                //请求异常前端UI不做任何响应
                mListView.stopRefresh();
                mListView.stopLoadMore();
                if (!isRefresh) {
                    //加载更多  恢复到之前页码
                    mListView.setPage(mListView.getPage() - 1);
                }
            }
        }

        @Override
        public void onFail() {
            handleHttpDataFailure();
            mListView.stopRefresh();
            mListView.stopLoadMore();
            //确认当前ListView还没有成功显示过时可以让用户点击重载
            if (mAdapter == null) {
                //					showLoding("点击重载",true);
            }
            //加载更多  恢复到之前页码
            if (!isRefresh) {
                mListView.setPage(mListView.getPage() - 1);
            }
        }
    }

    //ListView适配器
    protected class XListViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<T> list;

        public XListViewAdapter(Context context) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.list = new ArrayList<T>();
        }

        public XListViewAdapter(Context context, List<T> list) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.list = list;
        }

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return AbsListViewUI.this.getView(position, convertView, parent, inflater, list);
        }
    }


    /**
     * 设置ListView分页信息
     *
     * @return int[0]:起始页码 <br />
     * int[1]:当前页码 <br />
     * int[2]:每页显示数
     */
    public int[] setPageInfo() {
        return new int[]{1, 1, 20};
    }

    /**
     * 获取ListView分页信息
     *
     * @return int[0]:起始页码 <br />
     * int[1]:当前页码 <br />
     * int[2]:每页显示数
     */
    public int[] getPageInfo() {
        return pageInfo;
    }

    /**
     * 获取ListView对象
     */
    public XListView getListView() {
        return mListView;
    }

    /**
     * 获取ListView适配器
     */
    public XListViewAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置布局文件
     */
    protected abstract int setLayoutView();

    /**
     * 绑定布局文件关联
     */
    protected void bindView() {
    }

    ;

    /**
     * 设置查询本地数据库SQL条件()
     */
    protected From setSqlParams(From from) {
        return from;
    }

    ;

    /**
     * 设置无数据时列表是否允许下拉刷新(默认可以:true)
     */
    protected boolean setNoDateIsRefresh() {
        return true;
    }

    /**
     * 设置请求路径
     * */
    protected String setHttpPath(){return "";}

    /**
     * 设置请求参数
     */
    protected JSONObject setHttpParams() {
        return new JSONObject();
    }

    /**
     * 处理HTTP请求成功回参数据
     */
    protected List<T> handleHttpData(JSONObject response) {
        return null;
    }

    /**
     * 处理HTTP请求失败回参数据
     */
    protected void handleHttpDataFailure() {
        switch (loadPattern) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                showLodingFail();
                break;
        }
    }

    ;

    /**
     * 处理HTTP请求状态
     *
     * @param httpState 默认正常:true;终止页面响应:false
     */
    protected void setHttpState(boolean httpState) {
        this.httpState = httpState;
    }

    ;

    /**
     * 设置加载模式 <br />
     * 0:先加载本地再加载网络数据 <br />
     * 1:只加载本地数据 <br />
     * 2:只加载网络数据
     */
    protected void setLoadPattern(int loadPattern) {
        this.loadPattern = loadPattern;
    }

    ;

    /**
     * 设置框架加载网络数据后是否更新本地数据库 <br />
     *
     * @param updLocalData false:不更新,true:更新
     */
    protected void setUpdLocalData(boolean updLocalData) {
        this.updLocalData = updLocalData;
    }

    ;

    /**
     * 获取需要显示的每个ItemView
     */
    protected abstract View getView(int position, View convertView, ViewGroup parent, LayoutInflater inflater, List<T> list);
}