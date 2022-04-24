package com.ablondel.r6challenges.ui.login;

import static com.ablondel.r6challenges.service.UbiService.CHARSET_UTF8;
import static com.ablondel.r6challenges.service.UbiService.PC;
import static com.ablondel.r6challenges.service.UbiService.PS4;
import static com.ablondel.r6challenges.service.UbiService.UBI_DATE_DELIMITER;
import static com.ablondel.r6challenges.service.UbiService.UBI_DATE_FORMAT;
import static com.ablondel.r6challenges.service.UbiService.XONE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ablondel.r6challenges.R;
import com.ablondel.r6challenges.model.UserInfos;
import com.ablondel.r6challenges.model.auth.Authentication;
import com.ablondel.r6challenges.model.profile.ProfileList;
import com.ablondel.r6challenges.service.SharedPreferencesService;
import com.ablondel.r6challenges.service.UbiService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private UbiService ubiService;

    // UI references.
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
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        ubiService = new UbiService();

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

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


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
                keyBytes = key.getBytes(CHARSET_UTF8);
                String encodedKey = Base64.encodeToString(keyBytes, Base64.NO_WRAP);
                String authenticationJson = ubiService.authenticate(encodedKey);
                Log.d("Debug---authenticationJson", authenticationJson);
                if(ubiService.isValidResponse(authenticationJson)) {
                    userInfos.setAuthentication(new Gson().fromJson(authenticationJson, Authentication.class));
                    String profilesJson = ubiService.getProfiles(userInfos);
                    Log.d("Debug---profilesJson", profilesJson);
                    if(ubiService.isValidResponse(profilesJson)) {
                        userInfos.setProfileList(new Gson().fromJson(profilesJson, ProfileList.class));

                        // Check which platforms are owned by the user
                        // Platform is shared between consoles (PS4+PS5, XONE+XBSX)
                        String ps4PlatformJson = ubiService.getGame(userInfos, PS4);
                        String xonePlatformJson = ubiService.getGame(userInfos, XONE);
                        String pcPlatformJson = ubiService.getGame(userInfos, PC);

                        boolean ps4PlatformValid = ubiService.isValidResponse(ps4PlatformJson);
                        boolean xonePlatformValid = ubiService.isValidResponse(xonePlatformJson);
                        boolean pcPlatformValid = ubiService.isValidResponse(pcPlatformJson);

                        if(ps4PlatformValid && xonePlatformValid && pcPlatformValid) {

                            // Potential refactor : Create a Model "Game" with attributes "platform", "owned", "lastPlayedDate"
                            JsonObject root = JsonParser.parseString(ps4PlatformJson).getAsJsonObject();
                            boolean isPs4Owned = getViewerRoot(root).get("isOwned").getAsBoolean();
                            JsonElement lastPlayedDate = getViewerRoot(root).get("lastPlayedDate");
                            String ps4LastPlay = lastPlayedDate.isJsonNull() ? null : lastPlayedDate.getAsString();

                            root = JsonParser.parseString(xonePlatformJson).getAsJsonObject();
                            boolean isXoneOwned = getViewerRoot(root).get("isOwned").getAsBoolean();
                            lastPlayedDate = getViewerRoot(root).get("lastPlayedDate");
                            String xoneLastPlay = lastPlayedDate.isJsonNull() ? null : lastPlayedDate.getAsString();

                            root = JsonParser.parseString(pcPlatformJson).getAsJsonObject();
                            boolean isPcOwned = getViewerRoot(root).get("isOwned").getAsBoolean();
                            lastPlayedDate = getViewerRoot(root).get("lastPlayedDate");
                            String pcLastPlay = lastPlayedDate.isJsonNull() ? null : lastPlayedDate.getAsString();

                            List<String> onwedPlatforms = new ArrayList<>();
                            if(isPs4Owned) {
                                onwedPlatforms.add(PS4);
                            }
                            if(isXoneOwned) {
                                onwedPlatforms.add(XONE);
                            }
                            if(isPcOwned) {
                                onwedPlatforms.add(PC);
                            }
                            userInfos.setOwnedPlatforms(onwedPlatforms);

                            // Default platform is the last one that has been used
                            userInfos.setLastSelectedPlatform(findLastPlatformPlayed(isPs4Owned, isXoneOwned, isPcOwned, ps4LastPlay, xoneLastPlay, pcLastPlay));

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
            } catch (UnsupportedEncodingException e) {
                message = e.getMessage();
            } catch (GeneralSecurityException | IOException e) {
                message = e.getMessage();
            }
            sendMessage(message);
            Log.d("Result :" , message);

            if (isOk) {
                return true;
            } else {
                return false;
            }
        }

        private JsonObject getViewerRoot(JsonObject root) {
            return root.getAsJsonObject("data").getAsJsonObject("viewer").getAsJsonObject("game")
                    .getAsJsonObject("node").getAsJsonObject("viewer").getAsJsonObject("meta");
        }

        private String findLastPlatformPlayed(boolean isPs4Owned, boolean isXoneOwned, boolean isPcOwned, String ps4LastPlay, String xoneLastPlay, String pcLastPlay) {
            String platform = null;
            Date ps4LastPlayDate = null;
            Date xoneLastPlayDate = null;
            Date pcLastPlayDate = null;
            SimpleDateFormat formatter = new SimpleDateFormat(UBI_DATE_FORMAT, Locale.getDefault());

            try {
                if (isPs4Owned && null != ps4LastPlay) {
                    ps4LastPlayDate = formatter.parse(ps4LastPlay.split(UBI_DATE_DELIMITER)[0]);
                    platform = PS4;
                }
                if (isXoneOwned && null != xoneLastPlay) {
                    xoneLastPlayDate = formatter.parse(xoneLastPlay.split(UBI_DATE_DELIMITER)[0]);
                    if(null == platform || xoneLastPlayDate.after(ps4LastPlayDate)) {
                        platform = XONE;
                    }
                }
                if (isPcOwned && null != pcLastPlay) {
                    pcLastPlayDate = formatter.parse(pcLastPlay.split(UBI_DATE_DELIMITER)[0]);
                    if(null == platform || (platform == PS4 && pcLastPlayDate.after(ps4LastPlayDate)) ||
                            (platform == XONE && pcLastPlayDate.after(xoneLastPlayDate))) {
                        platform = PC;
                    }
                }
            }catch (ParseException e) {
                Log.e("ParseException", e.getMessage());
            }
            return platform;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.requestFocus();
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

