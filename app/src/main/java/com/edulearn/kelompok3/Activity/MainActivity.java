package com.edulearn.kelompok3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.edulearn.kelompok3.Fragment.AkademikFragment;
import com.edulearn.kelompok3.Fragment.HomeFragment;
import com.edulearn.kelompok3.Fragment.ProfileFragment;
import com.edulearn.kelompok3.Fragment.KelasFragment;
import com.edulearn.kelompok3.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private boolean doubleBackToExitPressedOnce = false;
//    private FirebaseAuth.AuthStateListener authListener;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        auth.addAuthStateListener(authListener);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (authListener != null) {
//            auth.removeAuthStateListener(authListener);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("FLOW", "MainActivity opened");

//        Toast.makeText(this, "MainActivity loaded", Toast.LENGTH_LONG).show();


        // 1. Inisialisasi Firebase Auth
//        FirebaseDatabase database = FirebaseDatabase.getInstance(
//                "https://db-lms-edulearn-default-rtdb.asia-southeast1.firebasedatabase.app"
//        );
//
//        DatabaseReference ref = database.getReference();

        auth = FirebaseAuth.getInstance();
//        authListener = firebaseAuth -> {
//            if (firebaseAuth.getCurrentUser() == null) {
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        };

        // 2. Cek Sesi Login. Jika user null (belum login), tendang balik ke LoginActivity
        if (auth.getCurrentUser() == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Tambahkan HomeFragment sebagai fragment default
        if (savedInstanceState == null) {
            Log.d("FLOW", "Load HomeFragment");

            navigateToFragment(new HomeFragment(), false);
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, new HomeFragment())
//                    .commitNow();
        }

        setupBottomNavigation();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        setupOnBackPressedDispatcher();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
//            boolean addToBackStack = false;

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_kelas) {
                selectedFragment = new KelasFragment();
//                addToBackStack = true;
            } else if (itemId == R.id.navigation_akademik) {
                selectedFragment = new AkademikFragment();
//                addToBackStack = true;
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
//                addToBackStack = true;
            }

//            if (selectedFragment != null) {
//                navigateToFragment(selectedFragment, addToBackStack);
//            }
            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }

    public void navigateToProfile() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        navigateToFragment(new ProfileFragment(), true);
    }

    private void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    private void setupOnBackPressedDispatcher() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (currentFragment instanceof HomeFragment
                        || currentFragment instanceof ProfileFragment
                        || currentFragment instanceof AkademikFragment
                        || currentFragment instanceof KelasFragment) {

                    if (doubleBackToExitPressedOnce) {
                        finish();
                        return;
                    }

                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(MainActivity.this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 1500);
                } else {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else {
                        finish();
                    }
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}