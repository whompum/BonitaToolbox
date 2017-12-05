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
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;

import com.whompum.bonitatoolbox.toolbox.R;
import com.whompum.bonitatoolbox.toolbox.Utils.ImageCrop;
import com.whompum.bonitatoolbox.toolbox.widgets.BonitaShapeDrawable;

/**
 * Created by bryan on 11/28/2017.
 */

public class BonitaCircleDisplay extends View {

    public static final String DEBUG = "BonitaCircleDisplay";

    public static final String SOURCE_IAE = "The source can only be a bitmap!";

    private static final CropRegion DEF_CROP_REGION = CropRegion.CENTER;


    @DrawableRes
    public static final int DEFAULT_IMAGE_RES = R.drawable.bitmap_default_image;

    //The wantedSize for the view to be
    private int wantedSize = Integer.MIN_VALUE;

    //Simple wrapper for the background source image since its given to us as a BitmapDrawable
    private BitmapDrawable sourceDrawable;
    private Bitmap scaledSource = null;
    private AspectRatio sourceAspectRatio;

    private CropRegion cropRegion = DEF_CROP_REGION;

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

        Drawable tempSrc = null;

        for(int i = 0; i < array.getIndexCount(); i++){

            final int attr = array.getIndex(i);

            if(attr == R.styleable.BonitaCircleDisplay_maxSize)
                this.maxWidth = array.getDimensionPixelSize(attr, Integer.MAX_VALUE);

            if(attr == R.styleable.BonitaCircleDisplay_size)
                this.wantedSize = array.getDimensionPixelSize(attr, Integer.MIN_VALUE);

            if(attr == R.styleable.BonitaCircleDisplay_circleSourceImage) {
                tempSrc = array.getDrawable(attr);
            }

            if(attr == R.styleable.BonitaCircleDisplay_cropRegion) //One below is CropRegion.CENTER
                this.cropRegion = resolveCropRegion(array.getInt(attr, 1));

        }

        if(tempSrc == null)
            this.sourceDrawable = findDefaultSourceImage();

        else if( !(tempSrc instanceof BitmapDrawable) )
            throw new IllegalArgumentException(SOURCE_IAE);

        else
            this.sourceDrawable = (BitmapDrawable) tempSrc;


        this.background = new BonitaShapeDrawable(backgroundShape);

       setBackground(background);

    }

    /**
     *
     * RULES
     * If EXACTLY: Be smallest of maxWidth / width / height (HIGHEST PRIORITY)
     *
     * IF SIZE attribute specified: Set smaller of that and maxWidth; (SECOND PRIORITY)
     *
     * set smallest of maxWidth imageWidth and imageHeight (LAST PRIORITY)
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        final int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

        final int widthSource = sourceDrawable.getBitmap().getWidth();
        final int heightSource = sourceDrawable.getBitmap().getHeight();


        int viewSize = Math.min(maxWidth, Math.min(widthSource, heightSource));

        if(wantedSize != Integer.MIN_VALUE){
            viewSize = Math.min(maxWidth, wantedSize);
        }

        if(widthMode == MeasureSpec.EXACTLY | heightMode == MeasureSpec.EXACTLY){
            viewSize = Math.min(maxWidth, Math.min(widthSpec, heightSpec));
        }

        setMeasuredDimension(viewSize, viewSize);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final Bitmap scaledBitmap = resizeBitmap(sourceDrawable.getBitmap());

        background.setShaderFactory(new BonitaShapeDrawable.BonitaShaderFactory(scaledBitmap, null));
    }


    /**
     *
     *
     * @param src the background image to scale
     * @return the scaled bitmap, or cropped bitmap.
     */
    private Bitmap resizeBitmap(final Bitmap src){

         Bitmap scaledSrc = null;

         final float imageAspectRatio = getAspectValue(src);

         if(imageAspectRatio == 1.0F) // A simple matter of scaling up/down
            scaledSrc = Bitmap.createScaledBitmap(src, getWidth(), getHeight(), true);

         else {
             scaledSrc = Bitmap.createScaledBitmap(scaleForUnevenAspect(src), getWidth(), getHeight(), true);

         }


    return scaledSrc;
    }

    //Crops bitmap to perfect ImageAspectRatio then calls resizeBitmap which will perfectly scale to fit view boundaries
    private Bitmap scaleForUnevenAspect(final Bitmap src){

        Rect cropRect = getCropRegion(src, cropRegion);

        final Bitmap croppedBitmap = ImageCrop.crop(src, cropRect, Bitmap.Config.ARGB_8888);

        return Bitmap.createScaledBitmap(croppedBitmap, 10,10, true);


    //return resizeBitmap(croppedBitmap);
    }

    //returns the region-rect to crop from the src image
    private Rect getCropRegion(final Bitmap src, final CropRegion cropRegion){

        final Rect cropRect = new Rect();

        final AspectRatio aspectRatio = getAspectRatio(src);


        //TODO figure out why cropping my views stretches the living fuck out of them.
        if(aspectRatio == AspectRatio.TALL){

          final int smallestSize = src.getWidth();

           if(cropRegion == CropRegion.START)
               cropRect.set(0,0, smallestSize, smallestSize);

           else if(cropRegion == CropRegion.CENTER){

               final int top = (src.getHeight() / 2) - (smallestSize/2);
               final int bottom = top + (smallestSize);

               cropRect.set(0, top,smallestSize, bottom);
           }
           else if(cropRegion == CropRegion.END){
               cropRect.set(0, src.getHeight() - smallestSize, smallestSize, src.getHeight() );
           }

        } else if(aspectRatio == AspectRatio.WIDE){
            //TODO add support for a wide aspect ratio image;
        }

    return cropRect;
    }


    private float getAspectValue(final Bitmap src){
        final int Xsource = src.getWidth();
        final int Ysource = src.getHeight();

    return Math.min(Xsource, Ysource) / Math.max(Xsource, Ysource);
    }

    //Simple utility method to fetch the background drawable with schims
    private BitmapDrawable findDefaultSourceImage(){

        BitmapDrawable drawable = null;

        if(Build.VERSION.SDK_INT >= 21)
            drawable = (BitmapDrawable) getResources().getDrawable(DEFAULT_IMAGE_RES, null);

        else
            drawable = (BitmapDrawable) getResources().getDrawable(DEFAULT_IMAGE_RES);

    return drawable;
    }


    private AspectRatio getAspectRatio(final Bitmap bmp){

        AspectRatio ratio = null;

        final int Wbmp = bmp.getWidth();
        final int Hbmp = bmp.getHeight();

        if(Hbmp > Wbmp)
            ratio = AspectRatio.TALL;
        else if(Wbmp > Hbmp)
            ratio = AspectRatio.WIDE;
        else if(Wbmp == Hbmp)
            ratio= AspectRatio.EQUAL;

    return ratio;
    }


    public CropRegion resolveCropRegion(final int value){

        if(value == 0)
            return CropRegion.START;

        else if (value == 2)
            return CropRegion.END;

    return CropRegion.CENTER;
    }


    public enum AspectRatio {
        TALL,
        WIDE,
        EQUAL
    }

    public enum CropRegion{
        START,
        CENTER,
        END
    }



}














