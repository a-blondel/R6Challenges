
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@lombok.Data
public class Meta {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("periodicChallenges")
    @Expose
    public PeriodicChallenges periodicChallenges;
    @SerializedName("activatedChallengesXp")
    @Expose
    public ActivatedChallengesXp activatedChallengesXp;
    @SerializedName("currencyPrizes")
    @Expose
    public CurrencyPrizes__2 currencyPrizes;
    @SerializedName("itemPrizes")
    @Expose
    public ItemPrizes__2 itemPrizes;
    @SerializedName("rewardPrizes")
    @Expose
    public RewardPrizes__2 rewardPrizes;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
