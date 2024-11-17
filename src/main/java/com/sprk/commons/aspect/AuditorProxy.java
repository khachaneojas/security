package com.sprk.commons.aspect;

import com.sprk.commons.document.EntitlementModel;
import com.sprk.commons.document.dto.MainTab;
import com.sprk.commons.dto.TokenValidationResponse;
import com.sprk.commons.dto.UserAuthenticatorR;
import com.sprk.commons.entity.primary.user.UserModel;
import com.sprk.commons.exception.AccountDisabledException;
import com.sprk.commons.exception.UnauthorizedException;
import com.sprk.commons.lang.JwtWizard;
import com.sprk.commons.repository.primary.UserAuthenticatorRepository;
import com.sprk.commons.repository.mongo.EntitlementAuthenticatorRepository;
import com.sprk.commons.tag.Action;
import com.sprk.commons.tag.Authority;
import com.sprk.commons.tag.View;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;



@Component
@RequiredArgsConstructor
public class AuditorProxy {
    private static final Logger log = LoggerFactory.getLogger(AuditorProxy.class);
    private final UserAuthenticatorRepository userAuthenticatorRepository;
    private final EntitlementAuthenticatorRepository entitlementAuthenticatorRepository;
    private final JwtWizard jwtHelper;

    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED
    )
    public TokenValidationResponse isTokenValid(String authorizationHeader, Set<View> authorizedViews, Set<Action> authorizedActions) {
        String tokenId = jwtHelper.getSubject(authorizationHeader);
        UserAuthenticatorR user = Optional
                .ofNullable(tokenId)
                .filter(StringUtils::isNotBlank)
                .map(userAuthenticatorRepository::findByUserUid)
                .orElseThrow(UnauthorizedException::new);

        if (!user.orgAssigned()) {
            throw new AccountDisabledException("Uh-oh! We couldn't find an organization linked to your account. Please reach out to our support team or administrator to get your organization assignment sorted out.");
        }

        if (!user.enabled()) {
            throw new AccountDisabledException("Oops! Looks like your account has been disabled by the administrator.");
        }

        if (jwtHelper.getPayload(authorizationHeader) != user.tokenAt().toEpochMilli()) {
            throw new UnauthorizedException("Your account may have been updated. To ensure you have access to the updated information, please log in again.");
        }

        EntitlementModel entitlement = Optional
                .of(tokenId)
                .map(entitlementAuthenticatorRepository::findByUserUid)
                .orElseThrow(UnauthorizedException::new);

        Set<MainTab> entitlements = entitlement.getEntitlements();
        if (!authorizedViews.isEmpty() && !authorizedActions.isEmpty() && !entitlements.isEmpty() && entitlements
                .stream()
                .flatMap(mainTabModel -> mainTabModel.getSub().stream())
                .noneMatch(subTabModel -> authorizedViews.contains(subTabModel.getName()) && subTabModel.getActions().stream().anyMatch(authorizedActions::contains))
        ) {
            throw new UnauthorizedException();
        }

        Set<String> authorities = entitlement.getAuthorities();
        if (null == authorities) {
            authorities = Set.of();
        }

        setAuthenticationInSecurityContext(user.userUid(), authorities);
        return TokenValidationResponse.builder()
                .pid(user.userPid())
                .uid(user.userUid())
                .none(authorities.isEmpty())
                .adminRole(authorities.contains(Authority.ROLE_ADMIN.name()))
                .salesRole(authorities.contains(Authority.ROLE_SALES.name()))
                .facultyRole(authorities.contains(Authority.ROLE_FACULTY.name()))
                .build();
    }

    private static void setAuthenticationInSecurityContext(String uid, Set<String> authorities) {
        Objects.requireNonNull(uid);
        Objects.requireNonNull(authorities);
        User userDetails = new User(uid, uid, authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet()));
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }
}
