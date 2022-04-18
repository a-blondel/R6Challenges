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
import com.ablondel.r6challenges.service.ParseResponseService;
import com.ablondel.r6challenges.service.SharedPreferencesService;
import com.ablondel.r6challenges.ui.login.LoginActivity;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

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
        });

        ImageButton refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener((v) -> {
            //SharedPreferencesService.getEncryptedSharedPreferences().edit().clear().apply();
            // Check expiration of userInfos
            // If expired, refresh the token and update userInfos
            // When not expired, get call RefreshChallengesTask
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
            //try {
                //String challengesJson = ubiService.getChallenges(encodedKey);
                String challengesJson = "{\"data\":{\"game\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66\",\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66\",\"periodicChallenges\":{\"totalXpCount\":350,\"xpEarnedCount\":0,\"totalCount\":9,\"challenges\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-04203bfe-f230-488d-aafe-675b3b0ce41f\",\"challengeId\":\"04203bfe-f230-488d-aafe-675b3b0ce41f\",\"description\":\"Eliminate opponents with shotguns to collect Battle Points and help the community complete this challenge.\",\"imageUrl\":\"https://ubiservices.cdn.ubi.com/8a755c07-9730-4204-a7fb-60a76c280950/challenge/Icon_Challenge_BattlePass.png\",\"name\":\"Battle Pass Community Challenge – Y7S1\",\"previewUrl\":\"https://ubiservices.cdn.ubi.com/8a755c07-9730-4204-a7fb-60a76c280950/challenge/Y6S4_BP_Challenge_ShotgunKillsNew_720X284.png\",\"startDate\":\"2022-04-14T18:40:13Z\",\"endDate\":\"2022-04-26T17:00:00Z\",\"isExpired\":false,\"type\":\"COMMUNITY\",\"xpPrize\":150,\"value\":2000000,\"formattedValue\":\"2 000 000\",\"currencyPrizes\":{\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-04203bfe-f230-488d-aafe-675b3b0ce41f-0a6b5326-49c6-0d18-e552-1e9353997304\",\"imageUrl\":\"https://ubiservices.cdn.ubi.com/8a755c07-9730-4204-a7fb-60a76c280950/challenge/Icon_Reward_BattlePoints.png\",\"name\":\"Name for [fr-FR]\",\"__typename\":\"PeriodicChallengePrize\"}],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"thresholds\":{\"totalCount\":1,\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-04203bfe-f230-488d-aafe-675b3b0ce41f-0\",\"value\":2000000,\"cumulatedValue\":2000000,\"formattedCumulatedValue\":\"2 000 000\",\"xpPrize\":150,\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-04203bfe-f230-488d-aafe-675b3b0ce41f-0\",\"isCollected\":false,\"__typename\":\"UserPeriodicChallengeThresholdMeta\"},\"__typename\":\"PeriodicChallengeThresholdUserEdge\"},\"currencyPrizes\":{\"edges\":[{\"meta\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-04203bfe-f230-488d-aafe-675b3b0ce41f-0-0a6b5326-49c6-0d18-e552-1e9353997304\",\"value\":850,\"__typename\":\"PeriodicChallengeThresholdPrizeMeta\"},\"node\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-04203bfe-f230-488d-aafe-675b3b0ce41f-0a6b5326-49c6-0d18-e552-1e9353997304\",\"imageUrl\":\"https://ubiservices.cdn.ubi.com/8a755c07-9730-4204-a7fb-60a76c280950/challenge/Icon_Reward_BattlePoints.png\",\"name\":\"Name for [fr-FR]\",\"__typename\":\"PeriodicChallengePrize\"},\"__typename\":\"PeriodicChallengeThresholdPrizeEdge\"}],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"__typename\":\"PeriodicChallengeThreshold\"}],\"__typename\":\"PeriodicChallengeThresholdsConnection\"},\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-04203bfe-f230-488d-aafe-675b3b0ce41f\",\"isActivated\":true,\"isCollectible\":false,\"isCompleted\":false,\"isInProgress\":true,\"isRedeemed\":false,\"contribution\":0,\"formattedContribution\":\"0\",\"progressPercentage\":21.4549,\"progress\":429098,\"formattedProgress\":\"429 098\",\"__typename\":\"UserPeriodicChallengeMeta\"},\"__typename\":\"PeriodicChallengeUserEdge\"},\"__typename\":\"PeriodicChallenge\"},{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-6a059305-a566-424b-8f51-dbba6d56ad76\",\"challengeId\":\"6a059305-a566-424b-8f51-dbba6d56ad76\",\"description\":\"Gagnez {threshold} manches avec Glaz, Fuze, Kapkan ou Tachanka en Multijoueur.\",\"imageUrl\":\"https://static8.ubi.com/u/Uplay/Games/RSIX7/challenges/RB6_PVP_Spetsnaz.png\",\"name\":\"SPETSNAZ au combat\",\"previewUrl\":null,\"startDate\":\"2022-04-14T17:00:00Z\",\"endDate\":\"2022-04-21T17:00:00Z\",\"isExpired\":false,\"type\":\"REGULAR\",\"xpPrize\":25,\"value\":8,\"formattedValue\":\"8\",\"currencyPrizes\":{\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-6a059305-a566-424b-8f51-dbba6d56ad76-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"}],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"thresholds\":{\"totalCount\":1,\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-6a059305-a566-424b-8f51-dbba6d56ad76-0\",\"value\":8,\"cumulatedValue\":8,\"formattedCumulatedValue\":\"8\",\"xpPrize\":25,\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-6a059305-a566-424b-8f51-dbba6d56ad76-0\",\"isCollected\":false,\"__typename\":\"UserPeriodicChallengeThresholdMeta\"},\"__typename\":\"PeriodicChallengeThresholdUserEdge\"},\"currencyPrizes\":{\"edges\":[{\"meta\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-6a059305-a566-424b-8f51-dbba6d56ad76-0-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"value\":250,\"__typename\":\"PeriodicChallengeThresholdPrizeMeta\"},\"node\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-6a059305-a566-424b-8f51-dbba6d56ad76-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"},\"__typename\":\"PeriodicChallengeThresholdPrizeEdge\"}],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"__typename\":\"PeriodicChallengeThreshold\"}],\"__typename\":\"PeriodicChallengeThresholdsConnection\"},\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-6a059305-a566-424b-8f51-dbba6d56ad76\",\"isActivated\":true,\"isCollectible\":false,\"isCompleted\":false,\"isInProgress\":false,\"isRedeemed\":false,\"contribution\":0,\"formattedContribution\":\"0\",\"progressPercentage\":0,\"progress\":0,\"formattedProgress\":\"0\",\"__typename\":\"UserPeriodicChallengeMeta\"},\"__typename\":\"PeriodicChallengeUserEdge\"},\"__typename\":\"PeriodicChallenge\"},{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-59994746-e264-4d38-9cbf-1821373dccdf\",\"challengeId\":\"59994746-e264-4d38-9cbf-1821373dccdf\",\"description\":\"Gagnez {threshold} manches avec Maestro ou Alibi en Multijoueur.\",\"imageUrl\":\"https://static8.ubi.com/u/Uplay/Games/RSIX7/challenges/RB6_PVP_GIS.png\",\"name\":\"G.I.S. au combat\",\"previewUrl\":null,\"startDate\":\"2022-04-14T17:00:00Z\",\"endDate\":\"2022-04-21T17:00:00Z\",\"isExpired\":false,\"type\":\"REGULAR\",\"xpPrize\":25,\"value\":4,\"formattedValue\":\"4\",\"currencyPrizes\":{\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-59994746-e264-4d38-9cbf-1821373dccdf-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"}],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"thresholds\":{\"totalCount\":1,\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-59994746-e264-4d38-9cbf-1821373dccdf-0\",\"value\":4,\"cumulatedValue\":4,\"formattedCumulatedValue\":\"4\",\"xpPrize\":25,\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-59994746-e264-4d38-9cbf-1821373dccdf-0\",\"isCollected\":false,\"__typename\":\"UserPeriodicChallengeThresholdMeta\"},\"__typename\":\"PeriodicChallengeThresholdUserEdge\"},\"currencyPrizes\":{\"edges\":[{\"meta\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-59994746-e264-4d38-9cbf-1821373dccdf-0-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"value\":250,\"__typename\":\"PeriodicChallengeThresholdPrizeMeta\"},\"node\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-59994746-e264-4d38-9cbf-1821373dccdf-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"},\"__typename\":\"PeriodicChallengeThresholdPrizeEdge\"}],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"__typename\":\"PeriodicChallengeThreshold\"}],\"__typename\":\"PeriodicChallengeThresholdsConnection\"},\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-59994746-e264-4d38-9cbf-1821373dccdf\",\"isActivated\":true,\"isCollectible\":false,\"isCompleted\":false,\"isInProgress\":false,\"isRedeemed\":false,\"contribution\":0,\"formattedContribution\":\"0\",\"progressPercentage\":0,\"progress\":0,\"formattedProgress\":\"0\",\"__typename\":\"UserPeriodicChallengeMeta\"},\"__typename\":\"PeriodicChallengeUserEdge\"},\"__typename\":\"PeriodicChallenge\"},{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-21e7cc7a-421c-4c7b-8d62-586d7cf96dc7\",\"challengeId\":\"21e7cc7a-421c-4c7b-8d62-586d7cf96dc7\",\"description\":\"Gagnez {threshold} manches en tant qu'assaillant en Multijoueur\",\"imageUrl\":\"https://static8.ubi.com/u/Uplay/Games/RSIX7/challenges/RB6_PVP_Attacker.png\",\"name\":\"Assaillant\",\"previewUrl\":null,\"startDate\":\"2022-04-14T17:00:00Z\",\"endDate\":\"2022-04-21T17:00:00Z\",\"isExpired\":false,\"type\":\"REGULAR\",\"xpPrize\":25,\"value\":3,\"formattedValue\":\"3\",\"currencyPrizes\":{\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-21e7cc7a-421c-4c7b-8d62-586d7cf96dc7-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"}],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"thresholds\":{\"totalCount\":1,\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-21e7cc7a-421c-4c7b-8d62-586d7cf96dc7-0\",\"value\":3,\"cumulatedValue\":3,\"formattedCumulatedValue\":\"3\",\"xpPrize\":25,\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-21e7cc7a-421c-4c7b-8d62-586d7cf96dc7-0\",\"isCollected\":false,\"__typename\":\"UserPeriodicChallengeThresholdMeta\"},\"__typename\":\"PeriodicChallengeThresholdUserEdge\"},\"currencyPrizes\":{\"edges\":[{\"meta\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-21e7cc7a-421c-4c7b-8d62-586d7cf96dc7-0-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"value\":250,\"__typename\":\"PeriodicChallengeThresholdPrizeMeta\"},\"node\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-21e7cc7a-421c-4c7b-8d62-586d7cf96dc7-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"},\"__typename\":\"PeriodicChallengeThresholdPrizeEdge\"}],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"__typename\":\"PeriodicChallengeThreshold\"}],\"__typename\":\"PeriodicChallengeThresholdsConnection\"},\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-21e7cc7a-421c-4c7b-8d62-586d7cf96dc7\",\"isActivated\":true,\"isCollectible\":false,\"isCompleted\":false,\"isInProgress\":false,\"isRedeemed\":false,\"contribution\":0,\"formattedContribution\":\"0\",\"progressPercentage\":0,\"progress\":0,\"formattedProgress\":\"0\",\"__typename\":\"UserPeriodicChallengeMeta\"},\"__typename\":\"PeriodicChallengeUserEdge\"},\"__typename\":\"PeriodicChallenge\"},{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7a3e8d52-e821-4c0e-91e2-7682db0f4fbc\",\"challengeId\":\"7a3e8d52-e821-4c0e-91e2-7682db0f4fbc\",\"description\":\"Faites {threshold} éliminations avec un fusil en Multijoueur\",\"imageUrl\":\"https://static8.ubi.com/u/Uplay/Games/RSIX7/challenges/RB6_PVP_Rifle.png\",\"name\":\"Maîtrise du fusil (compétition)\",\"previewUrl\":null,\"startDate\":\"2022-04-14T17:00:00Z\",\"endDate\":\"2022-04-21T17:00:00Z\",\"isExpired\":false,\"type\":\"REGULAR\",\"xpPrize\":25,\"value\":7,\"formattedValue\":\"7\",\"currencyPrizes\":{\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7a3e8d52-e821-4c0e-91e2-7682db0f4fbc-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"}],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"thresholds\":{\"totalCount\":1,\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7a3e8d52-e821-4c0e-91e2-7682db0f4fbc-0\",\"value\":7,\"cumulatedValue\":7,\"formattedCumulatedValue\":\"7\",\"xpPrize\":25,\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7a3e8d52-e821-4c0e-91e2-7682db0f4fbc-0\",\"isCollected\":false,\"__typename\":\"UserPeriodicChallengeThresholdMeta\"},\"__typename\":\"PeriodicChallengeThresholdUserEdge\"},\"currencyPrizes\":{\"edges\":[{\"meta\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7a3e8d52-e821-4c0e-91e2-7682db0f4fbc-0-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"value\":250,\"__typename\":\"PeriodicChallengeThresholdPrizeMeta\"},\"node\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7a3e8d52-e821-4c0e-91e2-7682db0f4fbc-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"},\"__typename\":\"PeriodicChallengeThresholdPrizeEdge\"}],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"__typename\":\"PeriodicChallengeThreshold\"}],\"__typename\":\"PeriodicChallengeThresholdsConnection\"},\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7a3e8d52-e821-4c0e-91e2-7682db0f4fbc\",\"isActivated\":true,\"isCollectible\":false,\"isCompleted\":false,\"isInProgress\":false,\"isRedeemed\":false,\"contribution\":0,\"formattedContribution\":\"0\",\"progressPercentage\":0,\"progress\":0,\"formattedProgress\":\"0\",\"__typename\":\"UserPeriodicChallengeMeta\"},\"__typename\":\"PeriodicChallengeUserEdge\"},\"__typename\":\"PeriodicChallenge\"},{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7f992cb3-515e-45a4-a891-ba3b90529e89\",\"challengeId\":\"7f992cb3-515e-45a4-a891-ba3b90529e89\",\"description\":\"Gagnez {threshold} manches avec Jackal ou Mira en Terrain d'entraînement.\",\"imageUrl\":\"https://static8.ubi.com/u/Uplay/Games/RSIX7/challenges/R6_PVE_GEO.png\",\"name\":\"G.EO. en action\",\"previewUrl\":null,\"startDate\":\"2022-04-14T17:00:00Z\",\"endDate\":\"2022-04-21T17:00:00Z\",\"isExpired\":false,\"type\":\"REGULAR\",\"xpPrize\":25,\"value\":3,\"formattedValue\":\"3\",\"currencyPrizes\":{\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7f992cb3-515e-45a4-a891-ba3b90529e89-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"}],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"thresholds\":{\"totalCount\":1,\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7f992cb3-515e-45a4-a891-ba3b90529e89-0\",\"value\":3,\"cumulatedValue\":3,\"formattedCumulatedValue\":\"3\",\"xpPrize\":25,\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7f992cb3-515e-45a4-a891-ba3b90529e89-0\",\"isCollected\":false,\"__typename\":\"UserPeriodicChallengeThresholdMeta\"},\"__typename\":\"PeriodicChallengeThresholdUserEdge\"},\"currencyPrizes\":{\"edges\":[{\"meta\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7f992cb3-515e-45a4-a891-ba3b90529e89-0-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"value\":250,\"__typename\":\"PeriodicChallengeThresholdPrizeMeta\"},\"node\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7f992cb3-515e-45a4-a891-ba3b90529e89-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"},\"__typename\":\"PeriodicChallengeThresholdPrizeEdge\"}],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"__typename\":\"PeriodicChallengeThreshold\"}],\"__typename\":\"PeriodicChallengeThresholdsConnection\"},\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7f992cb3-515e-45a4-a891-ba3b90529e89\",\"isActivated\":true,\"isCollectible\":false,\"isCompleted\":false,\"isInProgress\":false,\"isRedeemed\":false,\"contribution\":0,\"formattedContribution\":\"0\",\"progressPercentage\":0,\"progress\":0,\"formattedProgress\":\"0\",\"__typename\":\"UserPeriodicChallengeMeta\"},\"__typename\":\"PeriodicChallengeUserEdge\"},\"__typename\":\"PeriodicChallenge\"},{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-9eb8977d-bf1c-480c-816f-6e9a12db02a5\",\"challengeId\":\"9eb8977d-bf1c-480c-816f-6e9a12db02a5\",\"description\":\"Faites 10 tirs à la tête en Multijoueur\",\"imageUrl\":\"https://ubiservices.cdn.ubi.com/8a755c07-9730-4204-a7fb-60a76c280950/challenge/Aimlab_challenge_icon.png\",\"name\":\"Défi Concentration Aim Lab\",\"previewUrl\":\"https://ubiservices.cdn.ubi.com/8a755c07-9730-4204-a7fb-60a76c280950/challenge/Y7S1_Aimlab_Challenge_720x284.png\",\"startDate\":null,\"endDate\":null,\"isExpired\":true,\"type\":\"REGULAR\",\"xpPrize\":25,\"value\":10,\"formattedValue\":\"10\",\"currencyPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"itemPrizes\":{\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-9eb8977d-bf1c-480c-816f-6e9a12db02a5-fe3fe209-ef70-20c7-437c-925fb9a04f63\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Skin.png\",\"name\":\"RAYON DE PRÉCISION\",\"__typename\":\"PeriodicChallengePrize\"},{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-9eb8977d-bf1c-480c-816f-6e9a12db02a5-e8991538-ec92-0e2c-f842-8d22d8459c98\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Charm.png\",\"name\":\"CONCENTRATION LASER\",\"__typename\":\"PeriodicChallengePrize\"}],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"thresholds\":{\"totalCount\":1,\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-9eb8977d-bf1c-480c-816f-6e9a12db02a5-0\",\"value\":10,\"cumulatedValue\":10,\"formattedCumulatedValue\":\"10\",\"xpPrize\":25,\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-9eb8977d-bf1c-480c-816f-6e9a12db02a5-0\",\"isCollected\":false,\"__typename\":\"UserPeriodicChallengeThresholdMeta\"},\"__typename\":\"PeriodicChallengeThresholdUserEdge\"},\"currencyPrizes\":{\"edges\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"itemPrizes\":{\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-9eb8977d-bf1c-480c-816f-6e9a12db02a5-fe3fe209-ef70-20c7-437c-925fb9a04f63\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Skin.png\",\"name\":\"RAYON DE PRÉCISION\",\"__typename\":\"PeriodicChallengePrize\"},{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-9eb8977d-bf1c-480c-816f-6e9a12db02a5-e8991538-ec92-0e2c-f842-8d22d8459c98\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Charm.png\",\"name\":\"CONCENTRATION LASER\",\"__typename\":\"PeriodicChallengePrize\"}],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"__typename\":\"PeriodicChallengeThreshold\"}],\"__typename\":\"PeriodicChallengeThresholdsConnection\"},\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-9eb8977d-bf1c-480c-816f-6e9a12db02a5\",\"isActivated\":true,\"isCollectible\":true,\"isCompleted\":true,\"isInProgress\":false,\"isRedeemed\":false,\"contribution\":10,\"formattedContribution\":\"10\",\"progressPercentage\":100,\"progress\":10,\"formattedProgress\":\"10\",\"__typename\":\"UserPeriodicChallengeMeta\"},\"__typename\":\"PeriodicChallengeUserEdge\"},\"__typename\":\"PeriodicChallenge\"},{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-1474f4a2-d76c-4907-8d9c-0ac8ca2c93b2\",\"challengeId\":\"1474f4a2-d76c-4907-8d9c-0ac8ca2c93b2\",\"description\":\"Faites {threshold} éliminations avec un pistolet-mitrailleur en Multijoueur\",\"imageUrl\":\"https://static8.ubi.com/u/Uplay/Games/RSIX7/challenges/RB6_PVP_SMG.png\",\"name\":\"Maîtrise du pistolet-mitrailleur (compétition)\",\"previewUrl\":null,\"startDate\":null,\"endDate\":null,\"isExpired\":true,\"type\":\"REGULAR\",\"xpPrize\":25,\"value\":7,\"formattedValue\":\"7\",\"currencyPrizes\":{\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-1474f4a2-d76c-4907-8d9c-0ac8ca2c93b2-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"}],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"thresholds\":{\"totalCount\":1,\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-1474f4a2-d76c-4907-8d9c-0ac8ca2c93b2-0\",\"value\":7,\"cumulatedValue\":7,\"formattedCumulatedValue\":\"7\",\"xpPrize\":25,\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-1474f4a2-d76c-4907-8d9c-0ac8ca2c93b2-0\",\"isCollected\":false,\"__typename\":\"UserPeriodicChallengeThresholdMeta\"},\"__typename\":\"PeriodicChallengeThresholdUserEdge\"},\"currencyPrizes\":{\"edges\":[{\"meta\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-1474f4a2-d76c-4907-8d9c-0ac8ca2c93b2-0-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"value\":250,\"__typename\":\"PeriodicChallengeThresholdPrizeMeta\"},\"node\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-1474f4a2-d76c-4907-8d9c-0ac8ca2c93b2-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"},\"__typename\":\"PeriodicChallengeThresholdPrizeEdge\"}],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"__typename\":\"PeriodicChallengeThreshold\"}],\"__typename\":\"PeriodicChallengeThresholdsConnection\"},\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-1474f4a2-d76c-4907-8d9c-0ac8ca2c93b2\",\"isActivated\":true,\"isCollectible\":true,\"isCompleted\":true,\"isInProgress\":false,\"isRedeemed\":false,\"contribution\":7,\"formattedContribution\":\"7\",\"progressPercentage\":100,\"progress\":7,\"formattedProgress\":\"7\",\"__typename\":\"UserPeriodicChallengeMeta\"},\"__typename\":\"PeriodicChallengeUserEdge\"},\"__typename\":\"PeriodicChallenge\"},{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-ca5ae153-2373-405d-9875-2bfe6bf3d4d7\",\"challengeId\":\"ca5ae153-2373-405d-9875-2bfe6bf3d4d7\",\"description\":\"Atteignez le score minimal de {threshold} à la fin d'une manche en Multijoueur.\",\"imageUrl\":\"https://static8.ubi.com/u/Uplay/Games/RSIX7/challenges/RB6_PVP_MVP.png\",\"name\":\"Meilleur joueur\",\"previewUrl\":null,\"startDate\":null,\"endDate\":null,\"isExpired\":true,\"type\":\"REGULAR\",\"xpPrize\":25,\"value\":3000,\"formattedValue\":\"3 000\",\"currencyPrizes\":{\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-ca5ae153-2373-405d-9875-2bfe6bf3d4d7-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"}],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengePrizesConnection\"},\"thresholds\":{\"totalCount\":1,\"nodes\":[{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-ca5ae153-2373-405d-9875-2bfe6bf3d4d7-0\",\"value\":3000,\"cumulatedValue\":3000,\"formattedCumulatedValue\":\"3 000\",\"xpPrize\":25,\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-ca5ae153-2373-405d-9875-2bfe6bf3d4d7-0\",\"isCollected\":false,\"__typename\":\"UserPeriodicChallengeThresholdMeta\"},\"__typename\":\"PeriodicChallengeThresholdUserEdge\"},\"currencyPrizes\":{\"edges\":[{\"meta\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-ca5ae153-2373-405d-9875-2bfe6bf3d4d7-0-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"value\":250,\"__typename\":\"PeriodicChallengeThresholdPrizeMeta\"},\"node\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-ca5ae153-2373-405d-9875-2bfe6bf3d4d7-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"__typename\":\"PeriodicChallengePrize\"},\"__typename\":\"PeriodicChallengeThresholdPrizeEdge\"}],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"itemPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"rewardPrizes\":{\"nodes\":[],\"__typename\":\"PeriodicChallengeThresholdPrizesConnection\"},\"__typename\":\"PeriodicChallengeThreshold\"}],\"__typename\":\"PeriodicChallengeThresholdsConnection\"},\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-ca5ae153-2373-405d-9875-2bfe6bf3d4d7\",\"isActivated\":true,\"isCollectible\":true,\"isCompleted\":true,\"isInProgress\":false,\"isRedeemed\":false,\"contribution\":3000,\"formattedContribution\":\"3 000\",\"progressPercentage\":100,\"progress\":3000,\"formattedProgress\":\"3 000\",\"__typename\":\"UserPeriodicChallengeMeta\"},\"__typename\":\"PeriodicChallengeUserEdge\"},\"__typename\":\"PeriodicChallenge\"}],\"__typename\":\"UserGamePeriodicChallengesConnection\"},\"activatedChallengesXp\":{\"totalXpCount\":275,\"xpEarnedCount\":0,\"__typename\":\"UserGamePeriodicChallengesConnection\"},\"currencyPrizes\":{\"totalCount\":2,\"collectedValuesCount\":0,\"totalValuesCount\":2600,\"edges\":[{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-04203bfe-f230-488d-aafe-675b3b0ce41f-0a6b5326-49c6-0d18-e552-1e9353997304\",\"count\":850,\"collectedCount\":0,\"__typename\":\"UserGamePeriodicChallengePrizeMeta\"},\"node\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-04203bfe-f230-488d-aafe-675b3b0ce41f-0a6b5326-49c6-0d18-e552-1e9353997304\",\"imageUrl\":\"https://ubiservices.cdn.ubi.com/8a755c07-9730-4204-a7fb-60a76c280950/challenge/Icon_Reward_BattlePoints.png\",\"name\":\"Name for [fr-FR]\",\"type\":\"CURRENCY\",\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-04203bfe-f230-488d-aafe-675b3b0ce41f-0a6b5326-49c6-0d18-e552-1e9353997304\",\"collectedCount\":0,\"__typename\":\"UserPeriodicChallengePrizeMeta\"},\"__typename\":\"PeriodicChallengePrizeUserEdge\"},\"__typename\":\"PeriodicChallengePrize\"},\"__typename\":\"UserGamePeriodicChallengePrizeEdge\"},{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7f992cb3-515e-45a4-a891-ba3b90529e89-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"count\":1250,\"collectedCount\":0,\"__typename\":\"UserGamePeriodicChallengePrizeMeta\"},\"node\":{\"id\":\"05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7f992cb3-515e-45a4-a891-ba3b90529e89-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"imageUrl\":\"https://static8.cdn.ubi.com/u/Uplay/Games/RSIX7/challenges/Icon_R6_Renown.png\",\"name\":\"Renommée\",\"type\":\"CURRENCY\",\"viewer\":{\"meta\":{\"id\":\"f854e049-f7ff-44f4-87d9-662a6977640d-05bfb3f7-6c21-4c42-be1f-97a33fb5cf66-7f992cb3-515e-45a4-a891-ba3b90529e89-61467f3b-c1a5-45de-b738-ef59ab9339ff\",\"collectedCount\":0,\"__typename\":\"UserPeriodicChallengePrizeMeta\"},\"__typename\":\"PeriodicChallengePrizeUserEdge\"},\"__typename\":\"PeriodicChallengePrize\"},\"__typename\":\"UserGamePeriodicChallengePrizeEdge\"}],\"__typename\":\"UserGamePeriodicChallengePrizesConnection\"},\"itemPrizes\":{\"totalCount\":0,\"collectedValuesCount\":0,\"totalValuesCount\":0,\"edges\":[],\"__typename\":\"UserGamePeriodicChallengePrizesConnection\"},\"rewardPrizes\":{\"totalCount\":0,\"collectedValuesCount\":0,\"totalValuesCount\":0,\"edges\":[],\"__typename\":\"UserGamePeriodicChallengePrizesConnection\"},\"__typename\":\"UserGameMeta\"},\"__typename\":\"GameUserEdge\"},\"__typename\":\"Game\"}}}\n";

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (StringUtils.isNotBlank(challengesJson)) {
                    // Parse Challenges
                    isOk = true;
                } else {
                    //message = serviceHelper.getErrorMessage(authentication);
                    message = "Could not contact Ubisoft services!";
                }
            //} catch (UnsupportedEncodingException e) {
                //message = e.getMessage();
                //} catch (JSONException e) {
                //message = e.getMessage();
                //} catch (ParseException e) {
                //message = e.getMessage();
            //} catch (GeneralSecurityException | IOException e) {
                //message = e.getMessage();
            //}
            sendMessage(message);
            Log.d("Result :" , message);

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

        private void sendMessage(String message){
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