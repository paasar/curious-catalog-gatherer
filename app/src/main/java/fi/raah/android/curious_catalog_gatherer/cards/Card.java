package fi.raah.android.curious_catalog_gatherer.cards;

public class Card {

    private final String name;
    private final String multiverseId;

    public Card(String name, String multiverseId) {
        this.name = name;
        this.multiverseId = multiverseId;
    }

    public String getName() {
        return name;
    }

    public String getMultiverseId() {
        return multiverseId;
    }
}
