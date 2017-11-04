package fi.raah.android.curious_catalog_gatherer.http;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import fi.raah.android.curious_catalog_gatherer.Settings;

public class CatalogClient {

    private static AsyncHttpClient client = new AsyncHttpClient();
    private final Settings settings;

    public CatalogClient(Settings settings) {
        this.settings = settings;
    }

    private void get(String uri, RequestParams params, AsyncHttpResponseHandler responseHandler) {
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
            get("/api/ext/cards?cardName=" + URLEncoder.encode(cardName, "UTF-8"), params, asyncJsonHttpResponseHandler);
        } catch (UnsupportedEncodingException e) {
            Log.e("CCG", "ERROR " + e.getMessage());
        }
    }
}
