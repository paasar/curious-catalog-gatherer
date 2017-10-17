package fi.raah.android.curious_catalog_gatherer;

import java.util.List;

import fi.raah.android.curious_catalog_gatherer.model.CardOwners;
import fi.raah.android.curious_catalog_gatherer.model.Ownage;

public interface OwnersListener {
    void updateOwners(CardOwners ownageList);
}
