package com.utkuakgungor.filmruleti.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.utkuakgungor.filmruleti.R;

import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WebView webView= findViewById(R.id.webview);
        if(Objects.requireNonNull(getIntent().getStringExtra("type")).equals("terms")){
            webView.loadUrl("https://movie-roulette-2db47.web.app/terms.html");
        }
        else{
            webView.loadUrl("https://movie-roulette-2db47.web.app/privacy.html");
        }
    }
}