package fi.raah.android.curious_catalog_gatherer.model.listener;

import android.util.Log;
import android.view.View;

import fi.raah.android.curious_catalog_gatherer.ActivityCallback;
import fi.raah.android.curious_catalog_gatherer.model.CardManagerAdapter;

public class RemoveAllListener implements View.OnClickListener {
    private final ActivityCallback callback;
    private final CardManagerAdapter cardManagerAdapter;

    private boolean clicked = false;

    public RemoveAllListener(ActivityCallback callback, CardManagerAdapter cardManagerAdapter) {
        this.callback = callback;
        this.cardManagerAdapter = cardManagerAdapter;
        this.clicked = false;
    }

    @Override
    public void onClick(View v) {
        if (clicked) {
            cardManagerAdapter.clear();
        } else {
            callback.makeToast("Click again to remove all cards.");
            resetAfterAWhile();
        }
        clicked = true;
    }

    private void resetAfterAWhile() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    clicked = false;
                } catch (InterruptedException e) {
                    Log.e("CCG", "Could not reset remove button.", e);
                }

            }
        }.start();
    }
}
