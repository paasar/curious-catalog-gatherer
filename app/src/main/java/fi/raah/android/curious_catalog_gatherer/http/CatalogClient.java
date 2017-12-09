package fi.raah.android.curious_catalog_gatherer.http;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;
import fi.raah.android.curious_catalog_gatherer.Settings;

public class CatalogClient {

    private static AsyncHttpClient client = new AsyncHttpClient();
    private final Context context;
    private final Settings settings;

    public CatalogClient(Context context, Settings settings) {
        this.context = context;
        this.settings = settings;
    }

    private void get(String uri, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        updateAccessToken(settings.getCatalogToken());
        client.get(getFullUrl(uri), params, responseHandler);
    }

    private void post(String uri, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        updateAccessToken(settings.getCatalogToken());
        client.post(context, getFullUrl(uri), entity, "application/json", responseHandler);
    }

    private String getFullUrl(String uri) {
        return "https://" + settings.getCatalogDomainName() + uri;
    }

    public void getCardOwners(String cardName, RequestParams params, AsyncJsonHttpResponseHandler asyncJsonHttpResponseHandler) {
        try {
            get("/api/v2/ext/cards?cardName=" + URLEncoder.encode(cardName, "UTF-8"), params, asyncJsonHttpResponseHandler);
        } catch (UnsupportedEncodingException e) {
            Log.e("CCG", "ERROR " + e.getMessage());
        }
    }

    public void updateCard(String multiverseId, int newAmount, AsyncJsonHttpResponseHandler responseHandler) {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("multiverseid", multiverseId);
            jsonParams.put("ownedCount", newAmount);
            StringEntity entity = new StringEntity(jsonParams.toString());
            post("/api/v2/ext/cards", entity, responseHandler);
        } catch (JSONException e) {
            Log.e("CCG", "JSON exception while updating card.", e);
        } catch (UnsupportedEncodingException e) {
            Log.e("CCG", "Failed to create StringEntity for card update.", e);
        }
    }

    public void testToken(String domainName, String token, AsyncJsonHttpResponseHandler responseHandler) {
        updateAccessToken(token);
        client.get("https://" + domainName + "/api/v2/ext/hello", null, responseHandler);
    }

    private void updateAccessToken(String token) {
        client.removeHeader(HttpHeaders.AUTHORIZATION);
        client.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + token);
    }
}
