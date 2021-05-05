package com.example.modspe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.modspe.adapters.SQLiteHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DetailedActivity extends AppCompatActivity {

    private SQLiteHelper mDBHelper;
    private SQLiteDatabase mDb;
    private int install;
    TextView downloadText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        ImageView imageView = (ImageView) findViewById(R.id.imageView_Mod);
        ImageView backButton = findViewById(R.id.back_button);
        ImageView favoriteButtonDetailed = findViewById(R.id.favButtonDetailed);
        ImageView downloadButton = findViewById(R.id.download);
        downloadText = findViewById(R.id.download_text);

        mDBHelper = new SQLiteHelper(this);
        install = 0;


        Item item = new Item(getIntent().getStringExtra("title"),
        getIntent().getStringExtra("image"),
        getIntent().getStringExtra("desc"),
        getIntent().getStringExtra("file"),
        getIntent().getIntExtra("id", 0),
        getIntent().getIntExtra("favorite", 0));
        if (item.getFavorite() == 0) {
            favoriteButtonDetailed.setBackgroundResource(R.drawable.ic_favorite_false);
        } else if (item.getFavorite() == 1) {
            favoriteButtonDetailed.setBackgroundResource(R.drawable.ic_favorite_true);
        }

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionStatus = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                int permissionStatus2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionStatus == PackageManager.PERMISSION_GRANTED && permissionStatus2 == PackageManager.PERMISSION_GRANTED) {
                    if (install == 0){
                        downloadText.setText("Downloading");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                downloadText.setText("Install");
                                install = 1;
                            }
                        }, 2000);
                    } else {
                        try {
                            exportToMinecraft();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(DetailedActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });

        favoriteButtonDetailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getFavorite() == 0) {
                    item.setFavorite(1);
                    mDBHelper.add_fav(item.get_id());
                    favoriteButtonDetailed.setBackgroundResource(R.drawable.ic_favorite_true);
                } else if (item.getFavorite() == 1) {
                    item.setFavorite(0);
                    mDBHelper.remove_fav(item.get_id());
                    favoriteButtonDetailed.setBackgroundResource(R.drawable.ic_favorite_false);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        InputStream inputStream = null;
        try{
            inputStream = getApplicationContext().getAssets().open("images/" + getIntent().getStringExtra("image"));
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                if(inputStream!=null)
                    inputStream.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }

        TextView textView_desc = findViewById(R.id.desc_detail);
        TextView textView_title = findViewById(R.id.title_detail);

        textView_desc.setText(getIntent().getStringExtra("desc"));

        textView_title.setText(getIntent().getStringExtra("title"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences getPrefs1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor1 = getPrefs1.edit();
        editor1.putInt("open", 1);
        editor1.apply();
        if (getIntent().getIntExtra("fragment", 1) == 1){
            SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = getPrefs.edit();
            editor.putInt("lastPosition", 1);
        } else if (getIntent().getIntExtra("fragment", 1) == 2){
            SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = getPrefs.edit();
            editor.putInt("lastPosition", 2);
        }
    }

    private void exportToMinecraft() throws IOException {
        try {
            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                String p = getExternalFilesDir(null) + "/" + getIntent().getStringExtra("file"); // путь к files
                InputStream ims = getAssets().open("files/" + getIntent().getStringExtra("file"));
                FileOutputStream fileOutputStream = new FileOutputStream(p);
                byte[] buffer = new byte[ims.available()];
                ims.read(buffer, 0, ims.available());
                fileOutputStream.write(buffer);
                fileOutputStream.close();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setData(Uri.parse("minecraft://?import=" + p));
                    getApplicationContext().startActivity(intent);
                } catch (android.content.ActivityNotFoundException activityNotFoundException){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mojang.minecraftpe")));
                }
            } else {
                InputStream ims = getAssets().open("files/" + getIntent().getStringExtra("file"));
                FileOutputStream fileOutputStream = new FileOutputStream(new File(getFilesDir(), "/" +getIntent().getStringExtra("file")));
                byte[] buffer = new byte[ims.available()];
                ims.read(buffer, 0, ims.available());
                fileOutputStream.write(buffer);
                fileOutputStream.close();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri skinUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider",
                        new File(getFilesDir(), getIntent().getStringExtra("file")));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(skinUri);
                startActivity(intent);
            }
        } catch(Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mojang.minecraftpe")));
        }
    }

    public void Download() throws IOException {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionStatus2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED && permissionStatus2 == PackageManager.PERMISSION_GRANTED) {
            exportToMinecraft();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            if (install == 0){
                downloadText.setText("Downloading");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        downloadText.setText("Install");
                        install = 1;
                    }
                }, 2000);
            } else {
                try {
                    exportToMinecraft();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setmDBHelper(SQLiteHelper mDBHelper) { this.mDBHelper = mDBHelper; }
}