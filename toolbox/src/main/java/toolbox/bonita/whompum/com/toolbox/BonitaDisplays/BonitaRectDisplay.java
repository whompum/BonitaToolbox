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

package toolbox.bonita.whompum.com.toolbox.BonitaDisplays;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Debug;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import toolbox.bonita.whompum.com.toolbox.R;
import toolbox.bonita.whompum.com.toolbox.Utils.ImageCrop;
import toolbox.bonita.whompum.com.toolbox.Utils.LogTags;


/**
 * To future readers who think of me as dumb for writing two separate classes with almost exact near commonality:
 * There is a reason, and it was a stressfull reason... Please Do not ask.
 *
 *
 * TODOS
 * TODO: Document rough pieces of code :O
 * TODO: Fix the Shader stretching my images
 * TODO: In cropping methods, set a Matrix to use instead, then in onSizeChange simply attach the Matrix to the shader
 * TODO: Add accessibility support (contentDescription etc)
 * TODO: Add touch aninmations like ripple support
 * TODO: Adjust onMeasure to be more dynamic. E.G. W=wrap, H=20dp (with no problems)
 * TODO: Add support for image filtering
 * TODO: Add more ways to crop/scale the image.
 * TODO: Maybe add some support for autosizing?? E.G. it preserves its AspectRatio, then lists a number of sizes it can be displayed @ on
 * TODO: (CONT) the current screen. E.G. if its aspect ratio is 2/4, then it can display @ 150/300, 75/150, 300/600, 10/20, etc etec etc
 *
 */

public class BonitaRectDisplay extends View {

    public static final String DEBUG = "BonitaRectDisplay";

    @DrawableRes
    public static final int DEF_BACKGROUND = R.drawable.bitmap_default_image;

    //IllegalArgumentException thrown with this message IF the source attribute ! references a Bitmap
    public static final String SOURCE_IAE = "The source image must be a Bitmap";


    //Background Image of the view. Overrides the view#background attribute
    private BonitaShapeDrawable background;

    //Max width/Height of this view
    private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;

    private Bitmap rawSource = null;
    private float sourceRatioValue = 0F; // Aspect Ratio of the image

    private Shape backgroundShape = null;



    private float viewRatioValue = 0F;
    private AspectRatio viewAspect = null;



    private Rect cropRect = new Rect();
    private Bitmap _displayImage = null;



    public BonitaRectDisplay(final Context context){
        super(context);
    }

    public BonitaRectDisplay(final Context context, final AttributeSet set){
        super(context, set);
        initialize(context.obtainStyledAttributes(set, R.styleable.BonitaRectDisplay));
    }

    /**
     *
     *
     * Initializes / preloads / allocates any resources i'll need for this view
     *
     *
     * @param array holds the user-defined states of this object
     */
    private void initialize(final TypedArray array){

        Drawable tempSrc = null;

        int leftTop = 0;
        int rightTop = 0;
        int leftBottom = 0;
        int rightBottom = 0;

        for(int i = 0; i < array.getIndexCount(); i++){

            final int attr = array.getIndex(i);

            if(attr == R.styleable.BonitaRectDisplay_maxWidth)
                this.maxWidth = array.getDimensionPixelSize(attr, Integer.MAX_VALUE);

            if(attr == R.styleable.BonitaRectDisplay_maxHeight)
                this.maxHeight = array.getDimensionPixelSize(attr, Integer.MAX_VALUE);

            if(attr == R.styleable.BonitaRectDisplay_rectSourceImage)
                tempSrc = array.getDrawable(attr);

            if(attr == R.styleable.BonitaRectDisplay_LEFT_TOP_RADII)
                leftTop = array.getDimensionPixelSize(attr, 0);

            if(attr == R.styleable.BonitaRectDisplay_RIGHT_TOP_RADII)
                rightTop = array.getDimensionPixelSize(attr, 0);

            if(attr == R.styleable.BonitaRectDisplay_LEFT_BOTTOM_RADII)
                leftBottom = array.getDimensionPixelSize(attr, 0);

            if(attr == R.styleable.BonitaRectDisplay_RIGHT_BOTTOM_RADII)
                rightBottom = array.getDimensionPixelSize(attr, 0);

        }

        if(tempSrc == null)
            this.rawSource = findDefaultSourceImage().getBitmap();

        else if( !(tempSrc instanceof BitmapDrawable) )
            throw new IllegalArgumentException(SOURCE_IAE);

        else
            this.rawSource = ((BitmapDrawable) tempSrc).getBitmap();

        this.sourceRatioValue = getAspectRatioValue(rawSource.getWidth(), rawSource.getHeight());

        final RectFactory.GeometryAssignments assignments = new RectFactory.GeometryAssignments(leftTop, rightTop, leftBottom, rightBottom);

        this.backgroundShape = new RectFactory(assignments).makeRect();

        this.background = new BonitaShapeDrawable(backgroundShape);

        if(Build.VERSION.SDK_INT >= 16)
            setBackground(background);
        else
            setBackgroundDrawable(background);

    }


