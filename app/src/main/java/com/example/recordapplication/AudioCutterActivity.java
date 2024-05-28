package com.example.recordapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.florescu.android.rangeseekbar.BuildConfig;
import org.florescu.android.rangeseekbar.RangeSeekBar;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import java.util.UUID;

import java.io.File;
import java.io.IOException;
import androidx.core.content.FileProvider;

public class AudioCutterActivity extends AppCompatActivity {

    private static final String TAG = "AudioCutterActivity";

    private RangeSeekBar<Integer> rangeSeekBar;
    private TextView tvStart, tvEnd;
    private Button btnCutAudio;

    private String filePath;
    private MediaPlayer mediaPlayer;
    private int audioDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_cutter);

        rangeSeekBar = findViewById(R.id.rangeSeekBar);
        tvStart = findViewById(R.id.tvStart);
        tvEnd = findViewById(R.id.tvEnd);
        btnCutAudio = findViewById(R.id.btnCutAudio);

        // Get the file path from the intent
        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");

        if (filePath == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize MediaPlayer to get audio duration
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            audioDuration = mediaPlayer.getDuration() / 1000; // duration in seconds
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load audio file", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up RangeSeekBar
        rangeSeekBar.setRangeValues(0, audioDuration);
        rangeSeekBar.setOnRangeSeekBarChangeListener((bar, minValue, maxValue) -> {
            tvStart.setText("Start: " + formatTime(minValue));
            tvEnd.setText("End: " + formatTime(maxValue));
        });

        btnCutAudio.setOnClickListener(v -> cutAudio(rangeSeekBar.getSelectedMinValue(), rangeSeekBar.getSelectedMaxValue()));
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
    private void cutAudio(int startSeconds, int endSeconds) {
        File inputFile = new File(filePath);

        // Lấy tên tệp gốc và tạo tên tệp đầu ra với "cut_" phía trước tên gốc
        String originalFileName = inputFile.getName();
        String outputFileName = "cut_" + originalFileName;
        // Đường dẫn đến thư mục Downloads
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");


        // Tạo thư mục Downloads nếu nó không tồn tại
        if (!path.exists()) {
            path.mkdirs();
        }
        // Tạo outputFile trong thư mục Downloads
        File outputFile = new File(path, outputFileName);

        String startTime = formatTimeFFmpeg(startSeconds);
        String duration = formatTimeFFmpeg(endSeconds - startSeconds);

        String[] command = {
                "-y", // Thêm cờ -y để buộc ghi đè tệp
                "-i", inputFile.getAbsolutePath(),
                "-ss", startTime,
                "-t", duration,
                "-acodec", "mp3",
                "-vn", // Chỉ xử lý luồng âm thanh, bỏ qua luồng video
                outputFile.getAbsolutePath()
        };

        FFmpeg.executeAsync(command, (executionId, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                runOnUiThread(() -> {
                    String outputPath = outputFile.getAbsolutePath();
                    Toast.makeText(AudioCutterActivity.this, "Audio cut successfully! Output file saved at: " + outputPath, Toast.LENGTH_LONG).show();
                    playAudio(outputFile);
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(AudioCutterActivity.this, "Failed to cut audio", Toast.LENGTH_SHORT).show();
                });
            }

        });
    }


