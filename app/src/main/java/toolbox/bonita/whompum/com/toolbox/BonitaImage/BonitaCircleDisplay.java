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

package toolbox.bonita.whompum.com.toolbox.BonitaImage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import toolbox.bonita.whompum.com.toolbox.R;

/**
 * Created by bryan on 11/28/2017.
 */

public class BonitaCircleDisplay extends View {

    public static final String DEBUG = "BonitaCircleDisplay";

    public static final String SOURCE_IAE = "The source can only be a bitmap!";

    @DrawableRes
    public static final int DEFAULT_IMAGE_RES = R.drawable.bitmap_default_image;

    //The wantedSize for the view to be
    private int wantedSize = Integer.MIN_VALUE;

    //Simple wrapper for the background source image since its given to us as a BitmapDrawable
    private BitmapDrawable sourceDrawable;

    private Rect sourceRect = new Rect();

    private BonitaShapeDrawable background;

    private OvalShape backgroundShape = new OvalShape();

    private int maxWidth = Integer.MAX_VALUE;



    public BonitaCircleDisplay(final Context context){
        super(context);
    }

    public BonitaCircleDisplay(final Context context, final AttributeSet set){
        super(context, set);
        initialize(context.obtainStyledAttributes(set, R.styleable.BonitaCircleDisplay));
    }


    private void initialize(final TypedArray array){

        for(int i = 0; i < array.getIndexCount(); i++){

            final int attr = array.getIndex(i);

            if(attr == R.styleable.BonitaCircleDisplay_maxWidth)
                this.maxWidth = array.getDimensionPixelSize(attr, Integer.MAX_VALUE);

            if(attr == R.styleable.BonitaCircleDisplay_size)
                this.wantedSize = array.getDimensionPixelSize(attr, Integer.MIN_VALUE);

            if(attr == R.styleable.BonitaCircleDisplay_source) {
                final Drawable tempSrc = array.getDrawable(attr);

                if(tempSrc == null)
                    this.background = getDefaultBackground();
                else if( !(tempSrc instanceof BonitaShapeDrawable) )
                    throw new IllegalArgumentException(SOURCE_IAE);
                else
                    this.background = (BonitaShapeDrawable) tempSrc;
            }

        }

        this.background = new BonitaShapeDrawable(backgroundShape);

        if(Build.VERSION.SDK_INT >= 16)
        setBackground(background);

        else
        setBackgroundDrawable(background);


    }

    /**
     *
     * RULES
     * Be the size of the image.
     * If size of image is greater than maxWidth
     * be size of maxWidth;
     * If w | h are specified, be smallest of the two
     * If smallest side is width, and its bigger than maxWidth, be maxWidth;
     *
     *
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int viewSize = 0;

    /**
        if(wantedSize != Integer.MIN_VALUE){
            viewSize = (wantedSize >= maxWidth) ? maxWidth : wantedSize ;
            setMeasuredDimension(viewSize, viewSize);
        }
    */


    final int wMode = MeasureSpec.getMode(widthMeasureSpec);
    final int hMode = MeasureSpec.getMode(heightMeasureSpec);

    final int wSize = MeasureSpec.getSize(widthMeasureSpec);
    final int hSize = MeasureSpec.getSize(heightMeasureSpec);


    if(wMode == MeasureSpec.EXACTLY | hMode == MeasureSpec.EXACTLY){
        viewSize = Math.min(wSize, hSize);
    }

    if(wMode == MeasureSpec.EXACTLY & hMode == MeasureSpec.EXACTLY){
        viewSize = Math.min(wSize, hSize);
    }


    setMeasuredDimension(viewSize, viewSize);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(255, 25,20,18);
    }

    //Simply utility method to fetch the background drawable with schims
    private BonitaShapeDrawable getDefaultBackground(){

        BonitaShapeDrawable drawable = null;

        if(Build.VERSION.SDK_INT >= 21)
            drawable = (BonitaShapeDrawable) getResources().getDrawable(DEFAULT_IMAGE_RES, null);

        else
            drawable = (BonitaShapeDrawable) getResources().getDrawable(DEFAULT_IMAGE_RES);

    return drawable;
    }




}














