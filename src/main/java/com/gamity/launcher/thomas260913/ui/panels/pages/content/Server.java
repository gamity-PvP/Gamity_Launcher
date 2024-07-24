package com.gamity.launcher.thomas260913.ui.panels.pages.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gamity.launcher.thomas260913.JsonConfigParser;

public class Server {
    private String name;
    @JsonProperty("configURL")
    private String configURL;
    public String getName() {
        return name;
    }
    public Config getConfig() {
        return new JsonConfigParser().parseJsonURL(configURL);
    }
}
