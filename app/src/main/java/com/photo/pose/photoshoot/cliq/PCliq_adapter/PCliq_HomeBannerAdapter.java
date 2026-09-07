package com.photo.pose.photoshoot.cliq.PCliq_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.photo.pose.photoshoot.cliq.R;

import java.util.ArrayList;
import java.util.List;

public class PCliq_HomeBannerAdapter extends PagerAdapter {

    public static class BannerItem {
        public int imageRes;
        public String badge;
        public String title;
        public String subtitle;

        public BannerItem(int imageRes, String badge, String title, String subtitle) {
            this.imageRes = imageRes;
            this.badge = badge;
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    private final Context context;
    private final List<BannerItem> items;
    private final OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(int position, BannerItem item);
    }

    public PCliq_HomeBannerAdapter(Context context, OnBannerClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.items = new ArrayList<>();
        this.items.add(new BannerItem(
                R.drawable.pcliq_hero_banner_editorial,
                "✨ TRENDING INSPIRATION",
                "Pre-Wedding & Couple",
                "5,000+ curated aesthetic poses"
        ));
        this.items.add(new BannerItem(
                R.drawable.pcliq_banner_bridal,
                "👰 ROYAL BRIDAL",
                "Vogue Bride & Groom",
                "Opulent royal photoshoot poses"
        ));
        this.items.add(new BannerItem(
                R.drawable.pcliq_banner_streetwear,
                "🕶️ URBAN VOGUE",
                "Streetwear & Fashion",
                "Modern aesthetic outdoor angles"
        ));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.pcliq_item_home_slider, container, false);
        ImageView ivImage = view.findViewById(R.id.iv_slider_img);
        TextView tvBadge = view.findViewById(R.id.tv_slider_badge);
        TextView tvTitle = view.findViewById(R.id.tv_slider_title);
        TextView tvSubtitle = view.findViewById(R.id.tv_slider_subtitle);
        View btnExplore = view.findViewById(R.id.btn_slider_explore);

        final BannerItem item = items.get(position);
        ivImage.setImageResource(item.imageRes);
        tvBadge.setText(item.badge);
        tvTitle.setText(item.title);
        tvSubtitle.setText(item.subtitle);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBannerClick(position, item);
                }
            }
        };

        view.findViewById(R.id.cv_slider_card).setOnClickListener(clickListener);
        btnExplore.setOnClickListener(clickListener);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
