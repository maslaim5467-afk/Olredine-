package com.example.mycgame;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use the full screen for the OLREDINE main-menu artwork.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

        int imageId = getResources().getIdentifier(
                "menu_background", "drawable", getPackageName()
        );

        if (imageId == 0) {
            TextView error = new TextView(this);
            error.setText("OLREDINE: menu_background.png not found");
            error.setTextColor(Color.WHITE);
            error.setTextSize(18);
            error.setBackgroundColor(Color.rgb(7, 20, 38));
            error.setGravity(android.view.Gravity.CENTER);
            setContentView(error);
            return;
        }

        ImageView menu = new ImageView(this);
        menu.setImageResource(imageId);
        menu.setScaleType(ImageView.ScaleType.FIT_XY);
        menu.setContentDescription("OLREDINE main menu");
        menu.setBackgroundColor(Color.rgb(7, 20, 38));
        setContentView(menu);
    }
}
