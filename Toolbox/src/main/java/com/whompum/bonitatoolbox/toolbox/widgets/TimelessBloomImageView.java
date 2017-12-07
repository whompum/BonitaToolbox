/*
 * Copyright 2017 Bryan A. Mills
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whompum.bonitatoolbox.toolbox.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.whompum.bonitatoolbox.toolbox.R;
import com.whompum.bonitatoolbox.toolbox.Utils.LogTags;

/**
 * Created by bryan on 12/6/2017.
 */

public class TimelessBloomImageView extends AppCompatImageView {

    public static final String DEBUG = "TimelessBloomImageView";

    //Default background to use (Is bloom)
    @DrawableRes
    private static final int BACKGROUND_ID = R.drawable.timeless_bloom_circle;

    //Static fields for Repaeat count and mode
    private static final int REPEAT_MODE = ValueAnimator.RESTART;
    private static final int REPEAT_COUNT = ValueAnimator.INFINITE;

    //Background.  Cached here for simplicity
    private Drawable background;

    //Pre-allocate
    private ValueAnimator backgroundAnimator = new ValueAnimator();

    //cache width/height for easy re-use
    private int backgroundWidth = 0;
    private int backgroundHeight = 0;

    //PreAllocate
    private Rect backgroundBounds = new Rect();

    //cache cX, cY for easy re-use
    private int cX = 0;
    private int cY = 0;

    public TimelessBloomImageView(final Context context){
        super(context);
        initBackground();
        initAnimator();
    }

    public TimelessBloomImageView(final Context context, final AttributeSet set){
        super(context, set);
        initAnimator();
        initBackground();
    }

    public TimelessBloomImageView(final Context context, final AttributeSet set, final int defStyleAttr){
        super(context, set, defStyleAttr);
        initAnimator();
        initBackground();
    }

    private void initAnimator(){
        backgroundAnimator.setFloatValues(0f, 1f);
        backgroundAnimator.setRepeatCount(REPEAT_COUNT);
        backgroundAnimator.setRepeatMode(REPEAT_MODE);
        backgroundAnimator.setDuration(1000L);
        backgroundAnimator.addUpdateListener(animatorUpdateListener);
    }

    private void initBackground(){

        if(Build.VERSION.SDK_INT >= 21)
            this.background = getContext().getDrawable(BACKGROUND_ID);

        else
            this.background = getContext().getResources().getDrawable(BACKGROUND_ID);

        setBackground(background);
    }

    /**
     * Client method. Starts the animation
     */
    public void start(){
        this.backgroundWidth = background.getBounds().width();
        this.backgroundHeight = background.getBounds().height();

        this.cX = backgroundWidth/2;
        this.cY = backgroundHeight/2;

        backgroundAnimator.start();
    }

    /**
     * Stops animation
     */
    public void stop(){
        backgroundAnimator.end();
    }

    /**
     * Pauses animation
     */
    public void pause(){
        if(isRunning())
        backgroundAnimator.pause();
    }

    /**
     * Client method to check if the animation is running
     * @return T/F if the animation is animating
     */
    public boolean isRunning(){
       return backgroundAnimator.isRunning();
    }

    /**
     * Resumes animatino
     */
    public void resume(){
        backgroundAnimator.resume();
    }


    /**
     *
     * Scales a re-used Rect drawable and sets as drawable bounds
     *
     * newWidth/newHeight are the percentage sizes after scale is applied. E.G. *oldSize* 100 / *scacle* 0.1 = *newSize* 10;
     *
     * Then to make the background bloom from the center of the view
     * we simply align it to the middle vs cX - value, cY - value, cX + value, cY + value'
     *
     * @param scale % of w/h to set the new bounds to
     */
    private void setBackgroundScale(final float scale){


        final int newWidth = (int)(backgroundWidth * scale);
        final int newHeight = (int)(backgroundHeight * scale);


        backgroundBounds.set(cX - newWidth/2,cY - newHeight/2,
                                 cX + newWidth/2, cY + newHeight/2);

        background.setBounds(backgroundBounds);
    }


    /**
     * Set the background ALPHA
     *
     * @param alpha float alpha value (0.0F - 1.0F) is casted to int
     */
    private void setBackgroundAlpha(final float alpha){
        background.setAlpha( (int)(255*alpha) );
    }


    /**
     * Timing engine for the scaling / fade
     */
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setBackgroundAlpha(valueAnimator.getAnimatedFraction());
                setBackgroundScale(valueAnimator.getAnimatedFraction());
        }
    };


}










