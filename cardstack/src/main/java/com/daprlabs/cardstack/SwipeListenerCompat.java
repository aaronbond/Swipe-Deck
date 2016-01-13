package com.daprlabs.cardstack;


import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.nineoldandroids.animation.*;
import com.nineoldandroids.view.ViewHelper;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;


/**
 * Created by aaron on 14/01/2016.
 */
public class SwipeListenerCompat implements View.OnTouchListener {

    private float ROTATION_DEGREES = 15f;
    float OPACITY_END = 0.33f;
    private float initialX;
    private float initialY;

    private int mActivePointerId;
    private float initialXPress;
    private float initialYPress;
    private ViewGroup parent;
    private float parentWidth;
    private int paddingLeft;

    private View card;
    SwipeCallback callback;
    private boolean deactivated;
    private View rightView;
    private View leftView;


    public SwipeListenerCompat(View card, SwipeCallback callback, float initialX, float initialY) {
        this.card = card;
        this.initialX = initialX;
        this.initialY = initialY;
        this.callback = callback;
        this.parent = (ViewGroup) card.getParent();
        this.parentWidth = parent.getWidth();
    }

    public SwipeListenerCompat(View card, final SwipeCallback callback, float initialX, float initialY, float rotation, float opacityEnd) {
        this.card = card;
        this.initialX = initialX;
        this.initialY = initialY;
        this.callback = callback;
        this.parent = (ViewGroup) card.getParent();
        this.parentWidth = parent.getWidth();
        this.ROTATION_DEGREES = rotation;
        this.OPACITY_END = opacityEnd;
        this.paddingLeft = ((ViewGroup) card.getParent()).getPaddingLeft();
    }


    private boolean click = true;
    private float cardPositionX = 0;
    private float cardPositionY = 0;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (deactivated) return false;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                click = true;
                //gesture has begun
                float x;
                float y;

                mActivePointerId = event.getPointerId(0);

                x = event.getX();
                y = event.getY();

                initialXPress = x;
                initialYPress = y;
                break;

            case MotionEvent.ACTION_MOVE:
                //gesture is in progress
                click = false;
                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                final float xMove = event.getX(pointerIndex);
                final float yMove = event.getY(pointerIndex);

                //calculate distance moved
                final float dx = xMove - initialXPress;
                final float dy = yMove - initialYPress;
                //calc rotation here

                float posX = card.getLeft() + dx;
                float posY = card.getTop() + dy;
                //card.setRotation
                float distObjectX = posX - initialX;
                float rotation = ROTATION_DEGREES * 2.f * distObjectX / parentWidth;

                animate(card).setDuration(0).x(posX).y(posY).rotation(rotation).start();

                if (rightView != null && leftView != null){
                    //set alpha of left and right image
                    float alpha = (((posX - paddingLeft) / (parentWidth * OPACITY_END)));
                    //animate(rightView).setDuration(0).alpha(alpha);
                    ViewHelper.setAlpha(rightView, Math.max(alpha, 0));
                    //animate(leftView).setDuration(0).alpha(-alpha);
                    ViewHelper.setAlpha(leftView, Math.max(-alpha, 0));
                }
                cardPositionX = posX;
                cardPositionY = posY;

                break;

            case MotionEvent.ACTION_UP:
                //gesture has finished
                //check to see if card has moved beyond the left or right bounds or reset
                //card position
                checkCardForEvent();
                //check if this is a click event and then perform a click
                //this is a workaround, android doesn't play well with multiple listeners
                if (click) v.performClick();
                break;

            default:
                return false;
        }
        return true;
    }

    public void checkCardForEvent() {

        if (cardBeyondLeftBorder()) {
            animateOffScreenLeft(card, 150);

            this.deactivated = true;
        } else if (cardBeyondRightBorder()) {
            animateOffScreenRight(card, 150);

            this.deactivated = true;
        } else {
            resetCardPosition(card);
        }
    }

    private boolean cardBeyondLeftBorder() {
        //check if cards middle is beyond the left quarter of the screen
        return (cardPositionX + (card.getWidth() / 2) < (parent.getWidth() / 4.f));
    }

    private boolean cardBeyondRightBorder() {
        //check if card middle is beyond the right quarter of the screen
        return (cardPositionX + (card.getWidth() / 2) > ((parent.getWidth() / 4.f) * 3));
    }

    private void resetCardPosition(View v) {
        if(rightView!=null)ViewCompat.setAlpha(rightView,0);
        if(leftView!=null)ViewCompat.setAlpha(leftView,0);
        animate(v).x(initialX).y(initialY).rotation(0).setDuration(100);

        if (rightView != null && leftView != null){
            //animate(rightView).setDuration(0).alpha(alpha);
            ViewHelper.setAlpha(rightView, 0);
            //animate(leftView).setDuration(0).alpha(-alpha);
            ViewHelper.setAlpha(leftView, 0);
        }

    }

    public void animateOffScreenLeft(View view, int duration) {
        animate(view).setDuration(duration).x(-(parent.getWidth()))
                .y(0)
                .rotation(-30).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                callback.cardSwipedLeft();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void animateOffScreenRight(View v, int duration) {
        animate(v).setDuration(duration)
                .x(parent.getWidth() * 2)
                .y(0)
                .rotation(30).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                callback.cardSwipedRight();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void setRightView(View image) {
        this.rightView = image;
    }

    public void setLeftView(View image) {
        this.leftView = image;
    }

    public interface SwipeCallback {
        void cardSwipedLeft();
        void cardSwipedRight();
    }
}