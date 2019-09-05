package com.teamandroid.snapshare.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teamandroid.snapshare.R;
import com.teamandroid.snapshare.ui.login.LoginActivity;
import com.teamandroid.snapshare.ui.main.home.HomeFragment;
import com.teamandroid.snapshare.ui.main.profile.ProfileFragment;
import com.teamandroid.snapshare.ui.main.search.SearchFragment;
import com.teamandroid.snapshare.ui.post.PostActivity;

public class MainActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private HomeFragment homeFragment = HomeFragment.newInstance();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mFragmentManager.beginTransaction()
                            .replace(R.id.content_container, homeFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_search:
                    //TODO
                    SearchFragment searchFragment = SearchFragment.newInstance();
                    mFragmentManager.beginTransaction()
                            .replace(R.id.content_container, searchFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_add_post:
                    openPostActivity();
                    return true;
                case R.id.navigation_account:
                    //TODO
                    ProfileFragment profileFragment = ProfileFragment.newInstance();
                    mFragmentManager.beginTransaction()
                            .replace(R.id.content_container, profileFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mFragmentManager.beginTransaction()
                .replace(R.id.content_container, homeFragment)
                .addToBackStack(null)
                .commit();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void openPostActivity() {
        Intent intent = new Intent(this, PostActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
