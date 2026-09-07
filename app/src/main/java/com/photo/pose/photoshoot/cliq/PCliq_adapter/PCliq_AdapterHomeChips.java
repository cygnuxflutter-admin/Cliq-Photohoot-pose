package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemCat;
import com.photo.pose.photoshoot.cliq.R;

import java.util.ArrayList;

public class PCliq_AdapterHomeChips extends RecyclerView.Adapter<PCliq_AdapterHomeChips.ChipViewHolder> {

    public interface OnChipClickListener {
        void onChipClick(PCliq_ItemCat item, int position);
    }

    private final Context context;
    private final ArrayList<PCliq_ItemCat> arrayList;
    private final OnChipClickListener listener;
    private int selectedPosition = 0;

    public PCliq_AdapterHomeChips(Context context, ArrayList<PCliq_ItemCat> arrayList, OnChipClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pcliq_layout_home_chip, parent, false);
        return new ChipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipViewHolder holder, int position) {
        PCliq_ItemCat item = arrayList.get(position);
        holder.tvChipName.setText(item.getName());

        if (position == selectedPosition) {
            holder.tvChipName.setBackgroundResource(R.drawable.pcliq_bg_chip_selected);
            holder.tvChipName.setTextColor(ContextCompat.getColor(context, R.color.chip_selected_text));
        } else {
            holder.tvChipName.setBackgroundResource(R.drawable.pcliq_bg_chip_unselected);
            holder.tvChipName.setTextColor(ContextCompat.getColor(context, R.color.chip_unselected_text));
        }

        holder.itemView.setOnClickListener(v -> {
            int prevSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prevSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null && selectedPosition >= 0 && selectedPosition < arrayList.size()) {
                listener.onChipClick(arrayList.get(selectedPosition), selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setSelectedPosition(int position) {
        int prev = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(prev);
        notifyItemChanged(selectedPosition);
    }

    static class ChipViewHolder extends RecyclerView.ViewHolder {
        TextView tvChipName;

        public ChipViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChipName = itemView.findViewById(R.id.tv_chip_name);
        }
    }
}