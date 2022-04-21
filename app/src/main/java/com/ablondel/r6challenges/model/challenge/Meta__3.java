
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Meta__3 {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("isActivated")
    @Expose
    public Boolean isActivated;
    @SerializedName("isCollectible")
    @Expose
    public Boolean isCollectible;
    @SerializedName("isCompleted")
    @Expose
    public Boolean isCompleted;
    @SerializedName("isInProgress")
    @Expose
    public Boolean isInProgress;
    @SerializedName("isRedeemed")
    @Expose
    public Boolean isRedeemed;
    @SerializedName("contribution")
    @Expose
    public Integer contribution;
    @SerializedName("formattedContribution")
    @Expose
    public String formattedContribution;
    @SerializedName("progressPercentage")
    @Expose
    public Double progressPercentage;
    @SerializedName("progress")
    @Expose
    public Integer progress;
    @SerializedName("formattedProgress")
    @Expose
    public String formattedProgress;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
