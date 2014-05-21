package com.meet.me.customviewapp.app;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class CustomView extends ViewGroup {

    public static final int MSG_SCROLL_RIGHT = 1000;
    public static final int MSG_SCROLL_LEFT = 1001;

    public static final int WIN_OFFSET = 10;
    public static final int DIS_JUMP = 10;

    private int mScreenHeight;
    private int mScreenWidth;

    private int mMenuWidth;
    private Scroller mScroller;

    private MyHandler mHandler;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(getContext());
        mHandler = new MyHandler(this);

        createViews();
    }

    private void createViews() {
        View view1 = new View(getContext());
        view1.setBackgroundColor(Color.RED);

        View view2 = new View(getContext());
        view2.setBackgroundColor(Color.GREEN);

        addView(view1);
        addView(view2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChild(widthMeasureSpec, heightMeasureSpec);

        mScreenWidth = MeasureSpec.getSize(widthMeasureSpec);
        mScreenHeight = MeasureSpec.getSize(heightMeasureSpec);
        mMenuWidth = mScreenWidth / 2;

        setMeasuredDimension(mScreenWidth, mScreenHeight);
    }

    private float oldX;
    private float oldY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = x;
                oldY = y;
                return false;
            case MotionEvent.ACTION_MOVE: {
                float deltaX = x - oldX;
                float deltaY = y - oldY;

                if (deltaX > 0 && deltaX >= ViewConfiguration.get(getContext()).getScaledTouchSlop() && (Math.abs(deltaX) - Math.abs(deltaY)) > 0) {
                    return true;
                }
            }
            case MotionEvent.ACTION_UP:
                int deltaX = Math.round(x - oldX);
                mMenuWidth = mMenuWidth + deltaX;
                return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int deltaX = Math.round(x - oldX);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                getChildAt(1).layout(-mScreenWidth, 0, mMenuWidth + deltaX, mScreenHeight);
                return true;
            case MotionEvent.ACTION_UP:

                if (x > mScreenWidth / 2) {
                    mScroller.startScroll((int) x, 0, mScreenWidth - (int) x, 0, 2000);
                    mHandler.sendEmptyMessage(MSG_SCROLL_RIGHT);
                } else {
                    mScroller.startScroll((int) x, 0, (int) x, 0, 2000);
                    mHandler.sendEmptyMessage(MSG_SCROLL_LEFT);
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void measureChild(int screenWidth, int screenHeight) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = this.getChildAt(i);
            child.measure(screenWidth, screenHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getChildAt(0).layout(0, 0, mScreenWidth, mScreenHeight);
        getChildAt(1).layout(-mScreenWidth, 0, WIN_OFFSET, mScreenHeight);
    }

    public Scroller getScroller() {
        return mScroller;
    }

    private static class MyHandler extends Handler {

        private CustomView mCustomView;
        private Scroller mScroller;

        MyHandler(CustomView customView) {
            mCustomView = customView;
            mScroller = customView.getScroller();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mScroller.computeScrollOffset()) {
                switch (msg.what) {
                    case MSG_SCROLL_RIGHT:
                        mCustomView.getChildAt(1).layout(-mCustomView.mScreenWidth, 0, mScroller.getCurrX(), mCustomView.mScreenHeight);
                        sendEmptyMessage(MSG_SCROLL_RIGHT);
                        break;
                    case MSG_SCROLL_LEFT:
                        mCustomView.getChildAt(1).layout(-mCustomView.mScreenWidth, 0, -mScroller.getCurrX(), mCustomView.mScreenHeight);
                        sendEmptyMessage(MSG_SCROLL_LEFT);
                        break;
                }
            }

        }
    }
}
