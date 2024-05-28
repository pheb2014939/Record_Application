
package com.example.recordapplication.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.recordapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class RecorderFragment extends Fragment {

    private View view;
    private ImageButton btnRec;
    private TextView txtRecStatus;
    private Chronometer timeRec;
    private GifImageView gifView;
    private MediaRecorder recorder;
    private boolean isRecording;

    private File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");

    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView address;

    private String currentAddress;

    public final static int REQUEST_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment, container, false);

        btnRec = view.findViewById(R.id.btnRec);
        txtRecStatus = view.findViewById(R.id.txtRecStatus);
        gifView = view.findViewById(R.id.gifView);
        timeRec = view.findViewById(R.id.timeRec);
        address = view.findViewById(R.id.tvUserLocation);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        isRecording = false;

        askRuntimePermission();

        if (!path.exists()) {
            path.mkdirs();
        }

        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    getLastLocation();
                } else {
                    stopRecording();
                    gifView.setVisibility(View.GONE);
                    timeRec.stop();
                    txtRecStatus.setText("Recording Stopped");
                    btnRec.setImageResource(R.drawable.ic_record);
                    isRecording = false;
                }
            }
        });

        return view;
    }

    private void startRecording() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = format.format(new Date());
        String sanitizedAddress = currentAddress.replaceAll("[,\\s]+", " ");
        String fileName = path + "/" + sanitizedAddress + "_" + date + ".mp3";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (recorder != null && isRecording) {
            try {
                recorder.stop();
            } catch (RuntimeException stopException) {
                stopException.printStackTrace();
            } finally {
                recorder.release();
                recorder = null;
            }
        }
    }

    private void askRuntimePermission() {
        Dexter.withContext(getContext()).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                Toast.makeText(getContext(), "Permissions Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        if (addresses != null && !addresses.isEmpty()) {
                                            currentAddress = addresses.get(0).getAddressLine(0);
                                            address.setText("Address: " + currentAddress);
                                            startRecording();
                                            gifView.setVisibility(View.VISIBLE);
                                            timeRec.setBase(SystemClock.elapsedRealtime());
                                            timeRec.start();
                                            txtRecStatus.setText("Recording...");
                                            btnRec.setImageResource(R.drawable.ic_stop);
                                            isRecording = true;
                                        } else {
                                            Toast.makeText(getContext(), "Could not fetch address", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Could not get location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(getContext(), "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
