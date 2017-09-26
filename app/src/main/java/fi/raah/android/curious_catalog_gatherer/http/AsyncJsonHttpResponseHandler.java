package fi.raah.android.curious_catalog_gatherer.http;

import com.loopj.android.http.JsonHttpResponseHandler;

public class AsyncJsonHttpResponseHandler extends JsonHttpResponseHandler {
    @Override
    public boolean getUseSynchronousMode() {
        return false;
    }
}
