package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.stream.MalformedJsonException;
import fr.flowarg.flowupdater.download.json.*;
import fr.gamity.launcher.thomas260913.Launcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
    public static class OptifineParser {
        public Mod OptifineRequestMod(String version) throws Exception {
            ObjectMapper objectMapper = new ObjectMapper();
            if (version.contains("OptiFine")) {
                OptifineJson optifine = objectMapper.readValue(new URL("https://gamity-pvp.fr/apis/optifine/list.json"), OptifineJson.class);
                if (!optifine.optifine.isEmpty()) {
                    Stream<OptifineJson.OptifineList> filter = optifine.optifine.stream().filter(o -> Objects.equals(o.optifine.version, version));
                    OptifineJson.OptifineList.Optifine.DownloadInfos modinfo = filter.collect(Collectors.toList()).get(0).optifine.downloadInfo;
                    return new Mod(modinfo.name, modinfo.downloadURL, modinfo.sha1, modinfo.size);
                }
            } else {
                OptifineJson optifine = objectMapper.readValue(new URL("https://gamity-pvp.fr/apis/optifine/list.json?mc=" + version + "&latest=true"), OptifineJson.class);
                if (!optifine.optifine.isEmpty()) {
                    OptifineJson.OptifineList.Optifine.DownloadInfos modinfo = optifine.optifine.get(0).optifine.downloadInfo;
                    return new Mod(modinfo.name, modinfo.downloadURL, modinfo.sha1, modinfo.size);
                }
            }
            return null;
        }

        public List<OptifineJson.OptifineList> OptifineRequest(String type, String version, boolean latest) throws Exception {
            ObjectMapper objectMapper = new ObjectMapper();
            OptifineJson optifine = objectMapper.readValue(new URL("https://gamity-pvp.fr/apis/optifine/list.json?latest=" + latest + "&mc=" + version + "&type=" + type), OptifineJson.class);
            return optifine.optifine;
        }

        public static class OptifineJson {
            public List<OptifineList> optifine;

            public static class OptifineList {
                public String mcversion;
                public Optifine optifine;

                public static class Optifine {
                    public String type;
                    public String version;
                    public DownloadInfos downloadInfo;

                    public static class DownloadInfos {
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
                return CurseFileInfo.getFilesFromJson(url);
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
                    public String name;
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

        public static class ModrinthParser {
            public List<ModrinthVersionInfo> ModrinthParserUrl(URL url){
                return ModrinthVersionInfo.getModrinthVersionsFromJson(url);
            }
            public List<ModrinthVersionInfo> ModrinthParserJson(String json) throws Exception {
                ModrinthList objectMapper = new ObjectMapper().readValue(json, ModrinthList.class);
                List<ModrinthVersionInfo> result = new ArrayList<>();
                objectMapper.modrinthMods.forEach(file -> {
                    if(file.versionId != null && file.versionNumber == null && file.projectReference == null) {
                        result.add(new ModrinthVersionInfo(file.versionId));
                    }else if(file.versionId == null && file.versionNumber != null && file.projectReference != null){
                        result.add(new ModrinthVersionInfo(file.projectReference, file.versionNumber));
                    }else{
                        try {
                            throw new MalformedJsonException("json is malformed please read wiki");
                        } catch (MalformedJsonException e) {
                            Launcher.getInstance().getLogger().printStackTrace(e);
                            Launcher.getInstance().showErrorDialog(e);
                        }
                    }
                });
                return result;
            }
            public static class ModrinthList{
                public List<ModrinthParser.ModrinthList.Mods> modrinthMods;
                public static class Mods{
                    public String versionId;
                    public String versionNumber;
                    public String projectReference;
                    public String name;
                }
            }
        }
        public static class CurseModPackParser{
            public CurseModPackInfo CurseModPackParserUrl(URL url) throws IOException {
                CurseModPackInfoJson objectMapper = new ObjectMapper().readValue(url, CurseModPackInfoJson.class);
                CurseModPackInfo result = null;
                if(objectMapper.projectID != 0 && objectMapper.fileID != 0 && objectMapper.url == null) {
                    result = new CurseModPackInfo(objectMapper.projectID,objectMapper.fileID, objectMapper.extFile, objectMapper.excluded);
                }else if(objectMapper.projectID == 0 && objectMapper.fileID == 0 && objectMapper.url != null){
                    result = new CurseModPackInfo(objectMapper.url, objectMapper.extFile,objectMapper.excluded);
                }else{
                    try {
                        throw new MalformedJsonException("json is malformed please read wiki");
                    } catch (MalformedJsonException e) {
                        Launcher.getInstance().getLogger().printStackTrace(e);
                        Launcher.getInstance().showErrorDialog(e);
                    }
                }
                return result;
            }
            public CurseModPackInfo CurseModPackParserJson(String json) throws JsonProcessingException {
                CurseModPackInfoJson objectMapper = new ObjectMapper().readValue(json, CurseModPackInfoJson.class);
                CurseModPackInfo result = null;
                if(objectMapper.projectID != 0 && objectMapper.fileID != 0 && objectMapper.url == null) {
                    result = new CurseModPackInfo(objectMapper.projectID,objectMapper.fileID, objectMapper.extFile, objectMapper.excluded);
                }else if(objectMapper.projectID == 0 && objectMapper.fileID == 0 && objectMapper.url != null){
                    result = new CurseModPackInfo(objectMapper.url, objectMapper.extFile, objectMapper.excluded);
                }else{
                    try {
                        throw new MalformedJsonException("json is malformed please read wiki");
                    } catch (MalformedJsonException e) {
                        Launcher.getInstance().getLogger().printStackTrace(e);
                        Launcher.getInstance().showErrorDialog(e);
                    }
                }
                return result;
            }
            public static class CurseModPackInfoJson{
                public int projectID;
                public int fileID;
                public String url;
                public boolean extFile;
                public String[] excluded;
            }
        }
        public static class ModrinthModpackParser{
            public ModrinthModPackInfo ModrinthModpackParserUrl(URL url) throws IOException {
                ModrinthPackInfoJson objectMapper = new ObjectMapper().readValue(url, ModrinthPackInfoJson.class);
                ModrinthModPackInfo result = null;
                if(objectMapper.versionId != null && objectMapper.versionNumber == null && objectMapper.projectReference == null) {
                    result = new ModrinthModPackInfo(objectMapper.versionId,objectMapper.extFile, objectMapper.excluded);
                }else if(objectMapper.versionId == null && objectMapper.versionNumber != null && objectMapper.projectReference != null){
                    result = new ModrinthModPackInfo(objectMapper.projectReference, objectMapper.versionNumber,objectMapper.extFile, objectMapper.excluded);
                }else{
                    try {
                        throw new MalformedJsonException("json is malformed please read wiki");
                    } catch (MalformedJsonException e) {
                        Launcher.getInstance().getLogger().printStackTrace(e);
                        Launcher.getInstance().showErrorDialog(e);
                    }
                }
                return result;
            }
            public ModrinthModPackInfo ModrinthModpackParserJson(String json) throws JsonProcessingException {
                ModrinthPackInfoJson objectMapper = new ObjectMapper().readValue(json, ModrinthPackInfoJson.class);
                ModrinthModPackInfo result = null;
                if(objectMapper.versionId != null && objectMapper.versionNumber == null && objectMapper.projectReference == null) {
                    result = new ModrinthModPackInfo(objectMapper.versionId,objectMapper.extFile, objectMapper.excluded);
                }else if(objectMapper.versionId == null && objectMapper.versionNumber != null && objectMapper.projectReference != null){
                    result = new ModrinthModPackInfo(objectMapper.projectReference, objectMapper.versionNumber,objectMapper.extFile, objectMapper.excluded);
                }else{
                    try {
                        throw new MalformedJsonException("json is malformed please read wiki");
                    } catch (MalformedJsonException e) {
                        Launcher.getInstance().getLogger().printStackTrace(e);
                        Launcher.getInstance().showErrorDialog(e);
                    }
                }
                return result;
            }

            public static class ModrinthPackInfoJson{
                public String projectReference;
                public String versionNumber;
                public String versionId;
                public boolean extFile;
                public String[] excluded;
            }
        }
    }
    public static class VersionParser{
        public VersionList getVersion(){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(new URL("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"), VersionList.class);
            } catch (Exception e) {
                Launcher.getInstance().showErrorDialog(e);
                Launcher.getInstance().getLogger().printStackTrace(e);
            }
            return null;
        }
    }
}