package com.example.recordapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.recordapplication.Adapter.ViewPageAdapter;
import com.example.recordapplication.Fragments.RecorderFragment;
import com.example.recordapplication.Fragments.RecordingsFragment;
import com.example.recordapplication.Fragments.UploadFileFragment;
import com.google.android.material.tabs.TabLayout;

public class RecordingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);


        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);


        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager){
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.addFragment(new RecorderFragment(), "Recorder");
        viewPageAdapter.addFragment(new RecordingsFragment(), "Recordings");
        viewPageAdapter.addFragment(new UploadFileFragment(), "Upload");
        viewPager.setAdapter(viewPageAdapter);
    }

}