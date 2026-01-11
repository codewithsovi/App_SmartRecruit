package com.example.smartrecruit.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartrecruit.R;
import com.example.smartrecruit.fragments.HomeFragment;
import com.example.smartrecruit.fragments.JabatanFragment;
import com.example.smartrecruit.fragments.KandidatFragment;
import com.example.smartrecruit.fragments.HasilFragment;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navJabatan, navKandidat, navHasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INIT NAVIGATION
        navHome = findViewById(R.id.navHome);
        navJabatan = findViewById(R.id.navJabatan);
        navKandidat = findViewById(R.id.navKandidat);
        navHasil = findViewById(R.id.navHasil);

        // LOAD HOME FIRST
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {

        navHome.setOnClickListener(v ->
                loadFragment(new HomeFragment())
        );

        navJabatan.setOnClickListener(v ->
                loadFragment(new JabatanFragment())
        );

        navKandidat.setOnClickListener(v ->
                loadFragment(new KandidatFragment())
        );

        navHasil.setOnClickListener(v ->
                loadFragment(new HasilFragment())
        );
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
