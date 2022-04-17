package com.ablondel.r6challenges.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Authentication {
    private String ticket;
    private String profileId;
    private String userId;
    private String expiration;
    private String sessionId;
    private String rememberMeTicket;
}
