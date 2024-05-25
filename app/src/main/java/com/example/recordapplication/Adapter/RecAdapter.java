//package com.example.recordapplication.Adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.recordapplication.OnSelectListener;
//import com.example.recordapplication.R;
//import com.example.recordapplication.RecViewHolder;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.List;
//
//public class RecAdapter extends RecyclerView.Adapter<RecViewHolder> {
//
//    private Context context;
//    private List<File> fileList;
//    private OnSelectListener listener;
//
//
////    public RecAdapter(Context context, List<File> fileList, OnSelectListener listener) {
////        this.context = context;
////        this.fileList = fileList;
////        this.listener = listener;
////    }
//
//    //moi them
//    private HashMap<File, String> fileLocations;
//
//    public RecAdapter(Context context, List<File> fileList, OnSelectListener listener, HashMap<File, String> fileLocations) {
//        this.context = context;
//        this.fileList = fileList;
//        this.listener = listener;
//        this.fileLocations = fileLocations;
//    }
//    public void setFileLocation(File file, String location) {
//        fileLocations.put(file, location);
//        notifyDataSetChanged();
//    }
//
//
//
//    @NonNull
//    @Override
//    public RecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new RecViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_item, parent, false));
//
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecViewHolder holder, int position) {
//
//        holder.tvName.setText(fileList.get(position).getName());
//        holder.tvName.setSelected(true);
//
//        holder.container.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                listener.OnSelectListed(fileList.get(position));
//            }
//        });
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return fileList.size();
//    }
//}


// lưu địa chỉ người dùng cùng với tệp MP3 khi ghi âm và hiển thị địa chỉ lấy

//package com.example.recordapplication.Adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.recordapplication.OnSelectListener;
//import com.example.recordapplication.R;
//import com.example.recordapplication.RecViewHolder;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.List;
//
//public class RecAdapter extends RecyclerView.Adapter<RecViewHolder> {
//
//    private Context context;
//    private List<File> fileList;
//    private OnSelectListener listener;
//    private HashMap<File, String> fileLocations;
//
//    public RecAdapter(Context context, List<File> fileList, OnSelectListener listener, HashMap<File, String> fileLocations) {
//        this.context = context;
//        this.fileList = fileList;
//        this.listener = listener;
//        this.fileLocations = fileLocations;
//    }
//
//    @NonNull
//    @Override
//    public RecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new RecViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_item, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecViewHolder holder, int position) {
//        File file = fileList.get(position);
//        holder.tvName.setText(file.getName());
//        holder.tvName.setSelected(true);
//
//        String location = fileLocations.get(file);
//        holder.textViewUserAddress.setText(location != null ? location : "Unknown location");
//
//        holder.container.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                listener.OnSelectListed(file);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return fileList.size();
//    }
//
//    public void setFileLocation(File file, String location) {
//        fileLocations.put(file, location);
//        notifyDataSetChanged();
//    }
//}
// update tiếp
package com.example.recordapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        String location = fileLocations.get(file);
//        holder.textViewUserAddress.setText(location != null ? location : "Unknown location");

        holder.container.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                listener.OnSelectListed(file);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void setFileLocation(File file, String location) {
        fileLocations.put(file, location);
        notifyDataSetChanged();
    }
}