    /**
     * If EXACTLY, be that size
     * Else if WRAP, be the views size
     *
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int measuredWidth = 0;
        int measuredHeight = 0;

        final int sourceWidth = rawSource.getWidth();
        final int sourceHeight = rawSource.getHeight();

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        if(widthMode == MeasureSpec.EXACTLY)
            measuredWidth = Math.min(width, maxWidth);
        if(heightMode == MeasureSpec.EXACTLY)
            measuredHeight = Math.min(height, maxHeight);

        if(widthMode == MeasureSpec.AT_MOST | widthMode == MeasureSpec.UNSPECIFIED)
            measuredWidth =  Math.min(Math.min(sourceWidth, maxWidth), width);

        if(heightMode == MeasureSpec.AT_MOST | widthMode == MeasureSpec.UNSPECIFIED)
            measuredHeight = Math.min(Math.min(sourceHeight, maxHeight), height);


        setMeasuredDimension(measuredWidth, measuredHeight);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        this.viewRatioValue = getAspectRatioValue(w,h);
        this.viewAspect = getAspectRatio(w, h);

        this.sourceRatioValue = getAspectRatioValue(rawSource.getWidth(), rawSource.getHeight());

        _displayImage = rawSource;

        if(!doBoundsMatch(rawSource.getWidth(), w, rawSource.getHeight(), h)) //Should really determine the best scaleType to use here :)
            _displayImage = Bitmap.createScaledBitmap(cropCenterForAspectRatio(rawSource), w, h, true);

        if( !doBoundsMatch(_displayImage.getWidth(), w, _displayImage.getHeight(), h))
            Log.i(LogTags.ISSUES, "BOUNDS OF THE SCALED IMAGE AND VIEW BOUNDS DO NOT MATCH");

        else
            Log.i(LogTags.ISSUES, "BOUNDS OF THE SCALED IMAGE AND VIEW BOUNDS MATCH");

        this.background.setShaderFactory(new BonitaShapeDrawable.BonitaShaderFactory(_displayImage, null));

        Log.i(LogTags.ISSUES, "onSizeChanged()#BonitaRectDisplay" + " What's my Memory?");
    }


    private Bitmap cropCenterForAspectRatio(final Bitmap src){

        //The view bounds is square, so cropping is relatively easy
        if(viewAspect == AspectRatio.EQUAL)
            return cropSquare(src);



        final Rect imageBounds = new Rect(0,0,src.getWidth(), src.getHeight());
        /**
         * scale down crop rect before anything so we can simply scale up. Makes things easier but not necessary
         * We scale smallest side to 2 (for centering purposes. E.G. 1px on one side of a value, and 1px on the other
         * We then scale the larger side to its aspect ratio
         */
        if(viewAspect == AspectRatio.TALL)
            cropRect.set(0,0, 2, (int)(2/viewRatioValue));

        else if(viewAspect == AspectRatio.WIDE)
            cropRect.set(0,0,(int)(2/viewRatioValue), 2);

