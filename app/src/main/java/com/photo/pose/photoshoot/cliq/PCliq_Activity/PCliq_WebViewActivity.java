package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPage;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PCliq_WebViewActivity extends AppCompatActivity {

    Toolbar toolbar;
    PCliq_Methods methods;
    WebView webView;
    PCliq_ItemPage itemPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_webview);

        methods = new PCliq_Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        itemPage = (PCliq_ItemPage) getIntent().getSerializableExtra("item");

        toolbar = this.findViewById(R.id.toolbar_pages);
        toolbar.setTitle(itemPage.getTitle());
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = findViewById(R.id.webView_pages);
        webView.getSettings().setJavaScriptEnabled(true);

        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        String text;
        if (methods.isDarkMode()) {
            text = "<html><head>"
                    + "<style> body{color: #fff !important;text-align:left}"
                    + "</style></head>"
                    + "<body>"
                    + itemPage.getContent()
                    + "</body></html>";
        } else {
            text = "<html><head>"
                    + "<style> body{color: #000 !important;text-align:left}"
                    + "</style></head>"
                    + "<body>"
                    + itemPage.getContent()
                    + "</body></html>";
        }

        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}