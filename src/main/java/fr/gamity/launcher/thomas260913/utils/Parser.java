package fr.gamity.launcher.thomas260913.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;

public class Parser {
    public static class appInfo {
        public static Config parseJsonURL(String url) throws Exception {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new URL(url), Config.class);
        }
    }
}
