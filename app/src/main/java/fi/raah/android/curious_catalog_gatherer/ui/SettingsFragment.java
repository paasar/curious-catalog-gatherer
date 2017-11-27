package fi.raah.android.curious_catalog_gatherer.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import fi.raah.android.curious_catalog_gatherer.R;
import fi.raah.android.curious_catalog_gatherer.Settings;
import fi.raah.android.curious_catalog_gatherer.http.AsyncJsonHttpResponseHandler;
import fi.raah.android.curious_catalog_gatherer.http.CatalogClient;

public class SettingsFragment extends Fragment {

    private Settings settings;
    private CatalogClient catalogClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        final TextInputEditText domainNameInput = (TextInputEditText)view.findViewById(R.id.catalog_domain_name_input);
        final TextInputEditText tokenInput = (TextInputEditText)view.findViewById(R.id.catalog_token_input);
        final TextInputEditText usernameInput = (TextInputEditText)view.findViewById(R.id.catalog_username_input);
        final TextView messageText = (TextView)view.findViewById(R.id.settings_message);

        domainNameInput.setText(settings.getCatalogDomainName());
        tokenInput.setText(settings.getCatalogToken());
        usernameInput.setText(settings.getUsername());

        Button saveButton = (Button)view.findViewById(R.id.save_setting_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String domainName = domainNameInput.getText().toString();
                final String token = tokenInput.getText().toString();

                validateAndSave(domainName, token, messageText, usernameInput);
            }
        });

        Button clearButton = (Button)view.findViewById(R.id.clear_setting_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.saveSettings(null, null, null);

                domainNameInput.setText(null);
                tokenInput.setText(null);
                usernameInput.setText(null);
            }
        });

        return view;
    }

    private void validateAndSave(final String domainName, final String token, final TextView messageText, final TextInputEditText usernameInput) {
        catalogClient.testToken(domainName, token, new AsyncJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String username = response.getString("username");
                    settings.saveSettings(domainName, token, username);
                    messageText.setText("Hello, " + username +"! Settings saved.");
                    usernameInput.setText(username);
                } catch (JSONException e) {
                    //TODO
                    Log.e("CCG", "Failed to parse username.", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                messageText.setText("Could not connect. Please, check the settings. Error code was: " + statusCode);
            }
        });
    }

    public void setDependencies(Settings settings, CatalogClient catalogClient) {
        this.settings = settings;
        this.catalogClient = catalogClient;
    }
}
