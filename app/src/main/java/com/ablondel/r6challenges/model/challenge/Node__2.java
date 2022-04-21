
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@lombok.Data
public class Node__2 {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("value")
    @Expose
    public Integer value;
    @SerializedName("cumulatedValue")
    @Expose
    public Integer cumulatedValue;
    @SerializedName("formattedCumulatedValue")
    @Expose
    public String formattedCumulatedValue;
    @SerializedName("xpPrize")
    @Expose
    public Integer xpPrize;
    @SerializedName("viewer")
    @Expose
    public Viewer__1 viewer;
    @SerializedName("currencyPrizes")
    @Expose
    public CurrencyPrizes__1 currencyPrizes;
    @SerializedName("itemPrizes")
    @Expose
    public ItemPrizes__1 itemPrizes;
    @SerializedName("rewardPrizes")
    @Expose
    public RewardPrizes__1 rewardPrizes;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
