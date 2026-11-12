package com.example.mycgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.mycgame.ui.MainMenuScreen;
import com.example.mycgame.ui.NewGameScreen;

public class MainActivity extends Activity {
    private static final int SCREEN_MAIN_MENU = 0;
    private static final int SCREEN_NEW_GAME = 1;
    private int currentScreen = SCREEN_MAIN_MENU;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
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
        setContentView(new MainMenuScreen(this, this::showNewGame).create());
    }

    private void showNewGame() {
        currentScreen = SCREEN_NEW_GAME;
        setContentView(new NewGameScreen(this, this::showMainMenu).create());
    }

    @Override
    public void onBackPressed() {
        if (currentScreen == SCREEN_NEW_GAME) {
            showMainMenu();
        } else {
            super.onBackPressed();
        }
    }
}
