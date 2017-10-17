package fi.raah.android.curious_catalog_gatherer.model;

import java.util.List;

public class CardOwners {
    private String cardName;
    private List<Ownage> ownageList;

    public CardOwners(String cardName, List<Ownage> ownageList) {
        this.cardName = cardName;
        this.ownageList = ownageList;
    }

    public String getCardName() {
        return cardName;
    }

    public List<Ownage> getOwnageList() {
        return ownageList;
    }

}
