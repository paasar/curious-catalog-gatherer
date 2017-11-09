package fi.raah.android.curious_catalog_gatherer.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fi.raah.android.curious_catalog_gatherer.R;
import fi.raah.android.curious_catalog_gatherer.model.CardOwnersAdapter;

public class OwnersOverlayFragment extends ListFragment {

    private TextView cardNameView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle inState) {
        View view = inflater.inflate(R.layout.owners_overlay_fragment, container, false);
        cardNameView = (TextView)view.findViewById(R.id.card_name);

        CardOwnersAdapter cardOwnersAdapter = (CardOwnersAdapter) getListAdapter();
        cardNameView.setText(cardOwnersAdapter.getCardName());

        return view;
    }

    public void setCardName(String cardName) {
        if (cardNameView != null) {
            cardNameView.setText(cardName);
        }
    }
}