        /**
         * Now we check which dimension of the scaled down view rect (cropRect)
         * is the closest to match the image bounds.
         */
        final boolean isWidthClosestMatch = ( (imageBounds.width() - cropRect.width()) <
                                              (imageBounds.height() - cropRect.height()) );

        int scaledWidth = 0;
        int scaledHeight = 0;

        /**
         * Set the closest matching bounds to its respective Image boundaries
         * Scale the other side by the aspect ratio.
         * E.G. if width is closer, then we set width to image width
         * And then scale height. If TALL, height is larger than width (width / aspectRatio)
         * If WIDE, height is smaller (width * aspectRatio)
         */
        if(isWidthClosestMatch){
            Log.i(DEBUG, "WIDTH IS CLOSEST");
            scaledWidth = imageBounds.width();
            if(viewAspect == AspectRatio.TALL)
                scaledHeight = (int)(scaledWidth / viewRatioValue);
            else if(viewAspect == AspectRatio.WIDE)
                scaledHeight = (int)(scaledWidth * viewRatioValue);
        }else{
            Log.i(DEBUG, "HEIGHT IS CLOSEST");
            scaledHeight = imageBounds.height();
            if(viewAspect == AspectRatio.TALL)
                scaledWidth = (int)(scaledHeight * viewRatioValue);
            else if(viewAspect == AspectRatio.WIDE)
                scaledWidth = (int)(scaledHeight / viewRatioValue);
        }

        cropRect.set(0,0,scaledWidth, scaledHeight);

    return ImageCrop.crop(src, cropRect, Bitmap.Config.ARGB_8888);
    }


    /**
     *
     * FIRST find src images smallest side
     * SECOND form a perct square out of that smallest side.
     * THIRD: *set region if i want to*
     * FOURTH: CROP it like its hot:)
     *
     * @return a Bitmap cropped in a perfect square with the size of the Src's smallest side
     */
    private Bitmap cropSquare(final Bitmap src){

        Log.i(DEBUG, "CROPPING SQUARE");

        final int size = Math.min(src.getWidth(), src.getHeight()); //What if larger than maxWi dth/Height

        cropRect.set(0,0,size, size);

        return ImageCrop.crop(src, cropRect, Bitmap.Config.ARGB_8888);
    }


    public Bitmap getRawSource(){
        return this.rawSource;
    }

    public Bitmap getScaledSource(){
        return _displayImage;
    }





    public boolean doBoundsMatch(final Rect rectOne, final Rect rectTwo){

    return doBoundsMatch(rectOne.width(), rectTwo.width(), rectOne.height(), rectTwo.height());
    }
    public boolean doBoundsMatch(final int wOne, final int wTwo, final int hOne, final int hTwo){

        if(wOne != wTwo)
            return false;
        else if(hOne != hTwo)
            return false;

     return true;
    }

    private BitmapDrawable findDefaultSourceImage(){
        BitmapDrawable drawable = null;

        if(Build.VERSION.SDK_INT >= 21)
            drawable = (BitmapDrawable) getResources().getDrawable(DEF_BACKGROUND, null);

        else
            drawable = (BitmapDrawable) getResources().getDrawable(DEF_BACKGROUND);

        return drawable;
    }


    public float getAspectRatioValue(final Rect rect){
        return getAspectRatioValue((float)rect.width(), (float)rect.height());
    }
    public float getAspectRatioValue(final float w, final float h){

        Log.i(DEBUG, "getAspectRatioValue()#BonitaRectDisplay");
        Log.i(DEBUG, "WIDTH: " + String.valueOf(w));
        Log.i(DEBUG, "HEIGHT: " + String.valueOf(h));



        return Math.min(w,h) / Math.max(w,h);
    }



    public AspectRatio getAspectRatio(final Rect rect){
        return getAspectRatio(rect.width(), rect.height());
    }
    public AspectRatio getAspectRatio(final int w, final int h){

        if(w > h)
            return AspectRatio.WIDE;

        else if(h > w)
            return AspectRatio.TALL;


    return AspectRatio.EQUAL;
    }



    public enum AspectRatio {
        TALL,
        WIDE,
        EQUAL
    }



}




















