package com.example.recordapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//public class RecViewHolder extends RecyclerView.ViewHolder {
//
//    public TextView tvName;
////    public TextView textViewUserAddress; // Thêm TextView cho địa chỉ
//    public View container;
//
//    public RecViewHolder(@NonNull View itemView) {
//        super(itemView);
//        tvName = itemView.findViewById(R.id.tvName);
////        textViewUserAddress = itemView.findViewById(R.id.textViewUserAddress); // Liên kết TextView địa chỉ
//        container = itemView.findViewById(R.id.container);
//    }
//}
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recordapplication.R;

public class RecViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    public TextView tvName;
    public View container;

    public RecViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        container = itemView.findViewById(R.id.container);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }
}
