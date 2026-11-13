package com.example.mycgame.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycgame.data.CharacterCreationData;
import com.example.mycgame.data.CharacterCreationRepository;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class SaveLoadScreen {
    private final Activity activity;
    private final CharacterCreationRepository repository;
    private final Runnable onBack;
    private final int gold = Color.rgb(255, 194, 70);
    private final int background = Color.rgb(7, 20, 38);
    private final int panelColor = Color.rgb(24, 43, 64);
    private LinearLayout saveList;

    public SaveLoadScreen(Activity activity, CharacterCreationRepository repository, Runnable onBack) {
        this.activity = activity;
        this.repository = repository;
        this.onBack = onBack;
    }

    public View create() {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(14), dp(16), dp(12));
        root.setBackgroundColor(background);

        TextView title = new TextView(activity);
        title.setText("بارگذاری بازی");
        title.setTextColor(gold);
        title.setTextSize(25);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(54)));

        TextView subtitle = new TextView(activity);
        subtitle.setText("شخصیت‌های ذخیره‌شده در این دستگاه");
        subtitle.setTextColor(Color.rgb(202, 214, 226));
        subtitle.setTextSize(13);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, dp(34)));

        ScrollView scroll = new ScrollView(activity);
        saveList = new LinearLayout(activity);
        saveList.setOrientation(LinearLayout.VERTICAL);
        saveList.setPadding(dp(2), dp(8), dp(2), dp(8));
        scroll.addView(saveList, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        Button back = new Button(activity);
        back.setText("بازگشت به منوی اصلی");
        back.setOnClickListener(v -> onBack.run());
        root.addView(back, new LinearLayout.LayoutParams(-1, dp(48)));

        refresh();
        return root;
    }

    private void refresh() {
        saveList.removeAllViews();
        List<CharacterCreationRepository.SaveSlot> saves = repository.listSaves();
        if (saves.isEmpty()) {
            TextView empty = new TextView(activity);
            empty.setText("هنوز هیچ شخصیت ذخیره‌شده‌ای وجود ندارد.\nپس از تکمیل ساخت شخصیت، ذخیره آن در این بخش نمایش داده می‌شود.");
            empty.setTextColor(Color.rgb(210, 220, 230));
            empty.setTextSize(15);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(18), dp(28), dp(18), dp(28));
            empty.setBackground(cardBackground());
            saveList.addView(empty, new LinearLayout.LayoutParams(-1, -2));
            return;
        }

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        for (CharacterCreationRepository.SaveSlot slot : saves) {
            LinearLayout card = new LinearLayout(activity);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(14), dp(12), dp(14), dp(10));
            card.setBackground(cardBackground());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
            cardParams.setMargins(0, 0, 0, dp(10));
            saveList.addView(card, cardParams);

            TextView name = new TextView(activity);
            name.setText(slot.name);
            name.setTextColor(gold);
            name.setTextSize(18);
            name.setTypeface(Typeface.DEFAULT_BOLD);
            card.addView(name);

            TextView date = new TextView(activity);
            date.setText("آخرین ذخیره: " + dateFormat.format(new Date(slot.updatedAt)));
            date.setTextColor(Color.rgb(190, 203, 216));
            date.setTextSize(12);
            LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(-1, -2);
            dateParams.topMargin = dp(4);
            card.addView(date, dateParams);

            LinearLayout actions = new LinearLayout(activity);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams actionsParams = new LinearLayout.LayoutParams(-1, dp(44));
            actionsParams.topMargin = dp(9);
            card.addView(actions, actionsParams);

            Button load = new Button(activity);
            load.setText("مشاهده / بارگذاری");
            actions.addView(load, new LinearLayout.LayoutParams(0, -1, 1f));
            load.setOnClickListener(v -> openSave(slot.id));

            Button delete = new Button(activity);
            delete.setText("حذف");
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(dp(88), -1);
            deleteParams.leftMargin = dp(8);
            actions.addView(delete, deleteParams);
            delete.setOnClickListener(v -> confirmDelete(slot));
        }
    }

    private void openSave(long id) {
        CharacterCreationData data = repository.loadCharacter(id);
        if (data == null) {
            Toast.makeText(activity, "خواندن اطلاعات این ذخیره ممکن نشد.", Toast.LENGTH_LONG).show();
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("نام: ").append(value(data.playerName)).append('\n');
        details.append("سن: ").append(value(data.playerAge)).append('\n');
        details.append("جنسیت: ").append(genderLabel(data.gender)).append('\n');
        details.append("ملیت: ").append(value(data.countryName)).append('\n');
        details.append("محل زندگی: ").append(value(data.residenceLocationName))
            .append("، ").append(value(data.residenceCountryName)).append('\n');
        details.append("تحصیلات: ").append(value(data.educationLevel)).append('\n');
        details.append("وضعیت اقتصادی: ").append(value(data.economicStatus)).append('\n');
        details.append("مهارت‌های عمومی: ").append(data.selectedGeneralSkillIds.size()).append('\n');
        details.append("مهارت‌های تخصصی: ").append(data.selectedSpecializedSkillIds.size());

        new AlertDialog.Builder(activity)
            .setTitle("اطلاعات شخصیت ذخیره‌شده")
            .setMessage(details.toString() +
                "\n\nاطلاعات با موفقیت از دیتابیس خوانده شد. ورود به دنیای بازی هنوز پیاده‌سازی نشده است؛ این ذخیره دست‌نخورده باقی می‌ماند.")
            .setPositiveButton("متوجه شدم", null)
            .show();
    }

    private void confirmDelete(CharacterCreationRepository.SaveSlot slot) {
        new AlertDialog.Builder(activity)
            .setTitle("حذف ذخیره")
            .setMessage("آیا از حذف ذخیره «" + slot.name + "» مطمئن هستید؟ این کار قابل بازگشت نیست.")
            .setNegativeButton("انصراف", null)
            .setPositiveButton("حذف", (dialog, which) -> {
                if (repository.deleteCharacter(slot.id)) {
                    Toast.makeText(activity, "ذخیره حذف شد.", Toast.LENGTH_SHORT).show();
                    refresh();
                } else {
                    Toast.makeText(activity, "حذف ذخیره انجام نشد.", Toast.LENGTH_SHORT).show();
                }
            })
            .show();
    }

    private String value(String text) {
        return text == null || text.trim().isEmpty() ? "ثبت نشده" : text;
    }

    private String genderLabel(String gender) {
        if ("MALE".equalsIgnoreCase(gender)) return "مرد";
        if ("FEMALE".equalsIgnoreCase(gender)) return "زن";
        return value(gender);
    }

    private GradientDrawable cardBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(panelColor);
        drawable.setCornerRadius(dp(13));
        drawable.setStroke(dp(1), Color.rgb(105, 121, 139));
        return drawable;
    }

    private int dp(float value) {
        return (int)(value * activity.getResources().getDisplayMetrics().density + .5f);
    }
}
