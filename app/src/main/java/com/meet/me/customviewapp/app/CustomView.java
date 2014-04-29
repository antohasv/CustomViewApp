package com.meet.me.customviewapp.app;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class CustomView extends ViewGroup {

    private int mHeight;
    private int mWidth;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
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
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG", "MOVEEEE");
                float deltaX = x - oldX;
                float deltaY = y - oldY;

                if (deltaX > 0 && deltaX >= ViewConfiguration.getTouchSlop() && (Math.abs(deltaX) - Math.abs(deltaY)) > 0) {
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                int deltaX = Math.round(x - oldX);
                getChildAt(1).layout(-mWidth / 2 + deltaX, 0, mWidth / 2, mHeight);
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
        getChildAt(0).layout(0, 0, mWidth, mHeight);
        getChildAt(1).layout(-mWidth / 2, 0, mWidth / 2, mHeight);
    }
}
