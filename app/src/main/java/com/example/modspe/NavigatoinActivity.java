package com.example.modspe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Button;

import com.example.modspe.adapters.SQLiteHelper;
import com.example.modspe.fragments.FavoritesFragment;
import com.example.modspe.fragments.ModsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NavigatoinActivity extends AppCompatActivity {

    private SQLiteHelper mDBHelper;
    private SQLiteDatabase mDb;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigatoin);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navListener);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ModsFragment()).commit();
        bottomNavigation.setBackgroundResource(R.drawable.ic_menu_mods);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int lastposition = getPrefs.getInt("lastPosition", 0);
        if (lastposition == 1){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FavoritesFragment()).commit();
            bottomNavigation.setBackgroundResource(R.drawable.ic_menu_favorite);
        } else if (lastposition == 2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ModsFragment()).commit();
            bottomNavigation.setBackgroundResource(R.drawable.ic_menu_mods);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ModsFragment()).commit();
            bottomNavigation.setBackgroundResource(R.drawable.ic_menu_mods);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.nav_mods:
                            selectedFragment = new FavoritesFragment();
                            bottomNavigation.setBackgroundResource(R.drawable.ic_menu_favorite);
                            SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = getPrefs.edit();
                            editor.putInt("lastPosition", 1);
                            editor.apply();
                            onResume();
                            break;
                        case R.id.nav_fav:
                            selectedFragment = new ModsFragment();
                            bottomNavigation.setBackgroundResource(R.drawable.ic_menu_mods);
                            SharedPreferences getPrefs1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor1 = getPrefs1.edit();
                            editor1.putInt("lastPosition", 2);
                            editor1.apply();
                            onResume();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };
}