package fi.raah.android.curious_catalog_gatherer;

import android.content.SharedPreferences;

public class Settings {

    public static final String CATALOG_DOMAIN_NAME = "catalogDomainName";
    public static final String CATALOG_TOKEN = "catalogToken";
    public static final String CATALOG_USERNAME = "catalogUsername";

    private final SharedPreferences preferences;

    public Settings(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean isSettingsOk() {
        return !getCatalogDomainName().equals("") &&
               !getCatalogToken().equals("") &&
               !getUsername().equals("");
    }

    public String getCatalogDomainName() {
        return preferences.getString(CATALOG_DOMAIN_NAME, "");
    }

    public String getCatalogToken() {
        return preferences.getString(CATALOG_TOKEN, "");
    }

    public String getUsername() {
        return preferences.getString(CATALOG_USERNAME, "");
    }

    public void saveSettings(String domainName, String token, String username) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CATALOG_DOMAIN_NAME, domainName);
        editor.putString(CATALOG_TOKEN, token);
        editor.putString(CATALOG_USERNAME, username);
        editor.apply();
    }
}
