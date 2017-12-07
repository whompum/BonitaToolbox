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

package com.whompum.bonitatoolbox.app;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.whompum.bonitatoolbox.toolbox.Utils.LogTags;
import com.whompum.bonitatoolbox.toolbox.Views.BonitaCircleDisplay;
import com.whompum.bonitatoolbox.toolbox.widgets.FunPopup;
import com.whompum.bonitatoolbox.toolbox.widgets.ProgressSpinner;
import com.whompum.bonitatoolbox.toolbox.widgets.TimelessWaveAnimation;
import com.whompum.bonitatoolbox.toolbox.widgets.TimelessBloomImageView;


/**
 * Created by bryan on 11/25/2017.
 */

public class ActivityMain extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);


        final BonitaCircleDisplay circle = findViewById(R.id.circle);
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FunPopup.play(ActivityMain.this, "HELLO").haveFun(circle, 0, 0,circle.getHeight());
            }

        });


      final TimelessWaveAnimation timelessWave = findViewById(R.id.timelessWave);
      timelessWave.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              timelessWave.start();
          }
      });


      final ProgressSpinner progressSpin = findViewById(R.id.progressSpin);
      progressSpin.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              progressSpin.start();
          }
      });


        final TimelessBloomImageView bloomView = findViewById(R.id.timelessBloom);
        bloomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bloomView.start();
            }
        });


    }




}
