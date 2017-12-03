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

package toolbox.bonita.whompum.com.toolbox.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;

/**
 * CROPPING
 * Simple flow is i hand crop() an original source bitmap, and a cropRegion.
 * The cropRegion should live inside the bounds of the source, and draw that portion only
 * to resultBitmap. I return resultBitmap
 **/

public class ImageCrop {

    public static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;

    /**
     *
     * @param croppable The bitmap i want to crop and return a portion of
     * @param cropRegion The region of croppable i want to return a Bitmap of
     * @param config Bitmap configuration. May be null
     * @return The original bitmap but of crop region only.
     */
   public static Bitmap crop(final Bitmap croppable, final Rect cropRegion, @Nullable Bitmap.Config config){

       final Rect dest = new Rect(0,0, croppable.getWidth(), croppable.getHeight());


       final Bitmap result = makeCanvasBitmap(cropRegion, config);
       final Canvas cropCanvas = new Canvas(result);
       cropCanvas.drawBitmap(croppable, cropRegion, dest, null);


   return result;
   }


   private static Bitmap makeCanvasBitmap(final Rect cropRegion, @Nullable Bitmap.Config config){

       Bitmap.Config configuration = config;

       if(configuration ==null)
           configuration = DEFAULT_CONFIG;

       return Bitmap.createBitmap(cropRegion.width(), cropRegion.height(), configuration);
   }


}














