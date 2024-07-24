package com.gamity.launcher.thomas260913.ui.panels.pages.content;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Version {
    private String latest;
    @JsonProperty("versions")
    private List<String> versions;
    public List<String> getVersionList() {
        return versions;
    }
    public String getLatest() {
        return latest;
    }
}