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
        "شخصیت", "ملیت", "محل زندگی", "تحصیلات", "وضعیت اقتصادی", "خانواده", "مهارت‌ها"
    };
    private int selectedSection = 0;
    private LinearLayout content;
    private EditText nameInput;
    private EditText ageInput;
    private String playerName = "";
    private String playerAge = "18";
    private String gender = "مرد";
    private String innateTrait = "";

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
        menuTitle.setText("ساخت زندگی جدید");
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
                if (!validateCurrentFields()) return;
                saveCurrentFields();
                selectedSection = index;
                refreshSidebar(sidebar);
                renderSection();
            });
        }

        TextView back = new TextView(activity);
        back.setText("←  بازگشت به منوی اصلی");
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

        TextView previous = footerButton("قبلی");
        TextView next = footerButton("بعدی");
        footer.addView(previous, new LinearLayout.LayoutParams(0, -1, 1f));
        LinearLayout.LayoutParams nextParams = new LinearLayout.LayoutParams(0, -1, 1f);
        nextParams.setMargins(dp(8), 0, 0, 0);
        footer.addView(next, nextParams);

        previous.setOnClickListener(v -> {
            if (!validateCurrentFields()) return;
            saveCurrentFields();
            if (selectedSection == 0) onBack.run();
            else selectSection(selectedSection - 1, sidebar, next);
        });
        next.setOnClickListener(v -> {
            if (!validateCurrentFields()) return;
            saveCurrentFields();
            if (selectedSection < sections.length - 1) {
                selectSection(selectedSection + 1, sidebar, next);
            } else {
                next.setText("پایان");
                Toast.makeText(activity, "ساخت ذخیره بازی پس از تکمیل اطلاعات جهان فعال می‌شود.", Toast.LENGTH_SHORT).show();
            }
        });

        renderSection();
        return root;
    }

    private void selectSection(int index, LinearLayout sidebar, TextView next) {
        selectedSection = index;
        refreshSidebar(sidebar);
        next.setText(selectedSection == sections.length - 1 ? "پایان" : "بعدی");
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
        description.setText("اطلاعات زیر را برای ساخت زندگی جدید در OLREDINE مشخص کنید.");
        description.setTextColor(Color.rgb(190, 202, 216));
        description.setTextSize(13);
        content.addView(description, new LinearLayout.LayoutParams(-1, dp(34)));

        if (selectedSection == 0) {
            addLabel("نام شخصیت");
            nameInput = addInput(playerName, "نام شخصیت را وارد کنید", false);
            addLabel("جنسیت");
            LinearLayout genders = new LinearLayout(activity);
            genders.setOrientation(LinearLayout.HORIZONTAL);
            TextView male = choiceButton("مرد", gender.equals("مرد"));
            TextView female = choiceButton("زن", gender.equals("زن"));
            genders.addView(male, new LinearLayout.LayoutParams(0, dp(48), 1f));
            LinearLayout.LayoutParams femaleParams = new LinearLayout.LayoutParams(0, dp(48), 1f);
            femaleParams.setMargins(dp(8), 0, 0, 0);
            genders.addView(female, femaleParams);
            content.addView(genders);
            male.setOnClickListener(v -> { saveCurrentFields(); gender = "مرد"; renderSection(); });
            female.setOnClickListener(v -> { saveCurrentFields(); gender = "زن"; renderSection(); });

            addLabel("سن");
            ageInput = addInput(playerAge, "18", true);
            addLabel("ویژگی ذاتی");
            TextView traitHint = new TextView(activity);
            traitHint.setText("یک ویژگی را انتخاب کنید. این ویژگی از آغاز همراه شخصیت شماست؛ ویژگی‌های دیگر در طول زندگی و بر اساس رفتارها و تجربه‌ها کسب می‌شوند.");
            traitHint.setTextColor(Color.rgb(190, 202, 216));
            traitHint.setTextSize(12);
            traitHint.setPadding(0, 0, 0, dp(8));
            content.addView(traitHint, new LinearLayout.LayoutParams(-1, -2));

            String[][] traits = {
                {"منضبط", "تمایل به نظم و برنامه‌ریزی"},
                {"جاه‌طلب", "میل به پیشرفت و دستیابی به اهداف"},
                {"تحلیل‌گر", "گرایش به بررسی و تحلیل مسائل"},
                {"اجتماعی", "تمایل به ارتباط و تعامل"},
                {"ریسک‌پذیر", "آمادگی برای پذیرش ریسک"},
                {"صبور", "توانایی انتظار برای نتیجه بلندمدت"},
                {"خلاق", "گرایش به ایده‌ها و راه‌حل‌های تازه"},
                {"محتاط", "توجه به خطرات پیش از اقدام"}
            };

            for (int i = 0; i < traits.length; i += 2) {
                LinearLayout row = new LinearLayout(activity);
                row.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
                rowParams.bottomMargin = dp(8);
                content.addView(row, rowParams);

                for (int j = i; j < i + 2; j++) {
                    final String traitName = traits[j][0];
                    LinearLayout trait = traitChoiceButton(traitName, traits[j][1], innateTrait.equals(traitName));
                    LinearLayout.LayoutParams traitParams = new LinearLayout.LayoutParams(0, dp(76), 1f);
                    if (j == i + 1) traitParams.setMargins(dp(8), 0, 0, 0);
                    row.addView(trait, traitParams);
                    trait.setOnClickListener(v -> {
                        innateTrait = traitName;
                        renderSection();
                    });
                }
            }

            TextView selectedTrait = new TextView(activity);
            selectedTrait.setText(innateTrait.isEmpty()
                ? "هنوز ویژگی ذاتی انتخاب نشده است."
                : "ویژگی ذاتی انتخاب‌شده: " + innateTrait);
            selectedTrait.setTextColor(gold);
            selectedTrait.setTextSize(13);
            selectedTrait.setPadding(dp(4), dp(8), dp(4), dp(8));
            content.addView(selectedTrait);
        } else {
            TextView placeholder = new TextView(activity);
            placeholder.setText("ساختار این بخش آماده است؛ گزینه‌ها و اطلاعات آن در مراحل بعدی توسعه اضافه می‌شوند.");
            placeholder.setTextColor(text);
            placeholder.setTextSize(16);
            placeholder.setGravity(Gravity.CENTER);
            placeholder.setPadding(dp(18), dp(20), dp(18), dp(20));
            placeholder.setBackground(background(Color.rgb(28, 48, 70), 14, Color.rgb(110, 91, 61)));
            content.addView(placeholder, new LinearLayout.LayoutParams(-1, dp(150)));
        }
    }

    private boolean validateCurrentFields() {
        if (selectedSection != 0 || ageInput == null) return true;

        String value = ageInput.getText().toString().trim();
        if (value.isEmpty()) {
            ageInput.setError("سن را وارد کنید");
            ageInput.requestFocus();
            return false;
        }

        try {
            int age = Integer.parseInt(value);
            if (age < 12 || age > 30) {
                ageInput.setError("سن باید بین ۱۲ تا ۳۰ سال باشد");
                ageInput.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            ageInput.setError("سن واردشده معتبر نیست");
            ageInput.requestFocus();
            return false;
        }

        if (innateTrait.isEmpty()) {
            Toast.makeText(activity, "یک ویژگی ذاتی انتخاب کنید.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    private LinearLayout traitChoiceButton(String name, String description, boolean selected) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(10), dp(6), dp(10), dp(6));
        card.setBackground(background(
            selected ? Color.rgb(91, 71, 39) : Color.rgb(34, 54, 77),
            10,
            selected ? gold : Color.rgb(86, 103, 121)
        ));

        TextView title = new TextView(activity);
        title.setText(name);
        title.setTextColor(selected ? gold : text);
        title.setTextSize(14);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        card.addView(title);

        TextView detail = new TextView(activity);
        detail.setText(description);
        detail.setTextColor(Color.rgb(190, 202, 216));
        detail.setTextSize(10);
        card.addView(detail);
        return card;
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
