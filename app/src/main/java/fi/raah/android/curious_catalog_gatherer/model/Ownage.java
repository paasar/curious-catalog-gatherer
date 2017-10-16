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
}
