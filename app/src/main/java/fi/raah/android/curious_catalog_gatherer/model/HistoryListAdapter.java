package fi.raah.android.curious_catalog_gatherer.model;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import fi.raah.android.curious_catalog_gatherer.R;

public class HistoryListAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inflater;
    private CardOwnersHistoryQueue history;

    public HistoryListAdapter(Activity activity, CardOwnersHistoryQueue ownageHistory) {
        history = ownageHistory;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getGroupCount() {
        return history.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return history.get(groupPosition).getOwnageList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return history.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return history.get(groupPosition).getOwnageList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();

            holder.text = (TextView) convertView.findViewById(R.id.historyListItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(getChild(groupPosition, childPosition).toString());

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_group, parent, false);

            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.historyListHeader);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(getGroup(groupPosition).toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void push(CardOwners cardOwners) {
        history.push(cardOwners);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView text;
    }
}
