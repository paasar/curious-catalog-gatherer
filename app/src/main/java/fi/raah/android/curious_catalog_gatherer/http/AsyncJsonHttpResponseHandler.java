package fi.raah.android.curious_catalog_gatherer.http;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AsyncJsonHttpResponseHandler extends JsonHttpResponseHandler {
    @Override
    public boolean getUseSynchronousMode() {
        return false;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d("CCG", "It was an object! " + response);
        throw new UnsupportedOperationException("onSuccess object method not implemented.");
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray array) {
        Log.d("CCG", "It was an array! " + array);
        throw new UnsupportedOperationException("onSuccess array method not implemented.");
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.d("CCG", "It was a failure! " + responseString);
        throw new UnsupportedOperationException("onFailure method not implemented.");
    }
}
