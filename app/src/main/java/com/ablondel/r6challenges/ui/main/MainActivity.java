package com.ablondel.r6challenges.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.ablondel.r6challenges.App;
import com.ablondel.r6challenges.R;
import com.ablondel.r6challenges.model.UserInfos;
import com.ablondel.r6challenges.model.challenge.Challenges;
import com.ablondel.r6challenges.model.games.Game;
import com.ablondel.r6challenges.model.games.GamePlatformEnum;
import com.ablondel.r6challenges.model.profile.Profile;
import com.ablondel.r6challenges.model.util.SpinnerKeyValue;
import com.ablondel.r6challenges.service.SharedPreferencesService;
import com.ablondel.r6challenges.service.UbiService;
import com.ablondel.r6challenges.ui.login.LoginActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UbiService ubiService;
    private UserInfos userInfos;
    private RefreshChallengesTask refreshChallengesTask = null;
    private View progressView;
    private View mainContentView;
    private Handler handler;
    ChallengesRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContentView = findViewById(R.id.mainContent);
        progressView = findViewById(R.id.refreshProgress);
        ubiService = new UbiService();

        try {
            userInfos = new Gson().fromJson(SharedPreferencesService.getEncryptedSharedPreferences().getString("userInfos", null), UserInfos.class);

            if (null == userInfos) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            final List<SpinnerKeyValue> arraySpinner = new ArrayList<>();
            int selectedIndex = 0, i = 0;
            for(Game game : userInfos.getGames()) {
                if(game.isOwned()) {
                    String platform = GamePlatformEnum.getPlatformByKey(game.getPlatform()).getPlatform();
                    Profile platformProfile = userInfos.getProfileList().getProfileByPlatformType(platform);
                    arraySpinner.add(
                            new SpinnerKeyValue(
                                    game.getPlatform(),
                                    null == platformProfile ? "Undefined" : platformProfile.getNameOnPlatform() + " (" + platform + ")"
                            )
                    );
                    if(game.getPlatform().equals(userInfos.getLastSelectedPlatform())) {
                        selectedIndex = i;
                    }
                    i++;
                }
            }
            Spinner spinner = findViewById(R.id.playerWithPlatformSpinner);
            ArrayAdapter<SpinnerKeyValue> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, arraySpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(selectedIndex);
            // Fired on startup, so the challenges will load automatically anyway
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    SpinnerKeyValue spinnerKeyValue = (SpinnerKeyValue) parentView.getItemAtPosition(position);
                    if(!userInfos.getLastSelectedPlatform().equals(spinnerKeyValue.getKey())) {
                        userInfos.setLastSelectedPlatform(spinnerKeyValue.getKey());
                        try {
                            SharedPreferencesService.getEncryptedSharedPreferences().edit().putString("userInfos",new Gson().toJson(userInfos)).apply();
                        } catch (GeneralSecurityException | IOException e) {
                            Log.e("Could not write shared preferences", e.getMessage());
                        }
                    }
                    refreshChallenges();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }

            });

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
            finish();
        });

        ImageButton refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener((v) -> refreshChallenges());

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Toast.makeText(
                        getApplicationContext(),
                        (String) message.obj,
                        Toast.LENGTH_LONG).show();

            }
        };
    }

    private void refreshChallenges() {
        showProgress(true);
        refreshChallengesTask = new RefreshChallengesTask();
        refreshChallengesTask.execute((Void) null);
    }

    public class RefreshChallengesTask extends AsyncTask<Void, Void, Challenges> {

        @Override
        protected Challenges doInBackground(Void... params) {
            Challenges data = null;
            String message = "Challenges updated!";
            String challengesJson = ubiService.getChallenges(userInfos, GamePlatformEnum.getPlatformByKey(userInfos.getLastSelectedPlatform()).getSpaceId());
            if (ubiService.isValidResponse(challengesJson)) {
                data = new Gson().fromJson(challengesJson, Challenges.class);
            } else {
                message = ubiService.getErrorMessage(challengesJson);
            }
            sendMessage(message);
            Log.d("Result :", message);

            return data;
        }

        @Override
        protected void onPostExecute(final Challenges data) {
            RecyclerView recyclerView = findViewById(R.id.mainContentRecyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
            if(null != data) {
                adapter = new ChallengesRecyclerViewAdapter(App.getAppContext(), data.getData().getGame().getViewer().getMeta().getPeriodicChallenges().getChallenges());
                recyclerView.setAdapter(adapter);
            }
            refreshChallengesTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            refreshChallengesTask = null;
            showProgress(false);
        }

        private void sendMessage(String message) {
            Message msg = Message.obtain();
            msg.obj = message;
            msg.setTarget(handler);
            msg.sendToTarget();
        }
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mainContentView.setVisibility(show ? View.GONE : View.VISIBLE);
        mainContentView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainContentView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}