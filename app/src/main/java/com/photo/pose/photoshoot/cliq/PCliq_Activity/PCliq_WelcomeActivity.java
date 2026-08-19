package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.content.Intent;
import android.os.Bundle;
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
    ImageView PC_btn_signup, PC_btn_login;
    TextView PC_button_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_welcome);

        PC_methods = new PCliq_Methods(this);
        PC_sharedPref = new PCliq_SharedPref(this);

        PC_button_skip = findViewById(R.id.button_skip);
        PC_btn_signup = findViewById(R.id.btn_welcome_signup);
        PC_btn_login = findViewById(R.id.btn_welcome_login);

        PC_btn_login.setOnClickListener(view -> {
            Intent intent = new Intent(PCliq_WelcomeActivity.this, PCliq_LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
            startActivity(intent);
        });

        PC_btn_signup.setOnClickListener(view -> {
            Intent intent = new Intent(PCliq_WelcomeActivity.this, PCliq_RegisterActivity.class);
            startActivity(intent);
        });

        PC_button_skip.setOnClickListener(view -> {
            Intent intent = new Intent(PCliq_WelcomeActivity.this, PCliq_MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

    }
}