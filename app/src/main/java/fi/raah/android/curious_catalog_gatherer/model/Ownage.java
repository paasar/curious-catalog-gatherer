package fi.raah.android.curious_catalog_gatherer.model;

import android.support.annotation.NonNull;

public class Ownage {
    private String owner;
    private int amount;
    private String block;

    public Ownage(@NonNull String owner, int amount, @NonNull String block) {
        this.owner = owner;
        this.amount = amount;
        this.block = block;
    }

    public String getOwner() {
        return owner;
    }

    public int getAmount() {
        return amount;
    }

    public String getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return owner + " " + amount + " " + block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ownage ownage = (Ownage) o;

        if (amount != ownage.amount) return false;
        if (owner != null ? !owner.equals(ownage.owner) : ownage.owner != null) return false;
        return block != null ? block.equals(ownage.block) : ownage.block == null;

    }

    @Override
    public int hashCode() {
        int result = owner != null ? owner.hashCode() : 0;
        result = 31 * result + amount;
        result = 31 * result + (block != null ? block.hashCode() : 0);
        return result;
    }
}
