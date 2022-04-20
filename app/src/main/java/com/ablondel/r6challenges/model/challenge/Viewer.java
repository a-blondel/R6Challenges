
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Viewer {

    @SerializedName("meta")
    @Expose
    public Meta meta;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
