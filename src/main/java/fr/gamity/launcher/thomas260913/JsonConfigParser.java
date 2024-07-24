package fr.gamity.launcher.thomas260913;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gamity.launcher.thomas260913.ui.panels.pages.content.Config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class JsonConfigParser {
    public Config parseJsonPath(Path path) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            return objectMapper.readValue(new File(path.toUri()), Config.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Config parseJsonURL(String url) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            return objectMapper.readValue(new URL(url), Config.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
