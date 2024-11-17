package com.sprk.commons.lang;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;



@Component
@RequiredArgsConstructor
public class EnvProfile {

    private final Environment environment;

    @Cacheable("activeProfiles")
    public String[] getActiveProfiles() {
        return environment.getActiveProfiles();
    }

    public String getFirstActiveProfile() {
        String[] activeProfiles = getActiveProfiles();
        return (activeProfiles.length > 0) ? activeProfiles[0] : null;
    }

    public String getLastActiveProfile() {
        String[] activeProfiles = getActiveProfiles();
        return (activeProfiles.length > 0) ? activeProfiles[activeProfiles.length - 1] : null;
    }

    public boolean hasProfile(String profile) {
        return Arrays.asList(getActiveProfiles()).contains(profile);
    }

    public boolean hasAnyProfile(String[] profiles) {
        Objects.requireNonNull(profiles);
        Set<String> activeProfiles = new HashSet<>(Arrays.asList(getActiveProfiles()));
        Set<String> inputProfiles = new HashSet<>(Arrays.asList(profiles));
        inputProfiles.retainAll(activeProfiles);
        return !inputProfiles.isEmpty();
    }

    @Cacheable("defaultProfile")
    public boolean isDefaultProfile() {
        return getActiveProfiles().length == 0;
    }

    @Cacheable("productionProfile")
    public boolean isProductionProfile() {
        return Arrays.asList(getActiveProfiles()).contains("prod") || Arrays.asList(getActiveProfiles()).contains("production");
    }
}
