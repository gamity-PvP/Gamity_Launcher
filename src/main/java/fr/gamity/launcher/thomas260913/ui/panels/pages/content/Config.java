package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

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
    public static class Parser{
        public static class OptifineParser{
            public Mod OptifineRequestMod(String type,String version,boolean latest) throws Exception {
                ObjectMapper objectMapper = new ObjectMapper();
                OptifineJson optifine = objectMapper.readValue(new URL("https://gamity-pvp.fr/apis/optifine/list.json?latest="+latest+"&mc="+version+"&type="+type), OptifineJson.class);
                if(!optifine.optifine.isEmpty()){
                    OptifineJson.OptifineList.Optifine.DownloadInfos modinfo = optifine.optifine.get(0).optifine.downloadInfo;
                    return new Mod(modinfo.name, modinfo.downloadURL, modinfo.sha1, modinfo.size);
                }
                return null;
            }
            public Mod OptifineRequestMod(String version) throws Exception {
                ObjectMapper objectMapper = new ObjectMapper();
                if(version.contains("OptiFine")){
                    OptifineJson optifine = objectMapper.readValue(new URL("https://gamity-pvp.fr/apis/optifine/list.json"), OptifineJson.class);
                    if(!optifine.optifine.isEmpty()){
                        Stream<OptifineJson.OptifineList> filter = optifine.optifine.stream().filter(o-> Objects.equals(o.optifine.version, version));
                        OptifineJson.OptifineList.Optifine.DownloadInfos modinfo = filter.collect(Collectors.toList()).get(0).optifine.downloadInfo;
                        return new Mod(modinfo.name, modinfo.downloadURL, modinfo.sha1, modinfo.size);
                    }
                }else{
                    OptifineJson optifine = objectMapper.readValue(new URL("https://gamity-pvp.fr/apis/optifine/list.json?mc="+version+"&latest=true"), OptifineJson.class);
                    if(!optifine.optifine.isEmpty()){
                        Stream<OptifineJson.OptifineList> filter = optifine.optifine.stream().filter(o-> Objects.equals(o.optifine.version, version));
                        OptifineJson.OptifineList.Optifine.DownloadInfos modinfo = filter.collect(Collectors.toList()).get(0).optifine.downloadInfo;
                        return new Mod(modinfo.name, modinfo.downloadURL, modinfo.sha1, modinfo.size);
                    }
                }
                return null;
            }
            public List<OptifineJson.OptifineList> OptifineRequest(String type, String version, boolean latest) throws Exception {
                ObjectMapper objectMapper = new ObjectMapper();
                OptifineJson optifine = objectMapper.readValue(new URL("https://gamity-pvp.fr/apis/optifine/list.json?latest="+latest+"&mc="+version+"&type="+type), OptifineJson.class);
                return optifine.optifine;
            }
            public static class OptifineJson{
                public List<OptifineList> optifine;
                public static class OptifineList{
                    public String mcversion;
                    public Optifine optifine;
                    public static class Optifine{
                        public String type;
                        public String version;
                        public DownloadInfos downloadInfo;
                        public static class DownloadInfos{
                            public String name;
                            public String downloadURL;
                            public String sha1;
                            public long size;
                        }
                    }
                }
            }
        }
        public static class JsonConfigParser {
            public Config.CustomServer parseJsonPath(Path path) throws Exception {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(new File(path.toUri()), Config.CustomServer.class);
            }
            public Config.CustomServer parseJsonURL(String url) throws Exception {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(new URL(url), Config.CustomServer.class);
            }
        }
        public static class ModsJsonParser{
            public static class CurseParser{
                public List<CurseFileInfo> CurseParserUrl(URL url){
                    if(url.toString().startsWith("http")){
                        return CurseFileInfo.getFilesFromJson(url);
                    }else{
                        return new ArrayList<>();
                    }
                }
                public List<CurseFileInfo> CurseParserJson(String json) throws Exception {
                    CurseList objectMapper = new ObjectMapper().readValue(json, CurseList.class);
                    List<CurseFileInfo> result = new ArrayList<>();
                    objectMapper.curseFiles.forEach(files -> result.add(new CurseFileInfo(files.projectID, files.fileID)));
                    return result;
                }
                public static class CurseList{
                    public List<Curse> curseFiles;
                    public static class Curse{
                        public int projectID;
                        public int fileID;
                    }
                }
            }
            public static class ModsParser{
                public List<Mod> ModsParserUrl(URL url){
                    return Mod.getModsFromJson(url);
                }
                public List<Mod> ModsParserJson(String json) throws Exception {
                        ModsList objectMapper = new ObjectMapper().readValue(json, ModsList.class);
                        List<Mod> result = new ArrayList<>();
                        objectMapper.mods.forEach(file -> result.add(new Mod(file.name,file.downloadURL,file.sha1,file.size)));
                        return result;
                }
                public static class ModsList{
                    public List<Mods> mods;
                    public static class Mods{
                        public String name;
                        public String downloadURL;
                        public String sha1;
                        public long size;
                    }
                }
            }
        }
    }
    public static class CustomServer{
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
                public Mods mods;
                public Optifine optifine;
                public boolean allowOptifine;

                public static class Optifine {
                    public String optifineVersion;
                }

                public static class Mods {
                    public Custom custom;
                    public CurseForge curseForge;
                    public static class Custom {
                        public String json;
                    }

                    public static class CurseForge {
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
