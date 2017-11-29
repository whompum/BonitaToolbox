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

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.util.Log;

/**
 * For future modifications to our Bonita background drawable i'll extend now
 */

public class BonitaShapeDrawable extends ShapeDrawable {


    public BonitaShapeDrawable(Shape s) {
        super(s);
    }

    public static class BonitaShaderFactory extends ShapeDrawable.ShaderFactory{


        final static String DEBUG = "ShaderFactory";


        private Bitmap bitmap;
        private Matrix matrix;

        public BonitaShaderFactory(final Bitmap bitmap, final Matrix matrix){
            this.bitmap = bitmap;
            this.matrix = matrix;
            Log.i(DEBUG, "INITILIZING SHADER FACTORY");
        }

        @Override
        public Shader resize(int i, int i1) {
            Log.i(DEBUG, "RESIZE SHADER FACTORY NIGGA");
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            shader.setLocalMatrix(matrix);

            return shader;
        }




    }


}
