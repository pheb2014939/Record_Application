package com.example.recordapplication;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    RecyclerView recyclerView;
    List<AudioFile> audioFiles = new ArrayList<>();
    AudioFilesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionClass = new ConnectionClass();
        connect();

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AudioFilesAdapter(audioFiles, this::openAudioFileWithExternalApp, this::downloadAudioFile);
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

    private String getFileName(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String fileName = cursor.getString(nameIndex);
        cursor.close();
        return fileName;
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
                    File audioFileTemp = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "temp_audio.mp3");
                    try (FileOutputStream fos = new FileOutputStream(audioFileTemp)) {
                        fos.write(audioBytes);
                    }

                    runOnUiThread(() -> OnSelectListed(audioFileTemp));
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to open audio file", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void OnSelectListed(File file) {
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/x-wav");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void downloadAudioFile(AudioFile audioFile) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String query = "SELECT audio, mime_type, name FROM audio_files WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, audioFile.getId());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    byte[] audioBytes = rs.getBytes("audio");
                    String mimeType = rs.getString("mime_type");
                    String fileName = rs.getString("name");

                    File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File downloadedFile = new File(downloadsDir, fileName);

                    try (FileOutputStream fos = new FileOutputStream(downloadedFile)) {
                        fos.write(audioBytes);
                    }


                    Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", downloadedFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileUri, mimeType);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    runOnUiThread(() -> startActivity(intent));
                    runOnUiThread(() -> Toast.makeText(this, "Audio file downloaded", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to download audio file", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void connect() {
        new Thread(() -> {
            try {
                conn = connectionClass.CNN();
                String query = "SELECT * FROM users WHERE email= '" + getIntent().getStringExtra("email") + "'";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("username");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Welcome, " + name, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
