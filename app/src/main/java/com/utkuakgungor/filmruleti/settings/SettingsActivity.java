package com.utkuakgungor.filmruleti.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.utkuakgungor.filmruleti.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialButton privacyButton = findViewById(R.id.privacyButton);
        MaterialButton termsButton = findViewById(R.id.termsButton);
        ImageButton tmdbButton = findViewById(R.id.tmdbButton);

        tmdbButton.setOnClickListener(v -> {
            Uri uriUrl = Uri.parse("https://www.themoviedb.org/");
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        });

        privacyButton.setOnClickListener(v -> {
            Intent privacyIntent = new Intent(this,WebViewActivity.class);
            privacyIntent.putExtra("type", "privacy");
            startActivity(privacyIntent);
        });

        termsButton.setOnClickListener(v -> {
            Intent termsIntent = new Intent(this,WebViewActivity.class);
            termsIntent.putExtra("type", "terms");
            startActivity(termsIntent);
        });
        Spinner spinner=findViewById(R.id.settingsTheme);
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.themes));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("Ayarlar", MODE_PRIVATE);
        if (sharedPreferences.contains("Dark")) {
            spinner.setSelection(1);
        } else if (sharedPreferences.contains("Light")) {
            spinner.setSelection(2);
        } else {
            spinner.setSelection(0);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.themes)[1])) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.remove("Light");
                    editor.putString("Dark", "Dark");
                    editor.commit();
                } else if (parent.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.themes)[2])) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.remove("Dark");
                    editor.putString("Light", "Light");
                    editor.commit();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    editor.remove("Dark");
                    editor.remove("Light");
                    editor.commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}