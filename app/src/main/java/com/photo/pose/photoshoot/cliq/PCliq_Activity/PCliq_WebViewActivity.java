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
import androidx.core.content.ContextCompat;

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
        toolbar.setTitle(itemPage != null ? itemPage.getTitle() : "");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.text_espresso));
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (toolbar.getNavigationIcon() != null) {
                toolbar.getNavigationIcon().setTint(ContextCompat.getColor(this, R.color.text_espresso));
            }
        }

        webView = findViewById(R.id.webView_pages);
        webView.getSettings().setJavaScriptEnabled(true);

        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        String content = itemPage != null ? itemPage.getContent() : "";

        String text = "<html><head>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0, user-scalable=no'>"
                + "<style>"
                + "* { box-sizing: border-box; }"
                + "body { color: #503E32 !important; background-color: #FFFFFF !important; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; padding: 22px 18px 32px 18px; margin: 0; line-height: 1.75; font-size: 14.5px; word-wrap: break-word; }"
                + ".header-badge { display: inline-block; background: #FAF6F0; border: 1px solid #E5DDD5; color: #C19543; font-size: 11px; font-weight: 700; letter-spacing: 1.2px; padding: 5px 12px; border-radius: 12px; margin-bottom: 14px; text-transform: uppercase; }"
                + "h1, h2, h3, h4 { color: #2B1D15 !important; font-family: Georgia, 'Times New Roman', serif; font-weight: 700; margin-top: 18px; margin-bottom: 10px; line-height: 1.35; }"
                + "h1 { font-size: 21px; color: #2B1D15 !important; }"
                + "h2 { font-size: 17px; color: #2B1D15 !important; border-bottom: 1px solid #EDE4DC; padding-bottom: 6px; margin-top: 22px; }"
                + "h3 { font-size: 15px; color: #C19543 !important; }"
                + "p { color: #503E32 !important; margin: 0 0 14px 0; }"
                + "strong, b { color: #2B1D15 !important; font-weight: 600; }"
                + "a { color: #C19543 !important; text-decoration: underline; font-weight: 500; }"
                + "ul, ol { padding-left: 20px; margin: 0 0 16px 0; }"
                + "li { color: #503E32 !important; margin-bottom: 8px; }"
                + "hr { border: none; height: 1px; background: linear-gradient(to right, #C19543, #EDE4DC, transparent); margin: 16px 0 20px 0; }"
                + "</style></head>"
                + "<body>"
                + "<div class='header-badge'>CLIQ Studio • Policy &amp; Terms</div>"
                + content
                + "</body></html>";

        webView.setBackgroundColor(Color.WHITE);
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