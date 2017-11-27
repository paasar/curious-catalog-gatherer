package fi.raah.android.curious_catalog_gatherer.http;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.HttpHeaders;
import fi.raah.android.curious_catalog_gatherer.Settings;

public class CatalogClient {

    private static AsyncHttpClient client = new AsyncHttpClient();
    private final Settings settings;

    public CatalogClient(Settings settings) {
        this.settings = settings;
    }

    private void get(String uri, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        updateAccessToken(settings.getCatalogToken());
        client.get(getFullUrl(uri), params, responseHandler);
    }

//    private static void post(String path, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.post(getFullUrl(path), params, responseHandler);
//    }

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

    public void testToken(String domainName, String token, AsyncJsonHttpResponseHandler responseHandler) {
        updateAccessToken(token);
        client.get("https://" + domainName + "/api/v2/ext/hello", null, responseHandler);
    }

    private void updateAccessToken(String token) {
        client.removeHeader(HttpHeaders.AUTHORIZATION);
        client.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + token);
    }
}
