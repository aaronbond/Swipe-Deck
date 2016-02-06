package com.daprlabs.cardstack;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by aaron on 23/12/2015.
 */
public class SwipeCoordinatorLayout extends CoordinatorLayout {
    public SwipeCoordinatorLayout(Context context) {
        super(context);
        setClipChildren(false);
    }

    public SwipeCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClipChildren(false);
    }

    public SwipeCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(false);
    }

    //this is so that on versions of android pre lollipop it will render the cardstack above
    //everything else within the layout
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        ViewGroup.LayoutParams params = getLayoutParams();

        ArrayList<View> children = new ArrayList<>();
        View swipeDeck = null;
        for(int i=0; i< childCount; ++i){
            View child = getChildAt(i);
            if(child instanceof SwipeDeck){
                swipeDeck = getChildAt(i);
            }else{
                children.add(child);
            }
        }
        removeAllViews();
        removeAllViewsInLayout();
        for(View v : children){
            addViewInLayout(v, -1, v.getLayoutParams(), true);
        }
        if(swipeDeck != null){
            addViewInLayout(swipeDeck, -1, swipeDeck.getLayoutParams(), true);
        }
        invalidate();
        requestLayout();
    }

}
