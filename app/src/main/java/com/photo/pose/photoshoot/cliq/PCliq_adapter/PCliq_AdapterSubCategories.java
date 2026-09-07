package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemSubCat;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


public class PCliq_AdapterSubCategories extends RecyclerView.Adapter {

    Context context;
    PCliq_Methods methods;
    ArrayList<PCliq_ItemSubCat> arrayList, filteredArrayList;
    int selectedPos = -1;

    private class MyViewHolder extends RecyclerView.ViewHolder {

        View cl_sub_cat;
        TextView tv_sub_cat;

        private MyViewHolder(View view) {
            super(view);
            cl_sub_cat = view.findViewById(R.id.cl_sub_cat);
            tv_sub_cat = view.findViewById(R.id.tv_sub_cat_title);
        }
    }

    public PCliq_AdapterSubCategories(Context context, ArrayList<PCliq_ItemSubCat> arrayList) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        this.context = context;
        methods = new PCliq_Methods(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pcliq_layout_sub_categories, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        ((MyViewHolder) holder).tv_sub_cat.setText(arrayList.get(position).getName());

        if (selectedPos != holder.getAbsoluteAdapterPosition()) {
            ((MyViewHolder) holder).cl_sub_cat.setBackgroundResource(R.drawable.pcliq_bg_chip_unselected);
            ((MyViewHolder) holder).tv_sub_cat.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.chip_unselected_text));
        } else {
            ((MyViewHolder) holder).cl_sub_cat.setBackgroundResource(R.drawable.pcliq_bg_chip_selected);
            ((MyViewHolder) holder).tv_sub_cat.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.chip_selected_text));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getID(int pos) {
        return arrayList.get(pos).getId();
    }

    public String setSelected(int position) {
        if(position != selectedPos) {
            int oldPos = selectedPos;
            selectedPos = position;
            notifyItemChanged(selectedPos);
            notifyItemChanged(oldPos);
            return arrayList.get(selectedPos).getId();
        } else {
            int oldPos = selectedPos;
            selectedPos = -1;
            notifyItemChanged(oldPos);
            return "";
        }
    }

    public int getSelected() {
        return selectedPos;
    }
}