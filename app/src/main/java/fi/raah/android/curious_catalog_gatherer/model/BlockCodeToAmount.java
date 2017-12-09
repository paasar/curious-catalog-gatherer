package fi.raah.android.curious_catalog_gatherer.model;

public class BlockCodeToAmount {

    private final String blockCode;
    private int amount;

    public BlockCodeToAmount(String blockCode, int amount) {
        this.blockCode = blockCode;
        this.amount = amount;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
