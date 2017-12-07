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

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.whompum.bonitatoolbox.toolbox.R;

/**
 * Created by bryan on 12/6/2017.
 */

public class TimelessWaveAnimation extends View {


    public static final String DEBUG = "TimelessWaveAnimation";


    {
        Log.i(DEBUG, "hi");
    }



    @DrawableRes
    private static final int BACKGROUND_ID = R.drawable.timeless_wave_animation;

    public TimelessWaveAnimation(final Context context){
        super(context);
        initBackground();
    }

    public TimelessWaveAnimation(final Context context, final AttributeSet set){
        super(context, set);
        initBackground();
    }

    public TimelessWaveAnimation(final Context context, final AttributeSet set, final int defStyle){
        super(context, set, defStyle);
        initBackground();
    }

    private void initBackground(){

        if(Build.VERSION.SDK_INT >= 21)
            setBackground(getContext().getDrawable(BACKGROUND_ID));
        else
            setBackground(getContext().getResources().getDrawable(BACKGROUND_ID));
    }

    public void start(){
          ((AnimationDrawable)getBackground()).start();
    }

    public void stop(){
        ((AnimationDrawable)getBackground()).stop();
    }


    /**
     * Since we add the drawable progmatically, the size of the view can't be determined
     * Thus if we pass wrap_content, it will take up all the size it can (we're extending view thats why)
     * To get around this, we simply fetch the wanted size of the Drawable,
     * and pass that to the super implementation along with the clients LayoutParams mode
     * passing along these values is better than setting them ourselves because we can
     * let the view handle padding and what not
     *
     * @param widthMeasureSpec holds the LayoutParams mode we wanted
     * @param heightMeasureSpec holds the LayoutParams mode we wanted
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int width = getBackground().getIntrinsicWidth();
        final int height = getBackground().getIntrinsicHeight();

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, widthMode), MeasureSpec.makeMeasureSpec(height, heightMode));

    }
}
