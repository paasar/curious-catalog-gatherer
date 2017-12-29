package fi.raah.android.curious_catalog_gatherer.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import fi.raah.android.curious_catalog_gatherer.MainActivity;
import fi.raah.android.curious_catalog_gatherer.R;
import fi.raah.android.curious_catalog_gatherer.cards.CardService;
import fi.raah.android.curious_catalog_gatherer.model.listener.BlockCodeListener;
import fi.raah.android.curious_catalog_gatherer.model.listener.DecreaseListener;
import fi.raah.android.curious_catalog_gatherer.model.listener.IncreaseListener;
import fi.raah.android.curious_catalog_gatherer.model.listener.RemoveListener;

public class CardManagerAdapter extends ArrayAdapter<EditableCard> {

    private MainActivity activity;
    private final CardService cardService;
    private final List<EditableCard> cardList;

    public CardManagerAdapter(@NonNull MainActivity activity, CardService cardService, @NonNull List<EditableCard> cardList) {
        super(activity, R.layout.edit_item, cardList);

        this.activity = activity;
        this.cardService = cardService;
        this.cardList = cardList;
    }

    static class ViewHolder {
        public TextView cardName;
        public Spinner blockCodeSpinner;
        public TextView ownedCount;
        public TextView difference;
        public Button increase;
        public Button decrease;
        public Button remove;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        EditableCard item = cardList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            view = inflater.inflate(R.layout.edit_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.cardName = (TextView) view.findViewById(R.id.editListItem_cardName);
            viewHolder.ownedCount = (TextView) view.findViewById(R.id.editListItem_ownedCount);
            viewHolder.blockCodeSpinner = (Spinner) view.findViewById(R.id.editListItem_blockCodeSpinner);
            viewHolder.difference = (TextView) view.findViewById(R.id.editListItem_difference);
            viewHolder.increase = (Button) view.findViewById(R.id.editListItem_increase);
            viewHolder.decrease = (Button) view.findViewById(R.id.editListItem_decrease);
            viewHolder.remove = (Button) view.findViewById(R.id.editListItem_remove);

            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.cardName.setText(item.getName());

        holder.difference.setText(differenceAsString(item.getDifference()));
        holder.increase.setOnClickListener(new IncreaseListener(this, cardService, item, holder.difference));
        holder.decrease.setOnClickListener(new DecreaseListener(this, cardService, item, holder.difference));
        holder.remove.setOnClickListener(new RemoveListener(activity, this, item));

        ArrayAdapter adapter = new ArrayAdapter<>(activity, R.layout.spinner_item, item.getBlockCodes());
        holder.blockCodeSpinner.setAdapter(adapter);
        holder.blockCodeSpinner.setOnItemSelectedListener(new BlockCodeListener(holder.blockCodeSpinner, item, holder.ownedCount));
        holder.blockCodeSpinner.setSelection(item.getSelectedBlockIndex());
        adapter.notifyDataSetChanged();

        return view;
    }

    public String differenceAsString(int difference) {
        if (difference > 0) {
            return "+" + difference;
        } else {
            return String.valueOf(difference);
        }
    }

    @Override
    public void add(@Nullable EditableCard editableCard) {
        for (EditableCard card : cardList) {
            if (card.getName().equals(editableCard.getName())) {
                return;
            }
        }
        super.add(editableCard);
        Collections.sort(cardList);
        //TODO Increase existing card's difference after break and same card
        // or two cards in same recognition
    }

    public void removeByName(String cardName) {
        for (EditableCard editableCard : cardList) {
            if (editableCard.getName().equals(cardName)) {
                cardList.remove(editableCard);
                return;
            }
        }
    }

    public void reverseDifferences() {
        for (EditableCard editableCard : cardList) {
            editableCard.reverseDifference();
            notifyDataSetChanged();
        }
    }

    public List<EditableCard> getEditableCards() {
        return cardList;
    }
}
