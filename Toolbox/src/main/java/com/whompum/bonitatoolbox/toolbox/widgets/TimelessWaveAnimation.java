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
import android.view.View;
import android.widget.ImageView;

import com.whompum.bonitatoolbox.toolbox.R;

/**
 * Created by bryan on 12/6/2017.
 */

public class TimelessWaveAnimation extends View {

    public static final String BG_IAE = "Background must be an instance of AnimationDrawable";

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
        final Drawable background = getBackground();


        if( !(background instanceof AnimationDrawable) )
            throw new IllegalArgumentException("Background mus");

        else
            ((AnimationDrawable)background).start();
    }

    public void stop(){
        final Drawable background = getBackground();


        if( !(background instanceof AnimationDrawable) )
            throw new IllegalArgumentException("Background mus");

        else
            ((AnimationDrawable)background).stop();
    }

}
