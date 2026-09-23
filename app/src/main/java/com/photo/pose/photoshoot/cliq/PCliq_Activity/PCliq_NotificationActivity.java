package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterNotification;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemNotification;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.R;

import java.util.ArrayList;

public class PCliq_NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private TextView tvNoNotif;
    private ImageView ivBack;
    private PCliq_DBHelper dbHelper;
    private ArrayList<PCliq_ItemNotification> arrayList;
    private PCliq_AdapterNotification adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_notification);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(androidx.core.content.ContextCompat.getColor(this, R.color.bg_warm));
        }

        rvNotifications = findViewById(R.id.rv_notifications);
        tvNoNotif = findViewById(R.id.tv_no_notif);
        ivBack = findViewById(R.id.iv_back);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new PCliq_DBHelper(this);
        
        // Mark all as read when opening the screen
        dbHelper.markAllNotificationsAsRead();
        
        loadNotifications();

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void loadNotifications() {
        arrayList = dbHelper.getAllNotifications();
        
        if (arrayList.size() == 0) {
            tvNoNotif.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            tvNoNotif.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
            adapter = new PCliq_AdapterNotification(this, arrayList);
            rvNotifications.setAdapter(adapter);
        }
    }
}
