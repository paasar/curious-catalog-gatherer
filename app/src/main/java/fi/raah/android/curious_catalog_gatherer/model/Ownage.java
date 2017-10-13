package fi.raah.android.curious_catalog_gatherer.model;

public class Ownage {
    private String owner;
    private int amount;
    private String block;

    public Ownage(String owner, int amount, String block) {
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
}
