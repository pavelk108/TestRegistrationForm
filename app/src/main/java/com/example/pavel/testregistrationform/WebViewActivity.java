package com.example.pavel.testregistrationform;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {
    public static final String extraTitle = "extraTitle";
    public static final String extraURL = "extraURL";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();

        {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                //Configure action bar
                // enable back button
                actionBar.setDisplayHomeAsUpEnabled(true);

                //set title
                // title get from caller
                actionBar.setTitle(intent.getStringExtra(extraTitle));
            }
        }

        mWebView = findViewById(R.id.web_view);
        mWebView.loadUrl(intent.getStringExtra(extraURL));
    }

    // for back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
