package com.valette.defossez.firemapp.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.Arrays;
import java.util.List;

public class AddressAdapter extends ArrayAdapter<String> {

    List<String> addresses;

    public AddressAdapter(Context context, int id, List<String> addresses) {
        super(context, id, addresses);
        this.addresses = addresses;
    }

    @Override
    public Filter getFilter() {
        return addressFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter addressFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            filterResults.values = Arrays.asList("dummy");
            filterResults.count = addresses.size();
            return filterResults;
    }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if(!addresses.isEmpty()){
                if(getItem(0) != addresses.toArray()[0]){
                    clear();
                    addAll(addresses);
                }
            } else {
                addAll(addresses);
            }
        }
    };
}