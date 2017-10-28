package fi.raah.android.curious_catalog_gatherer;

import android.content.SharedPreferences;

import fi.raah.android.curious_catalog_gatherer.ui.SettingsFragment;

public class Settings {

    private final SharedPreferences preferences;

    public Settings(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean isSettingsOk() {
        return !preferences.getString(SettingsFragment.CATALOG_DOMAIN_NAME, "").equals("") &&
               !preferences.getString(SettingsFragment.CATALOG_TOKEN, "").equals("");
    }
}
