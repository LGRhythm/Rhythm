package com.zoda.head;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 木凡的花园 on 2016/3/16.
 */
public class TableHead extends ViewGroup {

    /**
     * 第一个childView高度
     */
    int height0 = 0;
    /**
     * 记录viewGroup的高度和宽度
     */
    private int vgHeight = 0;
    private int vgWitdh = 0;

    private Paint mPaint;

    /**
     * 第2个及大于第2个的childView的左边框x坐标
     */
    private List<Integer> cls = new ArrayList<Integer>();

    public TableHead(Context context) {
        this(context, null);
    }

    public TableHead(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TableHead(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;

        int height0 = 0;
        int weightx = 0;

        /**
         * 遍历所有childView根据其宽和高，以及margin进行布局
         */
        for (int i = 0; i < cCount; i++) {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();
            int cl = 0, ct = 0, cr = 0, cb = 0;
            if (i == 0) {
                cl = getWidth() / 2 - cWidth / 2;
                ct = cParams.topMargin;
                cb = ct + cHeight;
                height0 = cHeight + cParams.topMargin + cParams.bottomMargin;
                cr = cl + cWidth;
            }
            if (i == 1) {
                cl = cParams.leftMargin;
                ct = height0 + cParams.topMargin;
                cb = ct + cHeight;
                cr = cl + cWidth;
                weightx += cWidth + cParams.leftMargin + cParams.rightMargin;
            }
            if (i > 1) {
                cl = weightx + cParams.leftMargin;
                ct = height0 + cParams.topMargin;
                cb = ct + cHeight;
                cr = cl + cWidth;
                weightx += cWidth + cParams.leftMargin + cParams.rightMargin;
                cls.add(cl);
            }
            childView.layout(cl, ct, cr, cb);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        /**
         * childView个数
         */
        int cCount = getChildCount();

        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;

        /**
         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
         */
        for (int i = 0; i < cCount; i++) {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();
            if (i == 0) {
                height0 = cHeight + cParams.topMargin + cParams.bottomMargin;
                vgWitdh = 0;
            }
            if (i > 0) {
                if (i == 1) {
                    vgHeight = height0 + cHeight + cParams.topMargin + cParams.bottomMargin + 1;
                }
                if (i > 1) {
                    vgHeight = (getChildAt(i - 1).getMeasuredHeight() > cHeight ? getChildAt(i - 1).getMeasuredHeight() : cHeight) + cParams.topMargin + cParams.bottomMargin + height0;
                }
                vgWitdh += cWidth + cParams.leftMargin + cParams.rightMargin;
            }
        }

        /**
         * 如果是wrap_content设置为我们计算的值
         * 否则：直接设置为父容器计算的值
         */
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : vgWitdh, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : vgHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(0, height0, vgWitdh, height0, mPaint);
        for (int l : cls) {
            canvas.drawLine(l - 1, height0, l - 1, vgHeight, mPaint);
        }
        cls.clear();
        canvas.drawLine(0, vgHeight-1, vgWitdh, vgHeight-1, mPaint);
        super.onDraw(canvas);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }
}
