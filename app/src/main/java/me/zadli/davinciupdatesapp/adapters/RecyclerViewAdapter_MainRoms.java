package me.zadli.davinciupdatesapp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.stream.IntStream;

import me.zadli.davinciupdatesapp.R;
import me.zadli.davinciupdatesapp.fragments.bottom_sheets.BottomSheetDialogFragment_MainRoms;

public class RecyclerViewAdapter_MainRoms extends RecyclerView.Adapter<RecyclerViewAdapter_MainRoms.ViewHolder> {

    Context context;
    JSONObject roms;
    int count;
    SharedPreferences sharedPreferences;
    ArrayList<String> build_date = new ArrayList<>();
    ArrayList<String> rom_name = new ArrayList<>();
    int[] sortedItems;
    Drawable placeholder;

    public RecyclerViewAdapter_MainRoms(Context context, JSONObject roms, int count) throws JSONException {
        this.context = context;
        this.roms = roms;
        this.count = count;
        sharedPreferences = context.getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        for (int i = 0; i < count; i++) {
            build_date.add(i, roms.getJSONObject(String.valueOf(i)).getString("build_date"));
            rom_name.add(i, roms.getJSONObject(String.valueOf(i)).getString("rom_name"));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.rv_main_roms, null);
        View background = view.findViewById(R.id.rv_main_roms_background);
        if ((parent.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            background.setBackgroundColor(parent.getContext().getResources().getColor(R.color.background_night, parent.getContext().getTheme()));
            placeholder = ContextCompat.getDrawable(parent.getContext(), R.drawable.loading_placeholder_white);
        } else {
            placeholder = ContextCompat.getDrawable(parent.getContext(), R.drawable.loading_placeholder_black);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_MainRoms.ViewHolder holder, int position) {
        try {
            switch (sharedPreferences.getString("SORT_METHOD", "By Json")) {
                case "By Json":
                    setContent(holder, position);
                    break;
                case "By Name":
                    sortedItems = IntStream.range(0, count)
                            .boxed().sorted((i, j) -> rom_name.get(i).compareTo(rom_name.get(j)))
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

    public void setContent(@NonNull RecyclerViewAdapter_MainRoms.ViewHolder holder, int position) throws JSONException {
        Picasso.with(context)
                .load(roms.getJSONObject(String.valueOf(position)).getString("rom_image"))
                .resize(1368, 1024)
                .centerInside()
                .placeholder(placeholder)
                .into(holder.rv_main_roms_rom_image);
        holder.rv_main_roms_author.setText(String.format("%s: %s", context.getString(R.string.author), roms.getJSONObject(String.valueOf(position)).getString("author")));
        holder.rv_main_roms_rom_name.setText(roms.getJSONObject(String.valueOf(position)).getString("rom_name"));
        if (roms.getJSONObject(String.valueOf(position)).getBoolean("official")) {
            holder.rv_main_roms_official.setText(context.getString(R.string.official));
        } else {
            holder.rv_main_roms_official.setText(context.getString(R.string.unofficial));
        }
        holder.rv_main_roms_build_date.setText(roms.getJSONObject(String.valueOf(position)).getString("build_date"));
        holder.rv_main_roms_rom_version.setText(String.format("%s: %s", context.getString(R.string.version), roms.getJSONObject(String.valueOf(position)).getString("rom_version")));
        holder.rv_main_roms_rom_codename.setText(String.format("%s: %s", context.getString(R.string.codename), roms.getJSONObject(String.valueOf(position)).getString("rom_codename")));
        holder.rv_main_roms_android_version.setText(String.format("%s: %s", context.getString(R.string.android), roms.getJSONObject(String.valueOf(position)).getString("android_version")));
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView rv_main_roms_rom_image;
        TextView rv_main_roms_rom_name;
        TextView rv_main_roms_author;
        TextView rv_main_roms_official;
        TextView rv_main_roms_build_date;
        TextView rv_main_roms_rom_version;
        TextView rv_main_roms_rom_codename;
        TextView rv_main_roms_android_version;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rv_main_roms_rom_image = itemView.findViewById(R.id.rv_main_roms_rom_image);
            rv_main_roms_rom_name = itemView.findViewById(R.id.rv_main_roms_rom_name);
            rv_main_roms_author = itemView.findViewById(R.id.rv_main_roms_author);
            rv_main_roms_official = itemView.findViewById(R.id.rv_main_roms_official);
            rv_main_roms_build_date = itemView.findViewById(R.id.rv_main_roms_build_date);
            rv_main_roms_rom_version = itemView.findViewById(R.id.rv_main_roms_rom_version);
            rv_main_roms_rom_codename = itemView.findViewById(R.id.rv_main_roms_rom_codename);
            rv_main_roms_android_version = itemView.findViewById(R.id.rv_main_roms_android_version);

            itemView.setOnClickListener(v -> {
                try {
                    BottomSheetDialogFragment_MainRoms bottomSheetDialogFragment_mainRoms = new BottomSheetDialogFragment_MainRoms();
                    if (sharedPreferences.getString("SORT_METHOD", "By Json").equals("By Json")) {
                        bottomSheetDialogFragment_mainRoms.setArguments(send_data(getAdapterPosition()));
                    } else {
                        bottomSheetDialogFragment_mainRoms.setArguments(send_data(sortedItems[getAdapterPosition()]));
                    }
                    bottomSheetDialogFragment_mainRoms.show(((FragmentActivity) context).getSupportFragmentManager(), "MainBottomSheet");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }

        public Bundle send_data(int position) throws JSONException {
            Bundle send_data = new Bundle();
            send_data.putString("rom_image", roms.getJSONObject(String.valueOf(position)).getString("rom_image"));
            send_data.putString("changelog_link", roms.getJSONObject(String.valueOf(position)).getString("changelog_link"));
            send_data.putString("download_link", roms.getJSONObject(String.valueOf(position)).getString("download_link"));
            send_data.putString("download_vanilla_link", roms.getJSONObject(String.valueOf(position)).getString("download_vanilla_link"));
            send_data.putString("mirror_link", roms.getJSONObject(String.valueOf(position)).getString("mirror_link"));
            send_data.putString("mirror_vanilla_link", roms.getJSONObject(String.valueOf(position)).getString("mirror_vanilla_link"));
            send_data.putString("donate_paypal_link", roms.getJSONObject(String.valueOf(position)).getString("donate_paypal_link"));
            send_data.putString("donate_buymeacoffee_link", roms.getJSONObject(String.valueOf(position)).getString("donate_buymeacoffee_link"));
            send_data.putString("tg_author", roms.getJSONObject(String.valueOf(position)).getString("tg_author"));
            send_data.putString("tg_group", roms.getJSONObject(String.valueOf(position)).getString("tg_group"));
            return send_data;
        }
    }
}
