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
            .inflate(R.layout.item_prayer_card, parent, false);
        return new PrayerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PrayerViewHolder holder, int position) {
        PrayerItem prayer = prayers.get(position);
        holder.nameText.setText(TranslationManager.tr(prayer.name.toLowerCase()));
        holder.timeText.setText(prayer.time);
        
        // Set prayer icon
        String icon = getPrayerIcon(prayer.name);
        holder.iconText.setText(icon);
        
        if (prayer.isNext) {
            int primaryColor = holder.itemView.getContext().getColor(R.color.primary_green);
            holder.nameText.setTextColor(primaryColor);
            holder.timeText.setTextColor(primaryColor);
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
        TextView iconText;
        androidx.cardview.widget.CardView cardView;
        
        PrayerViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.prayer_name);
            timeText = itemView.findViewById(R.id.prayer_time);
            iconText = itemView.findViewById(R.id.prayer_icon);
            cardView = (androidx.cardview.widget.CardView) itemView;
        }
    }
    
    private String getPrayerIcon(String prayerName) {
        switch (prayerName) {
            case "Fajr": return "🌅";
            case "Dohr": return "☀️";
            case "Asr": return "🌤️";
            case "Maghreb": return "🌅";
            case "Isha": return "🌙";
            default: return "🕌";
        }
    }
}