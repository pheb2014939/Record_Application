//package com.example.recordapplication;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.OpenableColumns;
//import android.view.View;
//import android.widget.Toast;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class MainActivity extends AppCompatActivity {
//
//    ConnectionClass connectionClass;
//    Connection conn;
//    String name, str;
//    RecyclerView recyclerView;
//    List<AudioFile> audioFiles = new ArrayList<>();
//    AudioFilesAdapter adapter;
//    MediaPlayer mediaPlayer;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        connectionClass = new ConnectionClass();
//        conect();
//
//        recyclerView = findViewById(R.id.rv);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new AudioFilesAdapter(audioFiles, this::playAudioFile, this::downloadAudioFile);
//        recyclerView.setAdapter(adapter);
//
//        findViewById(R.id.btnSelectAudio).setOnClickListener(this::selectAudioFile);
//
//        fetchAudioFiles();
//    }
//
//    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                    Uri selectedAudioUri = result.getData().getData();
//                    if (selectedAudioUri != null) {
//                        uploadAudioFile(selectedAudioUri);
//                    }
//                }
//            });
//
//    public void selectAudioFile(View view) {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("audio/*");
//        filePickerLauncher.launch(intent);
//    }
//
//    private void uploadAudioFile(Uri fileUri) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try {
//                conn = connectionClass.CNN();
//                InputStream inputStream = getContentResolver().openInputStream(fileUri);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                byte[] buffer = new byte[1024];
//                int length;
//                while ((length = inputStream.read(buffer)) != -1) {
//                    byteArrayOutputStream.write(buffer, 0, length);
//                }
//                byte[] audioBytes = byteArrayOutputStream.toByteArray();
//
//                String mimeType = getContentResolver().getType(fileUri);
//                String fileName = getFileName(fileUri);
//
//                String query = "INSERT INTO audio_files (name, mime_type, audio) VALUES (?, ?, ?)";
//                PreparedStatement stmt = conn.prepareStatement(query);
//                stmt.setString(1, fileName);
//                stmt.setString(2, mimeType);
//                stmt.setBytes(3, audioBytes);
//                stmt.executeUpdate();
//
//                runOnUiThread(() -> {
//                    Toast.makeText(this, "Audio file uploaded successfully!", Toast.LENGTH_SHORT).show();
//                    fetchAudioFiles();
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(this, "Failed to upload audio file.", Toast.LENGTH_SHORT).show());
//            }
//        });
//    }
//
//    private void fetchAudioFiles() {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try {
//                conn = connectionClass.CNN();
//                String query = "SELECT id, name FROM audio_files";
//                PreparedStatement stmt = conn.prepareStatement(query);
//                ResultSet rs = stmt.executeQuery();
//
//                audioFiles.clear();
//
//                while (rs.next()) {
//                    int id = rs.getInt("id");
//                    String name = rs.getString("name");
//                    audioFiles.add(new AudioFile(id, name));
//                }
//
//                runOnUiThread(() -> adapter.notifyDataSetChanged());
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch audio files.", Toast.LENGTH_SHORT).show());
//            }
//        });
//    }
//
//    private void playAudioFile(AudioFile audioFile) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try {
//                conn = connectionClass.CNN();
//                String query = "SELECT audio, mime_type FROM audio_files WHERE id = ?";
//                PreparedStatement stmt = conn.prepareStatement(query);
//                stmt.setInt(1, audioFile.getId());
//                ResultSet rs = stmt.executeQuery();
//
//                if (rs.next()) {
//                    byte[] audioBytes = rs.getBytes("audio");
//                    String mimeType = rs.getString("mime_type");
//
//                    File tempFile = File.createTempFile("audio", null, getCacheDir());
//                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//                        fos.write(audioBytes);
//                    }
//
//                    runOnUiThread(() -> {
//                        if (mediaPlayer != null) {
//                            mediaPlayer.release();
//                        }
//                        mediaPlayer = new MediaPlayer();
//                        try {
//                            mediaPlayer.setDataSource(tempFile.getAbsolutePath());
//                            mediaPlayer.prepare();
//                            mediaPlayer.start();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Toast.makeText(this, "Failed to play audio file.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(this, "Failed to retrieve audio file.", Toast.LENGTH_SHORT).show());
//            }
//        });
//    }
//
//    private void downloadAudioFile(AudioFile audioFile) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try {
//                conn = connectionClass.CNN();
//                String query = "SELECT audio, mime_type, name FROM audio_files WHERE id = ?";
//                PreparedStatement stmt = conn.prepareStatement(query);
//                stmt.setInt(1, audioFile.getId());
//                ResultSet rs = stmt.executeQuery();
//
//                if (rs.next()) {
//                    byte[] audioBytes = rs.getBytes("audio");
//                    String fileName = rs.getString("name");
//
//                    File downloadDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AudioFiles");
//                    if (!downloadDir.exists()) {
//                        downloadDir.mkdirs();
//                    }
//
//                    File outputFile = new File(downloadDir, fileName);
//                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
//                        fos.write(audioBytes);
//                    }
//
//                    runOnUiThread(() -> Toast.makeText(this, "Audio file downloaded: " + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(this, "Failed to download audio file.", Toast.LENGTH_SHORT).show());
//            }
//        });
//    }
//
//    private String getFileName(Uri uri) {
//        String result = null;
//        if (uri.getScheme().equals("content")) {
//            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
//                if (cursor != null && cursor.moveToFirst()) {
//                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
//                }
//            }
//        }
//        if (result == null) {
//            result = uri.getPath();
//            int cut = result.lastIndexOf('/');
//            if (cut != -1) {
//                result = result.substring(cut + 1);
//            }
//        }
//        return result;
//    }
//
//    public void conect() {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try {
//                conn = connectionClass.CNN();
//                if (conn == null) {
//                    str = "Error in connection with MySQL server";
//                } else {
//                    str = "Connected with MySQL server";
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            runOnUiThread(() -> Toast.makeText(this, str, Toast.LENGTH_LONG).show());
//        });
//    }
//}


