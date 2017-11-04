package fi.raah.android.curious_catalog_gatherer;

import android.content.SharedPreferences;

public class Settings {

    public static final String CATALOG_DOMAIN_NAME = "catalogDomainName";
    public static final String CATALOG_TOKEN = "catalogToken";

    private final SharedPreferences preferences;

    public Settings(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean isSettingsOk() {
        return !preferences.getString(CATALOG_DOMAIN_NAME, "").equals("") &&
               !preferences.getString(CATALOG_TOKEN, "").equals("");
    }

    public String getCatalogDomainName() {
        return preferences.getString(CATALOG_DOMAIN_NAME, "");
    }

    public String getCatalogToken() {
        return preferences.getString(CATALOG_TOKEN, "");
    }
}
