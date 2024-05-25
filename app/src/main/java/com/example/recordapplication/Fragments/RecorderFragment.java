////cai chinh thuc
//package com.example.recordapplication.Fragments;
//
//import static com.example.recordapplication.GetLocationActivity.REQUEST_CODE;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Looper;
//import android.os.SystemClock;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Chronometer;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.example.recordapplication.GetLocationActivity;
//import com.example.recordapplication.R;
//import com.example.recordapplication.RecordingActivity;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.MultiplePermissionsReport;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
//import com.google.android.gms.location.FusedLocationProviderClient;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//import pl.droidsonroids.gif.GifImageView;
//
//public class RecorderFragment extends Fragment {
//
//    private View view;
//    private ImageButton btnRec;
//    private TextView txtRecStatus;
//    private Chronometer timeRec;
//    private GifImageView gifView;
//    private MediaRecorder recorder;
//    private boolean isRecording;
//
//    private File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
//
//
//
//    // phần muốn get location
//    FusedLocationProviderClient fusedLocationProviderClient;
//    TextView country, city, address, longitude, latitude;
//    Button getLocation;
//    public final static int REQUEST_CODE = 100;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment, container, false);
//
//        btnRec = view.findViewById(R.id.btnRec);
//        txtRecStatus = view.findViewById(R.id.txtRecStatus);
//        gifView = view.findViewById(R.id.gifView);
//        timeRec = view.findViewById(R.id.timeRec);
//        address = view.findViewById(R.id.tvUserLocation);
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
//
//
////        address = view.findViewById(R.id.txtAddress);
//
//
//        isRecording = false;
//
//
//        askRuntimePermission();
//
//        if (!path.exists()) {
//            path.mkdirs();
//
//        }
//
//
//        btnRec.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (!isRecording) {
//                    try {
//                        getLastLocation();
//
//
//
//                        startRecording();
//
//                        gifView.setVisibility(View.VISIBLE);
//                        timeRec.setBase(SystemClock.elapsedRealtime());
//                        timeRec.start();
//                        txtRecStatus.setText("Recording...");
//                        btnRec.setImageResource(R.drawable.ic_stop);
//                        isRecording = true;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(getContext(), "Couldn't Record", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    stopRecording();
//                    gifView.setVisibility(View.GONE);
//                    timeRec.stop();
//                    txtRecStatus.setText("Recording Stopped");
//                    btnRec.setImageResource(R.drawable.ic_record);
//                    isRecording = false;
//                }
//            }
//        });
//        return view;
//    }
//
//    private void startRecording() {
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//        String date = format.format(new Date());
//        String fileName = path + "/recording_" + date + ".mp3";
//
//        recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        recorder.setOutputFile(fileName);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//
//
//        try {
//            recorder.prepare();
//            recorder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//    }
//
//    private void stopRecording() {
//        if (recorder != null && isRecording) {
//            try {
//                recorder.stop();
//            } catch (RuntimeException stopException) {
//                // Handle the exception if the recorder wasn't properly started
//                stopException.printStackTrace();
//            } finally {
//                recorder.release();
//                recorder = null;
//            }
//        }
//
//    }
//
//    private void askRuntimePermission() {
//        Dexter.withContext(getContext()).withPermissions(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.RECORD_AUDIO
//        ).withListener(new MultiplePermissionsListener() {
//            @Override
//            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                Toast.makeText(getContext(), "Permissions Granted", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                permissionToken.continuePermissionRequest();
//            }
//        }).check();
//    }
//
//
//    private void getLastLocation() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                fusedLocationProviderClient.getLastLocation()
//                        .addOnSuccessListener(new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//                                if (location != null) {
//                                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
//                                    List<Address> addresses = null;
//                                    try {
//                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                        if (addresses != null && !addresses.isEmpty()) {
//                                            address.setText("Address: " + addresses.get(0).getAddressLine(0));
//
//                                        }
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        });
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            }
//        } else {
//            askPermission();
//        }
//    }
//
//    private void askPermission() {
//        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
//            } else {
//                Toast.makeText(getContext(), "Required Permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//}

// sau cái chính thức
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
        String sanitizedAddress = currentAddress.replaceAll("[,\\s]+", "_");
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

