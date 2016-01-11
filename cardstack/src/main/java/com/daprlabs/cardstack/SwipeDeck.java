package com.daprlabs.cardstack;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by aaron on 4/12/2015.
 */
public class SwipeDeck extends FrameLayout {

    private static final String TAG = "SwipeDeck.java";
    private static int NUMBER_OF_CARDS;
    private float ROTATION_DEGREES;
    private float CARD_SPACING;
    private boolean RENDER_ABOVE;
    private boolean RENDER_BELOW;
    private float OPACITY_END;
    private int CARD_GRAVITY;

    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;

    private SwipeEventCallback eventCallback;
    private CardPositionCallback cardPosCallback;

    /**
     * The adapter with all the data
     */
    private Adapter mAdapter;
    private int nextAdapterCard = 0;

    private View lastRemovedView;
    private SwipeListener swipeListener;
    private View topCard;
    private int leftImageResource;
    private int rightImageResource;

    public SwipeDeck(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SwipeDeck,
                0, 0);
        try {
            NUMBER_OF_CARDS = a.getInt(R.styleable.SwipeDeck_max_visible, 3);
            ROTATION_DEGREES = a.getFloat(R.styleable.SwipeDeck_rotation_degrees, 15f);
            CARD_SPACING = a.getDimension(R.styleable.SwipeDeck_card_spacing, 15f);
            RENDER_ABOVE = a.getBoolean(R.styleable.SwipeDeck_render_above, true);
            RENDER_BELOW = a.getBoolean(R.styleable.SwipeDeck_render_below, false);
            CARD_GRAVITY = a.getInt(R.styleable.SwipeDeck_card_gravity, 0);
            OPACITY_END = a.getFloat(R.styleable.SwipeDeck_opacity_end, 0.33f);
        } finally {
            a.recycle();
        }

        paddingBottom = getPaddingBottom();
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();

        //set clipping of view parent to false so cards render outside their view boundary

        //make sure not to clip to padding
        setClipToPadding(false);
        setClipChildren(false);

        //i don't think this does anything, doesn't seem to override parents clipping behaviour
//        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//        ViewCompat.setClipBounds(this, new Rect(0, 0, width, height));

