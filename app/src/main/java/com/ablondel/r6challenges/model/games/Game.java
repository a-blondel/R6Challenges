package com.ablondel.r6challenges.model.games;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Game {
    private String platform;
    private boolean owned;
    private String LastPlayedDate;
}