//// lưu địa chỉ người dùng cùng với tệp MP3 khi ghi âm và hiển thị địa chỉ
//package com.example.recordapplication.Fragments;
//
//import static com.example.recordapplication.GetLocationActivity.REQUEST_CODE;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.SystemClock;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Chronometer;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.example.recordapplication.Adapter.RecAdapter;
//import com.example.recordapplication.OnSelectListener;
//import com.example.recordapplication.R;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.MultiplePermissionsReport;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//
//import pl.droidsonroids.gif.GifImageView;
//
//public class RecorderFragment extends Fragment {
//
//    private View view;
//    private ImageButton btnRec;
//    private TextView txtRecStatus;
//    private Chronometer timeRec;
//    private GifImageView gifView;
//    private MediaRecorder recorder;
//    private boolean isRecording;
//    private String currentAddress;
//    private File currentFile;
//
//    private RecAdapter recAdapter; // Thêm dòng này để giữ tham chiếu đến RecAdapter
//
//    private HashMap<File, String> fileLocations = new HashMap<>();
//
//    private File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
//
//    FusedLocationProviderClient fusedLocationProviderClient;
//    TextView address;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment, container, false);
//
//        btnRec = view.findViewById(R.id.btnRec);
//        txtRecStatus = view.findViewById(R.id.txtRecStatus);
//        gifView = view.findViewById(R.id.gifView);
//        timeRec = view.findViewById(R.id.timeRec);
//        address = view.findViewById(R.id.tvUserLocation);
//        // Khởi tạo RecAdapter (phải chắc chắn rằng RecAdapter được khởi tạo trước khi gọi setFileLocation)
//        recAdapter = new RecAdapter(getContext(), new ArrayList<>(), new OnSelectListener() {
//            @Override
//            public void OnSelectListed(File file) {
//                // xử lý sự kiện khi một tệp được chọn
//            }
//        }, new HashMap<>());
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
//
//        isRecording = false;
//
//        askRuntimePermission();
//
//        if (!path.exists()) {
//            path.mkdirs();
//        }
//
//        btnRec.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isRecording) {
//                    try {
//                        getLastLocation();
//                        startRecording();
//                        gifView.setVisibility(View.VISIBLE);
//                        timeRec.setBase(SystemClock.elapsedRealtime());
//                        timeRec.start();
//                        txtRecStatus.setText("Recording...");
//                        btnRec.setImageResource(R.drawable.ic_stop);
//                        isRecording = true;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(getContext(), "Couldn't Record", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    stopRecording();
//                    gifView.setVisibility(View.GONE);
//                    timeRec.stop();
//                    txtRecStatus.setText("Recording Stopped");
//                    btnRec.setImageResource(R.drawable.ic_record);
//                    isRecording = false;
//
//                    // Lưu địa chỉ vào fileLocations và cập nhật Adapter
//                    if (currentFile != null && currentAddress != null) {
//                        fileLocations.put(currentFile, currentAddress);
//                        recAdapter.setFileLocation(currentFile, currentAddress);
//                    }
//                }
//            }
//        });
//        return view;
//    }
//
//    private void startRecording() {
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//        String date = format.format(new Date());
//        String fileName = path + "/recording_" + date + ".mp3";
//
//        currentFile = new File(fileName);
//
//        recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        recorder.setOutputFile(fileName);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//
//        try {
//            recorder.prepare();
//            recorder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void stopRecording() {
//        if (recorder != null && isRecording) {
//            try {
//                recorder.stop();
//            } catch (RuntimeException stopException) {
//                stopException.printStackTrace();
//            } finally {
//                recorder.release();
//                recorder = null;
//            }
//        }
//    }
//
//    private void askRuntimePermission() {
//        Dexter.withContext(getContext()).withPermissions(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.ACCESS_FINE_LOCATION
//        ).withListener(new MultiplePermissionsListener() {
//            @Override
//            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                Toast.makeText(getContext(), "Permissions Granted", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                permissionToken.continuePermissionRequest();
//            }
//        }).check();
//    }
//
//    private void getLastLocation() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                fusedLocationProviderClient.getLastLocation()
//                        .addOnSuccessListener(new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//                                if (location != null) {
//                                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
//                                    List<Address> addresses = null;
//                                    try {
//                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                        if (addresses != null && !addresses.isEmpty()) {
//                                            String addressText = addresses.get(0).getAddressLine(0);
//                                            address.setText("Address: " + addressText);
//
//                                            // Cập nhật địa chỉ cho tệp đang ghi
//                                            if (recAdapter != null) {
//                                                recAdapter.setFileLocation(new File(currentRecordingFilePath), addressText);
//                                            }
//                                        }
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        });
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            }
//        } else {
//            askPermission();
//        }
//    }
//
//
//    private void askPermission() {
//        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
//            } else {
//                Toast.makeText(getContext(), "Required Permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//}
//update luu lấy được
//package com.example.recordapplication.Fragments;
//
//import static com.example.recordapplication.GetLocationActivity.REQUEST_CODE;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.SystemClock;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Chronometer;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.example.recordapplication.Adapter.RecAdapter;
//import com.example.recordapplication.OnSelectListener;
//import com.example.recordapplication.R;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.MultiplePermissionsReport;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//
//import pl.droidsonroids.gif.GifImageView;
//
//public class RecorderFragment extends Fragment {
//
//    private View view;
//    private ImageButton btnRec;
//    private TextView txtRecStatus;
//    private Chronometer timeRec;
//    private GifImageView gifView;
//    private MediaRecorder recorder;
//    private boolean isRecording;
//    private String currentAddress;
//    private File currentFile;
//
//    private RecAdapter recAdapter; // Thêm dòng này để giữ tham chiếu đến RecAdapter
//
//    private HashMap<File, String> fileLocations = new HashMap<>();
//
//    private File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
//
//    FusedLocationProviderClient fusedLocationProviderClient;
//    TextView address;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment, container, false);
//
//        btnRec = view.findViewById(R.id.btnRec);
//        txtRecStatus = view.findViewById(R.id.txtRecStatus);
//        gifView = view.findViewById(R.id.gifView);
//        timeRec = view.findViewById(R.id.timeRec);
//        address = view.findViewById(R.id.tvUserLocation);
//
//        // Khởi tạo RecAdapter (phải chắc chắn rằng RecAdapter được khởi tạo trước khi gọi setFileLocation)
//        recAdapter = new RecAdapter(getContext(), new ArrayList<>(), new OnSelectListener() {
//            @Override
//            public void OnSelectListed(File file) {
//                // xử lý sự kiện khi một tệp được chọn
//            }
//        }, fileLocations);
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
//
//        isRecording = false;
//
//        askRuntimePermission();
//
//        if (!path.exists()) {
//            path.mkdirs();
//        }
//
//        btnRec.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isRecording) {
//                    try {
//                        getLastLocation();
//                        startRecording();
//                        gifView.setVisibility(View.VISIBLE);
//                        timeRec.setBase(SystemClock.elapsedRealtime());
//                        timeRec.start();
//                        txtRecStatus.setText("Recording...");
//                        btnRec.setImageResource(R.drawable.ic_stop);
//                        isRecording = true;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(getContext(), "Couldn't Record", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    stopRecording();
//                    gifView.setVisibility(View.GONE);
//                    timeRec.stop();
//                    txtRecStatus.setText("Recording Stopped");
//                    btnRec.setImageResource(R.drawable.ic_record);
//                    isRecording = false;
//
//                    // Lưu địa chỉ vào fileLocations và cập nhật Adapter
//                    if (currentFile != null && currentAddress != null) {
//                        fileLocations.put(currentFile, currentAddress);
//                        recAdapter.setFileLocation(currentFile, currentAddress);
//                    }
//                }
//            }
//        });
//        return view;
//    }
//
//    private void startRecording() {
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//        String date = format.format(new Date());
//        String fileName = path + "/recording_" + date + ".mp3";
//
//        currentFile = new File(fileName);
//
//        recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        recorder.setOutputFile(fileName);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//
//        try {
//            recorder.prepare();
//            recorder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void stopRecording() {
//        if (recorder != null && isRecording) {
//            try {
//                recorder.stop();
//            } catch (RuntimeException stopException) {
//                stopException.printStackTrace();
//            } finally {
//                recorder.release();
//                recorder = null;
//            }
//        }
//    }
//
//    private void askRuntimePermission() {
//        Dexter.withContext(getContext()).withPermissions(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.ACCESS_FINE_LOCATION
//        ).withListener(new MultiplePermissionsListener() {
//            @Override
//            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                Toast.makeText(getContext(), "Permissions Granted", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                permissionToken.continuePermissionRequest();
//            }
//        }).check();
//    }
//
//    private void getLastLocation() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                fusedLocationProviderClient.getLastLocation()
//                        .addOnSuccessListener(new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//                                if (location != null) {
//                                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
//                                    List<Address> addresses = null;
//                                    try {
//                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                        if (addresses != null && !addresses.isEmpty()) {
//                                            String addressText = addresses.get(0).getAddressLine(0);
//                                            address.setText("Address: " + addressText);
//
//                                            // Cập nhật địa chỉ cho tệp đang ghi
//                                            if (recAdapter != null && currentFile != null) {
//                                                currentAddress = addressText;
//                                                recAdapter.setFileLocation(currentFile, addressText);
//                                            }
//                                        }
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        });
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            }
//        } else {
//            askPermission();
//        }
//    }
//
//    private void askPermission() {
//        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
//            } else {
//                Toast.makeText(getContext(), "Required Permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//}

