package fi.raah.android.curious_catalog_gatherer.ui;

import android.util.SparseIntArray;

import fi.raah.android.curious_catalog_gatherer.R;

public class Icons {
    private SparseIntArray onIcons = new SparseIntArray();
    private SparseIntArray offIcons = new SparseIntArray();

    public Icons() {
        onIcons.put(R.id.action_card_history, R.drawable.ic_history_white_inverse_24dp);
        onIcons.put(R.id.action_manage_cards, R.drawable.ic_mode_edit_white_inverse_24dp);
        onIcons.put(R.id.action_card_overlay, R.drawable.ic_remove_red_eye_white_inverse_24dp);
        onIcons.put(R.id.action_manage_settings, R.drawable.ic_settings_white_inverse_24dp);

        offIcons.put(R.id.action_card_history, R.drawable.ic_history_white_24dp);
        offIcons.put(R.id.action_manage_cards, R.drawable.ic_mode_edit_white_24dp);
        offIcons.put(R.id.action_card_overlay, R.drawable.ic_remove_red_eye_white_24dp);
        offIcons.put(R.id.action_manage_settings, R.drawable.ic_settings_white_24dp);
    }

    public int off(int itemId) {
        return offIcons.get(itemId);
    }

    public int on(int itemId) {
        return onIcons.get(itemId);
    }
}
