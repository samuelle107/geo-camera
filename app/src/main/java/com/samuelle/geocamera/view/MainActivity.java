package com.samuelle.geocamera.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.samuelle.geocamera.R;
import com.samuelle.geocamera.presenter.MainActivityPresenter;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainActivityPresenter(this);

        // Check if the user has allowed all permissions
        // If not, then request missing permissions
        if (presenter.hasAllowedAllPermissions()) {
            initializeFragmentContainer();
        } else {
            presenter.requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        initializeFragmentContainer();
    }


    // Setup the fragment container and and the navigation between the tabs
    private void initializeFragmentContainer() {
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, new MapFragment())
                .commit();

        navigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.camera_navigation:
                    selectedFragment = new CameraFragment();
                    break;
                case R.id.map_navigation:
                    selectedFragment = new MapFragment();
                    break;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment)
                    .commit();

            return true;
        });
    }
}
