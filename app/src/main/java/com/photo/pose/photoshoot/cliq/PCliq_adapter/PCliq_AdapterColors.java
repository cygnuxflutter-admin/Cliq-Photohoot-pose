package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemColors;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class PCliq_AdapterColors extends RecyclerView.Adapter {

    ArrayList<PCliq_ItemColors> arrayList;
    PCliq_Methods methods;
    ArrayList<String> arrayListSelected = new ArrayList<>();

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_colors;
        TextView tv_color;
        RoundedImageView imageView, iv_color_tick;

        private MyViewHolder(View view) {
            super(view);
            rl_colors = view.findViewById(R.id.rl_colors);
            imageView = view.findViewById(R.id.iv_color);
            tv_color = view.findViewById(R.id.tv_color);
            iv_color_tick = view.findViewById(R.id.iv_color_tick);
        }
    }

    public PCliq_AdapterColors(Context context, ArrayList<PCliq_ItemColors> arrayList) {
        this.arrayList = arrayList;
        methods = new PCliq_Methods(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pcliq_layout_colors_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        String hex = arrayList.get(position).getColorHex();
        int color = Color.parseColor(hex.startsWith("#") ? hex : ("#" + hex));
        boolean isSelected = arrayListSelected.contains(arrayList.get(position).getId());

        double luminance = ColorUtils.calculateLuminance(color);
        boolean isLightColor = luminance > 0.75;

        if (isSelected) {
            myViewHolder.imageView.setBorderColor(Color.parseColor("#C19543"));
            myViewHolder.iv_color_tick.setVisibility(View.VISIBLE);
            if (isLightColor) {
                myViewHolder.iv_color_tick.setColorFilter(Color.parseColor("#2B1D15"));
            } else {
                myViewHolder.iv_color_tick.setColorFilter(Color.WHITE);
            }
        } else {
            if (isLightColor) {
                myViewHolder.imageView.setBorderColor(Color.parseColor("#A38F7A"));
            } else {
                myViewHolder.imageView.setBorderColor(Color.parseColor("#DED4CA"));
            }
            myViewHolder.iv_color_tick.setVisibility(View.GONE);
        }

        myViewHolder.imageView.setImageDrawable(new android.graphics.drawable.ColorDrawable(color));
        myViewHolder.imageView.setColorFilter(null);
        myViewHolder.tv_color.setText(arrayList.get(position).getColorName());
        myViewHolder.tv_color.setVisibility(View.GONE);
    }

    public void setMultipleSelected(String colorIDs) {
        ArrayList<String> arrayListColorIds = new ArrayList<>(Arrays.asList(colorIDs.split(",")));
        arrayListColorIds.removeAll(Collections.singleton(""));
        for (int i = 0; i < arrayListColorIds.size(); i++) {
            for (int j = 0; j < arrayList.size(); j++) {
                if (arrayListColorIds.get(i).equals(arrayList.get(j).getId())) {
                    arrayListSelected.add(arrayList.get(j).getId());
                    break;
                }
            }
        }
    }

    public void setSelected(int position) {
        if (arrayListSelected.contains(arrayList.get(position).getId())) {
            arrayListSelected.remove(arrayList.get(position).getId());
        } else {
            arrayListSelected.add(arrayList.get(position).getId());
        }
        notifyItemChanged(position);
    }

    public String getSelected() {
        String selectedIDs = "";
        if (arrayListSelected.size() > 0 && arrayListSelected.size() < getItemCount()) {
            StringBuilder selectedIDsBuilder = new StringBuilder(arrayListSelected.get(0));
            for (int i = 1; i < arrayListSelected.size(); i++) {
                selectedIDsBuilder.append(",").append(arrayListSelected.get(i));
            }
            selectedIDs = selectedIDsBuilder.toString();
        }
        return selectedIDs;
    }

    public void clearSelected() {
        arrayListSelected.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}