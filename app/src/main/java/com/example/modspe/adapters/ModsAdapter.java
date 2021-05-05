package com.example.modspe.adapters;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.modspe.Item;
import com.example.modspe.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class ModsAdapter extends RecyclerView.Adapter<ModsAdapter.ViewHolder> {
    private SQLiteHelper mDBHelper;
    private SQLiteDatabase mDb;

    ArrayList<Item> modItems = new ArrayList<>();
    private Context context;
    private ItemClickListener mClickListener;

    public ModsAdapter(Context context, ArrayList<Item> modItems) {
        this.context = context;
        this.modItems = modItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_mod, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModsAdapter.ViewHolder holder, int position) {
        ImageView img = (ImageView) holder.itemView.findViewById(R.id.icon);
        ImageView favBut = holder.itemView.findViewById(R.id.favoritesButton);
        TextView title = holder.itemView.findViewById(R.id.titleText);
        TextView desc = holder.itemView.findViewById(R.id.desc);


        InputStream inputStream = null;

        try{
            Item modItem = modItems.get(position);

            inputStream = context.getApplicationContext().getAssets().open("images/" + modItem.getImage());
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            img.setImageDrawable(drawable);
            title.setText(modItem.getTitle());
            desc.setText(modItem.getDesc());

            if (modItem.getFavorite() == 0){
                favBut.setBackgroundResource(R.drawable.ic_favorite_false);
            } else {
                favBut.setBackgroundResource(R.drawable.ic_favorite_true);
            }
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
    }

    @Override
    public int getItemCount() {
        return modItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageView favBut = itemView.findViewById(R.id.favoritesButton);
            favBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDBHelper = new SQLiteHelper(context);
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


                    int position = getAdapterPosition();

                    Item modItem = modItems.get(position);

                    if (modItem.getFavorite() == 0){
                        modItem.setFavorite(1);
                        mDBHelper.add_fav(modItem.get_id());
                        favBut.setBackgroundResource(R.drawable.ic_favorite_true);
                    } else if (modItem.getFavorite() == 1) {
                        modItem.setFavorite(0);
                        mDBHelper.remove_fav(modItem.get_id());
                        favBut.setBackgroundResource(R.drawable.ic_favorite_false);
                    }
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}