//// updata code lưu đã sữa 18:19
//package com.example.recordapplication.Fragments;
//
//import static com.example.recordapplication.GetLocationActivity.REQUEST_CODE;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.SystemClock;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Chronometer;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.example.recordapplication.Adapter.RecAdapter;
//import com.example.recordapplication.OnSelectListener;
//import com.example.recordapplication.R;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.MultiplePermissionsReport;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//
//import pl.droidsonroids.gif.GifImageView;
//
//public class RecorderFragment extends Fragment {
//
//    private View view;
//    private ImageButton btnRec;
//    private TextView txtRecStatus;
//    private Chronometer timeRec;
//    private GifImageView gifView;
//    private MediaRecorder recorder;
//    private boolean isRecording;
//    private String currentAddress;
//    private File currentFile;
//
//    private RecAdapter recAdapter; // Thêm dòng này để giữ tham chiếu đến RecAdapter
//
//    private HashMap<File, String> fileLocations = new HashMap<>();
//
//    private File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
//
//    FusedLocationProviderClient fusedLocationProviderClient;
//    TextView address;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment, container, false);
//
//        btnRec = view.findViewById(R.id.btnRec);
//        txtRecStatus = view.findViewById(R.id.txtRecStatus);
//        gifView = view.findViewById(R.id.gifView);
//        timeRec = view.findViewById(R.id.timeRec);
//        address = view.findViewById(R.id.tvUserLocation);
//
//        // Khởi tạo RecAdapter (phải chắc chắn rằng RecAdapter được khởi tạo trước khi gọi setFileLocation)
//        recAdapter = new RecAdapter(getContext(), new ArrayList<>(), new OnSelectListener() {
//            @Override
//            public void OnSelectListed(File file) {
//                // xử lý sự kiện khi một tệp được chọn
//            }
//        }, fileLocations);
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
//
//        isRecording = false;
//
//        askRuntimePermission();
//
//        if (!path.exists()) {
//            path.mkdirs();
//        }
//
//        btnRec.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isRecording) {
//                    try {
//                        getLastLocation(); // Gọi hàm này để lấy vị trí trước khi bắt đầu ghi âm
//                        startRecording();
//                        gifView.setVisibility(View.VISIBLE);
//                        timeRec.setBase(SystemClock.elapsedRealtime());
//                        timeRec.start();
//                        txtRecStatus.setText("Recording...");
//                        btnRec.setImageResource(R.drawable.ic_stop);
//                        isRecording = true;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(getContext(), "Couldn't Record", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    stopRecording();
//                    gifView.setVisibility(View.GONE);
//                    timeRec.stop();
//                    txtRecStatus.setText("Recording Stopped");
//                    btnRec.setImageResource(R.drawable.ic_record);
//                    isRecording = false;
//
//                    // Lưu địa chỉ vào fileLocations và cập nhật Adapter
//                    if (currentFile != null && currentAddress != null) {
//                        fileLocations.put(currentFile, currentAddress);
//                        recAdapter.setFileLocation(currentFile, currentAddress);
//                    }
//                }
//            }
//        });
//        return view;
//    }
//
//    private void startRecording() {
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//        String date = format.format(new Date());
//        String fileName = path + "/recording_" + date + ".mp3";
//
//        currentFile = new File(fileName);
//
//        recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        recorder.setOutputFile(fileName);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//
//        try {
//            recorder.prepare();
//            recorder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void stopRecording() {
//        if (recorder != null && isRecording) {
//            try {
//                recorder.stop();
//            } catch (RuntimeException stopException) {
//                stopException.printStackTrace();
//            } finally {
//                recorder.release();
//                recorder = null;
//            }
//        }
//    }
//
//    private void askRuntimePermission() {
//        Dexter.withContext(getContext()).withPermissions(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.ACCESS_FINE_LOCATION
//        ).withListener(new MultiplePermissionsListener() {
//            @Override
//            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                Toast.makeText(getContext(), "Permissions Granted", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                permissionToken.continuePermissionRequest();
//            }
//        }).check();
//    }
//
//    private void getLastLocation() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                fusedLocationProviderClient.getLastLocation()
//                        .addOnSuccessListener(new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//                                if (location != null) {
//                                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
//                                    List<Address> addresses = null;
//                                    try {
//                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                        if (addresses != null && !addresses.isEmpty()) {
//                                            String addressText = addresses.get(0).getAddressLine(0);
//                                            address.setText("Address: " + addressText);
//
//                                            // Cập nhật địa chỉ cho tệp đang ghi
//                                            currentAddress = addressText;
//                                        }
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                } else {
//                                    currentAddress = "Unknown location";
//                                }
//                            }
//                        });
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            }
//        } else {
//            askPermission();
//        }
//    }
//
//    private void askPermission() {
//        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
//            } else {
//                Toast.makeText(getContext(), "Required Permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//}

