package com.example.modspe.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.modspe.DetailedActivity;
import com.example.modspe.Item;
import com.example.modspe.R;
import com.example.modspe.adapters.FavoritesAdapter;
import com.example.modspe.adapters.SQLiteHelper;

import java.io.IOException;
import java.util.ArrayList;

public class FavoritesFragment extends Fragment {


    private SQLiteHelper mDBHelper;
    private SQLiteDatabase mDb;
    private int lastposition;
    private int start;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, null);

        mDBHelper = new SQLiteHelper(getContext());
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

        start = 1;

        Cursor cursor = mDb.query("mods", new String[]{"_id", "favorite", "desc", "title", "file", "image"},
                "favorite='1'",
                null,
                null,
                null,
                null);
        int favorite;
        int _id;
        String desc;
        String title;
        String file;
        String image;
        ArrayList<Item> items = new ArrayList<>();

        while (cursor.moveToNext()) {
            favorite = cursor.getInt(cursor.getColumnIndex("favorite"));
            _id = cursor.getInt(cursor.getColumnIndex("_id"));
            desc = cursor.getString(cursor.getColumnIndex("desc"));
            title = cursor.getString(cursor.getColumnIndex("title"));
            file = cursor.getString(cursor.getColumnIndex("file"));
            image = cursor.getString(cursor.getColumnIndex("image"));
            items.add(new Item(title, image, desc, file, _id, favorite));
        }
        cursor.close();
        // инициализация recyclerView
        RecyclerView recViewFavorites = root.findViewById(R.id.recyclerViewFavorites);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recViewFavorites.setLayoutManager(layoutManager);
        FavoritesAdapter adapter = new FavoritesAdapter(getContext(), items);
        adapter.setClickListener(this::onItemClick);
        recViewFavorites.setAdapter(adapter);

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        lastposition = getPrefs.getInt("lastPos", 0);
        recViewFavorites.scrollToPosition(lastposition);


        recViewFavorites.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastposition = layoutManager.findFirstVisibleItemPosition();
            }
        });

        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = getPrefs.edit();
        editor.putInt("lastPos", lastposition);
        editor.apply();
    }

    public void onItemClick(View view, int position){

        Cursor cursor = mDb.query("mods", new String[]{"_id", "favorite", "desc", "title", "file", "image"},
                "favorite='1'",
                null,
                null,
                null,
                null);
        int favorite;
        int _id;
        String desc;
        String title;
        String file;
        String image;
        ArrayList<String> images = new ArrayList<>();
        ArrayList<String> descs = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> files = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Integer> favorites = new ArrayList<>();

        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex("title"));
            desc = cursor.getString(cursor.getColumnIndex("desc"));
            image = cursor.getString(cursor.getColumnIndex("image"));
            file = cursor.getString(cursor.getColumnIndex("file"));
            _id = cursor.getInt(cursor.getColumnIndex("_id"));
            favorite = cursor.getInt(cursor.getColumnIndex("favorite"));
            descs.add(desc);
            images.add(image);
            titles.add(title);
            files.add(file);
            ids.add(_id);
            favorites.add(favorite);
        }
        cursor.close();

        try {
            Intent intent = new Intent(getActivity().getApplicationContext(), DetailedActivity.class);
            intent.putExtra("image", images.get(position));
            intent.putExtra("file", files.get(position));
            intent.putExtra("title", titles.get(position));
            intent.putExtra("desc", descs.get(position));
            intent.putExtra("id", ids.get(position));
            intent.putExtra("favorite", favorites.get(position));
            if (start == 1){
                startActivity(intent);
                start = 0;
            }
        } catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }
}