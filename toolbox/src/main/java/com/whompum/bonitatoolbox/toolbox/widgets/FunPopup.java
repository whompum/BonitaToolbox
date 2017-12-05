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

package toolbox.bonita.whompum.app.toolbox.BonitaDisplays.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import toolbox.bonita.whompum.app.toolbox.BonitaDisplays.Utils.LogTags;
import toolbox.bonita.whompum.app.toolbox.R;

/**
 * Created by bryan on 12/2/2017.
 */

public class FunPopup extends PopupWindow {

    public static final String DEBUG = "FunPopup";

    public static final String MSG_IAE = "The TextView Has No Text :0";

    public static final long DEF_DURATION = 5000L;
    public static final long DEF_FADE_DURATION = 1000L;

    public static final int SIZE = ViewGroup.LayoutParams.WRAP_CONTENT;

    @StyleRes
    public static final int DEF_STYLE = R.style.ToolboxStyle; //CHANGE

    @DrawableRes
    public static final int BACKGROUND = R.drawable.oval_shadow;

    private long duration = DEF_DURATION;
    private long fadeDuration = DEF_FADE_DURATION;


    private Handler fadeHandler = null;
    private Runnable fadeRunnable = null;

    private boolean hasStarted = false;

    /**
     * Bare-Bones constructor
     *
     * @param context inflates TextView
     * @param msg CAN NOT BE NULL else the TextView will have no message to it
     */
    private FunPopup(final Context context, @NonNull final CharSequence msg){
        this(context, DEF_STYLE, msg);
    }


    /**
     *
     * Called when we're wanting to make a pure TextView in code
     *
     * @param context Used to inflate the TextView
     * @param styleRes styling resource identification
     * @param msg Message of this popup. CAN be null because text may have been specified in the styleRes resource
     */
    private FunPopup(final Context context, @StyleRes final int styleRes, @Nullable final CharSequence msg) {
        this(new TextView(context, null, (styleRes == 0 ) ? DEF_STYLE : styleRes), msg);
    }

    /**
     * Instantiate the Popup with an already styled TextView
     *
     * @param popup The textview
     * @param msg The messasge the display in the TextView; Be aware there exist a contract
     *            If the client passes null as the msg variable, that is legal PROVIDED
     *            the TextView already has text in it. Else if getText() and msg are null
     *            an exception will be thrown
     */
    private FunPopup(final TextView popup, @Nullable final CharSequence msg){
        super(popup);


        if(popup.getText() == null) //The TextView must have Text before being set as content view
            if(msg != null)
                popup.setText(msg);
            else
                throw new IllegalArgumentException(MSG_IAE);

        popup.setBackground(fetchDefaultBackground(popup.getContext()));


        popup.setLayoutParams(new ViewGroup.LayoutParams(SIZE, SIZE));

        setOutsideTouchable(false);
        setTouchable(false);
        setFocusable(false);

        if(Build.VERSION.SDK_INT >= 22)
        setAttachedInDecor(true);

        setBackgroundDrawable(null);
        setContentView(popup);
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
      this.fadeHandler =  new Handler(dad);
      this.fadeRunnable = new Fade(fadeDuration, fadeHandler);
        fadeHandler.postDelayed(fadeRunnable, duration);

        hasStarted = true;
    }

    /**
     * LIFECYCLE
     */
    protected void onRestart(){
        getContentView().setAlpha(Fade.VALUE);
        onStart();
    }


