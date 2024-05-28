package com.example.recordapplication.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recordapplication.Adapter.RecAdapter;
import com.example.recordapplication.AudioCutterActivity;
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
    public File selectedFile; // Biến lưu trữ tệp được chọn
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

        // Đăng ký context menu cho RecyclerView
        registerForContextMenu(recyclerView);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (selectedFile != null) {
            int itemId = item.getItemId();

            if (itemId == R.id.edit) {
                Intent intent = new Intent(getContext(), AudioCutterActivity.class);
                intent.putExtra("filePath", selectedFile.getAbsolutePath());
                startActivity(intent);
                return true;
            } else if (itemId == R.id.delete) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this file?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (selectedFile.delete()) {
                                fileList.remove(selectedFile);
                                recAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "File deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to delete file", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            } else {
                return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }
}