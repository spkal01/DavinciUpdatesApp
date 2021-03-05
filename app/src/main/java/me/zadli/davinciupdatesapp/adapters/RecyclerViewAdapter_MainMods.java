package me.zadli.davinciupdatesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.stream.IntStream;

import me.zadli.davinciupdatesapp.R;

public class RecyclerViewAdapter_MainMods extends RecyclerView.Adapter<RecyclerViewAdapter_MainMods.ViewHolder> {

    Context context;
    JSONObject mods;
    int count;
    SharedPreferences sharedPreferences;
    ArrayList<String> build_date = new ArrayList<>();
    ArrayList<String> mods_name = new ArrayList<>();
    int[] sortedItems;
    Drawable placeholder;

    public RecyclerViewAdapter_MainMods(Context context, JSONObject mods, int count) throws JSONException {
        this.context = context;
        this.mods = mods;
        this.count = count;
        sharedPreferences = context.getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        for (int i = 0; i < count; i++) {
            build_date.add(i, mods.getJSONObject(String.valueOf(i)).getString("upload_date"));
            mods_name.add(i, mods.getJSONObject(String.valueOf(i)).getString("name"));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.rv_main_mods, null);
        View background = view.findViewById(R.id.rv_main_mods_background);
        if ((parent.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES){
            background.setBackgroundColor(parent.getContext().getResources().getColor(R.color.background_night, parent.getContext().getTheme()));
            placeholder = ContextCompat.getDrawable(parent.getContext(), R.drawable.loading_placeholder_white);
        } else {
            placeholder = ContextCompat.getDrawable(parent.getContext(), R.drawable.loading_placeholder_black);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            switch (sharedPreferences.getString("SORT_METHOD", "By Json")) {
                case "By Json":
                    setContent(holder, position);
                    break;
                case "By Name":
                    sortedItems = IntStream.range(0, count)
                            .boxed().sorted((i, j) -> mods_name.get(i).compareTo(mods_name.get(j)))
                            .mapToInt(ele -> ele).toArray();
                    setContent(holder, sortedItems[position]);
                    break;
                case "By Date":
                    sortedItems = IntStream.range(0, count)
                            .boxed().sorted((i, j) -> build_date.get(j).compareTo(build_date.get(i)))
                            .mapToInt(ele -> ele).toArray();
                    setContent(holder, sortedItems[position]);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setContent(@NonNull ViewHolder holder, int position) throws JSONException {
        Picasso.with(context)
                .load(mods.getJSONObject(String.valueOf(position)).getString("image"))
                .resize(1368, 1024)
                .centerInside()
                .placeholder(placeholder)
                .into(holder.rv_main_mods_image);
        holder.rv_main_mods_name.setText(mods.getJSONObject(String.valueOf(position)).getString("name"));
        holder.rv_main_mods_version.setText(mods.getJSONObject(String.valueOf(position)).getString("version"));
        holder.rv_main_mods_build_date.setText(mods.getJSONObject(String.valueOf(position)).getString("upload_date"));
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rv_main_mods_name;
        TextView rv_main_mods_build_date;
        TextView rv_main_mods_version;
        ImageView rv_main_mods_image;
        Button rv_main_mods_download_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_main_mods_name = itemView.findViewById(R.id.rv_main_mods_name);
            rv_main_mods_build_date = itemView.findViewById(R.id.rv_main_mods_build_date);
            rv_main_mods_version = itemView.findViewById(R.id.rv_main_mods_version);
            rv_main_mods_image = itemView.findViewById(R.id.rv_main_mods_image);
            rv_main_mods_download_button = itemView.findViewById(R.id.rv_main_mods_download_button);

            rv_main_mods_download_button.setOnClickListener(v -> {
                try {
                    if (sharedPreferences.getString("SORT_METHOD", "By Json").equals("By Json")) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mods.getJSONObject(String.valueOf(getAdapterPosition())).getString("download_link"))));
                    } else {
                        context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mods.getJSONObject(String.valueOf(sortedItems[getAdapterPosition()])).getString("download_link"))));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
