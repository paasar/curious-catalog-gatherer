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

    public int getTotalOwnage() {
        int sum = 0;
        for (Ownage ownage : ownageList) {
            sum += ownage.getAmount();
        }

        return sum;
    }

    @Override
    public String toString() {
        return cardName + " " + getTotalOwnage();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardOwners that = (CardOwners) o;

        if (cardName != null ? !cardName.equals(that.cardName) : that.cardName != null)
            return false;
        return ownageList != null ? ownageList.equals(that.ownageList) : that.ownageList == null;
    }

    @Override
    public int hashCode() {
        int result = cardName != null ? cardName.hashCode() : 0;
        result = 31 * result + (ownageList != null ? ownageList.hashCode() : 0);
        return result;
    }
}
