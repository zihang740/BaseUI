package com.hzh.frame.widget.xrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    public static final int TYPE_HEADER = 0;  //说明是带有Header的   
    public static final int TYPE_FOOTER = 1;  //说明是带有Footer的   
    public static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的
    protected List<T> mDatas;
    protected Context mContext;
    protected LayoutInflater mInflater;
    private View mHeaderView;    
    private View mFooterView;  

    public BaseRecyclerAdapter(Context ctx, List<T> list) {
    	mDatas = (list != null) ? list : new ArrayList<T>();
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
    }
    
    /** 重写这个方法，很重要，是加入Header和Footer的关键，我们通过判断item的类型，从而绑定不同的view    * */   
    @Override
    public int getItemViewType(int position) {   
        //1.无Header无Footer
        if (mHeaderView == null && mFooterView == null){     
        	//ItemContext的posistion不变
            return setItemChildViewType(position);   
        }      
        //2.有Header或Footer且当前项属于其中一种
        //2.1.当前项属于Header
        if (mHeaderView != null && position == 0){            
            return TYPE_HEADER;       
        }
        //2.2.当前项属于Footer
        if (mFooterView != null && position == getItemCount()-1){           
            //加载Footer               
            return TYPE_FOOTER;       
        }        
        //3.有Header但当前项不属于其中一种(Footer不关心)
        //3.1有Header
        if(mHeaderView != null){
            //有Header:ItemContext的posistion-1
            return setItemChildViewType(position-1);
        }else{
        //3.2无Header:
            //无Header:ItemContext的posistion不变
            return setItemChildViewType(position);
        }
    } 

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    	RecyclerViewHolder holder=null;
        switch (viewType) {
		case TYPE_HEADER://加载Header
			holder=new RecyclerViewHolder(mContext,mHeaderView);
			break;
		case TYPE_FOOTER://加载Footer
			holder=new RecyclerViewHolder(mContext,mFooterView);
			break;
        default://加载ItemView
            holder=new RecyclerViewHolder(mContext,getItemView(parent,viewType));
            break;
		}
        return holder;
    }

    protected View getItemView(ViewGroup parent,int viewType) {
        return mInflater.inflate(getItemLayoutId(viewType), parent, false);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        int viewType=getItemViewType(position);
        switch (viewType) {
            case TYPE_HEADER://加载Header
                break;
            case TYPE_FOOTER://加载Footer
                break;
            default://加载ItemView
                int thisPosition=position;
                //找到正确的position
                if (mHeaderView != null){
                    thisPosition=position-1;
                }
                bindData(holder, position, mDatas.get(thisPosition));
                break;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position,List payLoads) {
        if(payLoads.isEmpty()){
            onBindViewHolder(holder,position);
        }else{
            int viewType=getItemViewType(position);
            switch (viewType) {
                case TYPE_HEADER://加载Header
                    break;
                case TYPE_FOOTER://加载Footer
                    break;
                default://加载ItemView
                    int thisPosition=position;
                    //找到正确的position
                    if (mHeaderView != null){
                        thisPosition=position-1;
                    }
                    bindDataLocal(holder, position, mDatas.get(thisPosition));
                    break;
            }
        }
    }
    

    @Override
    public int getItemCount() {
    	if(mHeaderView == null && mFooterView == null){
    		return mDatas.size();   
    	}
    	if(mHeaderView == null && mFooterView != null){
    		return mDatas.size() + 1;   
    	}
    	if(mHeaderView != null && mFooterView == null){
    		return mDatas.size() + 1;   
    	}
        return mDatas.size() + 2;        
    }
    
    @Override
    //重写此方法可实现动态设置每个item项在行、列中所占的份数
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    	super.onAttachedToRecyclerView(recyclerView);
    }
    
    /**当列表项出现到可视界面的时候调用*/
    @Override
    public void onViewAttachedToWindow(RecyclerViewHolder holder) {
    	super.onViewAttachedToWindow(holder);
    	if(getItemViewType(holder.getPosition())!=TYPE_NORMAL){
        	ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if(lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                 StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                 switch (getItemViewType(holder.getPosition())) {
    			  case TYPE_HEADER:
    			      //设置占领全部空间
    				  p.setFullSpan(true);
    				break;
    			  case TYPE_FOOTER:
    			      //设置占领全部空间
    				  p.setFullSpan(true);
    				break;
    			 }
            }
    	}
    }

    
    public void setHeaderView(View headerView) {        
        this.mHeaderView = headerView; 
    }    
    public View getHeaderView() {   
    	return this.mHeaderView;
    }    
    public void setFooterView(View footerView) {
        this.mFooterView = footerView; 
    }  
    public View getFooterView() {   
    	return this.mFooterView;
    }
    public void removeHeaderView(){
        mHeaderView=null;
    }
    public void removeFooterView(){
        notifyItemRemoved(getItemCount());
        mFooterView=null;
    }

    public void setDatas(List<T> mDatas){
    	this.mDatas=mDatas;
        notifyDataSetChanged();
    }

    public List<T> getDatas(){
        return mDatas;
    }

    public void removeItem(int position){
        mDatas.remove(position);
        //第position个被删除的时候刷新，同样会有动画
        notifyItemRemoved(position);
        if(position != mDatas.size()){
            //刷新从positionStart开始itemCount数量的item了（这里的刷新指回调onBindViewHolder()方法）
            notifyItemRangeChanged(position, mDatas.size() - position);
        }
    }
    
    /**
     * 设置每个Item的布局文件
     * */
	public int getItemLayoutId(int viewType){return 0;}

    public void bindData(RecyclerViewHolder holder, int position, T item){}

    /**
     * 设计局部刷新 | 解决刷新闪烁问题
     * */
    public void bindDataLocal(RecyclerViewHolder holder, int position, T item){}

    /**
     * 设置除开头尾外的Item类型
     * */
    public int setItemChildViewType(int position) {
        return TYPE_NORMAL;
    }

}