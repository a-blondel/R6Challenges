package com.ablondel.r6challenges.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ablondel.r6challenges.R;
import com.ablondel.r6challenges.model.UserInfos;
import com.ablondel.r6challenges.model.challenge.Challenges;
import com.ablondel.r6challenges.service.SharedPreferencesService;
import com.ablondel.r6challenges.service.UbiService;
import com.ablondel.r6challenges.ui.login.LoginActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.ablondel.r6challenges.service.UbiService.R6_PS4_SPACEID;

public class MainActivity extends AppCompatActivity {

    private UbiService ubiService;
    private UserInfos userInfos;
    private RefreshChallengesTask refreshChallengesTask = null;
    private View progressView;
    private View mainContentView;
    private Handler handler;

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
        });

        ImageButton refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener((v) -> {
            showProgress(true);
            refreshChallengesTask = new RefreshChallengesTask();
            refreshChallengesTask.execute((Void) null);
        });

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

    /**
     * Represents an asynchronous task used to update the challenges
     */
    public class RefreshChallengesTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isOk = false;
            String message = "Challenges updated!";
            String challengesJson = ubiService.getChallenges(userInfos, R6_PS4_SPACEID);

            if (ubiService.isValidResponse(challengesJson)) {
                Challenges data = new Gson().fromJson(challengesJson, Challenges.class);
                isOk = true;
            } else {
                message = ubiService.getErrorMessage(challengesJson);
            }
            sendMessage(message);
            Log.d("Result :", message);

            if (isOk) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
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


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mainContentView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}