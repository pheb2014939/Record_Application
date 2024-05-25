package com.example.recordapplication.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
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
    private MediaPlayer mediaPlayer;
    private Handler seekBarHandler = new Handler();
    private Runnable updateSeekBar;
    private AudioFile currentAudioFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        connectionClass = new ConnectionClass();
        connect();

        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AudioFilesAdapter(audioFiles, this::playAudioFile, this::downloadAudioFile, this::onPlayClick, this::onStopClick);
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

    private void playAudioFile(AudioFile audioFile) {
        // Placeholder method for actual audio file playback
        // Add your playback logic here
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

                    File downloadDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AudioFiles");
                    if (!downloadDir.exists()) {
                        downloadDir.mkdirs();
                    }

                    File outputFile = new File(downloadDir, fileName);
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

    private void onPlayClick(AudioFile audioFile, SeekBar seekBar) {
        if (currentAudioFile != null && currentAudioFile.equals(audioFile)) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                return;
            }
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            try {
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File audioFilePath = new File(downloadsDir, audioFile.getName());
                mediaPlayer.setDataSource(audioFilePath.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());

                currentAudioFile = audioFile;

                updateSeekBar = new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            seekBarHandler.postDelayed(this, 1000);
                        }
                    }
                };
                seekBarHandler.postDelayed(updateSeekBar, 0);

                mediaPlayer.setOnCompletionListener(mp -> seekBarHandler.removeCallbacks(updateSeekBar));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to play audio file", Toast.LENGTH_SHORT).show();
            }
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void onStopClick() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying() || mediaPlayer.isLooping()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            seekBarHandler.removeCallbacks(updateSeekBar);
            currentAudioFile = null;
        }
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
