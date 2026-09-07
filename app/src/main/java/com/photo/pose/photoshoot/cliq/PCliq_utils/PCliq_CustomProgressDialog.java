package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.photo.pose.photoshoot.cliq.R;

public class PCliq_CustomProgressDialog extends Dialog {

    private TextView textView;
    private String pendingMessage = "";

    public PCliq_CustomProgressDialog(@NonNull Context context) {
        super(context);
    }

    public PCliq_CustomProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pcliq_lottie_anim_dialog);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        textView = findViewById(R.id.textView);
        if (textView != null && pendingMessage != null && !pendingMessage.isEmpty()) {
            textView.setText(pendingMessage);
        }
    }

    public void setMessage(CharSequence message) {
        if (message != null) {
            this.pendingMessage = message.toString();
            if (textView != null) {
                textView.setText(message);
            }
        }
    }

    public void setProgress(int value) {
        // Compatibility stub
    }

    public void setMax(int max) {
        // Compatibility stub
    }
}
