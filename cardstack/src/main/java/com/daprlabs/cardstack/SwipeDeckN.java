package com.daprlabs.cardstack;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by aaron on 24/03/2016.
 */
public class SwipeDeckN extends FrameLayout {

    //Swipe Deck Flags
    private static int NUMBER_OF_CARDS;
    private float ROTATION_DEGREES;
    private float CARD_SPACING;
    private boolean RENDER_ABOVE;
    private boolean RENDER_BELOW;
    private float OPACITY_END;

    //Swipe Deck container attributes
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;
    private int paddingLeft;

    //Deck
    private Deck deck;

    private boolean hardwareAccelerationEnabled = true;
    private Adapter mAdapter;

    public SwipeDeckN(Context context, AttributeSet attrs) {
        super(context, attrs);

        //set clipping of view parent to false so cards render outside their view boundary
        //make sure not to clip to padding
        setClipToPadding(false);
        setClipChildren(false);
    }

    /**
     * Set Hardware Acceleration Enabled.
     *
     * @param accel
     */
    public void setHardwareAccelerationEnabled(Boolean accel) {
        this.hardwareAccelerationEnabled = accel;
    }

    public void setAdapter(Adapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public void setSelection(int position){
        throw new UnsupportedOperationException("not yet implemented");
    }
    


}
