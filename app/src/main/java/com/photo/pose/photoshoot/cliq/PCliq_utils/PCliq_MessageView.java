package com.photo.pose.photoshoot.cliq.PCliq_utils;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.photo.pose.photoshoot.cliq.R;

public class PCliq_MessageView extends LinearLayout {

    private TextView message;
    private TextView title;

    public PCliq_MessageView(Context context) {
        this(context, null);
    }

    public PCliq_MessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PCliq_MessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        inflate(context, R.layout.pcliq_control_view, this);
        ViewGroup content = findViewById(R.id.content);
        inflate(context, R.layout.pcliq_spinner_text, content);
        title = findViewById(R.id.title);
        message = (TextView) content.getChildAt(0);
    }

    public void setTitleAndMessage(String title, String message) {
        setTitle(title);
        setMessage(message);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }
}
