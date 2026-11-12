package com.example.mycgame.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuScreen {
    private final Activity activity;
    private final Runnable onNewGame;
    private final int gold = Color.rgb(255, 194, 70);
    private final int cream = Color.rgb(255, 238, 190);
    private final int navy = Color.rgb(7, 20, 38);

    public MainMenuScreen(Activity activity, Runnable onNewGame) {
        this.activity = activity;
        this.onNewGame = onNewGame;
    }

    public View create() {
        int imageId = activity.getResources().getIdentifier("menu_background", "drawable", activity.getPackageName());
        if (imageId == 0) {
            TextView error = new TextView(activity);
            error.setText("OLREDINE: menu_background.png not found");
            error.setTextColor(Color.WHITE);
            error.setTextSize(18);
            error.setBackgroundColor(navy);
            error.setGravity(Gravity.CENTER);
            return error;
        }

        FrameLayout root = new FrameLayout(activity);
        ImageView bg = new ImageView(activity);
        bg.setImageResource(imageId);
        bg.setScaleType(ImageView.ScaleType.FIT_XY);
        root.addView(bg, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout panel = new LinearLayout(activity);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER);
        panel.setPadding(dp(10), dp(5), dp(10), dp(5));

        FrameLayout.LayoutParams pp = new FrameLayout.LayoutParams(
            activity.getResources().getDisplayMetrics().widthPixels * 42 / 100, -1, Gravity.RIGHT);
        pp.rightMargin = dp(18);
        pp.topMargin = dp(8);
        pp.bottomMargin = dp(8);

        TextView chart = new TextView(activity);
        chart.setText("▂ ▄ ▆ ▇  ↗");
        chart.setTextColor(gold);
        chart.setTextSize(27);
        chart.setGravity(Gravity.CENTER);
        chart.setTypeface(Typeface.DEFAULT_BOLD);
        panel.addView(chart, new LinearLayout.LayoutParams(-1, 0, 0.65f));

        TextView logo = new TextView(activity);
        logo.setText("OLREDINE");
        logo.setTextColor(gold);
        logo.setTextSize(35);
        logo.setGravity(Gravity.CENTER);
        logo.setTypeface(Typeface.create("serif", Typeface.BOLD));
        logo.setShadowLayer(5, 0, 2, Color.rgb(90, 45, 5));
        panel.addView(logo, new LinearLayout.LayoutParams(-1, 0, 1.05f));

        TextView sub = new TextView(activity);
        sub.setText("LIFE SIMULATION  •  BUSINESS  •  ECONOMY");
        sub.setTextColor(cream);
        sub.setTextSize(9);
        sub.setGravity(Gravity.CENTER);
        panel.addView(sub, new LinearLayout.LayoutParams(-1, 0, 0.45f));

        View line = new View(activity);
        line.setBackgroundColor(gold);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(1));
        lp.setMargins(dp(18), dp(2), dp(18), dp(7));
        panel.addView(line, lp);

        addButton(panel, "▶", "بدء حياة جديدة", true, onNewGame);
        addButton(panel, "↻", "متابعة اللعبة", false,
            () -> toast("لا توجد لعبة محفوظة حتى الآن"));
        addButton(panel, "▰", "تحميل لعبة", false,
            () -> toast("تحميل الألعاب المحفوظة سيضاف لاحقاً"));
        addButton(panel, "⚙", "الإعدادات", false,
            () -> toast("الإعدادات ستضاف لاحقاً"));
        addButton(panel, "ⓘ", "حول اللعبة", false,
            () -> toast("OLREDINE — Life Simulation • Business • Economy"));
        addButton(panel, "⇥", "خروج", false, activity::finish);

        root.addView(panel, pp);
        return root;
    }

    private void addButton(LinearLayout parent, String icon, String label,
                           boolean primary, Runnable action) {
        LinearLayout button = new LinearLayout(activity);
        button.setOrientation(LinearLayout.HORIZONTAL);
        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setPadding(dp(13), 0, dp(12), 0);
        button.setBackground(buttonBg(primary));
        button.setElevation(dp(5));
        button.setClickable(true);
        button.setFocusable(true);

        TextView iconView = new TextView(activity);
        iconView.setText(icon);
        iconView.setTextColor(primary ? navy : gold);
        iconView.setTextSize(27);
        iconView.setGravity(Gravity.CENTER);
        iconView.setTypeface(Typeface.DEFAULT_BOLD);
        button.addView(iconView, new LinearLayout.LayoutParams(dp(48), -1));

        TextView labelView = new TextView(activity);
        labelView.setText(label);
        labelView.setTextColor(primary ? Color.rgb(25, 25, 25) : Color.WHITE);
        labelView.setTextSize(17);
        labelView.setGravity(Gravity.CENTER);
        labelView.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        button.addView(labelView, new LinearLayout.LayoutParams(0, -1, 1));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, 0, 1f);
        params.setMargins(dp(3), dp(4), dp(3), dp(4));
        parent.addView(button, params);
        button.setOnClickListener(v -> action.run());
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                v.setScaleX(.985f);
                v.setScaleY(.96f);
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP ||
                       event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                v.setScaleX(1f);
                v.setScaleY(1f);
            }
            return false;
        });
    }

    private GradientDrawable buttonBg(boolean primary) {
        int[] colors = primary
            ? new int[]{Color.rgb(255,211,105), Color.rgb(224,151,35), Color.rgb(170,94,15)}
            : new int[]{Color.rgb(28,52,78), Color.rgb(10,27,47), Color.rgb(5,15,28)};
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        drawable.setCornerRadius(dp(19));
        drawable.setStroke(dp(2), primary ? cream : Color.rgb(211,155,64));
        return drawable;
    }

    private void toast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    private int dp(float value) {
        return (int)(value * activity.getResources().getDisplayMetrics().density + .5f);
    }
}
