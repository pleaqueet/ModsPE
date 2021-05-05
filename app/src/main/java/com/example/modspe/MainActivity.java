package com.example.modspe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.modspe.adapters.SQLiteHelper;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int progress;
    private SQLiteHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new Gson();

        String text = "data.json";
        byte[] buffer = null;
        InputStream is;
        try {
            is = getAssets().open(text);
            buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String json = new String(buffer);

        Items mods = gson.fromJson(json, Items.class);

        mDBHelper = new SQLiteHelper(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = getPrefs.edit();

        if (getPrefs.getInt("db", 1) == 1){
            for (int i = 0; i < mods.getMods().size(); i++){
                ContentValues cv = new ContentValues();
                cv.put("favorite", 0);
                cv.put("file", mods.getMods().get(i).getFile());
                cv.put("desc", mods.getMods().get(i).getDesc());
                cv.put("image", mods.getMods().get(i).getImage());
                cv.put("title", mods.getMods().get(i).getTitle());
                mDb.insert("mods", null, cv);
            }
            editor.putInt("db", 0);
            editor.apply();
        }

        ProgressBar pb = findViewById(R.id.progressBar);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(MainActivity.this, NavigatoinActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}