package com.example.recordapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recordapplication.Fragments.RecordingsFragment;
import com.example.recordapplication.OnSelectListener;
import com.example.recordapplication.R;
import com.example.recordapplication.RecViewHolder;

import java.io.File;
import java.util.HashMap;
import java.util.List;
public class RecAdapter extends RecyclerView.Adapter<RecViewHolder> {

    private Context context;
    private List<File> fileList;
    private OnSelectListener listener;
    private HashMap<File, String> fileLocations;

    public RecAdapter(Context context, List<File> fileList, OnSelectListener listener, HashMap<File, String> fileLocations) {
        this.context = context;
        this.fileList = fileList;
        this.listener = listener;
        this.fileLocations = fileLocations;
    }

    @NonNull
    @Override
    public RecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecViewHolder holder, int position) {
        File file = fileList.get(position);
        holder.tvName.setText(file.getName());
        holder.tvName.setSelected(true);

        holder.container.setOnClickListener(view -> listener.OnSelectListed(file));

        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            // Lưu trữ file được chọn vào RecordingsFragment
            if (listener instanceof RecordingsFragment) {
                ((RecordingsFragment) listener).selectedFile = file;
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}