//huong lam
//package com.example.recordapplication.Fragments;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.SystemClock;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Chronometer;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.example.recordapplication.R;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.MultiplePermissionsReport;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//import pl.droidsonroids.gif.GifImageView;
//
//public class RecorderFragment extends Fragment {
//
//    private View view;
//    private ImageButton btnRec;
//    private TextView txtRecStatus;
//    private Chronometer timeRec;
//    private GifImageView gifView;
//    private MediaRecorder recorder;
//    private boolean isRecording;
//
//    private FusedLocationProviderClient fusedLocationProviderClient;
//    private TextView address;
//    private final static int REQUEST_CODE = 100;
//
//    private File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment, container, false);
//
//        // Inflate the custom_item.xml and get the TextView from there
//        View customItemView = inflater.inflate(R.layout.custom_item, container, false);
//        address = customItemView.findViewById(R.id.txtAddress);
//
//        if (address == null) {
//            throw new RuntimeException("TextView address is null. Check your layout file for the correct ID.");
//        }
//
//        // Initialize views
//        btnRec = view.findViewById(R.id.btnRec);
//        txtRecStatus = view.findViewById(R.id.txtRecStatus);
//        gifView = view.findViewById(R.id.gifView);
//        timeRec = view.findViewById(R.id.timeRec);
////        address = findViewById(R.id.txtAddress);
//
//        // Initialize FusedLocationProviderClient
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
//
//        isRecording = false;
//
//        askRuntimePermission();
//
//        if (!path.exists()) {
//            path.mkdirs();
//        }
//
//        btnRec.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isRecording) {
//                    try {
//                        startRecording();
//                        gifView.setVisibility(View.VISIBLE);
//                        timeRec.setBase(SystemClock.elapsedRealtime());
//                        timeRec.start();
//                        txtRecStatus.setText("Recording...");
//                        btnRec.setImageResource(R.drawable.ic_stop);
//                        isRecording = true;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(getContext(), "Couldn't Record", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    stopRecording();
//                    gifView.setVisibility(View.GONE);
//                    timeRec.stop();
//                    txtRecStatus.setText("Recording Stopped");
//                    btnRec.setImageResource(R.drawable.ic_record);
//                    isRecording = false;
//                }
//            }
//        });
//        return view;
//    }
//
//    private void setContentView(int customItem) {
//    }
//
//    private void startRecording() {
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//        String date = format.format(new Date());
//        String fileName = path + "/recording_" + date + ".mp3";
//
//        recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        recorder.setOutputFile(fileName);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        getLastLocation();
//
//        try {
//            recorder.prepare();
//            recorder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void stopRecording() {
//        if (recorder != null && isRecording) {
//            try {
//                recorder.stop();
//            } catch (RuntimeException stopException) {
//                stopException.printStackTrace();
//            } finally {
//                recorder.release();
//                recorder = null;
//            }
//        }
//    }
//
//    private void askRuntimePermission() {
//        Dexter.withContext(getContext()).withPermissions(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.ACCESS_FINE_LOCATION
//        ).withListener(new MultiplePermissionsListener() {
//            @Override
//            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                Toast.makeText(getContext(), "Permissions Granted", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                permissionToken.continuePermissionRequest();
//            }
//        }).check();
//    }
//
//    private void getLastLocation() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                fusedLocationProviderClient.getLastLocation()
//                        .addOnSuccessListener(new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//                                if (location != null) {
//                                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
//                                    List<Address> addresses = null;
//                                    try {
//                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                        if (addresses != null && !addresses.isEmpty()) {
//                                            address.setText("Address: " + addresses.get(0).getAddressLine(0));
//                                        }
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        });
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            }
//        } else {
//            askPermission();
//        }
//    }
//
//    private void askPermission() {
//        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
//            } else {
//                Toast.makeText(getContext(), "Required Permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//}



//new update dia chỉ 23 may
// mot lan nua thoi
