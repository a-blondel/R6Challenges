package com.ablondel.r6challenges.ui.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ablondel.r6challenges.App;
import com.ablondel.r6challenges.R;
import com.ablondel.r6challenges.model.UserInfos;
import com.ablondel.r6challenges.model.challenge.Challenge__1;
import com.ablondel.r6challenges.model.challenge.CurrencyPrizes__1;
import com.ablondel.r6challenges.model.challenge.ItemPrizes__1;
import com.ablondel.r6challenges.model.challenge.Meta__3;
import com.ablondel.r6challenges.model.challenge.Node__2;
import com.ablondel.r6challenges.model.challenge.Node__4;
import com.ablondel.r6challenges.model.games.GamePlatformEnum;
import com.ablondel.r6challenges.service.SharedPreferencesService;
import com.ablondel.r6challenges.service.UbiService;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.SneakyThrows;

public class ChallengesRecyclerViewAdapter extends RecyclerView.Adapter<ChallengesRecyclerViewAdapter.ViewHolder> {
    public static final String COMMUNITY = "COMMUNITY";
    private LayoutInflater mInflater;
    private List<Challenge__1> mData;
    private ClaimChallengeTask claimChallengeTask = null;
    private Handler handler;
    private UbiService ubiService;
    private UserInfos userInfos;

    ChallengesRecyclerViewAdapter(Context context, List<Challenge__1> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ubiService = new UbiService();
        try {
            userInfos = new Gson().fromJson(SharedPreferencesService.getEncryptedSharedPreferences().getString("userInfos", null), UserInfos.class);
        } catch (GeneralSecurityException | IOException e) {
            Log.e("Could not read shared preferences", e.getMessage());
        }

        View view = mInflater.inflate(R.layout.challenge_row, parent, false);
        return new ViewHolder(view);
    }

    void updateChallenge(Challenge__1 updatedChallenge, int position) {
        for(Challenge__1 challenge : mData) {
            if(updatedChallenge.getId().equals(challenge.getId())) {
                challenge.getViewer().getMeta().setIsCollectible(updatedChallenge.getViewer().getMeta().getIsCollectible());
                challenge.getViewer().getMeta().setIsRedeemed(updatedChallenge.getViewer().getMeta().getIsRedeemed());
                break;
            }
        }
        notifyItemChanged(position);
    }

    @SneakyThrows
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Challenge__1 challenge__1 = mData.get(position);
        Node__2 thresholdsNode = challenge__1.getThresholds().getNodes().get(0);
        Meta__3 viewerMeta = challenge__1.getViewer().getMeta();

        holder.challengeNameTextView.setText(challenge__1.getName());

        if(!challenge__1.isExpired && null != challenge__1.getEndDate()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            long endDate = formatter.parse(challenge__1.getEndDate().toString()).getTime();
            long now = new Date().getTime();
            long diff = endDate - now;
            if (diff > 0l) {
                long diffDay = diff / (24 * 60 * 60 * 1000);
                diff = diff - (diffDay * 24 * 60 * 60 * 1000);
                long diffHours = diff / (60 * 60 * 1000);
                diff = diff - (diffHours * 60 * 60 * 1000);
                long diffMinutes = diff / (60 * 1000);

                holder.challengeTimeTextView.setVisibility(View.VISIBLE);
                holder.challengeTimeTextView.setText(new StringBuilder()
                        .append(String.format("%01d", diffDay) + "d").append(" ")
                        .append(String.format("%02d", diffHours) + "h").append(" ")
                        .append(String.format("%02d", diffMinutes) + "min").append(" ")
                );
            }
        } else {
            holder.challengeTimeTextView.setVisibility(View.GONE);
        }

        holder.challengeDescriptionTextView.setText(
                StringUtils.replace(
                        challenge__1.getDescription(),
                        "{threshold}",
                        thresholdsNode.getFormattedCumulatedValue()));

        if(COMMUNITY.equals(challenge__1.getType())) {
            holder.challengeContributionTextView.setVisibility(View.VISIBLE);
            holder.challengeContributionTextView.setText("Contribution: " + challenge__1.getViewer().getMeta().getFormattedContribution());
        } else {
            holder.challengeContributionTextView.setVisibility(View.GONE);
        }

        holder.challengeProgressionTextView.setText(new StringBuilder()
                .append(viewerMeta.getProgress())
                .append("/")
                .append(thresholdsNode.getValue())
        );
        holder.challengeProgressionProgressBar.setProgress(viewerMeta.getProgressPercentage().intValue());
        holder.challengeProgressionProgressBar.setMax(100);

