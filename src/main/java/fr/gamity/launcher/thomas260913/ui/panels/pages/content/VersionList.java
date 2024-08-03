package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gamity.launcher.thomas260913.Launcher;

import java.net.URL;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionList {
    public Latest latest;
    @JsonProperty("versions")
    public List<Version> versions;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Version {
        public String id;
        public String type;
        public String url;
        public String getJavaVersion(){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return String.valueOf(objectMapper.readValue(new URL(url), mcInfoVersion.class).javaVersion.majorVersion);
            }catch(Exception e){
                Launcher.getInstance().showErrorDialog(e);
                Launcher.getInstance().getLogger().printStackTrace(e);
            }
            return null;
        }
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class mcInfoVersion{
            public JavaVersion javaVersion;
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class JavaVersion{
                public int majorVersion;
            }
        }
    }
    public static class Latest {
        public String release;
        public String snapshot;
    }
}
