package fi.raah.android.curious_catalog_gatherer.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fi.raah.android.curious_catalog_gatherer.R;
import fi.raah.android.curious_catalog_gatherer.model.CardInfoAdapter;

public class CardInfoFragment extends ListFragment {

    private TextView cardNameView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle inState) {
        View view = inflater.inflate(R.layout.card_info_fragment, container, false);
        cardNameView = (TextView)view.findViewById(R.id.card_name);

        CardInfoAdapter cardInfoAdapter = (CardInfoAdapter) getListAdapter();
        cardNameView.setText(cardInfoAdapter.getCardName());

        return view;
    }

    public void setCardName(String cardName) {
        if (cardNameView != null) {
            cardNameView.setText(cardName);
        }
    }
}
