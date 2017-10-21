package fi.raah.android.curious_catalog_gatherer.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

public class CardOwnersAdapter extends ArrayAdapter<Ownage> {
    private List<Ownage> ownageList;
    private String cardName;

    public CardOwnersAdapter(@NonNull Context context, @NonNull List<Ownage> ownages) {
        super(context, android.R.layout.simple_list_item_1, ownages);
        this.ownageList = ownages;
        cardName = "No card scanned";
    }

    public void updateOwnageList(CardOwners cardOwners) {
        cardName = cardOwners.getCardName();

        this.ownageList.clear();
        this.ownageList.addAll(cardOwners.getOwnageList());
        notifyDataSetChanged();
    }

    public String getCardName() {
        return cardName;
    }
}
