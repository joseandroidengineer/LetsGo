package com.jge.letsgo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jge.letsgo.R;
import com.jge.letsgo.models.GoLocation;


import java.util.List;

public class GoListAdapter extends RecyclerView.Adapter<GoListViewHolder> {
    private List<GoLocation> listOfLocations;
    public GoListAdapter(List listOfLocations){
        this.listOfLocations = listOfLocations;

    }
    @NonNull
    @Override
    public GoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new GoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoListViewHolder holder, int position) {
        GoLocation goLocation = listOfLocations.get(position);
        holder.titleView.setText(goLocation.name);
        holder.descriptionView.setText(goLocation.description);
    }

    @Override
    public int getItemCount() {
        if(listOfLocations == null){
            return 0;
        }else{
            return listOfLocations.size();
        }
    }
}
