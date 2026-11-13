package com.example.mycgame;

import android.app.Activity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.Toast;
import com.example.mycgame.data.CharacterCreationData;
import com.example.mycgame.data.CharacterCreationRepository;
import android.view.View;

import com.example.mycgame.ui.MainMenuScreen;
import com.example.mycgame.ui.NewGameScreen;
import com.example.mycgame.ui.SaveLoadScreen;

public class MainActivity extends Activity {
    private static final int SCREEN_MAIN_MENU = 0;
    private static final int SCREEN_NEW_GAME = 1;
    private static final int SCREEN_SAVE_LOAD = 2;
    private int currentScreen = SCREEN_MAIN_MENU;
    private CharacterCreationRepository repository;
    private NewGameScreen newGameScreen;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        repository = new CharacterCreationRepository(this);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        showMainMenu();
    }

    private void showMainMenu() {
        currentScreen = SCREEN_MAIN_MENU;
        newGameScreen = null;
        setContentView(new MainMenuScreen(
            this, this::startNewGame, this::continueDraft, this::showSaveLoad
        ).create());
    }

    private void showSaveLoad() {
        currentScreen = SCREEN_SAVE_LOAD;
        newGameScreen = null;
        setContentView(new SaveLoadScreen(
            this, repository, this::showMainMenu
        ).create());
    }

    private void startNewGame() {
        if (repository.loadDraft() != null) {
            new AlertDialog.Builder(this)
                .setTitle("پیش‌نویس ذخیره‌شده")
                .setMessage("یک فرایند ساخت شخصیت نیمه‌تمام وجود دارد. شروع بازی جدید، پیش‌نویس قبلی را جایگزین می‌کند.")
                .setNegativeButton("ادامه پیش‌نویس", (dialog, which) -> continueDraft())
                .setPositiveButton("شروع جدید", (dialog, which) -> {
                    repository.deleteDraft();
                    openNewGame(null);
                })
                .show();
        } else {
            openNewGame(null);
        }
    }

    private void continueDraft() {
        CharacterCreationData draft = repository.loadDraft();
        if (draft == null) {
            Toast.makeText(this, "پیش‌نویس نیمه‌تمامی وجود ندارد.", Toast.LENGTH_SHORT).show();
            return;
        }
        openNewGame(draft);
    }

    private void openNewGame(CharacterCreationData data) {
        currentScreen = SCREEN_NEW_GAME;
        newGameScreen = new NewGameScreen(this, this::showMainMenu, repository, data);
        setContentView(newGameScreen.create());
    }

    @Override
    protected void onPause() {
        if (newGameScreen != null) newGameScreen.saveDraft();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (repository != null) repository.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (currentScreen == SCREEN_NEW_GAME) {
            if (newGameScreen != null) newGameScreen.saveDraft();
            showMainMenu();
        } else if (currentScreen == SCREEN_SAVE_LOAD) {
            showMainMenu();
        } else {
            super.onBackPressed();
        }
    }
}
