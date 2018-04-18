package com.freeme.voiceassistant;

import java.net.URL;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
    private WebView mWebView;
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            this.finish();
        }
    }

    public void loadUri(URL url) {
        mWebView.loadUrl(url.toString());
    }

    private WebViewClient mClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    };

    private WebChromeClient mChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        CookieSyncManager.createInstance(this);
        setContentView(R.layout.webview);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(mClient);
        mWebView.setWebChromeClient(mChromeClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.setHorizontalScrollBarEnabled(true);
        mWebView.setVerticalScrollBarEnabled(false);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getString(R.string.sr_search_title));

        Intent intent = getIntent();
        if (intent == null)
            return;
        Uri uri = intent.getData();
        if (uri != null) {
            mWebView.loadUrl(uri.toString());
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

}
