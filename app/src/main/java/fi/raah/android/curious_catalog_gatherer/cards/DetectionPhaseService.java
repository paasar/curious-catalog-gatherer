package fi.raah.android.curious_catalog_gatherer.cards;

import java.util.HashMap;
import java.util.Map;

public class DetectionPhaseService {
    private final Map<String, PrevAndCurrent> phaseData = new HashMap<>();

    DifferenceResult updateAndResolveDifference(String cardName, int detectionCount) {
        PrevAndCurrent prevAndCurrent = phaseData.get(cardName);
        if (prevAndCurrent != null) {
            int current = prevAndCurrent.getCurrent();

            if (current < detectionCount) {
                prevAndCurrent.setCurrent(detectionCount);
                return new DifferenceResult(prevAndCurrent.getOngoingCount(), true);
            } else {
                return new DifferenceResult(prevAndCurrent.getOngoingCount(), false);
            }

        } else {
            phaseData.put(cardName, new PrevAndCurrent(detectionCount));
            return new DifferenceResult(detectionCount, false);
        }
    }

    void nextPhase() {
        for (Map.Entry<String, PrevAndCurrent> prevAndCurrentEntry : phaseData.entrySet()) {
            PrevAndCurrent prevAndCurrent = prevAndCurrentEntry.getValue();
            prevAndCurrent.setPrevious(prevAndCurrent.getOngoingCount());
            prevAndCurrent.setCurrent(0);
        }
    }

    public void clear() {
        phaseData.clear();
    }

    public void increasePreviousDifference(String cardName) {
        PrevAndCurrent prevAndCurrent = phaseData.get(cardName);
        prevAndCurrent.increasePrevious();
    }

    public void decreasePreviousDifference(String cardName) {
        //TODO Going under zero should be handled somehow. Currently this may just feel whacky.
        PrevAndCurrent prevAndCurrent = phaseData.get(cardName);
        prevAndCurrent.decreasePrevious();
    }

    private class PrevAndCurrent {
        private int previous = 0;
        private int current;

        PrevAndCurrent(int current) {
            this.current = current;
        }

        int getPrevious() {
            return previous;
        }

        void setPrevious(int previous) {
            this.previous = previous;
        }

        int getCurrent() {
            return current;
        }

        void setCurrent(int current) {
            this.current = current;
        }

        int getOngoingCount() {
            return previous + current;
        }

        void increasePrevious() {
            this.previous += 1;
        }

        void decreasePrevious() {
            if (this.previous > 1) {
                this.previous -= 1;
            } else {
                this.previous = 0;
            }
        }
    }
}
