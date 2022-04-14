package com.ablondel.r6challenges.data;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.ablondel.r6challenges.data.model.LoggedInUser;
import com.ablondel.r6challenges.service.UbiService;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import static com.ablondel.r6challenges.service.UbiService.CHARSET_UTF8;
import static com.ablondel.r6challenges.service.UbiService.EXCEPTION_PATTERN;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private UserLoginTask mAuthTask = null;
    private UbiService ubiService = new UbiService();

    public Result<LoggedInUser> login(String username, String password) {

        try {

            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);

            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // attempt authentication against a network service.
            String key = mEmail + ":" + mPassword;

            boolean isOk = false;
            String message = "OK !";

            byte[] keyBytes;
            try {
                keyBytes = key.getBytes(CHARSET_UTF8);

                String encodedKey = Base64.encodeToString(keyBytes, Base64.NO_WRAP);

                String response = ubiService.authenticate(encodedKey);

                if (response != null && !response.equals("") && !response.contains(EXCEPTION_PATTERN)) {
                    //connectionViewModel.insert(serviceHelper.generateConnectionEntity(response, encodedKey));
                    isOk = true;
                } else {
                    //message = serviceHelper.getErrorMessage(response);
                    message = "Echec";
                }
            } catch (UnsupportedEncodingException e) {
                message = e.getMessage();
                //} catch (JSONException e) {
                //message = e.getMessage();
                //} catch (ParseException e) {
                //message = e.getMessage();
            }
            //sendMessage(message);
            Log.d("Resultat :" , message);

            if (isOk) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}