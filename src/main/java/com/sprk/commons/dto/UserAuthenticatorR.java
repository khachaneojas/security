package com.sprk.commons.dto;

import java.time.Instant;

public record UserAuthenticatorR(
        Long userPid,
        String userUid,
        Instant tokenAt,
        boolean enabled,
        boolean orgAssigned
) {}
