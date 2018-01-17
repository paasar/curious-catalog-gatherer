package fi.raah.android.curious_catalog_gatherer.model;

public class EditableCardCounts {

    private final int unique;
    private final int total;

    public EditableCardCounts(int unique, int total) {
        this.unique = unique;
        this.total = total;
    }

    public int getUnique() {
        return unique;
    }

    public int getTotal() {
        return total;
    }
}
