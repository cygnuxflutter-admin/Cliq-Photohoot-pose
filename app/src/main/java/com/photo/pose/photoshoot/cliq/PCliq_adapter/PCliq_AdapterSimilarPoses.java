package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.R;

import java.util.ArrayList;

public class PCliq_AdapterSimilarPoses extends RecyclerView.Adapter<PCliq_AdapterSimilarPoses.SimilarPoseViewHolder> {

    public interface OnSimilarPoseClickListener {
        void onSimilarPoseClick(PCliq_ItemPose item, int position);
    }

    private final Context context;
    private final ArrayList<PCliq_ItemPose> arrayList;
    private final OnSimilarPoseClickListener listener;

    public PCliq_AdapterSimilarPoses(Context context, ArrayList<PCliq_ItemPose> arrayList, OnSimilarPoseClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SimilarPoseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pcliq_layout_similar_pose_item, parent, false);
        return new SimilarPoseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarPoseViewHolder holder, int position) {
        PCliq_ItemPose item = arrayList.get(position);

        if (item != null && item.getImage() != null) {
            Glide.with(context)
                    .load(item.getImage())
                    .placeholder(R.drawable.pcliq_ic_placeholder_portrait)
                    .into(holder.ivThumb);
        }

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (listener != null && pos >= 0 && pos < arrayList.size()) {
                listener.onSimilarPoseClick(arrayList.get(pos), pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList != null ? arrayList.size() : 0;
    }

    static class SimilarPoseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;

        public SimilarPoseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_similar_pose);
        }
    }
}
