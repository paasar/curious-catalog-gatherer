package fi.raah.android.curious_catalog_gatherer.model.listener;

import android.util.Log;
import android.view.View;

import fi.raah.android.curious_catalog_gatherer.ActivityCallback;
import fi.raah.android.curious_catalog_gatherer.model.CardManagerAdapter;
import fi.raah.android.curious_catalog_gatherer.model.EditableCard;

public class RemoveListener implements View.OnClickListener {
    private final ActivityCallback callback;
    private final CardManagerAdapter cardManagerAdapter;
    private final EditableCard editableCard;

    private boolean clicked = false;

    public RemoveListener(ActivityCallback callback, CardManagerAdapter cardManagerAdapter, EditableCard editableCard) {
        this.callback = callback;
        this.cardManagerAdapter = cardManagerAdapter;
        this.editableCard = editableCard;
        this.clicked = false;
    }

    @Override
    public void onClick(View v) {
        if (clicked) {
            cardManagerAdapter.remove(editableCard);
        } else {
            callback.makeShortToast("Click again to remove card.");
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
