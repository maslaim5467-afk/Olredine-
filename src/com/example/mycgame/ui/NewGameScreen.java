package com.example.mycgame.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class NewGameScreen {
    private final Activity activity;
    private final Runnable onBack;
    private final int gold = Color.rgb(225, 180, 91);
    private final int navy = Color.rgb(12, 27, 46);
    private final int text = Color.rgb(244, 239, 226);
    private final String[] sections = {
        "الشخصية", "الملكية", "مكان الإقامة", "التعليم", "الوضع الاقتصادي", "العائلة", "المهارات"
    };
    private int selectedSection = 0;
    private LinearLayout content;
    private EditText nameInput;
    private EditText ageInput;
    private String playerName = "";
    private String playerAge = "18";
    private String gender = "ذكر";

    public NewGameScreen(Activity activity, Runnable onBack) {
        this.activity = activity;
        this.onBack = onBack;
    }

    public View create() {
        FrameLayout root = new FrameLayout(activity);
        int imageId = activity.getResources().getIdentifier("menu_background", "drawable", activity.getPackageName());
        if (imageId != 0) {
            ImageView background = new ImageView(activity);
            background.setImageResource(imageId);
            background.setScaleType(ImageView.ScaleType.CENTER_CROP);
            root.addView(background, new FrameLayout.LayoutParams(-1, -1));
        } else {
            root.setBackgroundColor(navy);
        }

        View shade = new View(activity);
        shade.setBackgroundColor(Color.argb(205, 5, 15, 28));
        root.addView(shade, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(dp(12), dp(10), dp(12), dp(10));
        root.addView(layout, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout sidebar = new LinearLayout(activity);
        sidebar.setOrientation(LinearLayout.VERTICAL);
        sidebar.setPadding(dp(8), dp(10), dp(8), dp(10));
        sidebar.setBackground(background(Color.rgb(13, 29, 49), 18, Color.rgb(139, 105, 49)));
        layout.addView(sidebar, new LinearLayout.LayoutParams(0, -1, 0.34f));

        TextView brand = new TextView(activity);
        brand.setText("OLREDINE");
        brand.setTextColor(gold);
        brand.setTextSize(22);
        brand.setGravity(Gravity.CENTER);
        brand.setTypeface(Typeface.create("serif", Typeface.BOLD));
        sidebar.addView(brand, new LinearLayout.LayoutParams(-1, dp(48)));

        TextView menuTitle = new TextView(activity);
        menuTitle.setText("إنشاء حياة جديدة");
        menuTitle.setTextColor(text);
        menuTitle.setTextSize(13);
        menuTitle.setGravity(Gravity.CENTER);
        sidebar.addView(menuTitle, new LinearLayout.LayoutParams(-1, dp(34)));

        View separator = new View(activity);
        separator.setBackgroundColor(Color.rgb(139, 105, 49));
        LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(-1, dp(1));
        sepParams.setMargins(dp(8), dp(5), dp(8), dp(9));
        sidebar.addView(separator, sepParams);

        for (int i = 0; i < sections.length; i++) {
            final int index = i;
            TextView item = new TextView(activity);
            item.setText((i + 1) + "   " + sections[i]);
            item.setTextColor(i == selectedSection ? Color.rgb(28, 25, 19) : text);
            item.setTextSize(14);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(dp(10), 0, dp(8), 0);
            item.setBackground(background(i == selectedSection ? gold : Color.TRANSPARENT, 10,
                i == selectedSection ? gold : Color.TRANSPARENT));
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(-1, 0, 1f);
            itemParams.setMargins(dp(2), dp(3), dp(2), dp(3));
            sidebar.addView(item, itemParams);
            item.setOnClickListener(v -> {
                saveCurrentFields();
                selectedSection = index;
                refreshSidebar(sidebar);
                renderSection();
            });
        }

        TextView back = new TextView(activity);
        back.setText("←  العودة للقائمة الرئيسية");
        back.setTextColor(gold);
        back.setTextSize(12);
        back.setGravity(Gravity.CENTER);
        sidebar.addView(back, new LinearLayout.LayoutParams(-1, dp(48)));
        back.setOnClickListener(v -> onBack.run());

        LinearLayout right = new LinearLayout(activity);
        right.setOrientation(LinearLayout.VERTICAL);
        right.setPadding(dp(14), dp(10), dp(14), dp(10));
        right.setBackground(background(Color.argb(238, 18, 34, 54), 18, Color.rgb(139, 105, 49)));
        LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(0, -1, 0.66f);
        rightParams.setMargins(dp(10), 0, 0, 0);
        layout.addView(right, rightParams);

        content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.addView(content);
        right.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        LinearLayout footer = new LinearLayout(activity);
        footer.setGravity(Gravity.CENTER_VERTICAL);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        right.addView(footer, new LinearLayout.LayoutParams(-1, dp(52)));

        TextView previous = footerButton("السابق");
        TextView next = footerButton("التالي");
        footer.addView(previous, new LinearLayout.LayoutParams(0, -1, 1f));
        LinearLayout.LayoutParams nextParams = new LinearLayout.LayoutParams(0, -1, 1f);
        nextParams.setMargins(dp(8), 0, 0, 0);
        footer.addView(next, nextParams);

        previous.setOnClickListener(v -> {
            saveCurrentFields();
            if (selectedSection == 0) onBack.run();
            else selectSection(selectedSection - 1, sidebar, next);
        });
        next.setOnClickListener(v -> {
            saveCurrentFields();
            if (selectedSection < sections.length - 1) {
                selectSection(selectedSection + 1, sidebar, next);
            } else {
                next.setText("إنهاء");
                Toast.makeText(activity, "سيتم تفعيل إنشاء الحفظ بعد اكتمال بيانات العالم.", Toast.LENGTH_SHORT).show();
            }
        });

        renderSection();
        return root;
    }

    private void selectSection(int index, LinearLayout sidebar, TextView next) {
        selectedSection = index;
        refreshSidebar(sidebar);
        next.setText(selectedSection == sections.length - 1 ? "إنهاء" : "التالي");
        renderSection();
    }

    private void refreshSidebar(LinearLayout sidebar) {
        for (int i = 0; i < sections.length; i++) {
            View child = sidebar.getChildAt(i + 3);
            if (child instanceof TextView) {
                TextView item = (TextView) child;
                item.setTextColor(i == selectedSection ? Color.rgb(28, 25, 19) : text);
                item.setBackground(background(i == selectedSection ? gold : Color.TRANSPARENT, 10,
                    i == selectedSection ? gold : Color.TRANSPARENT));
            }
        }
    }

    private void renderSection() {
        content.removeAllViews();
        TextView heading = new TextView(activity);
        heading.setText(sections[selectedSection]);
        heading.setTextColor(gold);
        heading.setTextSize(25);
        heading.setTypeface(Typeface.DEFAULT_BOLD);
        content.addView(heading, new LinearLayout.LayoutParams(-1, dp(48)));

        TextView description = new TextView(activity);
        description.setText("حدد البيانات التالية لتكوين بداية عالمك في OLREDINE.");
        description.setTextColor(Color.rgb(190, 202, 216));
        description.setTextSize(13);
        content.addView(description, new LinearLayout.LayoutParams(-1, dp(34)));

        if (selectedSection == 0) {
            addLabel("اسم الشخصية");
            nameInput = addInput(playerName, "أدخل اسم الشخصية", false);
            addLabel("الجنس");
            LinearLayout genders = new LinearLayout(activity);
            genders.setOrientation(LinearLayout.HORIZONTAL);
            TextView male = choiceButton("ذكر", gender.equals("ذكر"));
            TextView female = choiceButton("أنثى", gender.equals("أنثى"));
            genders.addView(male, new LinearLayout.LayoutParams(0, dp(48), 1f));
            LinearLayout.LayoutParams femaleParams = new LinearLayout.LayoutParams(0, dp(48), 1f);
            femaleParams.setMargins(dp(8), 0, 0, 0);
            genders.addView(female, femaleParams);
            content.addView(genders);
            male.setOnClickListener(v -> { saveCurrentFields(); gender = "ذكر"; renderSection(); });
            female.setOnClickListener(v -> { saveCurrentFields(); gender = "أنثى"; renderSection(); });

            addLabel("العمر");
            ageInput = addInput(playerAge, "18", true);
            addLabel("السمات الشخصية");
            addOption("هادئ • طموح • اجتماعي • تحليلي");
            addLabel("ملاحظة");
            addOption("يمكن تعديل بقية تفاصيل الشخصية لاحقاً.");
        } else {
            TextView placeholder = new TextView(activity);
            placeholder.setText("هذا القسم جاهز ضمن هيكل الصفحة، وسيتم بناء خياراته وربطها ببيانات العالم في مرحلة التطوير التالية.");
            placeholder.setTextColor(text);
            placeholder.setTextSize(16);
            placeholder.setGravity(Gravity.CENTER);
            placeholder.setPadding(dp(18), dp(20), dp(18), dp(20));
            placeholder.setBackground(background(Color.rgb(28, 48, 70), 14, Color.rgb(110, 91, 61)));
            content.addView(placeholder, new LinearLayout.LayoutParams(-1, dp(150)));
        }
    }

    private void saveCurrentFields() {
        if (nameInput != null) playerName = nameInput.getText().toString();
        if (ageInput != null) playerAge = ageInput.getText().toString();
        nameInput = null;
        ageInput = null;
    }

    private void addLabel(String value) {
        TextView label = new TextView(activity);
        label.setText(value);
        label.setTextColor(text);
        label.setTextSize(14);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(32));
        params.topMargin = dp(8);
        content.addView(label, params);
    }

    private EditText addInput(String value, String hint, boolean numeric) {
        EditText input = new EditText(activity);
        input.setSingleLine(true);
        input.setText(value);
        input.setHint(hint);
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.rgb(155, 169, 185));
        input.setTextSize(15);
        input.setPadding(dp(12), 0, dp(12), 0);
        input.setBackground(background(Color.rgb(34, 54, 77), 10, Color.rgb(111, 94, 67)));
        if (numeric) input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        content.addView(input, new LinearLayout.LayoutParams(-1, dp(46)));
        return input;
    }

    private TextView choiceButton(String label, boolean selected) {
        TextView button = new TextView(activity);
        button.setText(label);
        button.setGravity(Gravity.CENTER);
        button.setTextSize(15);
        button.setTextColor(selected ? Color.rgb(25, 25, 25) : text);
        button.setBackground(background(selected ? gold : Color.rgb(34, 54, 77), 10, Color.rgb(160, 126, 67)));
        return button;
    }

    private void addOption(String value) {
        TextView option = new TextView(activity);
        option.setText(value);
        option.setTextColor(Color.rgb(218, 224, 232));
        option.setTextSize(13);
        option.setPadding(dp(12), dp(12), dp(12), dp(12));
        option.setBackground(background(Color.rgb(34, 54, 77), 10, Color.rgb(86, 103, 121)));
        content.addView(option, new LinearLayout.LayoutParams(-1, dp(44)));
    }

    private TextView footerButton(String label) {
        TextView button = new TextView(activity);
        button.setText(label);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(Color.rgb(25, 25, 25));
        button.setTextSize(15);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setBackground(background(gold, 12, Color.rgb(255, 226, 158)));
        return button;
    }

    private GradientDrawable background(int color, int radius, int stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        if (stroke != Color.TRANSPARENT) drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private int dp(float value) {
        return (int)(value * activity.getResources().getDisplayMetrics().density + .5f);
    }
}
