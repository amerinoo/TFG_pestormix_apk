package com.example.albert.pestormix_apk.adapters;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.albert.pestormix_apk.R;
import com.example.albert.pestormix_apk.backend.cocktailApi.model.CocktailBean;

import java.util.List;

/**
 * Created by Albert on 28/05/2016.
 */
public class ListAdapter extends WearableListView.Adapter {
    private final LayoutInflater mInflater;
    private List<CocktailBean> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapter(Context context, List<CocktailBean> dataset) {
        mInflater = LayoutInflater.from(context);
        mDataset = dataset;
    }

    // Create new views for list items
    // (invoked by the WearableListView's layout manager)
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // Inflate our custom layout for list items
        return new ItemViewHolder(mInflater.inflate(R.layout.list_item, null));
    }

    // Replace the contents of a list item
    // Instead of creating new views, the list tries to recycle existing ones
    // (invoked by the WearableListView's layout manager)
    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder,
                                 int position) {
        // retrieve the text view
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        TextView view = itemHolder.textView;
        // replace text contents
        view.setText(mDataset.get(position).getName());
        // replace list item's metadata
        holder.itemView.setTag(position);
    }

    public CocktailBean getElement(int pos){
        return mDataset.get(pos);
    }

    // Return the size of your dataset
    // (invoked by the WearableListView's layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            textView = (TextView) itemView.findViewById(R.id.name);
        }
    }
}