package fi.raah.android.curious_catalog_gatherer;

import fi.raah.android.curious_catalog_gatherer.model.CardOwners;

public interface ActivityCallback {
    void updateOwners(CardOwners ownageList);
    void makeToast(String message);
}