//thêm nút bấm dừng ghi âm
package com.example.recordapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    ConnectionClass connectionClass;
    Connection conn;
    String name, str;
    RecyclerView recyclerView;
    List<AudioFile> audioFiles = new ArrayList<>();
    AudioFilesAdapter adapter;
    MediaPlayer mediaPlayer;
    Handler seekBarHandler = new Handler();
    Runnable updateSeekBar;
    AudioFile currentAudioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionClass = new ConnectionClass();
        conect();

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AudioFilesAdapter(audioFiles, this::playAudioFile, this::downloadAudioFile, this::onPlayClick, this::onStopClick);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnSelectAudio).setOnClickListener(this::selectAudioFile);

        fetchAudioFiles();
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedAudioUri = result.getData().getData();
                    if (selectedAudioUri != null) {
                        uploadAudioFile(selectedAudioUri);
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
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                byte[] audioBytes = byteArrayOutputStream.toByteArray();

                String mimeType = getContentResolver().getType(fileUri);
                String fileName = getFileName(fileUri);

                String query = "INSERT INTO audio_files (name, mime_type, audio) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, fileName);
                stmt.setString(2, mimeType);
                stmt.setBytes(3, audioBytes);
                stmt.executeUpdate();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Audio file uploaded", Toast.LENGTH_SHORT).show();
                    fetchAudioFiles();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to upload audio file", Toast.LENGTH_SHORT).show());
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

                runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch audio files", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void playAudioFile(AudioFile audioFile) {
        // Placeholder method for actual audio file playback
//                ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try {
//                conn = connectionClass.CNN();
//                String query = "SELECT audio, mime_type FROM audio_files WHERE id = ?";
//                PreparedStatement stmt = conn.prepareStatement(query);
//                stmt.setInt(1, audioFile.getId());
//                ResultSet rs = stmt.executeQuery();
//
//                if (rs.next()) {
//                    byte[] audioBytes = rs.getBytes("audio");
//                    String mimeType = rs.getString("mime_type");
//
//                    File tempFile = File.createTempFile("audio", null, getCacheDir());
//                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//                        fos.write(audioBytes);
//                    }
//
//                    runOnUiThread(() -> {
//                        if (mediaPlayer != null) {
//                            mediaPlayer.release();
//                        }
//                        mediaPlayer = new MediaPlayer();
//                        try {
//                            mediaPlayer.setDataSource(tempFile.getAbsolutePath());
//                            mediaPlayer.prepare();
//                            mediaPlayer.start();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Toast.makeText(this, "Failed to play audio file.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(this, "Failed to retrieve audio file.", Toast.LENGTH_SHORT).show());
//            }
//        });

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

                    runOnUiThread(() -> Toast.makeText(this, "Audio file downloaded: " + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to download audio file.", Toast.LENGTH_SHORT).show());
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
                // Load audio file from file path or URL
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
                Toast.makeText(this, "Failed to play audio file", Toast.LENGTH_SHORT).show();
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

//    private void onPlayClick(AudioFile audioFile, SeekBar seekBar) {
//        if (currentAudioFile != null && currentAudioFile.equals(audioFile)) {
//            if (mediaPlayer != null) {
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.pause();
//                } else {
//                    mediaPlayer.start();
//                }
//            }
//        } else {
//            if (mediaPlayer != null) {
//                mediaPlayer.release();
//            }
//            mediaPlayer = new MediaPlayer();
//            try {
//                // Load audio file from file path or URL
//                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                File audioFilePath = new File(downloadsDir, audioFile.getName());
//                mediaPlayer.setDataSource(audioFilePath.getAbsolutePath());
//
//                mediaPlayer.setOnPreparedListener(mp -> {
//                    mediaPlayer.start();
//                    seekBar.setMax(mediaPlayer.getDuration());
//                    currentAudioFile = audioFile;
//
//                    updateSeekBar = new Runnable() {
//                        @Override
//                        public void run() {
//                            if (mediaPlayer != null) {
//                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                                seekBarHandler.postDelayed(this, 1000);
//                            }
//                        }
//                    };
//                    seekBarHandler.postDelayed(updateSeekBar, 0);
//
//                    mediaPlayer.setOnCompletionListener(mp3 -> seekBarHandler.removeCallbacks(updateSeekBar));
//                });
//                mediaPlayer.prepareAsync(); // Asynchronous preparation
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Failed to play audio file", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser && mediaPlayer != null) {
//                    mediaPlayer.seekTo(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//    }
//
//

    private void onStopClick() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying() || mediaPlayer.isLooping() || mediaPlayer.isPlaying()) {
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
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
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

    public void conect() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                conn = connectionClass.CNN();
                if (conn == null) {
                    str = "Error in connection with MySQL server";
                } else {
                    str = "Connected with MySQL server";
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            runOnUiThread(() -> Toast.makeText(this, str, Toast.LENGTH_LONG).show());
        });
    }
}