//    private void cutAudio(int startSeconds, int endSeconds) {
//        File inputFile = new File(filePath);
//
//        // Lấy tên tệp gốc và tạo tên tệp đầu ra với "cut_" phía trước tên gốc
//        String originalFileName = inputFile.getName();
//        String outputFileName = "cut_" + originalFileName;
//        File outputFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), outputFileName);
//
//        String startTime = formatTimeFFmpeg(startSeconds);
//        String duration = formatTimeFFmpeg(endSeconds - startSeconds);
//
//        String[] command = {
//                "-y", // Thêm cờ -y để buộc ghi đè tệp
//                "-i", inputFile.getAbsolutePath(),
//                "-ss", startTime,
//                "-t", duration,
//                "-acodec", "mp3",
//                "-vn", // Chỉ xử lý luồng âm thanh, bỏ qua luồng video
//                outputFile.getAbsolutePath()
//        };
//
//        FFmpeg.executeAsync(command, (executionId, returnCode) -> {
//            if (returnCode == Config.RETURN_CODE_SUCCESS) {
//                runOnUiThread(() -> {
//                    String outputPath = outputFile.getAbsolutePath();
//                    Toast.makeText(AudioCutterActivity.this, "" + outputPath, Toast.LENGTH_LONG).show();
//                    playAudio(outputFile);
//                });
//            } else {
//                runOnUiThread(() -> {
//                    Toast.makeText(AudioCutterActivity.this, "Failed to cut audio", Toast.LENGTH_SHORT).show();
//                });
//            }
//
//        });
//    }

    private String formatTimeFFmpeg(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
private void playAudio(File file) {

    Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(uri, "audio/x-wav");
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    this.startActivity(intent);
}



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
//public class AudioCutterActivity extends AppCompatActivity {
//
//    private static final String TAG = "AudioCutterActivity";
//
//    private RangeSeekBar<Integer> rangeSeekBar;
//    private TextView tvStart, tvEnd;
//    private Button btnCutAudio;
//
//    private String filePath;
//    private MediaPlayer mediaPlayer;
//    private int audioDuration;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_audio_cutter);
//
//        rangeSeekBar = findViewById(R.id.rangeSeekBar);
//        tvStart = findViewById(R.id.tvStart);
//        tvEnd = findViewById(R.id.tvEnd);
//        btnCutAudio = findViewById(R.id.btnCutAudio);
//
//        // Get the file path from the intent
//        Intent intent = getIntent();
//        filePath = intent.getStringExtra("filePath");
//
//        if (filePath == null) {
//            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Initialize MediaPlayer to get audio duration
//        mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource(filePath);
//            mediaPlayer.prepare();
//            audioDuration = mediaPlayer.getDuration() / 1000; // duration in seconds
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to load audio file", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Set up RangeSeekBar
//        rangeSeekBar.setRangeValues(0, audioDuration);
//        rangeSeekBar.setOnRangeSeekBarChangeListener((bar, minValue, maxValue) -> {
//            tvStart.setText("Start: " + formatTime(minValue));
//            tvEnd.setText("End: " + formatTime(maxValue));
//        });
//
//        btnCutAudio.setOnClickListener(v -> cutAudio(rangeSeekBar.getSelectedMinValue(), rangeSeekBar.getSelectedMaxValue()));
//    }
//
//    private String formatTime(int seconds) {
//        int minutes = seconds / 60;
//        int secs = seconds % 60;
//        return String.format("%d:%02d", minutes, secs);
//    }
//
//    private void cutAudio(int startSeconds, int endSeconds) {
//        File inputFile = new File(filePath);
//        String randomFileName = UUID.randomUUID().toString() + ".mp3";
//        File outputFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), randomFileName);
////        File outputFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "cut_audio.mp3");
//
//        String startTime = formatTimeFFmpeg(startSeconds);
//        String duration = formatTimeFFmpeg(endSeconds - startSeconds);
//
//        String[] command = {
//                "-i", inputFile.getAbsolutePath(),
//                "-ss", startTime,
//                "-t", duration,
//                "-c", "copy",
//                outputFile.getAbsolutePath()
//        };
//
//        FFmpeg.executeAsync(command, (executionId, returnCode) -> {
//            if (returnCode == Config.RETURN_CODE_SUCCESS) {
//                runOnUiThread(() -> {
//                    Toast.makeText(AudioCutterActivity.this, "Audio cut successfully!", Toast.LENGTH_SHORT).show();
//                    playAudio(outputFile);
//                });
//            } else {
//                runOnUiThread(() -> {
//                    Toast.makeText(AudioCutterActivity.this, "Failed to cut audio", Toast.LENGTH_SHORT).show();
//                });
//            }
//        });
//    }
//
//    private String formatTimeFFmpeg(int seconds) {
//        int hours = seconds / 3600;
//        int minutes = (seconds % 3600) / 60;
//        int secs = seconds % 60;
//        return String.format("%02d:%02d:%02d", hours, minutes, secs);
//    }
//
//    private void playAudio(File file) {
//        Uri uri = Uri.fromFile(file);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "audio/*");
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        startActivity(intent);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }
//}
