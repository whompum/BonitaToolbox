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

package java.BonitaDisplays;

import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.util.Log;

/**
 * Created by bryan on 11/25/2017.
 *
 * Accepts a GradientDrawable and spits out a ShapeDrawable with exact dimensions / Radius' etc.
 *
 *
 */

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class RectFactory {

    public static final String DEBUG = "RectFactory";

    public static final int LEFT_TOP_ONE = 0;
    public static final int LEFT_TOP_TWO = 1;

    public static final int RIGHT_TOP_ONE = 2;
    public static final int RIGHT_TOP_TWO = 3;

    public static final int RIGHT_BOTTOM_ONE = 4;
    public static final int RIGHT_BOTTOM_TWO = 5;

    public static final int LEFT_BOTTOM_ONE = 6;
    public static final int LEFT_BOTTOM_TWO = 7;

    private static final int CORNER_SIZE = 8;

    final float[] cornersOuter = new float[8];
    final float[] cornersInner = new float[8];

    private GeometryAssignments shapeAssigments;

    private Shape shape;



    public RectFactory(final GeometryAssignments shapeAssigments){
        this.shapeAssigments = shapeAssigments;

        initRadii();
    }

    /**
     * Simple Factory Method to fetch a ShapeObject based on
     * the ShapeAssignements parameter
     *
     * @return OvalShape | RoundRectShape | RectShape
     */
    public Shape makeRect(){

        this.shape = getRectShape();

    return shape;
    }

    public Shape getShape(){
        if(shape!=null)
            return shape;

    return makeRect();
    }



    /**
     *
     * First this method sets the corner/inner radius values using the ShapeAssigments object
     * Next this method will loop through the array to search for any non-negative values (RoundRect)
     * if found, it will set the local shape variable as a RoundRect, and break;
     *
     * @return RoundRectShape | RectShape
     */
    private Shape getRectShape(){

        setRadii();

        for(int a = 0; a < CORNER_SIZE; a++){

            if(cornersOuter[a] > 0)
                return new RoundRectShape(cornersOuter, null, cornersInner);

        }

        return new RectShape();
    }




    /**
     * Initializes the corner radius arrays with zero
     * for loop processing
     *
     */
    private void initRadii(){

        for(int a = 0; a < CORNER_SIZE; a++ ){
            cornersInner[a] = 0;
            cornersOuter[a] = 0;
        }


    }


    /**
     *  Sets the corners for the array
     */
    private void setRadii(){

        cornersOuter[LEFT_TOP_ONE] = shapeAssigments.topLeft;
        cornersOuter[LEFT_TOP_TWO] = shapeAssigments.topLeft;

        cornersOuter[RIGHT_TOP_ONE] = shapeAssigments.topRight;
        cornersOuter[RIGHT_TOP_TWO] = shapeAssigments.topRight;

        cornersOuter[RIGHT_BOTTOM_ONE] = shapeAssigments.bottomRight;
        cornersOuter[RIGHT_BOTTOM_TWO] = shapeAssigments.bottomRight;

        cornersOuter[LEFT_BOTTOM_ONE] = shapeAssigments.bottomLeft;
        cornersOuter[LEFT_BOTTOM_TWO] = shapeAssigments.bottomLeft;

    }


    public GeometryAssignments getShapeAssigments(){
        return shapeAssigments;
    }


    public static class GeometryAssignments {


        public GeometryAssignments(final int topLeft, final int topRight,
                                   final int bottomLeft, final int bottomRight){

            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomLeft = bottomLeft;
            this.bottomRight = bottomRight;
        }


        private int topLeft;
        private int topRight;
        private int bottomLeft;
        private int bottomRight;

        public int getTopLeft(){
            return topLeft;
        }

        public int getTopRight(){
            return topRight;
        }

        public int getBottomLeft(){
            return bottomLeft;
        }

        public int getBottomRight(){
            return bottomRight;
        }

        @Override
        public String toString(){

            return
                    "TOP LEFT: " + String.valueOf(getTopLeft()) + "\n" +
                    "TOP RIGHT: " + String.valueOf(getTopRight()) + "\n" +
                    "BOTTOM LEFT: " + String.valueOf(getBottomLeft()) + "\n" +
                    "BOTTOM RIGHT: " + String.valueOf(getBottomRight());


        }



    }



}










