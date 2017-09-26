package fi.raah.android.curious_catalog_gatherer.http;

import com.loopj.android.http.*;

public class CatalogClient {

    private static final String BASE_URL = ;//Intentionally left blank for now as requested by the catalog service maintainer.

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String uri, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getFullUrl(uri), params, responseHandler);
    }

//    public static void post(String path, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.post(getFullUrl(path), params, responseHandler);
//    }

    private static String getFullUrl(String uri) {
        return BASE_URL + uri;
    }
}
