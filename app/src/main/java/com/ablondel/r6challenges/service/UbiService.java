package com.ablondel.r6challenges.service;

import android.util.Log;

import com.ablondel.r6challenges.model.UserInfos;
import com.ablondel.r6challenges.model.auth.Authentication;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UbiService {

    // Urls
    private static final String AUTH_URL = "https://public-ubiservices.ubi.com/v3/profiles/sessions";
    private static final String PROFILES_URL = "https://public-ubiservices.ubi.com/v2/profiles?userId=";
    private static final String GRAPHQL_URL = "https://public-ubiservices.ubi.com/v1/profiles/me/uplay/graphql";

    // Methods
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";

    // Headers keys
    private static final String HEADER_UBI_APPID = "Ubi-AppId";
    private static final String HEADER_UBI_SESSIONID = "Ubi-SessionId";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    // Headers values
    public static final String APP_ID = "39baebad-39e5-4552-8c25-2c9b919064e2";
    private static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CHARSET_UTF8 = "UTF-8";
    private static final String AUTHORIZATION_BASIC = "Basic ";
    private static final String UBI_TOKEN_PREFIX = "Ubi_v1 t=";
    private static final String UBI_REFRESH_PREFIX = "rm_v1 t=";

    // Others
    public static final String UBI_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String UBI_DATE_DELIMITER = "\\.";

    // Error handling
    private static final String UBI_ERROR_CODE = "errorCode";
    private static final String UBI_ERRORS = "errors";
    private static final String UBI_ERROR_BEGIN = "\"message\":\"";
    private static final String UBI_ERROR_END = "\",";
    private static final String UBI_EMPTY_RESPONSE = "Empty response";
    public static final String EXCEPTION_PATTERN = "EXCEPTION: ";


    public String authenticate(String encodedKey) {
        return callWebService(AUTH_URL, AUTHORIZATION_BASIC + encodedKey, POST_METHOD, "{rememberMe: true}", null);
    }

    public String updateRefreshToken(UserInfos userInfos) {
        return callWebService(AUTH_URL, UBI_REFRESH_PREFIX + userInfos.getAuthentication().getRememberMeTicket(),
                POST_METHOD, "{rememberMe: true}", null);
    }

    public String getGame(UserInfos userInfos, String platform) {
        checkExpiration(userInfos);
        String body = "{\"operationName\":\"gameFromSlug\",\"variables\":{\"slug\":\"rainbow-six-siege\",\"platform\":\"" + platform + "\"},\"query\":\"query gameFromSlug($slug: String!, $platform: PlatformType) {\\n  viewer {\\n    id\\n    game(slug: $slug, platform: $platform) {\\n      node {\\n        ...gameProps\\n        slug\\n        description\\n        facebookUrl\\n        instagramUrl\\n        redditUrl\\n        twitterUrl\\n        websiteUrl\\n        challengesEnabled: isPeriodicChallengeSupported\\n        isPeriodicChallengeSupported\\n        classicChallenges {\\n          totalCount\\n          __typename\\n        }\\n        statsEnabled: isStatisticsSupported\\n        rewards {\\n          totalCount\\n          __typename\\n        }\\n        availablePlatforms {\\n          nodes {\\n            id\\n            name\\n            type\\n            __typename\\n          }\\n          __typename\\n        }\\n        availablePlatformGroups {\\n          id\\n          name\\n          type\\n          __typename\\n        }\\n        viewer {\\n          meta {\\n            id\\n            isOwned\\n            hasFirstPartyAccount: isLinkedToFirstPartyAccount\\n            lastPlayedDate\\n            ...friendsPlayingGames\\n            ownedPlatformGroups {\\n              id\\n              name\\n              type\\n              __typename\\n            }\\n            ownedCrossplayPlatforms {\\n              nodes {\\n                id\\n                type\\n                name\\n                __typename\\n              }\\n              __typename\\n            }\\n            __typename\\n          }\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment gameProps on Game {\\n  id\\n  spaceId\\n  name\\n  slug\\n  coverUrl: lowBoxArtUrl\\n  bannerUrl: backgroundUrl\\n  releaseDate\\n  releaseStatus\\n  platform {\\n    ...platformProps\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment platformProps on Platform {\\n  id\\n  name\\n  type\\n  __typename\\n}\\n\\nfragment friendsPlayingGames on UserGameMeta {\\n  friends {\\n    edges {\\n      node {\\n        id\\n        name\\n        avatarUrl\\n        networks {\\n          edges {\\n            node {\\n              id\\n              publicCodeName\\n              __typename\\n            }\\n            meta {\\n              id\\n              name\\n              __typename\\n            }\\n            __typename\\n          }\\n          __typename\\n        }\\n        __typename\\n      }\\n      meta {\\n        id\\n        lastPlayedDate\\n        ownedCrossplayPlatforms {\\n          nodes {\\n            id\\n            name\\n            type\\n            __typename\\n          }\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\"}";
        return callWebService(GRAPHQL_URL, UBI_TOKEN_PREFIX + userInfos.getAuthentication().getTicket(),
                POST_METHOD, body, userInfos.getAuthentication().getSessionId());
    }

    public String getChallenges(UserInfos userInfos, String spaceId) {
        checkExpiration(userInfos);
        String body = "{\"operationName\":\"getWeeklyChallenges\",\"variables\":{\"spaceId\":\"" + spaceId + "\"},\"query\":\"query getWeeklyChallenges($spaceId: String!) {\\n  game(spaceId: $spaceId) {\\n    id\\n    viewer {\\n      meta {\\n        id\\n        periodicChallenges {\\n          totalXpCount\\n          xpEarnedCount\\n          totalCount\\n          challenges: nodes {\\n            ...WeeklyChallengeData\\n            __typename\\n          }\\n          __typename\\n        }\\n        activatedChallengesXp: periodicChallenges(filterBy: {periodicChallenge: {isExpired: false}}) {\\n          totalXpCount\\n          xpEarnedCount\\n          __typename\\n        }\\n        currencyPrizes: periodicChallengePrizes(filterBy: {periodicChallengePrize: {type: CURRENCY}}) {\\n          ...gamePeriodicChallengePrizesConnection\\n          __typename\\n        }\\n        itemPrizes: periodicChallengePrizes(filterBy: {periodicChallengePrize: {type: ITEM}}) {\\n          ...gamePeriodicChallengePrizesConnection\\n          __typename\\n        }\\n        rewardPrizes: periodicChallengePrizes(filterBy: {periodicChallengePrize: {type: REWARD}}) {\\n          ...gamePeriodicChallengePrizesConnection\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment WeeklyChallengeData on PeriodicChallenge {\\n  id\\n  challengeId\\n  description\\n  imageUrl\\n  name\\n  previewUrl\\n  startDate\\n  endDate\\n  isExpired\\n  type\\n  xpPrize\\n  value\\n  formattedValue\\n  currencyPrizes: prizes(filterBy: {type: CURRENCY}) {\\n    nodes {\\n      id\\n      imageUrl\\n      name\\n      __typename\\n    }\\n    __typename\\n  }\\n  itemPrizes: prizes(filterBy: {type: ITEM}) {\\n    nodes {\\n      id\\n      imageUrl\\n      name\\n      __typename\\n    }\\n    __typename\\n  }\\n  rewardPrizes: prizes(filterBy: {type: REWARD}) {\\n    nodes {\\n      id\\n      imageUrl\\n      name\\n      __typename\\n    }\\n    __typename\\n  }\\n  thresholds {\\n    totalCount\\n    nodes {\\n      id\\n      value\\n      cumulatedValue\\n      formattedCumulatedValue\\n      xpPrize\\n      viewer {\\n        meta {\\n          id\\n          isCollected\\n          __typename\\n        }\\n        __typename\\n      }\\n      currencyPrizes: prizes(filterBy: {type: CURRENCY}) {\\n        edges {\\n          meta {\\n            id\\n            value: count\\n            __typename\\n          }\\n          node {\\n            id\\n            imageUrl\\n            name\\n            __typename\\n          }\\n          __typename\\n        }\\n        __typename\\n      }\\n      itemPrizes: prizes(filterBy: {type: ITEM}) {\\n        nodes {\\n          id\\n          imageUrl\\n          name\\n          __typename\\n        }\\n        __typename\\n      }\\n      rewardPrizes: prizes(filterBy: {type: REWARD}) {\\n        nodes {\\n          id\\n          imageUrl\\n          name\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n  viewer {\\n    meta {\\n      id\\n      isActivated\\n      isCollectible\\n      isCompleted\\n      isInProgress\\n      isRedeemed\\n      contribution\\n      formattedContribution\\n      progressPercentage\\n      progress\\n      formattedProgress\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment gamePeriodicChallengePrizesConnection on UserGamePeriodicChallengePrizesConnection {\\n  totalCount\\n  collectedValuesCount\\n  totalValuesCount\\n  edges {\\n    meta {\\n      id\\n      count\\n      collectedCount\\n      __typename\\n    }\\n    node {\\n      id\\n      imageUrl\\n      name\\n      type\\n      viewer {\\n        meta {\\n          id\\n          collectedCount\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\"}";
        return callWebService(GRAPHQL_URL, UBI_TOKEN_PREFIX + userInfos.getAuthentication().getTicket(),
                POST_METHOD, body, userInfos.getAuthentication().getSessionId());
    }

    public String claimChallenge(UserInfos userInfos, String spaceId, String challengeId) {
        checkExpiration(userInfos);
        String body = "{\"operationName\":\"collectWeeklyChallenge\",\"variables\":{\"spaceId\":\"" + spaceId + "\",\"challengeId\":\"" + challengeId + "\"},\"query\":\"mutation collectWeeklyChallenge($spaceId: String!, $challengeId: String!) {\\n  collectPeriodicChallenge(spaceId: $spaceId, challengeId: $challengeId) {\\n    periodicChallenge {\\n      id\\n      thresholds {\\n        nodes {\\n          id\\n          viewer {\\n            meta {\\n              id\\n              isCollected\\n              __typename\\n            }\\n            __typename\\n          }\\n          __typename\\n        }\\n        __typename\\n      }\\n      viewer {\\n        meta {\\n          id\\n          isCollectible\\n          isCompleted\\n          isInProgress\\n          isRedeemed\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    game {\\n      id\\n      viewer {\\n        meta {\\n          id\\n          ...periodicChallengeOverviewFragment\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment periodicChallengeOverviewFragment on UserGameMeta {\\n  currencyPrizes: periodicChallengePrizes(filterBy: {periodicChallengePrize: {type: CURRENCY}}) {\\n    ...gamePeriodicChallengePrizesConnection\\n    __typename\\n  }\\n  itemPrizes: periodicChallengePrizes(filterBy: {periodicChallengePrize: {type: ITEM}}) {\\n    ...gamePeriodicChallengePrizesConnection\\n    __typename\\n  }\\n  rewardPrizes: periodicChallengePrizes(filterBy: {periodicChallengePrize: {type: REWARD}}) {\\n    ...gamePeriodicChallengePrizesConnection\\n    __typename\\n  }\\n  inProgressPeriodicChallenges: periodicChallenges(filterBy: {isInProgress: true}) {\\n    totalCount\\n    __typename\\n  }\\n  completedPeriodicChallenges: periodicChallenges(filterBy: {isCompleted: true, periodicChallenge: {isExpired: false}}) {\\n    totalCount\\n    __typename\\n  }\\n  periodicChallenges(filterBy: {periodicChallenge: {isExpired: false}}) {\\n    totalCount\\n    collectiblePrizesCount\\n    totalXpCount\\n    xpEarnedCount\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment gamePeriodicChallengePrizesConnection on UserGamePeriodicChallengePrizesConnection {\\n  totalCount\\n  collectedValuesCount\\n  totalValuesCount\\n  edges {\\n    meta {\\n      id\\n      count\\n      collectedCount\\n      __typename\\n    }\\n    node {\\n      id\\n      imageUrl\\n      name\\n      type\\n      viewer {\\n        meta {\\n          id\\n          collectedCount\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\"}";
        return callWebService(GRAPHQL_URL, UBI_TOKEN_PREFIX + userInfos.getAuthentication().getTicket(),
                POST_METHOD, body, userInfos.getAuthentication().getSessionId());
    }

    public String getProfiles(UserInfos userInfos) {
        checkExpiration(userInfos);
        String userId = userInfos.getAuthentication().getUserId();
        return callWebService(PROFILES_URL + userId, UBI_TOKEN_PREFIX + userInfos.getAuthentication().getTicket(),
                GET_METHOD, null, userInfos.getAuthentication().getSessionId());
    }

    private String callWebService(String connectionUrl, String authorization, String method, String body, String sessionId) {
        String response;
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(connectionUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (method.equals(POST_METHOD)) {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod(POST_METHOD);
            }
            urlConnection.setRequestProperty(HEADER_UBI_APPID, APP_ID);
            urlConnection.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
            urlConnection.setRequestProperty(HEADER_AUTHORIZATION, authorization);
            if(null != sessionId) {
                urlConnection.setRequestProperty(HEADER_UBI_SESSIONID, sessionId);
            }
            if (method.equals(POST_METHOD)) {
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                osw.write(body);
                osw.flush();
                osw.close();
                os.close();
            }
            urlConnection.connect();
            response = getResponse(urlConnection);
        } catch (IOException e) {
            response = EXCEPTION_PATTERN + e.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    private void checkExpiration(UserInfos userInfos) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(UBI_DATE_FORMAT, Locale.getDefault());
            Date expiration = formatter.parse(userInfos.getAuthentication().getExpiration().split(UBI_DATE_DELIMITER)[0]);
            if (System.currentTimeMillis() > expiration.getTime() - (1000 * 60 * 5)) {

                String updatedAuthJson = updateRefreshToken(userInfos);
                Log.d("Refresh token", updatedAuthJson);
                userInfos.setAuthentication(new Gson().fromJson(updatedAuthJson, Authentication.class));
                try {
                    SharedPreferencesService.getEncryptedSharedPreferences().edit().putString("userInfos",new Gson().toJson(userInfos)).apply();
                } catch (GeneralSecurityException | IOException e) {
                    Log.e("Unable to update token", e.getMessage());
                }
            }
        } catch (ParseException e) {
            Log.e("checkExpiration", e.getMessage());
        }
    }

    private String getResponse(HttpURLConnection urlConnection) throws IOException {
        StringBuilder response = new StringBuilder();
        BufferedReader br;
        if (100 <= urlConnection.getResponseCode() && urlConnection.getResponseCode() <= 399) {
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
        }
        String strCurrentLine;
        while ((strCurrentLine = br.readLine()) != null) {
            response.append(strCurrentLine);
        }
        return response.toString();
    }

    public boolean isValidResponse(String response){
        return StringUtils.isNotBlank(response) && !response.contains(UBI_ERROR_CODE) &&!response.contains(EXCEPTION_PATTERN) &&!response.contains(UBI_ERRORS);
    }

    public String getErrorMessage(String response){
        String message;
        if (response.contains(UBI_ERROR_CODE) || response.contains(UBI_ERRORS)) {
            String errorMessageBegin = UBI_ERROR_BEGIN;
            String errorMessageEnd = UBI_ERROR_END;
            int pFrom = response.indexOf(errorMessageBegin) + errorMessageBegin.length();
            int pTo = response.indexOf(errorMessageEnd, pFrom);

            message = response.substring(pFrom, pTo);
        } else if(response.contains(EXCEPTION_PATTERN)) {
            message = response;
        } else {
            message = UBI_EMPTY_RESPONSE;
        }
        return message;
    }

}
