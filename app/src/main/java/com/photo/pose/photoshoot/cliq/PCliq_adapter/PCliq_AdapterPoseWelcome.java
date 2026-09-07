package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class PCliq_AdapterPoseWelcome extends RecyclerView.Adapter<PCliq_AdapterPoseWelcome.MyViewHolder> {

    ArrayList<PCliq_ItemPose> arrayList;
    Context context;
    PCliq_SharedPref sharedPref;
    PCliq_Methods methods;

    public PCliq_AdapterPoseWelcome(Context context, ArrayList<PCliq_ItemPose> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        methods = new PCliq_Methods(context);
        sharedPref = new PCliq_SharedPref(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rootlayout;
        View vieww;
        RoundedImageView my_image_view;

        private MyViewHolder(View view) {
            super(view);
            rootlayout = view.findViewById(R.id.rootlayout);
            my_image_view = view.findViewById(R.id.iv_wallpaper);
            vieww = view.findViewById(R.id.view_wall);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pcliq_layout_pose_welcome, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        Log.e("TAG", "onBindViewHolder: "+arrayList.get(position).getPosetips());
        int placeholder;
        if(arrayList.get(position).getType().equals(PCliq_Constant.TAG_PORTRAIT)) {
            placeholder = R.drawable.pcliq_ic_placeholder_portrait;
        } else if(arrayList.get(position).getTitle().equals(PCliq_Constant.TAG_LANDSCAPE)) {
            placeholder = R.drawable.pcliq_ic_placeholder_landscape;
        } else {
            placeholder = R.drawable.pcliq_ic_placeholder_square;
        }

        Picasso.get()
                .load(arrayList.get(position).getImage())
                .placeholder(placeholder)
                .resize(methods.getImageThumbWidth(arrayList.get(holder.getAbsoluteAdapterPosition()).getType()), methods.getImageThumbHeight(arrayList.get(holder.getAbsoluteAdapterPosition()).getType()))
                .centerCrop()
                .into(holder.my_image_view);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }
}