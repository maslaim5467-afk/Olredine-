package com.example.mycgame;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private final int gold = Color.rgb(255, 194, 70);
    private final int cream = Color.rgb(255, 238, 190);
    private final int navy = Color.rgb(7, 20, 38);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        int imageId = getResources().getIdentifier("menu_background", "drawable", getPackageName());
        if (imageId == 0) {
            TextView error = new TextView(this);
            error.setText("OLREDINE: menu_background.png not found");
            error.setTextColor(Color.WHITE); error.setTextSize(18);
            error.setBackgroundColor(navy); error.setGravity(Gravity.CENTER);
            setContentView(error); return;
        }

        FrameLayout root = new FrameLayout(this);
        ImageView bg = new ImageView(this);
        bg.setImageResource(imageId); bg.setScaleType(ImageView.ScaleType.FIT_XY);
        root.addView(bg, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER);
        panel.setPadding(dp(10), dp(5), dp(10), dp(5));

        FrameLayout.LayoutParams pp = new FrameLayout.LayoutParams(
            getResources().getDisplayMetrics().widthPixels * 42 / 100, -1, Gravity.RIGHT);
        pp.rightMargin = dp(18); pp.topMargin = dp(8); pp.bottomMargin = dp(8);

        TextView chart = new TextView(this);
        chart.setText("▂ ▄ ▆ ▇  ↗"); chart.setTextColor(gold);
        chart.setTextSize(27); chart.setGravity(Gravity.CENTER);
        chart.setTypeface(Typeface.DEFAULT_BOLD);
        panel.addView(chart, new LinearLayout.LayoutParams(-1, 0, 0.65f));

        TextView logo = new TextView(this);
        logo.setText("OLREDINE"); logo.setTextColor(gold); logo.setTextSize(35);
        logo.setGravity(Gravity.CENTER);
        logo.setTypeface(Typeface.create("serif", Typeface.BOLD));
        logo.setShadowLayer(5, 0, 2, Color.rgb(90, 45, 5));
        panel.addView(logo, new LinearLayout.LayoutParams(-1, 0, 1.05f));

        TextView sub = new TextView(this);
        sub.setText("LIFE SIMULATION  •  BUSINESS  •  ECONOMY");
        sub.setTextColor(cream); sub.setTextSize(9); sub.setGravity(Gravity.CENTER);
        panel.addView(sub, new LinearLayout.LayoutParams(-1, 0, 0.45f));

        View line = new View(this); line.setBackgroundColor(gold);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(1));
        lp.setMargins(dp(18), dp(2), dp(18), dp(7)); panel.addView(line, lp);

        addButton(panel, "▶", "شروع بازی جدید", true,
            () -> toast("شروع بازی به‌زودی فعال می‌شود"));
        addButton(panel, "↻", "ادامه بازی", false,
            () -> toast("هنوز ذخیره‌ای وجود ندارد"));
        addButton(panel, "▰", "بارگذاری بازی", false,
            () -> toast("بارگذاری بازی به‌زودی فعال می‌شود"));
        addButton(panel, "⚙", "تنظیمات", false,
            () -> toast("تنظیمات به‌زودی اضافه می‌شود"));
        addButton(panel, "ⓘ", "درباره بازی", false,
            () -> toast("OLREDINE — Life Simulation • Business • Economy"));
        addButton(panel, "⇥", "خروج", false, this::finish);

        root.addView(panel, pp);
        setContentView(root);
    }

    private void addButton(LinearLayout parent, String icon, String label,
                           boolean primary, Runnable action) {
        LinearLayout b = new LinearLayout(this);
        b.setOrientation(LinearLayout.HORIZONTAL); b.setGravity(Gravity.CENTER_VERTICAL);
        b.setPadding(dp(13), 0, dp(12), 0); b.setBackground(buttonBg(primary));
        b.setElevation(dp(5)); b.setClickable(true); b.setFocusable(true);

        TextView iv = new TextView(this);
        iv.setText(icon); iv.setTextColor(primary ? navy : gold);
        iv.setTextSize(27); iv.setGravity(Gravity.CENTER);
        iv.setTypeface(Typeface.DEFAULT_BOLD);
        b.addView(iv, new LinearLayout.LayoutParams(dp(48), -1));

        TextView tv = new TextView(this);
        tv.setText(label); tv.setTextColor(primary ? Color.rgb(25, 25, 25) : Color.WHITE);
        tv.setTextSize(17); tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        b.addView(tv, new LinearLayout.LayoutParams(0, -1, 1));

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, 0, 1f);
        p.setMargins(dp(3), dp(4), dp(3), dp(4)); parent.addView(b, p);
        b.setOnClickListener(v -> action.run());
        b.setOnTouchListener((v, e) -> {
            if (e.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                v.setScaleX(.985f); v.setScaleY(.96f);
            } else if (e.getAction() == android.view.MotionEvent.ACTION_UP ||
                       e.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                v.setScaleX(1f); v.setScaleY(1f);
            }
            return false;
        });
    }

    private GradientDrawable buttonBg(boolean primary) {
        int[] colors = primary
            ? new int[]{Color.rgb(255,211,105), Color.rgb(224,151,35), Color.rgb(170,94,15)}
            : new int[]{Color.rgb(28,52,78), Color.rgb(10,27,47), Color.rgb(5,15,28)};
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        d.setCornerRadius(dp(19));
        d.setStroke(dp(2), primary ? cream : Color.rgb(211,155,64));
        return d;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + .5f);
    }
}