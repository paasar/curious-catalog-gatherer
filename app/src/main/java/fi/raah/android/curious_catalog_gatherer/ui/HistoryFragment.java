package fi.raah.android.curious_catalog_gatherer.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import fi.raah.android.curious_catalog_gatherer.R;
import fi.raah.android.curious_catalog_gatherer.model.HistoryListAdapter;

public class HistoryFragment extends Fragment {
    private ExpandableListView listView;
    private HistoryListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        listView = (ExpandableListView) view.findViewById(android.R.id.list);

        listView.setGroupIndicator(null);
        listView.setEmptyView(view.findViewById(android.R.id.empty));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView.setAdapter(adapter);
    }

    public void setAdapter(HistoryListAdapter adapter) {
        this.adapter = adapter;
    }
}
