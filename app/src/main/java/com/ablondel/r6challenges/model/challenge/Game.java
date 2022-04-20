
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Game {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("viewer")
    @Expose
    public Viewer viewer;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
