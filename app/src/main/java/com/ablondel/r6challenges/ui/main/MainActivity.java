package com.ablondel.r6challenges.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.ablondel.r6challenges.R;
import com.ablondel.r6challenges.model.UserInfos;
import com.ablondel.r6challenges.service.SharedPreferencesService;
import com.ablondel.r6challenges.ui.login.LoginActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            UserInfos userInfos = new Gson().fromJson(SharedPreferencesService.getEncryptedSharedPreferences().getString("userInfos", null), UserInfos.class);

            if(null == userInfos) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        } catch (GeneralSecurityException | IOException e) {
            Log.e("Could not read shared preferences", e.getMessage());
        }

        ImageButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener((v) -> {
            try {
                SharedPreferencesService.getEncryptedSharedPreferences().edit().clear().apply();
            } catch (GeneralSecurityException | IOException e) {
                Log.e("Could not clean shared preferences", e.getMessage());
            }
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } );
    }
}