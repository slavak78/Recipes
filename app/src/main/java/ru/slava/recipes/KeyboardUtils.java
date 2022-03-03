package ru.slava.recipes;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;


public class KeyboardUtils {
    private static KeyboardUtils instance;
    private InputMethodManager inputMethodManager;
    private KeyboardUtils() {

    }

    public static KeyboardUtils getInstance() {
        if (instance == null) instance = new KeyboardUtils();
        return instance;
    }

    private InputMethodManager getInputMethodManager() {
        if (inputMethodManager == null)
            inputMethodManager = (InputMethodManager) Recipes.get().getSystemService(Activity.INPUT_METHOD_SERVICE);
        return inputMethodManager;
    }

    public void hide(final Activity activity) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                getInputMethodManager().hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            } catch (NullPointerException e) {
                e.printStackTrace(); }
        });
    }
}