        CurrencyPrizes__1 prizes__1 = challenge__1.getThresholds().getNodes().get(0).getCurrencyPrizes();
        ItemPrizes__1 itemPrizes__1 = challenge__1.getThresholds().getNodes().get(0).getItemPrizes();
        if(0 != prizes__1.getEdges().size()) {
            String rewardType = prizes__1.getEdges().get(0).getNode().getName();
            Integer rewardValue = prizes__1.getEdges().get(0).getMeta().getValue();
            holder.challengeRewardsTextView.setText(new StringBuilder()
                    .append(rewardValue).append(" ").append(rewardType)
                    .append(" - ")
                    .append(challenge__1.getXpPrize()).append(" ").append("XP"));
        } else if (itemPrizes__1.getNodes().size() > 0) {
            int i = 0;
            StringBuilder prizesBuilder = new StringBuilder();
            for(Node__4 node__4 : itemPrizes__1.getNodes()) {
                if(i != 0) {
                    prizesBuilder.append(" - ");
                }
                prizesBuilder.append(node__4.getName());
                i++;
            }
            holder.challengeRewardsTextView.setText(prizesBuilder.toString());
        }

        if(challenge__1.getViewer().getMeta().getIsCompleted() && challenge__1.getViewer().getMeta().isRedeemed) {
            holder.challengeRewardsObtainedTextView.setVisibility(View.VISIBLE);
        }

        if(challenge__1.getViewer().getMeta().getIsCollectible() && !challenge__1.getViewer().getMeta().isRedeemed) {
            holder.challengeClaimButton.setVisibility(View.VISIBLE);
            holder.challengeClaimButton.setEnabled(true);
        } else {
            holder.challengeClaimButton.setVisibility(View.GONE);
        }

        holder.challengeClaimButton.setOnClickListener((v) -> {
            holder.challengeClaimButton.setEnabled(false);
            claimChallengeTask = new ClaimChallengeTask(this, position);
            claimChallengeTask.execute(challenge__1);
        });

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Toast.makeText(
                        App.getAppContext(),
                        (String) message.obj,
                        Toast.LENGTH_LONG).show();
            }
        };
    }

    public class ClaimChallengeTask extends AsyncTask<Object, Void, Challenge__1> {
        ChallengesRecyclerViewAdapter adapter;
        int position;

        public ClaimChallengeTask(ChallengesRecyclerViewAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        protected Challenge__1 doInBackground(Object... params) {
            Challenge__1 data = null;
            Challenge__1 challengeToClaim = (Challenge__1) params[0];
            String message = "Challenge claimed!";
            String claimChallengeJson = ubiService.claimChallenge(userInfos, GamePlatformEnum.getPlatformByKey(userInfos.getLastSelectedPlatform()).getSpaceId(), challengeToClaim.getChallengeId());

            if (ubiService.isValidResponse(claimChallengeJson)) {
                data = new Gson().fromJson(JsonParser.parseString(claimChallengeJson).getAsJsonObject()
                        .getAsJsonObject("data").getAsJsonObject("collectPeriodicChallenge").getAsJsonObject("periodicChallenge"), Challenge__1.class);
            } else {
                message = ubiService.getErrorMessage(claimChallengeJson);
            }
            sendMessage(message);
            Log.d("Result", message);
            return data;
        }

        @Override
        protected void onPostExecute(final Challenge__1 data) {
            if(null != data) {
                adapter.updateChallenge(data, position);
            }
            claimChallengeTask = null;
        }

        @Override
        protected void onCancelled() {
            claimChallengeTask = null;
        }

        private void sendMessage(String message) {
            Message msg = Message.obtain();
            msg.obj = message;
            msg.setTarget(handler);
            msg.sendToTarget();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView challengeNameTextView;
        TextView challengeTimeTextView;
        TextView challengeDescriptionTextView;
        TextView challengeContributionTextView;
        TextView challengeProgressionTextView;
        ProgressBar challengeProgressionProgressBar;
        TextView challengeRewardsTextView;
        TextView challengeRewardsObtainedTextView;
        Button challengeClaimButton;

        ViewHolder(View itemView) {
            super(itemView);
            challengeNameTextView = itemView.findViewById(R.id.challengeNameTextView);
            challengeTimeTextView = itemView.findViewById(R.id.challengeTimeTextView);
            challengeDescriptionTextView = itemView.findViewById(R.id.challengeDescriptionTextView);
            challengeContributionTextView = itemView.findViewById(R.id.challengeContributionTextView);
            challengeProgressionTextView = itemView.findViewById(R.id.challengeProgressionTextView);
            challengeProgressionProgressBar = itemView.findViewById(R.id.challengeProgressionProgressBar);
            challengeRewardsTextView = itemView.findViewById(R.id.challengeRewardsTextView);
            challengeRewardsObtainedTextView = itemView.findViewById(R.id.challengeRewardsObtainedTextView);
            challengeClaimButton = itemView.findViewById(R.id.challengeClaimButton);
        }
    }

}
