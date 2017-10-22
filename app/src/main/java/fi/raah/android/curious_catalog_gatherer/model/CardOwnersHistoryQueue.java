package fi.raah.android.curious_catalog_gatherer.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public class CardOwnersHistoryQueue {
    private int max;
    private Map<String, CardOwners> queue =
             Collections.synchronizedMap(
                new LinkedHashMap<String, CardOwners>() {
                    @Override
                    protected boolean removeEldestEntry(Entry eldest) {
                        return size() > max;
                    }
                });

    public CardOwnersHistoryQueue(int max) {
        this.max = max;
    }

    public void push(CardOwners cardOwners) {
        String key = cardOwners.getCardName();
        if (queue.containsKey(key)) {
            queue.remove(key);
        }

        queue.put(key, cardOwners);
    }

    public int size() {
        return queue.size();
    }

    private int reversePosition(int position) {
        return (size() - 1) - position;
    }


    public CardOwners get(int position) {
        return (CardOwners)queue.values().toArray()[reversePosition(position)];
    }
}
