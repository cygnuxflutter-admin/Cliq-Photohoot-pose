package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemCat;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PCliq_AdapterCategories extends RecyclerView.Adapter {

    Activity context;
    ArrayList<PCliq_ItemCat> arrayList;
    ArrayList<PCliq_ItemCat> filteredArrayList;
    NameFilter filter;
    int width, height;
    PCliq_Methods methods;
//    private final PCliq_NativeAdUtil nativeAdUtil;
    private class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout cl_cat;
        RoundedImageView imageView;
        TextView textView_cat, tv_cat_total;
//        RelativeLayout native_banner_ad_container;
//        private final RelativeLayout ad_layout;
        private MyViewHolder(View view) {
            super(view);
            cl_cat = view.findViewById(R.id.cl_cat);
            imageView = view.findViewById(R.id.iv_cat);
            textView_cat = view.findViewById(R.id.tv_cat_title);
//            ad_layout = itemView.findViewById(R.id.ad_layout);
//            native_banner_ad_container = view.findViewById(R.id.native_banner_ad_container);
//            tv_cat_total = view.findViewById(R.id.tv_cat_total);
        }
    }

    public PCliq_AdapterCategories(Activity context, ArrayList<PCliq_ItemCat> arrayList) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        this.context = context;
        methods = new PCliq_Methods(context);
        width = methods.getColumnWidth(2, 8);
        height = (int) (width / 1.5);
//        this.nativeAdUtil = new PCliq_NativeAdUtil(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View contentView = inflater.inflate(R.layout.pcliq_layout_categories, parent, false);
        return new MyViewHolder(contentView);


    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {


//        if (arrayList.get(position) != null) {
            ((MyViewHolder) holder).cl_cat.setVisibility(View.VISIBLE);
//            ((MyViewHolder) holder).ad_layout.setVisibility(View.GONE);
//            ((MyViewHolder) holder).textView_cat.setText(arrayList.get(position).getName());
//        ((MyViewHolder) holder).tv_cat_total.setText(arrayList.get(position).getTotalWallpaper());
            Picasso.get()
                    .load(arrayList.get(position).getImage())
//                .resize(methods.getImageThumbWidth(context.getString(R.string.categories)), methods.getImageThumbHeight(context.getString(R.string.categories)))
//                .centerCrop()
//                    .placeholder(R.drawable.pcliq_placeholder_cat)
                    .into(((MyViewHolder) holder).imageView);
//        } else {
//            ((MyViewHolder) holder).cl_cat.setVisibility(View.GONE);
//            ((MyViewHolder) holder).ad_layout.setVisibility(View.VISIBLE);
//            nativeAdUtil.fillAdmobNativeAd(((MyViewHolder) holder).native_banner_ad_container);
//        }

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

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    public int getRealPos(int pos, ArrayList<PCliq_ItemCat> arrayListTemp) {
        return arrayListTemp.indexOf(arrayList.get(pos));
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<PCliq_ItemCat> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getName();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arrayList = (ArrayList<PCliq_ItemCat>) results.values;
            notifyDataSetChanged();
        }
    }
}