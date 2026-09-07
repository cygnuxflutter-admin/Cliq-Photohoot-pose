package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.R;

import java.util.ArrayList;

public class PCliq_AdapterCameraPoseStrip extends RecyclerView.Adapter<PCliq_AdapterCameraPoseStrip.PoseThumbViewHolder> {

    public interface OnPoseClickListener {
        void onPoseClick(PCliq_ItemPose item, int position);
    }

    private final Context context;
    private final ArrayList<PCliq_ItemPose> arrayList;
    private final OnPoseClickListener listener;
    private int selectedPosition = 0;

    public PCliq_AdapterCameraPoseStrip(Context context, ArrayList<PCliq_ItemPose> arrayList, OnPoseClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PoseThumbViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pcliq_layout_camera_pose_thumb, parent, false);
        return new PoseThumbViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PoseThumbViewHolder holder, int position) {
        PCliq_ItemPose item = arrayList.get(position);

        if (item != null && item.getImage() != null) {
            Glide.with(context)
                    .load(item.getImage())
                    .placeholder(R.drawable.pcliq_ic_placeholder_portrait)
                    .into(holder.ivThumb);
        }

        if (position == selectedPosition) {
            holder.cardView.setStrokeColor(Color.parseColor("#C19543"));
            holder.cardView.setStrokeWidth(4);
            holder.cardView.setScaleX(1.06f);
            holder.cardView.setScaleY(1.06f);
        } else {
            holder.cardView.setStrokeColor(Color.parseColor("#40C19543"));
            holder.cardView.setStrokeWidth(2);
            holder.cardView.setScaleX(1.0f);
            holder.cardView.setScaleY(1.0f);
        }

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            if (listener != null && selectedPosition >= 0 && selectedPosition < arrayList.size()) {
                listener.onPoseClick(arrayList.get(selectedPosition), selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList != null ? arrayList.size() : 0;
    }

    public void setSelectedPosition(int position) {
        int prev = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(prev);
        notifyItemChanged(selectedPosition);
    }

    static class PoseThumbViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView ivThumb;

        public PoseThumbViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv_pose_thumb);
            ivThumb = itemView.findViewById(R.id.iv_pose_thumb);
        }
    }
}
