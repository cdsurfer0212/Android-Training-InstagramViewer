package com.example.instagramviewer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class CustomLinearLayout extends LinearLayout {
    private BaseAdapter adapter;
    private OnClickListener onClickListener = null;

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
    }
    

    public CustomLinearLayout(Context context) {
        super(context);
    }
    
    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindLinearLayout() {
        int count = adapter.getCount();
        
        this.removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = adapter.getView(i, null, null);
            v.setOnClickListener(this.onClickListener);
            
            addView(v, i);
        }
    }

}
