package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemNotification;
import com.photo.pose.photoshoot.cliq.R;

import java.util.ArrayList;

public class PCliq_AdapterNotification extends RecyclerView.Adapter<PCliq_AdapterNotification.ViewHolder> {

    private Context context;
    private ArrayList<PCliq_ItemNotification> arrayList;

    public PCliq_AdapterNotification(Context context, ArrayList<PCliq_ItemNotification> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pcliq_item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PCliq_ItemNotification item = arrayList.get(position);
        
        holder.tvTitle.setText(item.getTitle());
        holder.tvMessage.setText(item.getMessage());
        
        if (item.getImage() != null && !item.getImage().isEmpty() && !item.getImage().equals("null")) {
            holder.cvImageContainer.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(item.getImage())
                    .placeholder(R.drawable.pcliq_ic_placeholder_landscape)
                    .into(holder.ivImage);
        } else {
            holder.cvImageContainer.setVisibility(View.GONE);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (item.getUrl() != null && !item.getUrl().isEmpty() && !item.getUrl().equals("null")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage;
        ImageView ivImage;
        View cvImageContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_noti_title);
            tvMessage = itemView.findViewById(R.id.tv_noti_message);
            ivImage = itemView.findViewById(R.id.iv_noti_image);
            cvImageContainer = itemView.findViewById(R.id.cv_image_container);
        }
    }
}
