package fi.raah.android.curious_catalog_gatherer.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fi.raah.android.curious_catalog_gatherer.R;
import fi.raah.android.curious_catalog_gatherer.model.CardOwners;
import fi.raah.android.curious_catalog_gatherer.model.Ownage;

public class OwnersOverlayFragment extends ListFragment {

    private TextView cardNameView;

    private List<Ownage> ownageList = new ArrayList<>();
    private ArrayAdapter<Ownage> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle inState) {
        View view = inflater.inflate(R.layout.owners_overlay_fragment, container, false);
        cardNameView = (TextView)view.findViewById(R.id.card_name);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, ownageList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    public void updateOwnageList(CardOwners cardOwners) {
        if (adapter == null) return;

        cardNameView.setText(cardOwners.getCardName());

        this.ownageList.clear();
        //TODO empty list -> "no owners" or empty view?
        this.ownageList.addAll(cardOwners.getOwnageList());
        adapter.notifyDataSetChanged();
    }
}
