package fr.gamity.launcher.thomas260913.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.download.json.Mod;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config {
    public static class ServerList {
        private List<Server> servers;

        public List<Server> getServers() {
            return servers;
        }

        public static class Server {
            private String name;
            @JsonProperty("configURL")
            private String configURL;
            public String getName() {
                return name;
            }
            public Config.CustomServer getConfig() throws Exception {
                return new Parser.JsonConfigParser().parseJsonURL(configURL);
            }
        }
    }
    public static class CustomServer{
        public String name;
        public McInfo mcinfo;
        public static class McInfo {
            public String type;
            public ModLoader modLoader;
            public Mc mc;
            public boolean autoconnect;
            public Server server;
            public static class ModLoader {
                public String version;
                public Mods mods;
                public Optifine optifine;
                public boolean allowOptifine;

                public static class Optifine {
                    public String optifineVersion;
                }

                public static class Mods {
                    public Custom custom;
                    public CurseForge curseForge;
                    public Modrinth modrinth;
                    public CurseForgeModpack curseForgeModpack;
                    public ModrinthModpack modrinthModpack;
                    public static class Custom {
                        public String json;
                    }
                    public static class CurseForge {
                        public String json;
                    }
                    public static class Modrinth {
                        public String json;
                    }
                    public static class CurseForgeModpack {
                        public String json;
                    }
                    public static class ModrinthModpack {
                        public String json;
                    }
                }
            }

            public static class Mc {
                public String version;
                public String java;
                public String extfiles;
            }
            public static class Server {
                public String ip;
                public String port;
            }
        }
    }
}
