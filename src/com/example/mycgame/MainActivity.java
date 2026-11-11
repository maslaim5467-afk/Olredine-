package com.example.mycgame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("mygame");
    }

    private native String getGameStatus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText(getGameStatus());
        tv.setTextSize(24);
        setContentView(tv);
    }
}
