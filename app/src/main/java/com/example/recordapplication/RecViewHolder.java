//package com.example.recordapplication;
//
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class RecViewHolder extends RecyclerView.ViewHolder {
//
//
//    public TextView tvName;
//    public LinearLayout container;
//
//    public TextView textViewUserAddress;
//
//    public RecViewHolder(@NonNull View itemView) {
//        super(itemView);
//        tvName = itemView.findViewById(R.id.txtName);
//        container = itemView.findViewById(R.id.container);
//        textViewUserAddress = itemView.findViewById(R.id.textViewUserAddress);
//    }
//}
//// lưu địa chỉ người dùng cùng với tệp MP3 khi ghi âm và hiển thị địa chỉ lấy
//package com.example.recordapplication;
//
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class RecViewHolder extends RecyclerView.ViewHolder {
//    public TextView tvName;
//    public LinearLayout container;
//    public TextView textViewUserAddress;
//
//    public RecViewHolder(@NonNull View itemView) {
//        super(itemView);
//        tvName = itemView.findViewById(R.id.txtName);
//        container = itemView.findViewById(R.id.container);
//        textViewUserAddress = itemView.findViewById(R.id.textViewUserAddress);
//    }
//}

// cập nhật tiếp
package com.example.recordapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecViewHolder extends RecyclerView.ViewHolder {

    public TextView tvName;
//    public TextView textViewUserAddress; // Thêm TextView cho địa chỉ
    public View container;

    public RecViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
//        textViewUserAddress = itemView.findViewById(R.id.textViewUserAddress); // Liên kết TextView địa chỉ
        container = itemView.findViewById(R.id.container);
    }
}
