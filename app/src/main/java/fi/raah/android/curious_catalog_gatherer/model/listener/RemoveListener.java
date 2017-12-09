package fi.raah.android.curious_catalog_gatherer.model.listener;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import fi.raah.android.curious_catalog_gatherer.model.CardManagerAdapter;
import fi.raah.android.curious_catalog_gatherer.model.EditableCard;

public class RemoveListener implements View.OnClickListener {
    private final Activity activity;
    private final CardManagerAdapter cardManagerAdapter;
    private final EditableCard editableCard;

    private boolean clicked = false;

    public RemoveListener(Activity activity, CardManagerAdapter cardManagerAdapter, EditableCard editableCard) {
        this.activity = activity;
        this.cardManagerAdapter = cardManagerAdapter;
        this.editableCard = editableCard;
        this.clicked = false;
    }

    @Override
    public void onClick(View v) {
        if (clicked) {
            cardManagerAdapter.remove(editableCard);
        } else {
            Toast.makeText(activity, "Click again to remove card.", Toast.LENGTH_SHORT).show();
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
