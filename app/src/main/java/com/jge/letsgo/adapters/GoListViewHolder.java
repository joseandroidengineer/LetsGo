package com.jge.letsgo.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jge.letsgo.R;

public class GoListViewHolder extends RecyclerView.ViewHolder {
    public TextView titleView;
    public TextView descriptionView;
    public GoListViewHolder(@NonNull View itemView) {
        super(itemView);
        titleView = itemView.findViewById(R.id.title_view);
        descriptionView = itemView.findViewById(R.id.description_view);

    }
}