        //render the cards and card deck above or below everything
        if (RENDER_ABOVE) {
            ViewCompat.setTranslationZ(this, Float.MAX_VALUE);
        }
        if (RENDER_BELOW) {
            ViewCompat.setTranslationZ(this, Float.MIN_VALUE);
        }
    }


    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //handle data set changes

                //if we need to add any cards at this point (ie. the amount of cards on screen
                //is less than the max number of cards to display) add the cards.
                int childCount = getChildCount();
                for (int i = childCount; i < NUMBER_OF_CARDS; ++i) {
                    addNextCard();
                }
                //position the items correctly on screen
                for (int i = 0; i < getChildCount(); ++i) {
                    positionItem(i);
                }
            }
        });

        removeAllViewsInLayout();
        requestLayout();
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    public void setSelection(int position) {
        throw new UnsupportedOperationException("Not supported");
    }

    public View getSelectedView() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null || mAdapter.getCount() == 0) {
            nextAdapterCard = 0;
            removeAllViewsInLayout();
            return;
        }

        //pull in views from the adapter at the position the top of the deck is set to
        //stop when you get to for cards or the end of the adapter
        int childCount = getChildCount();
        for (int i = childCount; i < NUMBER_OF_CARDS; ++i) {
            addNextCard();
        }
        for (int i = 0; i < getChildCount(); ++i) {
            positionItem(i);
        }
        //position the new children we just added and set up the top card with a listener etc
    }

    private void removeTopCard() {
        //top card is now the last in view children
        View child = getChildAt(getChildCount() - 1);
        child.setOnTouchListener(null);
        child.setX(0);
        child.setY(0);
        child.setRotation(0);
        removeView(child);
        // Record that this view was removed so that the next call to addNextCard can reuse it.
        lastRemovedView = child;

        //if there are no more children left after top card removal let the callback know
        if (getChildCount() <= 0 && eventCallback != null) {
            eventCallback.cardsDepleted();
        }
    }

    private void addNextCard() {
        if (nextAdapterCard < mAdapter.getCount()) {
            // TODO: Make view recycling work
            // TODO: Instead of removing the view from here and adding it again when it's swiped
            // ... don't remove and add to this instance: don't call removeView & addView in sequence.
            View newBottomChild = mAdapter.getView(nextAdapterCard, null/*lastRemovedView*/, this);
            this.lastRemovedView = null;

            //set the initial Y value so card appears from under the deck
            newBottomChild.setY(paddingTop);
            addAndMeasureChild(newBottomChild);
            nextAdapterCard++;
        }
        setupTopCard();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setZTranslations() {
        //this is only needed to add shadows to cardviews on > lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int count = getChildCount();
            for (int i = 0; i < count; ++i) {
                getChildAt(i).setTranslationZ(i * 10);
            }
        }
    }

    /**
     * Adds a view as a child view and takes care of measuring it
     *
     * @param child The view to add
     */
    private void addAndMeasureChild(View child) {
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        //every time we add and measure a child refresh the children on screen and order them
        ArrayList<View> children = new ArrayList<>();
        children.add(child);
        for (int i = 0; i < getChildCount(); ++i) {
            children.add(getChildAt(i));
        }

        removeAllViews();

        for (View c : children) {
            addViewInLayout(c, -1, params, true);
            int itemWidth = getWidth() - (paddingLeft + paddingRight);
            int itemHeight = getHeight() - (paddingTop + paddingBottom);
            c.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.EXACTLY | itemHeight); //MeasureSpec.UNSPECIFIED

            //ensure that if there's a left and right image set their alpha to 0 initially
            //alpha animation is handled in the swipe listener
            if (leftImageResource != 0) child.findViewById(leftImageResource).setAlpha(0);
            if (rightImageResource != 0) child.findViewById(rightImageResource).setAlpha(0);
        }
        setZTranslations();
    }

    /**
     * Positions the children at the "correct" positions
     */
    private void positionItem(int index) {

        View child = getChildAt(index);

        int width = child.getMeasuredWidth();
        int height = child.getMeasuredHeight();
        int left = (getWidth() - width) / 2;
        child.layout(left, paddingTop, left + width, paddingTop + height);
        //layout each child slightly above the previous child (we start with the bottom)
        int childCount = getChildCount();
        float offset = (int) (((childCount - 1) * CARD_SPACING) - (index * CARD_SPACING));
        child.animate()
                .setDuration(200)
                .y(paddingTop + offset);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = widthSize;
        } else {
            //Be whatever you want
            width = widthSize;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = heightSize;
        } else {
            //Be whatever you want
            height = heightSize;
        }
        setMeasuredDimension(width, height);
    }


    private void setupTopCard() {
        final View child = getChildAt(getChildCount() - 1);

        //this calculation is to get the correct position in the adapter of the current top card
        //the card position on setup top card is currently always the bottom card in the view
        //at any given time.
        int initialX = paddingLeft;
        int initialY = paddingTop;

        if (child != null) {
            //make sure we have a card

            swipeListener = new SwipeListener(child, new SwipeListener.SwipeCallback() {
                @Override
                public void cardSwipedLeft() {
                    int positionInAdapter = nextAdapterCard - getChildCount();
                    removeTopCard();
                    if (eventCallback != null) eventCallback.cardSwipedLeft(positionInAdapter);
                    addNextCard();
                }

                @Override
                public void cardSwipedRight() {
                    int positionInAdapter = nextAdapterCard - getChildCount();
                    removeTopCard();
                    addNextCard();
                    if (eventCallback != null) eventCallback.cardSwipedRight(positionInAdapter);
                }

            }, initialX, initialY, ROTATION_DEGREES, OPACITY_END);


            //if we specified these image resources, get the views and pass them to the swipe listener
            //for the sake of animating them
            View rightView = null;
            View leftView = null;
            if (!(rightImageResource == 0)) rightView = child.findViewById(rightImageResource);
            if (!(leftImageResource == 0)) leftView = child.findViewById(leftImageResource);
            swipeListener.setLeftView(leftView);
            swipeListener.setRightView(rightView);

            child.setOnTouchListener(swipeListener);
        }
    }

    public void setEventCallback(SwipeEventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }



    public void swipeTopCardLeft() {
        int childCount = getChildCount();
        if(childCount > 0){
            swipeListener.animateOffScreenLeft().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    swipeListener.checkCardForEvent();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    public void swipeTopCardRight() {
        int childCount = getChildCount();
        if(childCount > 0){
            swipeListener.animateOffScreenRight().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    swipeListener.checkCardForEvent();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    public void setPositionCallback(CardPositionCallback callback) {
        cardPosCallback = callback;
    }

    public void setLeftImage(int imageResource) {
        leftImageResource = imageResource;
    }

    public void setRightImage(int imageResource) {
        rightImageResource = imageResource;
    }

    public interface SwipeEventCallback {
        //returning the object position in the adapter
        void cardSwipedLeft(int position);

        void cardSwipedRight(int position);

        void cardsDepleted();
    }

    public interface CardPositionCallback {
        void xPos(Float x);

        void yPos(Float y);
    }
}
