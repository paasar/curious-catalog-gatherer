package fi.raah.android.curious_catalog_gatherer;

import fi.raah.android.curious_catalog_gatherer.model.CardOwners;
import fi.raah.android.curious_catalog_gatherer.model.EditableCard;

public interface ActivityCallback {
    void cardDataUpdate(CardOwners ownageList, EditableCard editableCard, boolean refresh);
    void makeToast(String message);
}
