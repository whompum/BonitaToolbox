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
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.whompum.bonitatoolbox.toolbox.R;
import com.whompum.bonitatoolbox.toolbox.Utils.LogTags;


/**
 * Created by bryan on 12/2/2017.
 */

public class FunPopup extends PopupWindow {

    public static final String DEBUG = "FunPopup";

    public static final String MSG_IAE = "The TextView Has No Text :0";

    public static final long DEF_DURATION = 4000L;
    public static final long DEF_FADE_DURATION = 1000L;

    public static final int SIZE = ViewGroup.LayoutParams.WRAP_CONTENT;

    @LayoutRes
    public static final int CONTENT_ID = R.layout.fun_popup_textview;


    private long duration = DEF_DURATION;
    private long fadeDuration = DEF_FADE_DURATION;


    private Handler fadeHandler = null;
    private Runnable fadeRunnable = null;

    private boolean hasStarted = false;
    private boolean hasEnded = false;


    /**
     * Bare-Bones constructor
     *
     * @param context inflates TextView
     * @param msg CAN NOT BE NULL else the TextView will have no message to it
     */
    private FunPopup(final Context context, @NonNull final CharSequence msg){
        this(  (ViewGroup)LayoutInflater.from(context).inflate(CONTENT_ID, null, false)  , msg);
    }


    /**
     * Instantiate the Popup with an already styled TextView
     *
     * @param popupContent The textview
     * @param msg The messasge the display in the TextView; Be aware there exist a contract
     *            If the client passes null as the msg variable, that is legal PROVIDED
     *            the TextView already has text in it. Else if getText() and msg are null
     *            an exception will be thrown
     */
    private FunPopup(final ViewGroup popupContent, @Nullable final CharSequence msg){
        super(popupContent, SIZE, SIZE);

        final TextView content = popupContent.findViewById(R.id.popupTextView);


        if(msg != null)
            content.setText(msg);
        else
            throw new IllegalArgumentException(MSG_IAE);


        setOutsideTouchable(false);
        setTouchable(false);
        setFocusable(false);

        if(Build.VERSION.SDK_INT >= 22)
        setAttachedInDecor(true);

        setBackgroundDrawable(null);

    }



    /**
     * Change the background color of the View
     *
     * @param color the color to set to the background
     */
    public void changeBackgroundColor(final int color){
       if(getContentView()!=null)
           getContentView().setBackgroundColor(color);
    }

    /**
     * LIFECYCLE
     * Starts the fade animation, but with a delay of duration.
     * Hands the Fade object a fadeDuration that will say how long to run
     * after duration is delayed.
     */
    protected void onStart(){

      if(hasEnded) //Used when onKill() is called, but i want to re-show this
        bringMeBackToLife();

      this.fadeHandler =  new Handler(dad);
      this.fadeRunnable = new Fade(fadeDuration, fadeHandler);
           fadeHandler.postDelayed(fadeRunnable, duration);

           hasStarted = true;
           hasEnded = false;
    }

    /**
     * LIFECYCLE
     *
     * TODO: FIX. DOESN'T WORK RIGHT! onRestart()#FunPopup.class
     */
    protected void onRestart(){
        bringMeBackToLife();
        onStart();
    }


    /**
     * LIFECYCLE
     */
    protected void onKill(){
        endFade();
        hasEnded = true;
        hasStarted = false;
        dismiss();
    }


    private void bringMeBackToLife(){
        if(getContentView()!= null)
            getContentView().setAlpha(Fade.VALUE);
    }

    private void endFade(){
        this.fadeHandler.removeCallbacks(fadeRunnable);
    }



    public void haveMoreFun(){
        onRestart();
    }

    public void killFun(){
        onKill();
    }



    public FunPopup setLingerDuration(final long duration){

        if(hasStarted)
            Log.i(LogTags.NOTIFICATIONS, "FunPopup Already Started \n" + "" +
                    "Changes will take effect if restarted.");


        this.duration = duration;
    return this;
    }

    public FunPopup setFadeDuration(final long fadeDuration){

        if(hasStarted)
            Log.i(LogTags.NOTIFICATIONS, "FunPopup already started \n" + "" +
                    "Changes will take effect if restarted.");

        this.fadeDuration = fadeDuration;
    return this;
    }

