package com.example.recordapplication.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recordapplication.AudioFile;
import com.example.recordapplication.AudioFilesAdapter;
import com.example.recordapplication.ConnectionClass;
import com.example.recordapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadFileFragment extends Fragment {

    private ConnectionClass connectionClass;
    private Connection conn;
    private RecyclerView recyclerView;
    private List<AudioFile> audioFiles = new ArrayList<>();
    private AudioFilesAdapter adapter;
    private Handler seekBarHandler = new Handler();
    private Runnable updateSeekBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        connectionClass = new ConnectionClass();
        connect();

        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AudioFilesAdapter(audioFiles, this::openAudioFileWithExternalApp, this::downloadAudioFile);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btnSelectAudio).setOnClickListener(this::selectAudioFile);

        fetchAudioFiles();

        return view;
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri selectedAudioUri = data.getData();
                        if (selectedAudioUri != null) {
                            uploadAudioFile(selectedAudioUri);
                        }
                    }
                }
            });
    public void selectAudioFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        filePickerLauncher.launch(intent);
    }

    private void uploadAudioFile(Uri fileUri) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                conn = connectionClass.CNN();
                InputStream inputStream = getActivity().getContentResolver().openInputStream(fileUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                byte[] audioBytes = byteArrayOutputStream.toByteArray();

                String mimeType = getActivity().getContentResolver().getType(fileUri);
                String fileName = getFileName(fileUri);

                String query = "INSERT INTO audio_files (name, mime_type, audio) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, fileName);
                stmt.setString(2, mimeType);
                stmt.setBytes(3, audioBytes);
                stmt.executeUpdate();

                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Audio file uploaded", Toast.LENGTH_SHORT).show();
                    fetchAudioFiles();
                });
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to upload audio file", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void fetchAudioFiles() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                conn = connectionClass.CNN();
                String query = "SELECT id, name FROM audio_files";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                audioFiles.clear();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    audioFiles.add(new AudioFile(id, name));
                }

                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to fetch audio files", Toast.LENGTH_SHORT).show());
            }
        });
    }



    private void openAudioFileWithExternalApp(AudioFile audioFile) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String query = "SELECT audio FROM audio_files WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, audioFile.getId());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    byte[] audioBytes = rs.getBytes("audio");
                    File audioFileTemp = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "temp_audio.mp3");
                    try (FileOutputStream fos = new FileOutputStream(audioFileTemp)) {
                        fos.write(audioBytes);
                    }

                    getActivity().runOnUiThread(() -> OnSelectListed(audioFileTemp));
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to open audio file", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void OnSelectListed(File file) {
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/x-wav");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
    private void downloadAudioFile(AudioFile audioFile) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                conn = connectionClass.CNN();
                String query = "SELECT audio, mime_type, name FROM audio_files WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, audioFile.getId());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    byte[] audioBytes = rs.getBytes("audio");
                    String fileName = rs.getString("name");

                    File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!downloadsDir.exists()) {
                        downloadsDir.mkdirs();
                    }

                    File outputFile = new File(downloadsDir, fileName);
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        fos.write(audioBytes);
                    }

                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Audio file downloaded: " + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to download audio file.", Toast.LENGTH_SHORT).show());
            }
        });
    }




    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    public void connect() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                conn = connectionClass.CNN();
                String str;
                if (conn == null) {
                    str = "Error in connection with MySQL server";
                } else {
                    str = "Connected with MySQL server";
                }
                String finalStr = str;
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), finalStr, Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }





}
