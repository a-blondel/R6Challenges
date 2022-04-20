
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Challenge__1 {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("challengeId")
    @Expose
    public String challengeId;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("imageUrl")
    @Expose
    public String imageUrl;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("previewUrl")
    @Expose
    public Object previewUrl;
    @SerializedName("startDate")
    @Expose
    public Object startDate;
    @SerializedName("endDate")
    @Expose
    public Object endDate;
    @SerializedName("isExpired")
    @Expose
    public Boolean isExpired;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("xpPrize")
    @Expose
    public Integer xpPrize;
    @SerializedName("value")
    @Expose
    public Integer value;
    @SerializedName("formattedValue")
    @Expose
    public String formattedValue;
    @SerializedName("currencyPrizes")
    @Expose
    public CurrencyPrizes currencyPrizes;
    @SerializedName("itemPrizes")
    @Expose
    public ItemPrizes itemPrizes;
    @SerializedName("rewardPrizes")
    @Expose
    public RewardPrizes rewardPrizes;
    @SerializedName("thresholds")
    @Expose
    public Thresholds thresholds;
    @SerializedName("viewer")
    @Expose
    public Viewer__2 viewer;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
