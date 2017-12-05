package toolbox.bonita.whompum.app.toolbox;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import toolbox.bonita.whompum.app.toolbox.BonitaDisplays.BonitaCircleDisplay;

import toolbox.bonita.whompum.app.toolbox.BonitaDisplays.widgets.FunPopup;


/**
 * Created by bryan on 11/25/2017.
 */

public class ActivityMain extends AppCompatActivity {


    private Bitmap source;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(app.bonita.whompum.app.toolbox.R.layout.layout_main);


        BonitaCircleDisplay circleDisplay = (BonitaCircleDisplay) findViewById(app.bonita.whompum.app.toolbox.R.id.circle);

        FunPopup.play(this, "TESTING OUT the new popup MAHNNNN").haveFun(circleDisplay, Gravity.NO_GRAVITY, 100, 100);

    }




}
