package com.example.mycgame.ui;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

public class NewGameScreen {
    private static class LocationEntry {
        final String id;
        final String name;
        final String type;
        LocationEntry(String id, String name, String type) {
            this.id = id; this.name = name; this.type = type;
        }
        String typeLabel() {
            if ("METROPOLIS".equals(type)) return "کلان‌شهر";
            if ("VILLAGE".equals(type)) return "منطقه روستایی";
            return "شهر متوسط";
        }
        @Override public String toString() { return name + " — " + typeLabel(); }
    }

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
    private String countryCode = "";
    private String countryName = "";
    private String residenceCountryCode = "";
    private String residenceCountryName = "";
    private String residenceLocationId = "";
    private String residenceLocationName = "";
    private String residenceType = "";
    private String educationLevel = "";
    private String educationStatus = "";
    private String educationField = "";
    private String educationPerformance = "";
    private String economicStatus = "";
    private String familyStructure = "";
    private String familyParentStatus = "";
    private String familySupport = "";
    private String familyEconomicStatus = "";

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
                if (index > selectedSection && !validateCurrentFields()) return;
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
            if (selectedSection == 0 && !validateCurrentFields()) return;
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
        } else if (selectedSection == 1) {
            addLabel("ملیت اصلی");
            TextView countryButton = choiceButton(
                countryName.isEmpty() ? "انتخاب کشور" : countryName + "  (" + countryCode + ")",
                !countryCode.isEmpty()
            );
            content.addView(countryButton, new LinearLayout.LayoutParams(-1, dp(52)));
            countryButton.setOnClickListener(v -> showCountryPicker(false));

            TextView countryHint = new TextView(activity);
            countryHint.setText("یک کشور را به‌عنوان ملیت اصلی انتخاب کنید. برای پیدا کردن کشور می‌توانید نام فارسی یا کد دوحرفی آن را جست‌وجو کنید.");
            countryHint.setTextColor(Color.rgb(190, 202, 216));
            countryHint.setTextSize(12);
            countryHint.setPadding(dp(4), dp(12), dp(4), 0);
            content.addView(countryHint, new LinearLayout.LayoutParams(-1, -2));

            TextView count = new TextView(activity);
            count.setText("فهرست شامل ۱۹۵ کشور است.");
            count.setTextColor(gold);
            count.setTextSize(13);
            count.setPadding(dp(4), dp(16), dp(4), 0);
            content.addView(count);
        } else if (selectedSection == 2) {
            addLabel("کشور محل زندگی");
            TextView residenceCountryButton = choiceButton(
                residenceCountryName.isEmpty() ? "انتخاب کشور محل زندگی" : residenceCountryName + "  (" + residenceCountryCode + ")",
                !residenceCountryCode.isEmpty()
            );
            content.addView(residenceCountryButton, new LinearLayout.LayoutParams(-1, dp(50)));
            residenceCountryButton.setOnClickListener(v -> showCountryPicker(true));

            TextView countryHint = new TextView(activity);
            countryHint.setText("کشور محل زندگی مستقل از ملیت شخصیت است. فهرست کشورها بر اساس مکان‌های قابل انتخاب در نسخه فعلی بازی محدود شده است.");
            countryHint.setTextColor(Color.rgb(190, 202, 216));
            countryHint.setTextSize(12);
            countryHint.setPadding(dp(4), dp(8), dp(4), dp(8));
            content.addView(countryHint, new LinearLayout.LayoutParams(-1, -2));

            addLabel("شهر / منطقه محل زندگی");
            TextView locationButton = choiceButton(
                residenceLocationName.isEmpty() ? "ابتدا کشور را انتخاب کنید" : residenceLocationName,
                !residenceLocationId.isEmpty()
            );
            content.addView(locationButton, new LinearLayout.LayoutParams(-1, dp(50)));
            locationButton.setEnabled(!residenceCountryCode.isEmpty());
            locationButton.setOnClickListener(v -> showLocationPicker());

            if (!residenceType.isEmpty()) {
                TextView typeValue = new TextView(activity);
                String typeLabel = "METROPOLIS".equals(residenceType) ? "کلان‌شهر"
                    : ("VILLAGE".equals(residenceType) ? "منطقه روستایی" : "شهر متوسط");
                typeValue.setText("نوع منطقه: " + typeLabel);
                typeValue.setTextColor(gold);
                typeValue.setTextSize(14);
                typeValue.setPadding(dp(4), dp(12), dp(4), dp(8));
                content.addView(typeValue, new LinearLayout.LayoutParams(-1, -2));
            }

            TextView residenceHint = new TextView(activity);
            residenceHint.setText("نوع منطقه از داده‌های ثابت همان مکان تعیین می‌شود و بازیکن آن را جداگانه انتخاب نمی‌کند.");
            residenceHint.setTextColor(Color.rgb(190, 202, 216));
            residenceHint.setTextSize(12);
            residenceHint.setPadding(dp(4), dp(4), dp(4), dp(8));
            content.addView(residenceHint, new LinearLayout.LayoutParams(-1, -2));
        } else if (selectedSection == 3) {
            TextView intro = new TextView(activity);
            intro.setText("سابقه آموزشی شخصیت را مشخص کنید. این اطلاعات در آینده بر فرصت‌های شغلی، مهارت‌ها و مسیر پیشرفت اثر خواهند گذاشت.");
            intro.setTextColor(Color.rgb(190, 202, 216));
            intro.setTextSize(12);
            intro.setPadding(dp(4), 0, dp(4), dp(10));
            content.addView(intro, new LinearLayout.LayoutParams(-1, -2));

            addOptionGroup("بالاترین سطح تحصیلات تکمیل‌شده", new String[]{
                "بدون تحصیلات رسمی", "آموزش ابتدایی", "آموزش متوسطه اول",
                "آموزش متوسطه", "دیپلم فنی‌وحرفه‌ای", "آموزش عالی کوتاه‌مدت",
                "کارشناسی", "کارشناسی ارشد", "دکتری"
            }, educationLevel, value -> {
                educationLevel = value;
                if ("بدون تحصیلات رسمی".equals(value)) {
                    educationField = "بدون رشته مشخص";
                    educationPerformance = "";
                }
                renderSection();
            });

            addOptionGroup("وضعیت تحصیلی فعلی", new String[]{
                "در حال تحصیل", "فارغ‌التحصیل", "ترک تحصیل", "وقفه در تحصیل"
            }, educationStatus, value -> {
                educationStatus = value;
                renderSection();
            });

            if (!"بدون تحصیلات رسمی".equals(educationLevel)) {
                addOptionGroup("رشته یا حوزه تحصیلی", new String[]{
                    "فناوری و مهندسی", "اقتصاد و مدیریت", "علوم پزشکی",
                    "علوم پایه", "هنر و طراحی", "علوم انسانی و اجتماعی",
                    "فنی‌وحرفه‌ای", "سایر رشته‌ها", "بدون رشته مشخص"
                }, educationField, value -> {
                    educationField = value;
                    renderSection();
                });

                addOptionGroup("عملکرد تحصیلی", new String[]{
                    "ضعیف", "متوسط", "خوب", "عالی"
                }, educationPerformance, value -> {
                    educationPerformance = value;
                    renderSection();
                });
            } else {
                TextView noFormalEducation = new TextView(activity);
                noFormalEducation.setText("برای این سابقه، رشته و عملکرد تحصیلی رسمی ثبت نمی‌شود.");
                noFormalEducation.setTextColor(gold);
                noFormalEducation.setTextSize(12);
                noFormalEducation.setPadding(dp(4), dp(8), dp(4), dp(8));
                content.addView(noFormalEducation);
            }
        } else if (selectedSection == 4) {
            TextView intro = new TextView(activity);
            intro.setText("وضعیت اقتصادی فقط شرایط شروع شخصیت را مشخص می‌کند. مقدار دقیق پول یا ثروت در این مرحله نمایش داده نمی‌شود.");
            intro.setTextColor(Color.rgb(190, 202, 216));
            intro.setTextSize(12);
            intro.setPadding(dp(4), 0, dp(4), dp(10));
            content.addView(intro, new LinearLayout.LayoutParams(-1, -2));

            addEconomicStatusOptions();

            TextView economicHint = new TextView(activity);
            economicHint.setText("درآمد و منابع مالی در جریان بازی و بر اساس فعالیت‌هایی مانند کار، کسب‌وکار یا سرمایه‌گذاری شکل می‌گیرند. وضعیت اقتصادی خانواده نیز در بخش خانواده جداگانه مشخص می‌شود.");
            economicHint.setTextColor(Color.rgb(190, 202, 216));
            economicHint.setTextSize(12);
            economicHint.setPadding(dp(4), dp(10), dp(4), dp(8));
            content.addView(economicHint, new LinearLayout.LayoutParams(-1, -2));
        } else if (selectedSection == 5) {
            TextView intro = new TextView(activity);
            intro.setText("شرایط خانوادگی شخصیت را مشخص کنید. ساختار خانواده درباره محل و شیوه زندگی است؛ وضعیت والدین مستقل از این موضوع ثبت می‌شود.");
            intro.setTextColor(Color.rgb(190, 202, 216));
            intro.setTextSize(12);
            intro.setPadding(dp(4), 0, dp(4), dp(10));
            content.addView(intro, new LinearLayout.LayoutParams(-1, -2));

            addFamilyOptionGroup("ساختار و محل زندگی خانوادگی", new String[][]{
                {"TWO_PARENT_HOUSEHOLD", "زندگی با هر دو والد"},
                {"SINGLE_PARENT_HOUSEHOLD", "زندگی با یکی از والدین"},
                {"GUARDIAN_HOUSEHOLD", "زندگی با سرپرست یا قیم"},
                {"RELATIVES_HOUSEHOLD", "زندگی با بستگان"},
                {"INDEPENDENT_NO_GUARDIAN", "زندگی مستقل / بدون سرپرست"}
            }, familyStructure, value -> {
                familyStructure = value;
                renderSection();
            });

            addFamilyOptionGroup("وضعیت والدین", new String[][]{
                {"BOTH_ALIVE", "هر دو والد در قید حیات‌اند"},
                {"FATHER_ONLY_ALIVE", "فقط پدر در قید حیات است"},
                {"MOTHER_ONLY_ALIVE", "فقط مادر در قید حیات است"},
                {"NEITHER_ALIVE", "هیچ‌کدام در قید حیات نیستند"},
                {"PARENT_STATUS_UNKNOWN", "وضعیت نامشخص"}
            }, familyParentStatus, value -> {
                familyParentStatus = value;
                renderSection();
            });

            addFamilyOptionGroup("میزان حمایت خانوادگی", new String[][]{
                {"STRONG_SUPPORT", "حمایت قوی"},
                {"MODERATE_SUPPORT", "حمایت معمولی"},
                {"LIMITED_SUPPORT", "حمایت محدود"},
                {"FAMILY_CONFLICT", "روابط پرتنش"},
                {"NO_EFFECTIVE_SUPPORT", "بدون حمایت مؤثر"}
            }, familySupport, value -> {
                familySupport = value;
                renderSection();
            });

            addFamilyOptionGroup("وضعیت اقتصادی خانواده", new String[][]{
                {"FAMILY_VERY_DIFFICULT", "بسیار دشوار"},
                {"FAMILY_DIFFICULT", "دشوار"},
                {"FAMILY_ORDINARY", "معمولی"},
                {"FAMILY_SUITABLE", "مناسب"},
                {"FAMILY_FAVORABLE", "مطلوب"}
            }, familyEconomicStatus, value -> {
                familyEconomicStatus = value;
                renderSection();
            });

            TextView familyHint = new TextView(activity);
            familyHint.setText("وضعیت اقتصادی خانواده از شرایط اقتصادی شروع شخصیت در بخش قبل جداست. این انتخاب‌ها فعلاً فقط اطلاعات اولیه را ثبت می‌کنند و هنوز پیامدهای گیم‌پلی ندارند.");
            familyHint.setTextColor(Color.rgb(190, 202, 216));
            familyHint.setTextSize(12);
            familyHint.setPadding(dp(4), dp(10), dp(4), dp(8));
            content.addView(familyHint, new LinearLayout.LayoutParams(-1, -2));
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
        if (selectedSection == 0 && ageInput != null) {
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
        }

        if (selectedSection == 1 && countryCode.isEmpty()) {
            Toast.makeText(activity, "ملیت اصلی را انتخاب کنید.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedSection == 2) {
            if (residenceCountryCode.isEmpty()) {
                Toast.makeText(activity, "کشور محل زندگی را انتخاب کنید.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (residenceLocationId.isEmpty()) {
                Toast.makeText(activity, "شهر یا منطقه محل زندگی را انتخاب کنید.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (selectedSection == 3) {
            if (educationLevel.isEmpty()) {
                Toast.makeText(activity, "سطح تحصیلات را انتخاب کنید.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (educationStatus.isEmpty()) {
                Toast.makeText(activity, "وضعیت تحصیلی را انتخاب کنید.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!"بدون تحصیلات رسمی".equals(educationLevel)
                    && (educationField.isEmpty() || educationPerformance.isEmpty())) {
                Toast.makeText(activity, "رشته و عملکرد تحصیلی را مشخص کنید.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (selectedSection == 4 && economicStatus.isEmpty()) {
            Toast.makeText(activity, "وضعیت اقتصادی اولیه را انتخاب کنید.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedSection == 5) {
            if (familyStructure.isEmpty()) {
                Toast.makeText(activity, "ساختار خانواده را انتخاب کنید.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (familyParentStatus.isEmpty()) {
                Toast.makeText(activity, "وضعیت والدین را مشخص کنید.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (familySupport.isEmpty()) {
                Toast.makeText(activity, "میزان حمایت خانوادگی را انتخاب کنید.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (familyEconomicStatus.isEmpty()) {
                Toast.makeText(activity, "وضعیت اقتصادی خانواده را انتخاب کنید.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void saveCurrentFields() {
        if (nameInput != null) playerName = nameInput.getText().toString();
        if (ageInput != null) playerAge = ageInput.getText().toString();
        nameInput = null;
        ageInput = null;
    }


    private interface OptionSelectionListener {
        void onSelected(String value);
    }

    private void addOptionGroup(String label, String[] options, String selected,
                                OptionSelectionListener listener) {
        addLabel(label);
        for (int i = 0; i < options.length; i += 2) {
            LinearLayout row = new LinearLayout(activity);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
            rowParams.bottomMargin = dp(7);
            content.addView(row, rowParams);

            for (int j = i; j < Math.min(i + 2, options.length); j++) {
                final String option = options[j];
                TextView button = choiceButton(option, option.equals(selected));
                button.setTextSize(12);
                button.setPadding(dp(5), dp(5), dp(5), dp(5));
                LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(0, dp(48), 1f);
                if (j > i) buttonParams.setMargins(dp(7), 0, 0, 0);
                row.addView(button, buttonParams);
                button.setOnClickListener(v -> listener.onSelected(option));
            }
            if (options.length % 2 == 1 && i + 1 == options.length - 1) {
                View spacer = new View(activity);
                row.addView(spacer, new LinearLayout.LayoutParams(0, dp(48), 1f));
            }
        }
    }

    private void addFamilyOptionGroup(String label, String[][] options, String selected,
                                      OptionSelectionListener listener) {
        addLabel(label);
        for (int i = 0; i < options.length; i += 2) {
            LinearLayout row = new LinearLayout(activity);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
            rowParams.bottomMargin = dp(7);
            content.addView(row, rowParams);

            for (int j = i; j < Math.min(i + 2, options.length); j++) {
                final String optionId = options[j][0];
                TextView button = choiceButton(options[j][1], optionId.equals(selected));
                button.setTextSize(12);
                button.setPadding(dp(5), dp(5), dp(5), dp(5));
                LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(0, dp(52), 1f);
                if (j > i) buttonParams.setMargins(dp(7), 0, 0, 0);
                row.addView(button, buttonParams);
                button.setOnClickListener(v -> listener.onSelected(optionId));
            }
            if (options.length % 2 == 1 && i + 1 == options.length - 1) {
                View spacer = new View(activity);
                row.addView(spacer, new LinearLayout.LayoutParams(0, dp(52), 1f));
            }
        }
    }

    private void addEconomicStatusOptions() {
        String[][] options = {
            {"VERY_DIFFICULT", "بسیار دشوار", "منابع اولیه بسیار محدود و فشار اقتصادی زیاد"},
            {"DIFFICULT", "دشوار", "منابع محدود و شرایط مالی سخت"},
            {"ORDINARY", "معمولی", "شرایط اقتصادی عادی"},
            {"SUITABLE", "مناسب", "برخورداری از مقداری پشتوانه مالی"},
            {"FAVORABLE", "مطلوب", "پشتوانه مالی نسبتاً خوب و شروع راحت‌تر"}
        };

        addLabel("شرایط اقتصادی شروع شخصیت");
        for (int i = 0; i < options.length; i += 2) {
            LinearLayout row = new LinearLayout(activity);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
            rowParams.bottomMargin = dp(8);
            content.addView(row, rowParams);

            for (int j = i; j < Math.min(i + 2, options.length); j++) {
                final String statusId = options[j][0];
                boolean selected = statusId.equals(economicStatus);

                LinearLayout card = new LinearLayout(activity);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setGravity(Gravity.CENTER_VERTICAL);
                card.setPadding(dp(10), dp(7), dp(10), dp(7));
                card.setBackground(background(
                    selected ? Color.rgb(91, 71, 39) : Color.rgb(34, 54, 77),
                    10,
                    selected ? gold : Color.rgb(86, 103, 121)
                ));

                TextView title = new TextView(activity);
                title.setText(options[j][1]);
                title.setTextColor(selected ? gold : text);
                title.setTextSize(14);
                title.setTypeface(Typeface.DEFAULT_BOLD);
                card.addView(title);

                TextView description = new TextView(activity);
                description.setText(options[j][2]);
                description.setTextColor(Color.rgb(190, 202, 216));
                description.setTextSize(10);
                card.addView(description);

                LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(0, dp(82), 1f);
                if (j > i) cardParams.setMargins(dp(7), 0, 0, 0);
                row.addView(card, cardParams);

                card.setOnClickListener(v -> {
                    economicStatus = statusId;
                    renderSection();
                });
            }

            if (options.length % 2 == 1 && i + 1 == options.length - 1) {
                View spacer = new View(activity);
                row.addView(spacer, new LinearLayout.LayoutParams(0, dp(82), 1f));
            }
        }
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

    private int worldDataResourceId() {
        return activity.getResources().getIdentifier("world_data", "xml", activity.getPackageName());
    }

    private ArrayList<String[]> loadCountries() {
        ArrayList<String[]> result = new ArrayList<>();
        int resourceId = worldDataResourceId();
        if (resourceId == 0) return result;
        XmlResourceParser parser = activity.getResources().getXml(resourceId);
        try {
            int event;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG && "country".equals(parser.getName())) {
                    String code = parser.getAttributeValue(null, "code");
                    String name = parser.getAttributeValue(null, "name");
                    if (code != null && name != null) result.add(new String[]{code, name});
                }
            }
        } catch (Exception e) {
            Toast.makeText(activity, "خواندن فهرست کشورها ناموفق بود.", Toast.LENGTH_SHORT).show();
        } finally {
            parser.close();
        }
        Collator collator = Collator.getInstance(new Locale("fa"));
        Collections.sort(result, (a, b) -> collator.compare(a[1], b[1]));
        return result;
    }

    private HashSet<String> loadAvailableResidenceCountryCodes() {
        HashSet<String> result = new HashSet<>();
        int resourceId = worldDataResourceId();
        if (resourceId == 0) return result;
        XmlResourceParser parser = activity.getResources().getXml(resourceId);
        try {
            int event;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG && "location".equals(parser.getName())) {
                    String country = parser.getAttributeValue(null, "country");
                    if (country != null) result.add(country);
                }
            }
        } catch (Exception e) {
            Toast.makeText(activity, "خواندن فهرست مکان‌های قابل انتخاب ناموفق بود.", Toast.LENGTH_SHORT).show();
        } finally {
            parser.close();
        }
        return result;
    }

    private ArrayList<LocationEntry> loadLocations(String countryCodeFilter) {
        ArrayList<LocationEntry> result = new ArrayList<>();
        int resourceId = worldDataResourceId();
        if (resourceId == 0) return result;
        XmlResourceParser parser = activity.getResources().getXml(resourceId);
        try {
            int event;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG && "location".equals(parser.getName())) {
                    String country = parser.getAttributeValue(null, "country");
                    if (countryCodeFilter.equals(country)) {
                        String id = parser.getAttributeValue(null, "id");
                        String name = parser.getAttributeValue(null, "name");
                        String type = parser.getAttributeValue(null, "type");
                        if (id != null && name != null && type != null) {
                            result.add(new LocationEntry(id, name, type));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(activity, "خواندن فهرست شهرها ناموفق بود.", Toast.LENGTH_SHORT).show();
        } finally {
            parser.close();
        }
        Collator collator = Collator.getInstance(new Locale("fa"));
        Collections.sort(result, (a, b) -> collator.compare(a.name, b.name));
        return result;
    }

    private void showCountryPicker(boolean forResidence) {
        LinearLayout panel = new LinearLayout(activity);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(16), dp(12), dp(16), dp(12));
        panel.setBackground(background(Color.rgb(18, 34, 54), 14, gold));

        TextView title = new TextView(activity);
        title.setText(forResidence ? "انتخاب کشور محل زندگی" : "انتخاب ملیت");
        title.setTextColor(gold);
        title.setTextSize(19);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        panel.addView(title, new LinearLayout.LayoutParams(-1, dp(40)));

        EditText search = new EditText(activity);
        search.setSingleLine(true);
        search.setHint("جست‌وجوی کشور یا کد کشور");
        search.setTextColor(Color.WHITE);
        search.setHintTextColor(Color.rgb(155, 169, 185));
        search.setTextSize(14);
        search.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        search.setPadding(dp(12), 0, dp(12), 0);
        search.setBackground(background(Color.rgb(34, 54, 77), 9, Color.rgb(111, 94, 67)));
        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(-1, dp(46));
        searchParams.setMargins(0, dp(6), 0, dp(8));
        panel.addView(search, searchParams);

        ArrayList<String> countryLabels = new ArrayList<>();
        HashSet<String> availableResidenceCountries = forResidence
            ? loadAvailableResidenceCountryCodes() : new HashSet<String>();
        for (String[] country : loadCountries()) {
            if (forResidence && !availableResidenceCountries.contains(country[0])) continue;
            countryLabels.add(country[1] + " (" + country[0] + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, 0, countryLabels) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView row = convertView instanceof TextView
                    ? (TextView) convertView : new TextView(activity);
                row.setText(getItem(position));
                row.setTextColor(text);
                row.setTextSize(14);
                row.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                row.setPadding(dp(12), dp(8), dp(12), dp(8));
                row.setMinHeight(dp(44));
                row.setBackground(background(Color.rgb(34, 54, 77), 8, Color.rgb(70, 88, 108)));
                return row;
            }
        };

        ListView list = new ListView(activity);
        list.setAdapter(adapter);
        list.setFastScrollEnabled(true);
        list.setBackgroundColor(Color.rgb(18, 34, 54));
        panel.addView(list, new LinearLayout.LayoutParams(-1, dp(320)));

        AlertDialog dialog = new AlertDialog.Builder(activity)
            .setView(panel).setNegativeButton("بستن", null).create();

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        list.setOnItemClickListener((parent, view, position, id) -> {
            String selected = adapter.getItem(position);
            if (selected == null) return;
            int codeStart = selected.lastIndexOf('(');
            int codeEnd = selected.lastIndexOf(')');
            if (codeStart < 0 || codeEnd <= codeStart) return;
            String selectedCode = selected.substring(codeStart + 1, codeEnd);
            String selectedName = selected.substring(0, codeStart).trim();

            if (forResidence) {
                if (!selectedCode.equals(residenceCountryCode)) {
                    residenceLocationId = "";
                    residenceLocationName = "";
                    residenceType = "";
                }
                residenceCountryCode = selectedCode;
                residenceCountryName = selectedName;
            } else {
                countryName = selectedName;
                countryCode = selectedCode;
            }
            dialog.dismiss();
            renderSection();
        });
        dialog.show();
    }

    private void showLocationPicker() {
        ArrayList<LocationEntry> locations = loadLocations(residenceCountryCode);
        if (locations.isEmpty()) {
            Toast.makeText(activity, "فعلاً مکانی برای این کشور تعریف نشده است.", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout panel = new LinearLayout(activity);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(16), dp(12), dp(16), dp(12));
        panel.setBackground(background(Color.rgb(18, 34, 54), 14, gold));

        TextView title = new TextView(activity);
        title.setText("انتخاب شهر یا منطقه");
        title.setTextColor(gold);
        title.setTextSize(19);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        panel.addView(title, new LinearLayout.LayoutParams(-1, dp(40)));

        EditText search = new EditText(activity);
        search.setSingleLine(true);
        search.setHint("جست‌وجوی شهر یا منطقه");
        search.setTextColor(Color.WHITE);
        search.setHintTextColor(Color.rgb(155, 169, 185));
        search.setTextSize(14);
        search.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        search.setPadding(dp(12), 0, dp(12), 0);
        search.setBackground(background(Color.rgb(34, 54, 77), 9, Color.rgb(111, 94, 67)));
        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(-1, dp(46));
        searchParams.setMargins(0, dp(6), 0, dp(8));
        panel.addView(search, searchParams);

        ArrayAdapter<LocationEntry> adapter = new ArrayAdapter<LocationEntry>(activity, 0, locations) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView row = convertView instanceof TextView
                    ? (TextView) convertView : new TextView(activity);
                LocationEntry item = getItem(position);
                row.setText(item == null ? "" : item.toString());
                row.setTextColor(text);
                row.setTextSize(14);
                row.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                row.setPadding(dp(12), dp(8), dp(12), dp(8));
                row.setMinHeight(dp(46));
                row.setBackground(background(Color.rgb(34, 54, 77), 8, Color.rgb(70, 88, 108)));
                return row;
            }
        };

        ListView list = new ListView(activity);
        list.setAdapter(adapter);
        list.setFastScrollEnabled(true);
        list.setBackgroundColor(Color.rgb(18, 34, 54));
        panel.addView(list, new LinearLayout.LayoutParams(-1, dp(320)));

        AlertDialog dialog = new AlertDialog.Builder(activity)
            .setView(panel).setNegativeButton("بستن", null).create();

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        list.setOnItemClickListener((parent, view, position, id) -> {
            LocationEntry selected = adapter.getItem(position);
            if (selected == null) return;
            residenceLocationId = selected.id;
            residenceLocationName = selected.name;
            residenceType = selected.type;
            dialog.dismiss();
            renderSection();
        });
        dialog.show();
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
