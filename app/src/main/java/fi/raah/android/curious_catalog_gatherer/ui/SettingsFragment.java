package fi.raah.android.curious_catalog_gatherer.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import fi.raah.android.curious_catalog_gatherer.MainActivity;
import fi.raah.android.curious_catalog_gatherer.R;
import fi.raah.android.curious_catalog_gatherer.Settings;
import fi.raah.android.curious_catalog_gatherer.http.AsyncJsonHttpResponseHandler;
import fi.raah.android.curious_catalog_gatherer.http.CatalogClient;
import fi.raah.android.curious_catalog_gatherer.model.DomainAndToken;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private MainActivity activity;
    private Settings settings;
    private CatalogClient catalogClient;

    private TextInputEditText domainNameInput;
    private TextInputEditText tokenInput;
    private TextInputEditText usernameInput;
    private TextView messageText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        this.activity = activity;
        this.settings = activity.getSettings();
        this.catalogClient = activity.getCatalogClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        domainNameInput = (TextInputEditText)view.findViewById(R.id.catalog_domain_name_input);
        tokenInput = (TextInputEditText)view.findViewById(R.id.catalog_token_input);
        usernameInput = (TextInputEditText)view.findViewById(R.id.catalog_username_input);
        messageText = (TextView)view.findViewById(R.id.settings_message);


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

        Button scanQRCodeButton = (Button)view.findViewById(R.id.scan_qr_button);
        scanQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // TODO Use the Mobile Vision API for QR code reading instead of an external app.
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");

                    startActivityForResult(intent, 0);
                } catch (Exception e){
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validateAndSaveDataFromIntent();
    }

    private void validateAndSaveDataFromIntent() {
        DomainAndToken domainAndToken = activity.getDomainAndToken();
        if (domainAndToken != null) {
            updateUI_validateAndSave(domainAndToken.getDomainName(), domainAndToken.getToken());
        }
    }

    private void updateUI_validateAndSave(String domainName, String token) {
        domainNameInput.setText(domainName);
        tokenInput.setText(token);
        usernameInput.setText("");

        validateAndSave(domainName, token, messageText, usernameInput);
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
                messageText.setText(
                        "Could not connect. Please, check the settings. Error was: " + statusCode +
                        " " + throwable.getMessage());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                messageText.setText(
                        "Could not connect. Please, check the settings. Error was: " + statusCode +
                        " " + throwable.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                if (contents.contains("/")) {
                    String[] scannedData = contents.split("/");
                    if (scannedData.length == 2) {
                        String domainName = scannedData[0];
                        String token = scannedData[1];
                        updateUI_validateAndSave(domainName, token);
                    }
                }
            }
            if (resultCode == RESULT_CANCELED){
                activity.makeShortToast("QR code reading was cancelled.");
            }
        }
        activity.resetReturnFromQRScan();
    }
}
