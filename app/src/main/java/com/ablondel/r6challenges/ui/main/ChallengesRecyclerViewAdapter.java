package com.ablondel.r6challenges.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ablondel.r6challenges.R;
import com.ablondel.r6challenges.model.challenge.Challenge__1;
import com.ablondel.r6challenges.model.challenge.CurrencyPrizes__1;
import com.ablondel.r6challenges.model.challenge.ItemPrizes__1;
import com.ablondel.r6challenges.model.challenge.Meta__3;
import com.ablondel.r6challenges.model.challenge.Node__2;
import com.ablondel.r6challenges.model.challenge.Node__4;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.SneakyThrows;

public class ChallengesRecyclerViewAdapter extends RecyclerView.Adapter<ChallengesRecyclerViewAdapter.ViewHolder> {
    public static final String COMMUNITY = "COMMUNITY";
    private LayoutInflater mInflater;
    private List<Challenge__1> mData;

    ChallengesRecyclerViewAdapter(Context context, List<Challenge__1> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.challenge_row, parent, false);
        return new ViewHolder(view);
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
            long now = new Date().getTime() - (1000 * 60 * 60 * 24 * 14);
            long diff = endDate - now;
            if (diff > 0l) {
                long diffDay = diff / (24 * 60 * 60 * 1000);
                diff = diff - (diffDay * 24 * 60 * 60 * 1000);
                long diffHours = diff / (60 * 60 * 1000);
                diff = diff - (diffHours * 60 * 60 * 1000);
                long diffMinutes = diff / (60 * 1000);

                holder.challengeTimeTextView.setText(new StringBuilder()
                        .append(String.format("%01d", diffDay) + "d").append(" ")
                        .append(String.format("%02d", diffHours) + "h").append(" ")
                        .append(String.format("%02d", diffMinutes) + "min").append(" ")
                );
            }
        }

        holder.challengeDescriptionTextView.setText(
                StringUtils.replace(
                        challenge__1.getDescription(),
                        "{threshold}",
                        thresholdsNode.getFormattedCumulatedValue()));

        if(COMMUNITY.equals(challenge__1.getType())) {
            holder.challengeContributionTextView.setVisibility(View.VISIBLE);
            holder.challengeContributionTextView.setText("Contribution: " + challenge__1.getViewer().getMeta().getFormattedContribution());
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
