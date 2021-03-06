package fi.raah.android.curious_catalog_gatherer.model.listener;

import android.view.View;
import android.widget.TextView;

import fi.raah.android.curious_catalog_gatherer.cards.CardService;
import fi.raah.android.curious_catalog_gatherer.model.CardManagerAdapter;
import fi.raah.android.curious_catalog_gatherer.model.EditableCard;

public class IncreaseListener implements View.OnClickListener {

    private CardManagerAdapter cardManagerAdapter;
    private CardService cardService;
    private final EditableCard editableCard;
    private final TextView differenceView;

    public IncreaseListener(CardManagerAdapter cardManagerAdapter, CardService cardService, EditableCard editableCard, TextView differenceView) {
        this.cardManagerAdapter = cardManagerAdapter;
        this.cardService = cardService;
        this.editableCard = editableCard;
        this.differenceView = differenceView;
    }

    @Override
    public void onClick(View v) {
        int currentDifference = editableCard.getDifference();
        int newDifference = plusOneSkipZero(currentDifference);
        editableCard.setDifference(newDifference);
        differenceView.setText(cardManagerAdapter.differenceAsString(newDifference));
        cardService.increasePreviousDifference(editableCard.getName());
    }

    private int plusOneSkipZero(int currentDifference) {
        return currentDifference == -1 ? 1 : currentDifference + 1;
    }
}
