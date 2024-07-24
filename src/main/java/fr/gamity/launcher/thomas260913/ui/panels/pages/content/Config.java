package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

public class Config {
    public String name;
    public McInfo mcinfo;
    public static class McInfo {
        public String type;
        public Forge forge;
        public Mc mc;
        public boolean autoconnect;
        public Server server;
        public static class Forge {
            public String version;
            public String mods;
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
