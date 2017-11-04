package fi.raah.android.curious_catalog_gatherer.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fi.raah.android.curious_catalog_gatherer.R;

import static fi.raah.android.curious_catalog_gatherer.Settings.CATALOG_DOMAIN_NAME;
import static fi.raah.android.curious_catalog_gatherer.Settings.CATALOG_TOKEN;


public class SettingsFragment extends Fragment {

    // TODO Settings
    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        final TextInputEditText domainNameInput = (TextInputEditText)view.findViewById(R.id.catalog_domain_name_input);
        final TextInputEditText tokenInput = (TextInputEditText)view.findViewById(R.id.catalog_token_input);

        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        domainNameInput.setText(preferences.getString(CATALOG_DOMAIN_NAME, null));
        tokenInput.setText(preferences.getString(CATALOG_TOKEN, null));

        Button saveButton = (Button)view.findViewById(R.id.save_setting_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO check validity
                String domainName = domainNameInput.getText().toString();
                String token = tokenInput.getText().toString();
                saveSettings(domainName, token);
            }
        });

        Button clearButton = (Button)view.findViewById(R.id.clear_setting_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings(null, null);

                domainNameInput.setText(null);
                tokenInput.setText(null);
            }
        });

        return view;
    }

    private void saveSettings(String domainName, String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CATALOG_DOMAIN_NAME, domainName);
        editor.putString(CATALOG_TOKEN, token);
        editor.apply();
    }
}
