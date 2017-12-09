package fi.raah.android.curious_catalog_gatherer.model;

import android.support.annotation.NonNull;

public class Ownage {
    private String owner;
    private int amount;
    private String blockName;
    private String blockCode;

    public Ownage(@NonNull String owner, int amount, @NonNull String blockName, @NonNull String blockCode) {
        this.owner = owner;
        this.amount = amount;
        this.blockName = blockName;
        this.blockCode = blockCode;
    }

    public String getOwner() {
        return owner;
    }

    public int getAmount() {
        return amount;
    }

    public String getBlockName() {
        return blockName;
    }

    public String getBlockCode() {
        return blockCode;
    }

    @Override
    public String toString() {
        return owner + " " + amount + " " + blockName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ownage ownage = (Ownage) o;

        if (amount != ownage.amount) return false;
        if (!owner.equals(ownage.owner)) return false;
        if (!blockName.equals(ownage.blockName)) return false;
        return blockCode.equals(ownage.blockCode);
    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + amount;
        result = 31 * result + blockName.hashCode();
        result = 31 * result + blockCode.hashCode();
        return result;
    }
}
