package fi.raah.android.curious_catalog_gatherer;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fi.raah.android.curious_catalog_gatherer.model.Ownage;

public class OwnersOverlayFragment extends ListFragment {

    private List<Ownage> ownageList = new ArrayList<>();
    private ArrayAdapter<Ownage> adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ownageList.add(new Ownage("laa", 1, "blaa"));
        ownageList.add(new Ownage("laa2", 1, "blaa"));
        ownageList.add(new Ownage("laa3", 1, "blaa"));
        ownageList.add(new Ownage("laa4", 1, "blaa"));
        ownageList.add(new Ownage("laa5", 1, "blaa"));
//        ownageList.add(new Ownage("EMPTY", 0, ""));
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, ownageList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    public void updateOwnageList(List<Ownage> ownageList) {
        if (adapter == null) return;

        this.ownageList.clear();
        this.ownageList.addAll(ownageList);
        adapter.notifyDataSetChanged();
    }
}
