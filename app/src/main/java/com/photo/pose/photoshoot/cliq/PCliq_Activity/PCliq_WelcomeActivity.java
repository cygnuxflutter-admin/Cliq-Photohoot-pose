package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onesignal.OneSignal;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import androidx.appcompat.app.AppCompatActivity;

public class PCliq_WelcomeActivity extends AppCompatActivity {

    PCliq_SharedPref PC_sharedPref;
    PCliq_Methods PC_methods;
    View PC_btn_signup, PC_btn_login;
    View PC_button_skip, PC_tv_guest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_welcome);

        PC_methods = new PCliq_Methods(this);
        PC_methods.setStatusColor(getWindow());
        PC_methods.forceRTLIfSupported(getWindow());
        PC_sharedPref = new PCliq_SharedPref(this);

        View rlTopBar = findViewById(R.id.rl_welcome_top_bar);
        if (rlTopBar != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rlTopBar, (v, insets) -> {
                androidx.core.graphics.Insets statusBarInsets = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars());
                v.setPadding(v.getPaddingLeft(), statusBarInsets.top + 16, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }

        View cardContainer = findViewById(R.id.ll_welcome_card_container);
        if (cardContainer != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(cardContainer, (v, insets) -> {
                androidx.core.graphics.Insets navBarInsets = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.navigationBars());
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), navBarInsets.bottom + 24);
                return insets;
            });
        }

        PC_button_skip = findViewById(R.id.button_skip);
        PC_tv_guest = findViewById(R.id.tv_welcome_guest);
        PC_btn_signup = findViewById(R.id.btn_welcome_signup);
        PC_btn_login = findViewById(R.id.btn_welcome_login);

        if (PC_btn_login != null) {
            PC_btn_login.setOnClickListener(view -> {
                Intent intent = new Intent(PCliq_WelcomeActivity.this, PCliq_LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("from", "");
                startActivity(intent);
            });
        }

        if (PC_btn_signup != null) {
            PC_btn_signup.setOnClickListener(view -> {
                Intent intent = new Intent(PCliq_WelcomeActivity.this, PCliq_RegisterActivity.class);
                startActivity(intent);
            });
        }

        View.OnClickListener skipClickListener = view -> {
            Intent intent = new Intent(PCliq_WelcomeActivity.this, PCliq_MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        };

        if (PC_button_skip != null) {
            PC_button_skip.setOnClickListener(skipClickListener);
        }

        if (PC_tv_guest != null) {
            PC_tv_guest.setOnClickListener(skipClickListener);
        }

    }
}