//package com.example.recordapplication.Fragments;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.FileProvider;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.recordapplication.Adapter.RecAdapter;
//import com.example.recordapplication.OnSelectListener;
//import com.example.recordapplication.R;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class RecordingsFragment extends Fragment implements OnSelectListener {
//
//    private RecyclerView recyclerView;
//    private List<File> fileList;
//    private RecAdapter recAdapter;
//
//
//    private File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
//    View view;
//
//    public RecordingsFragment() {
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_recording, container, false);
//        displayFiles();
//        return view;
//
//
//    }
//
//    private void displayFiles() {
//        recyclerView = view.findViewById(R.id.recycler_records);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
//        fileList = new ArrayList<>();
//        fileList.addAll(filesFile(path));
//        recAdapter = new RecAdapter(getContext(), fileList, this);
//        recyclerView.setAdapter(recAdapter);
//
//    }
//
//
//
//
//    public ArrayList<File> filesFile(File file){
//        ArrayList<File> arrayList = new ArrayList<>();
//        File[] files = file.listFiles();
//
//        for (File singleFile : files){
//            if (singleFile.getName().toLowerCase().endsWith(".mp3")){
//                arrayList.add(singleFile);
//            }
//        }
//        return arrayList;
//    }
//
//
//    @Override
//    public void OnSelectListed(File file) {
//
//        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "audio/x-wav");
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        getContext().startActivity(intent);
//
//
//
//
//
//    }
//
//
//
//
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser){
//            displayFiles();
//        }
//    }
//
//
//
//
//}
//
//
//
//
//day là cái đang dùng
//package com.example.recordapplication.Fragments;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.FileProvider;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.recordapplication.Adapter.RecAdapter;
//import com.example.recordapplication.OnSelectListener;
//import com.example.recordapplication.R;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//public class RecordingsFragment extends Fragment implements OnSelectListener {
//
//    private RecyclerView recyclerView;
//    private List<File> fileList;
//    private RecAdapter recAdapter;
//
//    private File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
//    View view;
//
//    public RecordingsFragment() {
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_recording, container, false);
//        displayFiles();
//        return view;
//    }
//
//    private void displayFiles() {
//        recyclerView = view.findViewById(R.id.recycler_records);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
//        fileList = new ArrayList<>();
//        fileList.addAll(filesFile(path));
//
//        // Sắp xếp fileList theo thời gian, mới nhất ở trên đầu
//        Collections.sort(fileList, new Comparator<File>() {
//            @Override
//            public int compare(File file1, File file2) {
//                return Long.compare(file2.lastModified(), file1.lastModified());
//            }
//        });
//
//        recAdapter = new RecAdapter(getContext(), fileList, this);
//        recyclerView.setAdapter(recAdapter);
//    }
//
//    public ArrayList<File> filesFile(File file) {
//        ArrayList<File> arrayList = new ArrayList<>();
//        File[] files = file.listFiles();
//
//        for (File singleFile : files) {
//            if (singleFile.getName().toLowerCase().endsWith(".mp3")) {
//                arrayList.add(singleFile);
//            }
//        }
//        return arrayList;
//    }
//
//    @Override
//    public void OnSelectListed(File file) {
//        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "audio/x-wav");
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        getContext().startActivity(intent);
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            displayFiles();
//        }
//    }
//}


// lưu địa chỉ người dùng cùng với tệp MP3 khi ghi âm và hiển thị địa chỉ
package com.example.recordapplication.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recordapplication.Adapter.RecAdapter;
import com.example.recordapplication.OnSelectListener;
import com.example.recordapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class RecordingsFragment extends Fragment implements OnSelectListener {

    private RecyclerView recyclerView;
    private List<File> fileList;
    private RecAdapter recAdapter;
    private HashMap<File, String> fileLocations = new HashMap<>();

    private File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
    View view;

    public RecordingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recording, container, false);
        displayFiles();
        return view;
    }

    private void displayFiles() {
        recyclerView = view.findViewById(R.id.recycler_records);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        fileList = new ArrayList<>();
        fileList.addAll(filesFile(path));

        // Sắp xếp fileList theo thời gian, mới nhất ở trên đầu
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return Long.compare(file2.lastModified(), file1.lastModified());
            }
        });

        recAdapter = new RecAdapter(getContext(), fileList, this, fileLocations);
        recyclerView.setAdapter(recAdapter);
    }

    public ArrayList<File> filesFile(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile : files) {
            if (singleFile.getName().toLowerCase().endsWith(".mp3")) {
                arrayList.add(singleFile);
            }
        }
        return arrayList;
    }

    @Override
    public void OnSelectListed(File file) {
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/x-wav");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        getContext().startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            displayFiles();
        }
    }
}

