package com.ablondel.r6challenges.ui.login;

import static com.ablondel.r6challenges.service.UbiService.UBI_DATE_DELIMITER;
import static com.ablondel.r6challenges.service.UbiService.UBI_DATE_FORMAT;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ablondel.r6challenges.R;
import com.ablondel.r6challenges.model.UserInfos;
import com.ablondel.r6challenges.model.auth.Authentication;
import com.ablondel.r6challenges.model.games.Game;
import com.ablondel.r6challenges.model.games.GamePlatformEnum;
import com.ablondel.r6challenges.model.profile.ProfileList;
import com.ablondel.r6challenges.service.SharedPreferencesService;
import com.ablondel.r6challenges.service.UbiService;
import com.ablondel.r6challenges.ui.main.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private UserLoginTask mAuthTask = null;
    private UbiService ubiService;

    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        ubiService = new UbiService();

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Toast.makeText(
                        getApplicationContext(),
                        (String) message.obj,
                        Toast.LENGTH_LONG).show();

            }
        };
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        mEmailView.setError(null);
        mPasswordView.setError(null);
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean empty = TextUtils.isEmpty(password) || TextUtils.isEmpty(email);
        if (!empty) {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        public static final String ATTRIBUTE_IS_OWNED = "isOwned";
        public static final String ATTRIBUTE_LAST_PLAYED_DATE = "lastPlayedDate";
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String key = mEmail + ":" + mPassword;
            boolean isOk = false;
            String message = "Connected!";
            byte[] keyBytes;
            try {
                UserInfos userInfos = new UserInfos();
                keyBytes = key.getBytes(StandardCharsets.UTF_8);
                String encodedKey = Base64.encodeToString(keyBytes, Base64.NO_WRAP);
                String authenticationJson = ubiService.authenticate(encodedKey);
                if(ubiService.isValidResponse(authenticationJson)) {
                    userInfos.setAuthentication(new Gson().fromJson(authenticationJson, Authentication.class));
                    String profilesJson = ubiService.getProfiles(userInfos);
                    if(ubiService.isValidResponse(profilesJson)) {
                        userInfos.setProfileList(new Gson().fromJson(profilesJson, ProfileList.class));

                        // Check which platforms are owned by the user
                        // Platform is shared between consoles (PS4+PS5, XONE+XBSX)
                        String ps4PlatformJson = ubiService.getGame(userInfos, GamePlatformEnum.PS4.name());
                        String xonePlatformJson = ubiService.getGame(userInfos, GamePlatformEnum.XONE.name());
                        String pcPlatformJson = ubiService.getGame(userInfos, GamePlatformEnum.PC.name());
                        boolean ps4PlatformValid = ubiService.isValidResponse(ps4PlatformJson);
                        boolean xonePlatformValid = ubiService.isValidResponse(xonePlatformJson);
                        boolean pcPlatformValid = ubiService.isValidResponse(pcPlatformJson);

                        if(ps4PlatformValid && xonePlatformValid && pcPlatformValid) {
                            Game ps4Game = new Game(), xoneGame = new Game(), pcGame = new Game();
                            ps4Game.setPlatform(GamePlatformEnum.PS4.name());
                            xoneGame.setPlatform(GamePlatformEnum.XONE.name());
                            pcGame.setPlatform(GamePlatformEnum.PC.name());
                            JsonObject root = JsonParser.parseString(ps4PlatformJson).getAsJsonObject();
                            ps4Game.setOwned(getViewerRoot(root).get(ATTRIBUTE_IS_OWNED).getAsBoolean());
                            JsonElement lastPlayedDate = getViewerRoot(root).get(ATTRIBUTE_LAST_PLAYED_DATE);
                            ps4Game.setLastPlayedDate(lastPlayedDate.isJsonNull() ? null : lastPlayedDate.getAsString());

                            root = JsonParser.parseString(xonePlatformJson).getAsJsonObject();
                            xoneGame.setOwned(getViewerRoot(root).get(ATTRIBUTE_IS_OWNED).getAsBoolean());
                            lastPlayedDate = getViewerRoot(root).get(ATTRIBUTE_LAST_PLAYED_DATE);
                            xoneGame.setLastPlayedDate(lastPlayedDate.isJsonNull() ? null : lastPlayedDate.getAsString());

                            root = JsonParser.parseString(pcPlatformJson).getAsJsonObject();
                            pcGame.setOwned(getViewerRoot(root).get(ATTRIBUTE_IS_OWNED).getAsBoolean());
                            lastPlayedDate = getViewerRoot(root).get(ATTRIBUTE_LAST_PLAYED_DATE);
                            pcGame.setLastPlayedDate(lastPlayedDate.isJsonNull() ? null : lastPlayedDate.getAsString());

                            List<Game> games = Arrays.asList(ps4Game, xoneGame, pcGame);
                            userInfos.setGames(games);

                            // Default platform is the last one that has been used
                            userInfos.setLastSelectedPlatform(findLastPlatformPlayed(games));

                            SharedPreferencesService.getEncryptedSharedPreferences().edit().putString("userInfos",new Gson().toJson(userInfos)).apply();
                            isOk = true;
                        } else {
                            if(!ps4PlatformValid) {
                                message = ubiService.getErrorMessage(ps4PlatformJson);
                            } else if(!xonePlatformValid) {
                                message = ubiService.getErrorMessage(xonePlatformJson);
                            } else {
                                message = ubiService.getErrorMessage(pcPlatformJson);
                            }
                        }
                    } else {
                        message = ubiService.getErrorMessage(profilesJson);
                    }
                } else {
                    message = ubiService.getErrorMessage(authenticationJson);
                }
            } catch (GeneralSecurityException | IOException e) {
                message = e.getMessage();
            }
            sendMessage(message);
            Log.d("Result :" , message);

            return isOk;
        }

        private JsonObject getViewerRoot(JsonObject root) {
            return root.getAsJsonObject("data").getAsJsonObject("viewer").getAsJsonObject("game")
                    .getAsJsonObject("node").getAsJsonObject("viewer").getAsJsonObject("meta");
        }

        private String findLastPlatformPlayed(List<Game> games) {
            String platform = null;
            SimpleDateFormat formatter = new SimpleDateFormat(UBI_DATE_FORMAT, Locale.getDefault());
            try {
                for(Game game : games) {
                    if(game.isOwned() && null != game.getLastPlayedDate()) {
                        Date lastPlayedDate = formatter.parse(game.getLastPlayedDate().split(UBI_DATE_DELIMITER)[0]);
                        if(platform == null || (lastPlayedDate != null && lastPlayedDate.after(formatter.parse(getGameByPlatform(games, platform).getLastPlayedDate().split(UBI_DATE_DELIMITER)[0])))) {
                            platform = game.getPlatform();
                        }
                    }
                }
            } catch (NullPointerException | ParseException e) {
                Log.e("ParseException", e.getMessage());
            }
            return platform;
        }

        private Game getGameByPlatform(List<Game> games, String platform) {
            Game foundGame = null;
            for(Game game : games) {
                if(platform.equals(game.getPlatform())) {
                    foundGame = game;
                    break;
                }
            }
            return foundGame;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        private void sendMessage(String message){
            Message msg = Message.obtain();
            msg.obj = message;
            msg.setTarget(mHandler);
            msg.sendToTarget();
        }
    }
}

