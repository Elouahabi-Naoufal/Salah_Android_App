package com.salah.times;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Collections;

public class AdhkarAdapter extends RecyclerView.Adapter<AdhkarAdapter.ViewHolder> {
    private List<AdhkarItem> adhkarList;
    private String type;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onDeleteItem(int id);
        void onItemMoved(int fromPosition, int toPosition);
    }

    public AdhkarAdapter(List<AdhkarItem> adhkarList, String type, OnItemActionListener listener) {
        this.adhkarList = adhkarList;
        this.type = type;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_adhkar_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AdhkarItem item = adhkarList.get(position);
        holder.adhkarText.setText(item.getText());
        
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteItem(item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return adhkarList.size();
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(adhkarList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(adhkarList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        
        if (listener != null) {
            listener.onItemMoved(fromPosition, toPosition);
        }
    }

    public void updateList(List<AdhkarItem> newList) {
        this.adhkarList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView adhkarText;
        ImageView dragHandle;
        ImageView deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            adhkarText = itemView.findViewById(R.id.adhkar_text);
            dragHandle = itemView.findViewById(R.id.drag_handle);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}