    /**
     * LIFECYCLE
     */
    protected void onKill(){
        //TODO Fade out the animation
        dismiss();
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


    public void haveFun(final View parent, int gravity, int xLoc, int yLoc){
        showAtLocation(parent, gravity, xLoc, yLoc);
        this.onStart();
    }





    /**
     * Utility helper method to fetch a Drawable on different API levels
     * @param context Inflates Drawable objects
     * @return the inflated Drawable
     */
    private Drawable fetchDefaultBackground(final Context context){

        Log.i(DEBUG, "SDK VERSION: " + String.valueOf(Build.VERSION.SDK_INT));

        Drawable drawable = null;

        if(Build.VERSION.SDK_INT >= 22)
            context.getDrawable(BACKGROUND);

        else if(Build.VERSION.SDK_INT < 22)  //Deprecated after 22
            context.getResources().getDrawable(BACKGROUND);


        return drawable;
    }



    private final Handler.Callback dad = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            //Set the alpha value of content view to the compute view in the Handler
            FunPopup.this.getContentView().setAlpha((Float)message.obj);
            //Get the elapsed time (arg1) and check if equal to fadeDuration. If so, remove the runnable callback, and kill
            if(message.arg1 == fadeDuration){
                fadeHandler.removeCallbacks(fadeRunnable);
                //Kill object as well
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
     * The client brought their own TextView to the party and wants to use it
     * The msg can be null because their TextView may have a Text on it. If not
     * and they still passed null as the msg, then an exception will be raised. That's good behavior. Good boy, FunPopup.
     *
     * @param contentView User defined ContentView
     * @param msg contentView's message, or null if it already has one
     * @return *sigh* funPopup again....
     */
    public static FunPopup play(@NonNull final TextView contentView, @Nullable final CharSequence msg){
        return new FunPopup(contentView, msg);
    }


    /**
     * Utility method of play(TextView, CharSequence)
     *
     * The user wants to use their own TextView, HOWEVER they also wanted to use a String Resource
     *
     * @param contentView User defined TextView
     * @param msgId R.string
     * @return FUN-F****** POPUP!
     */
    public static FunPopup play(@NonNull final TextView contentView, @StringRes final int msgId){
        return play(contentView, resolveStringResource(contentView.getContext(), msgId));
    }


    /**
     *
     * This method is used when there's a Style, that they want to apply to the TextView
     * NOTE: A textview can only be given a style at instantiation time so this assumes
     * they didn't already have a TextView to use. mmmkay
     *
     * PLEASE NOTE: This method assumes a text resource is provided in the style resource
     *
     * @param context Context used by the Popup
     * @param styleRes Styling resource used by the Popups TextView
     * @return A newly made FunPopup !
     */
    public static FunPopup play(@NonNull final Context context, @StyleRes Integer styleRes){
        return play(context, styleRes, null);
    }


    /**
     * This method is used when the client wants to apply a custom style to the FunPopup, CAN pass null
     * if your style has its own message
     *
     * @param context Used to inflate views and stuff
     * @param styleRes reference to the styling they want applied to the textView
     * @param msg Text to display
     * @return a FunPopup object
     */
    public static FunPopup play(@NonNull final Context context, @StyleRes final Integer styleRes, final CharSequence msg){
        return new FunPopup(context, styleRes, msg);
    }

    /**
     * Called when the client wants to instance a new FunPopup with a specific style, using a string from a resource
     *  UTILITY method btw
     *
     * @param context inflates stuff
     * @param styleRes styles things
     * @param msgId says stuff
     * @return the thing we're doing all of this for
     */
    public static FunPopup play(@NonNull final Context context, @StyleRes final Integer styleRes, @StringRes final int msgId){
        return play(context, styleRes, resolveStringResource(context, msgId));
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

        private static final long CYCLE = 100L; //Every 100 MS deliver an updated value to handler

        private static final float VALUE = 1.0F;

        private long fadeDuration = 1000L;
        private long elapsedTime = 0L;

        private Handler handler;

        private Message message = new Message();

        private Float value = VALUE;

        /**
         *
         * @param fadeDuration How long the anim should last
         * @param handler The handler that started it
         */
        private Fade(final long fadeDuration, @NonNull  final Handler handler){
            if(fadeDuration > 1000 & (fadeDuration % CYCLE != 0) ) //If less than 1 second, and not a perfect divisor for 100, don't set it
            this.fadeDuration = fadeDuration;
            else
                Log.i(LogTags.NOTIFICATIONS,"Fade-IMPL @ class FunPopup: \n" +
                                                 "Fade Duration must be greater than a 1000 with 100 evenly dividing into it. \n" +
                                                 "Fade Duration: " + String.valueOf(fadeDuration));

            this.handler = handler;
        }

        @Override
        public void run() {
            //Increment elapsed time by CYCLE
            elapsedTime += CYCLE;


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











