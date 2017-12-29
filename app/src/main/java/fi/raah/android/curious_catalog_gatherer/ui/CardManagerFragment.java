package fi.raah.android.curious_catalog_gatherer.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fi.raah.android.curious_catalog_gatherer.ActivityCallback;
import fi.raah.android.curious_catalog_gatherer.R;
import fi.raah.android.curious_catalog_gatherer.cards.CardService;
import fi.raah.android.curious_catalog_gatherer.model.BlockCodeToAmount;
import fi.raah.android.curious_catalog_gatherer.model.CardManagerAdapter;
import fi.raah.android.curious_catalog_gatherer.model.EditableCard;
import fi.raah.android.curious_catalog_gatherer.model.listener.RemoveAllListener;

public class CardManagerFragment extends ListFragment {

    private ListView listView;
    private CardManagerAdapter adapter;
    private CardService cardService;
    private ActivityCallback activityCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle inState) {
        View view = inflater.inflate(R.layout.card_manager_fragment, container, false);

        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setEmptyView(view.findViewById(android.R.id.empty));

        Button clearButton = (Button)view.findViewById(R.id.clear_cards_button);
        clearButton.setOnClickListener(new RemoveAllListener(activityCallback, adapter, cardService));

        Button reverseDifferencesButton = (Button)view.findViewById(R.id.reverse_differences_button);
        reverseDifferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.reverseDifferences();
            }
        });

        Button updateButton = (Button)view.findViewById(R.id.update_cards_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<EditableCard> editableCards = adapter.getEditableCards();
                Log.d("CCG", "Update button clicked! # of edits: " + editableCards.size());
                cardService.sendUpdatedOwnagesToCatalog(activityCallback, editableCards);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(adapter);
        if (adapter != null) {
            List<BlockCodeToAmount> m = new ArrayList<>();
        }
    }

    public void setAdapter(CardManagerAdapter adapter) {
        this.adapter = adapter;
    }

    public void setDependencies(ActivityCallback activityCallback, CardService cardService) {
        this.activityCallback = activityCallback;
        this.cardService = cardService;
    }
}