    public void haveFun(final View parent){
        this.haveFun(parent, Gravity.NO_GRAVITY);
    }

    public void haveFun(final View parent, int gravity, int xOffset, int yOffset){

        final int xLoc = (int)parent.getX() + xOffset;
        final int yLoc = (int)parent.getY() + yOffset;

        super.showAtLocation(parent, gravity, xLoc, yLoc);
        this.onStart();
    }

    public void haveFun(final View parent, int gravity){
        this.haveFun(parent, gravity, 0,0);
    }

    public void haveFun(final View parent, int xOffset, int yOffset){
        this.haveFun(parent, Gravity.NO_GRAVITY, xOffset, yOffset);
    }




    private final Handler.Callback dad = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            //Set the alpha value of content view to the compute view in the Handler
            FunPopup.this.getContentView().setAlpha((Float)message.obj);

            //Get the elapsed time (arg1) and check if equal to fadeDuration. If so, remove the runnable callback, and kill
            if(message.arg1 == fadeDuration){
                onKill();
                return true;
            }
            return true;
        }
    };


    /**
     * Barebones method to use this class. Just a String and a Context
     * and this class will take care of all inflation styling and whatnot
     *
     * @param context You know...
     * @param msg msessage displayed in the TextView
     * @return again... the popup...
     */
    public static FunPopup play(@NonNull final Context context, @NonNull final CharSequence msg){
        return new FunPopup(context, msg);
    }

    /**
     * Utility method of play(Context ,Charsequence) where the client wants a String resource instead
     *
     * @param context inflates the views
     * @param msgId R.string value
     * @return pop..... UP!
     */
    public static FunPopup play(@NonNull final Context context, @StringRes final int msgId){
        return play(context, resolveStringResource(context, msgId));
    }


    /**
     * Utility method to resolve a String resource and convert to a CharSequence :)
     * @param context used to generate the string from the R.* file
     * @param id the R resource id
     * @return  whatever value has 'id'
     */
    private static CharSequence resolveStringResource(@NonNull final Context context, @StringRes int id){
        return context.getString(id);
    }

    /**
     * Computes an alpha value to fades away the popup
     * @ specified intervals.
     * Then it posts itself as a delayed message to the handler where it is called, and computes another value
     */
    private static class Fade implements Runnable{

        public static final String DEBUG = "Fade";

        public static final long FADE_DURATION_DEF = 500;

        private static final long CYCLE = 50L; //Every 100 MS deliver an updated value to handler

        private static final float VALUE = 1.0F;

        private long fadeDuration = FADE_DURATION_DEF;
        private long elapsedTime = 0L;

        private Handler handler;

        private Float value = VALUE;

        /**
         *
         * @param fadeDuration How long the anim should last
         * @param handler The handler that started it
         */
        private Fade(final long fadeDuration, @NonNull  final Handler handler){
            if((fadeDuration % CYCLE != 0) ) //If not a perfect divisor for 100, don't set it
                Log.i(LogTags.NOTIFICATIONS,"Fade-IMPL @ class FunPopup: \n" +
                                                 "Fade Duration must be greater than a 1000 with 100 evenly dividing into it. \n" +
                                                 "Fade Duration: " + String.valueOf(fadeDuration));
            else
            this.fadeDuration = fadeDuration;


            this.handler = handler;
        }

        @Override
        public void run() {
            //Increment elapsed time by CYCLE
            elapsedTime += CYCLE;

            final Message message = new Message();

            computeValue();

            //Store alpha value in Float and hand to handler (Re-use an object in case this method is run a lot)
            message.obj = value;
            message.arg1 = (int)elapsedTime;

            handler.sendMessage(message);
            handler.postDelayed(this, CYCLE);
        }

        /**
         * Computes a value based on elapsed time / duration / and the alpha value
         *
         * VALUE is the percentage of the time left relative to duration,
         * to its own cieling and floor value
         *
         */
         private void computeValue(){
             value = (float)(fadeDuration - elapsedTime) / fadeDuration;
             Log.i(DEBUG, "FADE VALUE: " + String.valueOf(value));
         }

    }



}











