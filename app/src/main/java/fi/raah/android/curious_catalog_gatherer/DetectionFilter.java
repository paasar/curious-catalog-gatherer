package fi.raah.android.curious_catalog_gatherer;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

import fi.raah.android.curious_catalog_gatherer.cards.CardService;

public class DetectionFilter {

    //TODO Dagger?
    private CardService cardService;

    public DetectionFilter(CardService cardService) {
        this.cardService = cardService;
    }

    public List<TextBlock> filterSingleLineBlocks(SparseArray<TextBlock> blocks) {
        List<TextBlock> result = new ArrayList<>();

        for (int i = 0, length = blocks.size(); i < length; i++ ) {
            TextBlock block = blocks.valueAt(i);
            List<Line> lines = (List<Line>)block.getComponents();
            if (lines.size() == 1) {
                result.add(block);
            }
        }

        return result;
    }

    public CardAndNonCard splitIntoCardsAndNonCards(List<TextBlock> blocks) {
        CardAndNonCard result = new CardAndNonCard();

        for (TextBlock block : blocks) {
            if (cardService.isCardName(block.getValue())) {
                result.addCardBlock(block);
            } else {
                result.addNonCardBlock(block);
            }
        }

        return result;
    }

    public class CardAndNonCard {
        private List<TextBlock> cardBlocks = new ArrayList<>();
        private List<TextBlock> nonCardBlocks = new ArrayList<>();

        public void addCardBlock(TextBlock block) {
            cardBlocks.add(block);
        }

        public void addNonCardBlock(TextBlock block) {
            nonCardBlocks.add(block);
        }

        public List<TextBlock> getCardBlocks() {
            return cardBlocks;
        }

        public List<TextBlock> getNonCardBlocks() {
            return nonCardBlocks;
        }
    }
}
