package fi.raah.android.curious_catalog_gatherer.model.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import fi.raah.android.curious_catalog_gatherer.model.EditableCard;

public class BlockCodeListener implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private EditableCard item;
    private TextView ownedCount;

    public BlockCodeListener(Spinner spinner, EditableCard item, TextView ownedCount) {
        this.spinner = spinner;
        this.item = item;
        this.ownedCount = ownedCount;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String blockCode = (String)spinner.getSelectedItem();

        item.setSelectedBlockIndex(position);
        int newCount = item.getOwnedCount(blockCode);
        ownedCount.setText(String.valueOf(newCount));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        throw new UnsupportedOperationException();
    }
}
