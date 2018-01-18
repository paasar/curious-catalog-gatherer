package fi.raah.android.curious_catalog_gatherer.cards;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CardServiceTest {

    @Test
    public void removeBracketsFromStart() {
        assertEquals("Fling", CardService.removeBracketsFromStart("Fling"));
        assertEquals("Fling", CardService.removeBracketsFromStart("(Fling"));
        assertEquals("Fling", CardService.removeBracketsFromStart("((Fling"));
        assertEquals("Fling (something)", CardService.removeBracketsFromStart("Fling (something)"));
    }
}
