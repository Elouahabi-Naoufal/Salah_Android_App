package com.salah.times;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PrayerAdapter extends RecyclerView.Adapter<PrayerAdapter.PrayerViewHolder> {
    private List<PrayerItem> prayers;
    
    public static class PrayerItem {
        public String name;
        public String time;
        public boolean isNext;
        
        public PrayerItem(String name, String time, boolean isNext) {
            this.name = name;
            this.time = time;
            this.isNext = isNext;
        }
    }
    
    public PrayerAdapter(List<PrayerItem> prayers) {
        this.prayers = prayers;
    }
    
    @NonNull
    @Override
    public PrayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new PrayerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PrayerViewHolder holder, int position) {
        PrayerItem prayer = prayers.get(position);
        holder.nameText.setText(TranslationManager.tr(prayer.name.toLowerCase()));
        holder.timeText.setText(prayer.time);
        
        if (prayer.isNext) {
            holder.nameText.setTextColor(0xFF66BB6A); // Green for next prayer
            holder.timeText.setTextColor(0xFF66BB6A);
        } else {
            holder.nameText.setTextColor(0xFFFFFFFF); // White for dark theme
            holder.timeText.setTextColor(0xB3FFFFFF); // Semi-transparent white
        }
    }
    
    @Override
    public int getItemCount() {
        return prayers.size();
    }
    
    public void updatePrayers(List<PrayerItem> newPrayers) {
        this.prayers = newPrayers;
        notifyDataSetChanged();
    }
    
    static class PrayerViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView timeText;
        
        PrayerViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(android.R.id.text1);
            timeText = itemView.findViewById(android.R.id.text2);
        }
    }
}