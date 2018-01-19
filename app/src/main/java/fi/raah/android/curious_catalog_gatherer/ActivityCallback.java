package fi.raah.android.curious_catalog_gatherer;

import fi.raah.android.curious_catalog_gatherer.model.CardOwners;
import fi.raah.android.curious_catalog_gatherer.model.EditableCard;
import fi.raah.android.curious_catalog_gatherer.model.EditableCardCounts;

public interface ActivityCallback {
    void cardDataUpdate(CardOwners ownageList, EditableCard editableCard, boolean refresh);
    void makeShortToast(String message);
    void makeLongToast(String message);
    EditableCardCounts getEditableCardCounts();
}
