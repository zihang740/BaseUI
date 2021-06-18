package com.hzh.frame.comn.ItemDecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.hzh.frame.R;
import com.hzh.frame.util.Util;
import com.hzh.frame.widget.xrecyclerview.BaseRecyclerAdapter;


/**
 * 吸顶效果
 * @version 1.0
 * @date 2017/8/21 
 */
public class BaseSuctionTopItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private int groupHeight;
    private DecorationCallback callback;
    private TextPaint textPaint;
    private Paint paint,linePaint;
    
    public BaseSuctionTopItemDecoration(Context context, DecorationCallback decorationCallback) {
        this.mContext=context;
        this.callback = decorationCallback;
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(mContext, R.color.base_title_bg_end));
        linePaint = new Paint();
        linePaint.setColor(ContextCompat.getColor(mContext, R.color.base_8a8a8a));
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(Util.dip2px(context,12));
        textPaint.setColor(ContextCompat.getColor(context, R.color.white));
        textPaint.setTextAlign(Paint.Align.LEFT);
        groupHeight= Util.dip2px(context,25);
    }

    /**
     * 执行顺序:1
     * (注:跟随滑动,不断回调,每次滑动都会调用,parent里面的信息是可见区域信息,如:.getChildCount则返回的是可见的item总数)
     * 实现Item的类似padding的效果，也就是让我们正常的Item进行移动
     * 可以通过outRect.set(l,t,r,b)设置指定itemview的paddingLeft，paddingTop， paddingRight， paddingBottom
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = parent.getChildLayoutPosition(view);
        switch (parent.getAdapter().getItemViewType(itemPosition)){
            case BaseRecyclerAdapter.TYPE_HEADER:
                break;
            case BaseRecyclerAdapter.TYPE_FOOTER:
                break;
            case BaseRecyclerAdapter.TYPE_NORMAL://1.预留(padding)出分割线和分组的位置
                if (callback.isGroup(itemPosition)){
                    outRect.top = 1;//分割线高度
                }else{
                    outRect.top = groupHeight;//分组头高度
                }
                break;
        }
    }


    @Override
    /**
     * 执行顺序:2
     * (注:跟随滑动,不断回调,每次滑动都会调用,parent里面的信息是可见区域信息,如:.getChildCount则返回的是可见的item总数)
     * 可以实现类似绘制背景的效果，内容在正常的Item下面，被覆盖。正常我们要结合
     * getItemOffsets将正常Item错开，免得被正常Item覆盖掉
     */
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int itemPosition = parent.getChildLayoutPosition(view);
            switch (parent.getAdapter().getItemViewType(itemPosition)){
                case BaseRecyclerAdapter.TYPE_HEADER:
                    break;
                case BaseRecyclerAdapter.TYPE_FOOTER:
                    break;
                case BaseRecyclerAdapter.TYPE_NORMAL://2.绘制分割线
                    if (callback.isGroup(itemPosition)){
                        c.drawRect(left+20,view.getTop()-1,right-20,view.getTop(),linePaint);
                    }
                    break;
            }
        }
    }

    @Override
    /**
     * 执行顺序:3
     * (注:跟随滑动,不断回调,每次滑动都会调用,parent里面的信息是可见区域信息,如:.getChildCount则返回的是可见的item总数)
     * 可以绘制在内容的上面，覆盖在正常的Item内容
     */
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int itemPosition = parent.getChildLayoutPosition(view);
            switch (parent.getAdapter().getItemViewType(itemPosition)){
                case BaseRecyclerAdapter.TYPE_HEADER:
                    break;
                case BaseRecyclerAdapter.TYPE_FOOTER:
                    break;
                case BaseRecyclerAdapter.TYPE_NORMAL://3.绘制组
                    if(i==0){//判断当前可的第一项Item
                        //(注:isGroup()传入的item+了1的,所有在callback的isGroup中判断下,dataList>size返回true默认同组,不绘制分组)
                        if(!callback.isGroup(itemPosition+1) && view.getBottom()<groupHeight) {//2个分组交汇情况
                            //当前Item和下一个Item不同组 && 当前Item的下边距位置<分组高度
                            c.drawRect(left, view.getBottom() - groupHeight, right, view.getBottom(), paint);//绘制矩形
                            c.drawRect(left, view.getBottom(), right, view.getBottom(), paint);//绘制交汇时分割线
                            c.drawText(callback.getGroupName(itemPosition), left + Util.dip2px(mContext, 10), view.getBottom() - (groupHeight / 2) + Util.dip2px(mContext, 6), textPaint);//绘制文本
                        }else{//2个分组未交汇情况
                            c.drawRect(left, 0, right, groupHeight, paint);//绘制矩形
                            c.drawText(callback.getGroupName(itemPosition), left + Util.dip2px(mContext, 10), groupHeight / 2 + Util.dip2px(mContext, 6), textPaint);//绘制文本
                        }
                    }else{//判断其他Item
                        if (!callback.isGroup(itemPosition)) {
                            c.drawRect(left, view.getTop() - groupHeight, right, view.getTop(), paint);//绘制矩形
                            c.drawText(callback.getGroupName(itemPosition), left + Util.dip2px(mContext, 10), view.getTop() - (groupHeight / 2) + Util.dip2px(mContext, 6), textPaint);//绘制文本
                        }
                    }
                    break;
            }
        }
    }

    public interface DecorationCallback {

        /**
         * 对比前一个item是否同组
         * @param position 当前item坐标
         * @return true 同组
         * @return false 不是同组
         * */
        boolean isGroup(int position);

        /**
         * 获取当前组名称
         * */
        String getGroupName(int position);
    }
    
//    接口实现例子
//    new SuctionTopItemDecoration(this, new SuctionTopItemDecoration.DecorationCallback() {
//        @Override
//        public boolean isGroup(int position) {
//            if(position<=0){
//                return false;
//            }
//            if(position>=getAdapter().getDatalist().size()){
//                return true;
//            }
//            //equalsIgnoreCase:忽略大小写比较A=a
//            if(getAdapter().getDatalist().get(position-1).getTableGroupName().equalsIgnoreCase(getAdapter().getDatalist().get(position).getTableGroupName())){
//                return true;
//            }else{
//                return false;
//            }
//        }
//
//        @Override
//        public String getGroupName(int position) {
//            return getAdapter().getDatalist().get(position).getTableGroupName();
//        }
//    })
}