package fi.raah.android.curious_catalog_gatherer.cards;

public class DifferenceResult {
    private final int difference;
    private final boolean refresh;

    public DifferenceResult(int difference, boolean refresh) {
        this.difference = difference;
        this.refresh = refresh;
    }

    public int getDifference() {
        return difference;
    }

    public boolean isRefresh() {
        return refresh;
    }
}
