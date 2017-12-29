package fi.raah.android.curious_catalog_gatherer.cards;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DetectionPhaseServiceTest {

    @Test
    public void withEmptyHistory() {
        DetectionPhaseService service = new DetectionPhaseService();

        assertInsertingToEmptyHistory(service);

        service.clear();
        assertInsertingToEmptyHistory(service);
    }

    private void assertInsertingToEmptyHistory(DetectionPhaseService service) {
        String card1 = "card1";
        DifferenceResult result1 = service.updateAndResolveDifference(card1, 1);
        assertEquals("First call should equal given count",
                1, result1.getDifference());
        assertEquals("First call should not require refresh",
                false, result1.isRefresh());

        DifferenceResult result2 = service.updateAndResolveDifference(card1, 1);
        assertEquals("Other call with same value should equal given count",
                1, result2.getDifference());
        assertEquals("Unchanged amount should not require refresh",
                false, result2.isRefresh());

        DifferenceResult result3 = service.updateAndResolveDifference(card1, 2);
        assertEquals("Bigger count should update to that count",
                2, result3.getDifference());
        assertEquals("Bigger count requires refresh",
                true, result3.isRefresh());

        DifferenceResult result4 = service.updateAndResolveDifference(card1, 1);
        assertEquals("Smaller count should result in the biggest count so far",
                2, result4.getDifference());
        assertEquals("Smaller count should not require refresh",
                false, result4.isRefresh());
    }

    @Test
    public void withExistingHistory() {
        DetectionPhaseService service = new DetectionPhaseService();

        String card2 = "cardNameOverPhases";
        service.updateAndResolveDifference(card2, 1);

        service.nextPhase();

        DifferenceResult result1 = service.updateAndResolveDifference(card2, 1);
        assertEquals("New phase detection count should be added to previous count",
                2, result1.getDifference());
        assertEquals("First call after phase change requires refresh",
                true, result1.isRefresh());

        DifferenceResult result2 = service.updateAndResolveDifference(card2, 2);
        assertEquals("New phase bigger detection count should update the count",
                3, result2.getDifference());
        assertEquals("Bigger count in next phase change requires refresh",
                true, result2.isRefresh());

        DifferenceResult result3 = service.updateAndResolveDifference(card2, 1);
        assertEquals("New phase smaller detection count should not decrease the count",
                3, result3.getDifference());
        assertEquals("Smaller count in next phase change does not require refresh",
                false, result3.isRefresh());

        assertInsertingToEmptyHistory(service);
    }

    @Test
    public void withExistingHistoryThatIsManuallyChanged() {
        DetectionPhaseService service = new DetectionPhaseService();
        String card1 = "card1";
        service.updateAndResolveDifference(card1, 1);
        service.nextPhase();
        service.increasePreviousDifference(card1);

        DifferenceResult result1 = service.updateAndResolveDifference(card1, 1);
        assertEquals("One in previous phase, one from manual and one in this phase = 3",
                3, result1.getDifference());

        service.decreasePreviousDifference(card1);
        DifferenceResult result2 = service.updateAndResolveDifference(card1, 1);
        assertEquals("One in previous phase and one in this phase = 2",
                2, result2.getDifference());

        service.decreasePreviousDifference(card1);
        service.decreasePreviousDifference(card1);
        service.decreasePreviousDifference(card1);
        DifferenceResult result3 = service.updateAndResolveDifference(card1, 1);
        assertEquals("Previous count should not go sub zero",
                1, result3.getDifference());
    }
}