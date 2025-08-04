package fr.gamity.launcher.thomas260913.game;

import fr.flowarg.flowupdater.download.json.*;
import fr.flowarg.flowupdater.versions.fabric.FabricVersion;
import fr.flowarg.flowupdater.versions.fabric.FabricVersionBuilder;
import fr.flowarg.flowupdater.versions.forge.ForgeVersion;
import fr.flowarg.flowupdater.versions.forge.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.neoforge.NeoForgeVersion;
import fr.flowarg.flowupdater.versions.neoforge.NeoForgeVersionBuilder;
import fr.gamity.launcher.thomas260913.Launcher;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.JavaUtil;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;

import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class BuildClient {
    public BuildClient(Config.CustomServer config, Path gameDir, IProgressCallback callback, boolean optifineEnable) {
        try {
            final VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                    .withName(config.mcinfo.mc.version)
                    .build();

            FlowUpdater updater;
            List<Mod> mods = new ArrayList<>();
            List<CurseFileInfo> CurseMods = new ArrayList<>();
            List<ModrinthVersionInfo> ModrinthMods = new ArrayList<>();
            CurseModPackInfo CurseModPack = null;
            ModrinthModPackInfo ModrinthModPack = null;
            if (Objects.equals(config.mcinfo.type, "forge") || Objects.equals(config.mcinfo.type, "fabric") || Objects.equals(config.mcinfo.type, "neoforge")) {
                if(config.mcinfo.modLoader.mods != null) {
                    if (config.mcinfo.modLoader.mods.custom != null) {
                        if (!Objects.equals(config.mcinfo.modLoader.mods.custom.json, "") && config.mcinfo.modLoader.mods.custom.json != null) {
                            if (config.mcinfo.modLoader.mods.custom.json.startsWith("http")) {
                                mods = new Parser.ModsJsonParser.ModsParser().ModsParserUrl(new URL(config.mcinfo.modLoader.mods.custom.json));
                            } else if (config.mcinfo.modLoader.mods.custom.json.startsWith("{")) {
                                mods = new Parser.ModsJsonParser.ModsParser().ModsParserJson(config.mcinfo.modLoader.mods.custom.json);
                            }
                        }
                    }
                    if (config.mcinfo.modLoader.mods.curseForge != null) {
                        if (!Objects.equals(config.mcinfo.modLoader.mods.curseForge.json, "") && config.mcinfo.modLoader.mods.curseForge.json != null) {
                            if (config.mcinfo.modLoader.mods.curseForge.json.startsWith("http")) {
                                CurseMods = new Parser.ModsJsonParser.CurseParser().CurseParserUrl(new URL(config.mcinfo.modLoader.mods.curseForge.json));
                            } else if (config.mcinfo.modLoader.mods.curseForge.json.startsWith("{")) {
                                CurseMods = new Parser.ModsJsonParser.CurseParser().CurseParserJson(config.mcinfo.modLoader.mods.curseForge.json);
                            }
                        }
                    }
                    if (config.mcinfo.modLoader.mods.modrinthModpack != null) {
                        if (!Objects.equals(config.mcinfo.modLoader.mods.modrinthModpack.json, "") && config.mcinfo.modLoader.mods.modrinthModpack.json != null) {
                            if (config.mcinfo.modLoader.mods.modrinthModpack.json.startsWith("http")) {
                                ModrinthModPack = new Parser.ModsJsonParser.ModrinthModpackParser().ModrinthModpackParserUrl(new URL(config.mcinfo.modLoader.mods.modrinthModpack.json));
                            } else if (config.mcinfo.modLoader.mods.modrinthModpack.json.startsWith("{")) {
                                ModrinthModPack = new Parser.ModsJsonParser.ModrinthModpackParser().ModrinthModpackParserJson(config.mcinfo.modLoader.mods.modrinthModpack.json);
                            }
                        }
                    }
                    if (config.mcinfo.modLoader.mods.modrinth != null) {
                        if (!Objects.equals(config.mcinfo.modLoader.mods.modrinth.json, "") && config.mcinfo.modLoader.mods.modrinth.json != null) {
                            if (config.mcinfo.modLoader.mods.modrinth.json.startsWith("http")) {
                                ModrinthMods = new Parser.ModsJsonParser.ModrinthParser().ModrinthParserUrl(new URL(config.mcinfo.modLoader.mods.modrinth.json));
                            } else if (config.mcinfo.modLoader.mods.modrinth.json.startsWith("{")) {
                                ModrinthMods = new Parser.ModsJsonParser.ModrinthParser().ModrinthParserJson(config.mcinfo.modLoader.mods.modrinth.json);
                            }
                        }
                    }
                    if (config.mcinfo.modLoader.mods.curseForgeModpack != null) {
                        if (!Objects.equals(config.mcinfo.modLoader.mods.curseForgeModpack.json, "") && config.mcinfo.modLoader.mods.curseForgeModpack.json != null) {
                            if (config.mcinfo.modLoader.mods.curseForgeModpack.json.startsWith("http")) {
                                CurseModPack = new Parser.ModsJsonParser.CurseModPackParser().CurseModPackParserUrl(new URL(config.mcinfo.modLoader.mods.curseForgeModpack.json));
                            } else if (config.mcinfo.modLoader.mods.curseForgeModpack.json.startsWith("{")) {
                                CurseModPack = new Parser.ModsJsonParser.CurseModPackParser().CurseModPackParserJson(config.mcinfo.modLoader.mods.curseForgeModpack.json);
                            }
                        }
                    }
                }
                if (optifineEnable && config.mcinfo.modLoader.allowOptifine) {
                    if (!Objects.equals(config.mcinfo.modLoader.optifine.optifineVersion, "") && config.mcinfo.modLoader.optifine.optifineVersion != null) {
                        mods.add(new Parser.OptifineParser().OptifineRequestMod(config.mcinfo.modLoader.optifine.optifineVersion));
                    } else {
                        mods.add(new Parser.OptifineParser().OptifineRequestMod(config.mcinfo.mc.version));
                    }
                }
            }
            switch (config.mcinfo.type) {
                case "forge":
                    ForgeVersion forge = new ForgeVersionBuilder()
                            .withForgeVersion(config.mcinfo.modLoader.version)
                            .withMods(mods)
                            .withCurseMods(CurseMods)
                            .withModrinthMods(ModrinthMods)
                            .withCurseModPack(CurseModPack)
                            .withModrinthModPack(ModrinthModPack)
                            .withFileDeleter(new ModFileDeleter(true))
                            .build();
                    if (config.mcinfo.mc.extfiles != null && config.mcinfo.mc.extfiles.startsWith("http")) {
                        updater = new FlowUpdater.FlowUpdaterBuilder()
                                .withExternalFiles(ExternalFile.getExternalFilesFromJson(config.mcinfo.mc.extfiles))
                                .withVanillaVersion(vanillaVersion)
                                .withModLoaderVersion(forge)
                                .withLogger(Launcher.getInstance().getLogger())
                                .withProgressCallback(callback)
                                .build();
                    } else {
                        updater = new FlowUpdater.FlowUpdaterBuilder()
                                .withVanillaVersion(vanillaVersion)
                                .withModLoaderVersion(forge)
                                .withLogger(Launcher.getInstance().getLogger())
                                .withProgressCallback(callback)
                                .build();
                    }
                    break;
                case "neoforge":
                    NeoForgeVersion neoforge = new NeoForgeVersionBuilder()
                            .withNeoForgeVersion(config.mcinfo.modLoader.version.split("-")[1])
                            .withMods(mods)
                            .withCurseMods(CurseMods)
                            .withModrinthMods(ModrinthMods)
                            .withCurseModPack(CurseModPack)
                            .withModrinthModPack(ModrinthModPack)
                            .withFileDeleter(new ModFileDeleter(true))
                            .build();
                    if (config.mcinfo.mc.extfiles != null && config.mcinfo.mc.extfiles.startsWith("http")) {
                        updater = new FlowUpdater.FlowUpdaterBuilder()
                                .withExternalFiles(ExternalFile.getExternalFilesFromJson(config.mcinfo.mc.extfiles))
                                .withVanillaVersion(vanillaVersion)
                                .withModLoaderVersion(neoforge)
                                .withLogger(Launcher.getInstance().getLogger())
                                .withProgressCallback(callback)
                                .build();
                    } else {
                        updater = new FlowUpdater.FlowUpdaterBuilder()
                                .withVanillaVersion(vanillaVersion)
                                .withModLoaderVersion(neoforge)
                                .withLogger(Launcher.getInstance().getLogger())
                                .withProgressCallback(callback)
                                .build();
                    }
                    break;
                case "fabric":
                    FabricVersion fabric = new FabricVersionBuilder()
                            .withFabricVersion(config.mcinfo.modLoader.version.split("-")[1])
                            .withMods(mods)
                            .withCurseMods(CurseMods)
                            .withModrinthMods(ModrinthMods)
                            .withCurseModPack(CurseModPack)
                            .withModrinthModPack(ModrinthModPack)
                            .withFileDeleter(new ModFileDeleter(true))
                            .build();
                    if (config.mcinfo.mc.extfiles != null && config.mcinfo.mc.extfiles.startsWith("http")) {
                        updater = new FlowUpdater.FlowUpdaterBuilder()
                                .withExternalFiles(ExternalFile.getExternalFilesFromJson(config.mcinfo.mc.extfiles))
                                .withVanillaVersion(vanillaVersion)
                                .withModLoaderVersion(fabric)
                                .withLogger(Launcher.getInstance().getLogger())
                                .withProgressCallback(callback)
                                .build();
                    } else {
                        updater = new FlowUpdater.FlowUpdaterBuilder()
                                .withVanillaVersion(vanillaVersion)
                                .withModLoaderVersion(fabric)
                                .withLogger(Launcher.getInstance().getLogger())
                                .withProgressCallback(callback)
                                .build();
                    }
                    break;
                case "vanilla":
                    if (config.mcinfo.mc.extfiles != null && config.mcinfo.mc.extfiles.startsWith("http")) {
                        updater = new FlowUpdater.FlowUpdaterBuilder()
                                .withExternalFiles(ExternalFile.getExternalFilesFromJson(config.mcinfo.mc.extfiles))
                                .withVanillaVersion(vanillaVersion)
                                .withLogger(Launcher.getInstance().getLogger())
                                .withProgressCallback(callback)
                                .build();

                    } else {
                        updater = new FlowUpdater.FlowUpdaterBuilder()
                                .withVanillaVersion(vanillaVersion)
                                .withLogger(Launcher.getInstance().getLogger())
                                .withProgressCallback(callback)
                                .build();

                    }
                    break;
                case "snapshot":
                    final VanillaVersion vanillaVersion2 = new VanillaVersion.VanillaVersionBuilder()
                            .withName(config.mcinfo.mc.version)
                            .withSnapshot(true)
                            .build();
                    if (config.mcinfo.mc.extfiles != null && config.mcinfo.mc.extfiles.startsWith("http")) {
                        updater = new FlowUpdater.FlowUpdaterBuilder()
                                .withExternalFiles(ExternalFile.getExternalFilesFromJson(config.mcinfo.mc.extfiles))
                                .withVanillaVersion(vanillaVersion2)
                                .withLogger(Launcher.getInstance().getLogger())
                                .withProgressCallback(callback)
                                .build();

                    } else {
                        updater = new FlowUpdater.FlowUpdaterBuilder()
                                .withVanillaVersion(vanillaVersion2)
                                .withLogger(Launcher.getInstance().getLogger())
                                .withProgressCallback(callback)
                                .build();
                    }
                default:
                    throw new NoSuchElementException("Loader " + config.mcinfo.type + " is unknow for this launcher\nplease contact admin to add it");
            }
            updater.update(gameDir);
        }catch (Exception e){
            Launcher.getInstance().showErrorDialog(e);
            Launcher.getInstance().getLogger().printStackTrace(e);
        }
    }
    public void startGame(Config.CustomServer config,String ram,Path gameDir) {
        try {
            NoFramework noFramework = new NoFramework(
                    gameDir,
                    Launcher.getInstance().getMCAccount().getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );
            if(Launcher.getInstance().getMCAccount().isCrack()){
                noFramework.getAdditionalVmArgs().add("-DauthServer=0.0.0.0");
            }
            noFramework.setServerName(config.name);
            JavaUtil.setJavaCommand(null);
            switch (config.mcinfo.mc.java) {
                case "21":
                    System.setProperty("java.home", Launcher.getInstance().getJava21().toAbsolutePath().toString());
                    break;
                case "17":
                    System.setProperty("java.home", Launcher.getInstance().getJava17().toAbsolutePath().toString());
                    break;
                case "8":
                    System.setProperty("java.home", Launcher.getInstance().getJava8().toAbsolutePath().toString());
                    break;
                default:
                    throw new NoSuchElementException("java " + config.mcinfo.mc.java + " is unknow for this launcher\nplease contact admin to add if you have to get this version of java");
            }
            switch (config.mcinfo.type) {
                case "vanilla":
                    noFramework.getAdditionalVmArgs().add(ram);
                    if (config.mcinfo.autoconnect) {
                        if(Integer.parseInt(config.mcinfo.mc.version.split("\\.")[1]) > 20 || (Integer.parseInt(config.mcinfo.mc.version.split("\\.")[1]) == 20 && !Objects.equals(config.mcinfo.mc.version, "1.20.1"))) {
                            noFramework.getAdditionalArgs().addAll(Arrays.asList("--quickPlayMultiplayer", config.mcinfo.server.ip + ":" + (!Objects.equals(config.mcinfo.server.port, "") ? config.mcinfo.server.port : "25565")));
                        }else{
                            noFramework.getAdditionalArgs().addAll(Arrays.asList("--server", config.mcinfo.server.ip, "--port", !Objects.equals(config.mcinfo.server.port, "") ? config.mcinfo.server.port : "25565"));
                        }
                    }

                    noFramework.launch(config.mcinfo.mc.version, null, NoFramework.ModLoader.VANILLA);
                    break;
                case "neoforge":
                    noFramework.getAdditionalVmArgs().add(ram);
                    if (config.mcinfo.autoconnect) {
                        noFramework.getAdditionalArgs().addAll(Arrays.asList("--quickPlayMultiplayer", config.mcinfo.server.ip + ":" + (!Objects.equals(config.mcinfo.server.port, "") ? config.mcinfo.server.port : "25565")));
                    }
                    noFramework.launch(config.mcinfo.mc.version, config.mcinfo.modLoader.version.split("-")[1], NoFramework.ModLoader.NEO_FORGE);
                    break;
                case "fabric":
                    noFramework.getAdditionalVmArgs().add(ram);
                    if (config.mcinfo.autoconnect) {
                        if(Integer.parseInt(config.mcinfo.mc.version.split("\\.")[1]) > 20 || (Integer.parseInt(config.mcinfo.mc.version.split("\\.")[1]) == 20 && !Objects.equals(config.mcinfo.mc.version, "1.20.1"))) {
                            noFramework.getAdditionalArgs().addAll(Arrays.asList("--quickPlayMultiplayer", config.mcinfo.server.ip + ":" + (!Objects.equals(config.mcinfo.server.port, "") ? config.mcinfo.server.port : "25565")));
                        }else{
                            noFramework.getAdditionalArgs().addAll(Arrays.asList("--server", config.mcinfo.server.ip, "--port", !Objects.equals(config.mcinfo.server.port, "") ? config.mcinfo.server.port : "25565"));
                        }
                    }
                    noFramework.launch(config.mcinfo.mc.version, config.mcinfo.modLoader.version.split("-")[1], NoFramework.ModLoader.FABRIC);
                    break;
                case "forge":
                    String forgeType = getForgeType(config.mcinfo.modLoader.version);
                    switch (forgeType) {
                        case "newforge":
                            noFramework.getAdditionalVmArgs().add(ram);
                            if (config.mcinfo.autoconnect) {
                                if(Integer.parseInt(config.mcinfo.mc.version.split("\\.")[1]) > 20 || (Integer.parseInt(config.mcinfo.mc.version.split("\\.")[1]) == 20 && !Objects.equals(config.mcinfo.mc.version, "1.20.1"))) {
                                    noFramework.getAdditionalArgs().addAll(Arrays.asList("--quickPlayMultiplayer", config.mcinfo.server.ip + ":" + (!Objects.equals(config.mcinfo.server.port, "") ? config.mcinfo.server.port : "25565")));
                                }else{
                                    noFramework.getAdditionalArgs().addAll(Arrays.asList("--server", config.mcinfo.server.ip, "--port", !Objects.equals(config.mcinfo.server.port, "") ? config.mcinfo.server.port : "25565"));
                                }
                            }
                            NoFramework.ModLoader.FORGE.setJsonFileNameProvider((version, modLoaderVersion) -> modLoaderVersion.replaceAll("-", "-forge-") + ".json");
                            noFramework.launch(config.mcinfo.mc.version, config.mcinfo.modLoader.version, NoFramework.ModLoader.FORGE);
                            break;
                        case "oldforge":
                            noFramework.getAdditionalVmArgs().add(ram);
                            if (config.mcinfo.autoconnect) {
                                    noFramework.getAdditionalArgs().addAll(Arrays.asList("--server", config.mcinfo.server.ip, "--port", !Objects.equals(config.mcinfo.server.port, "") ? config.mcinfo.server.port : "25565"));
                            }
                            NoFramework.ModLoader.OLD_FORGE.setJsonFileNameProvider((version, modLoaderVersion) -> version + "-Forge" + modLoaderVersion + "-" + version + ".json");
                            noFramework.launch(config.mcinfo.mc.version, config.mcinfo.modLoader.version.split("-")[1], NoFramework.ModLoader.OLD_FORGE);
                            break;
                        case "very_oldforge":
                            noFramework.getAdditionalVmArgs().add(ram);
                            if (config.mcinfo.autoconnect) {
                                    noFramework.getAdditionalArgs().addAll(Arrays.asList("--server", config.mcinfo.server.ip, "--port", !Objects.equals(config.mcinfo.server.port, "") ? config.mcinfo.server.port : "25565"));
                            }
                            NoFramework.ModLoader.VERY_OLD_FORGE.setJsonFileNameProvider((version, modLoaderVersion) -> version + "-Forge" + modLoaderVersion + "-" + version + ".json");
                            noFramework.launch(config.mcinfo.mc.version, config.mcinfo.modLoader.version.split("-")[1], NoFramework.ModLoader.VERY_OLD_FORGE);
                            break;
                    }
                    break;
            }
        }catch(Exception e){
            Launcher.getInstance().showErrorDialog(e);
            Launcher.getInstance().getLogger().printStackTrace(e);
        }
    }
    private static String getForgeType(String version){
        String[] data = version.split("-");
        String mcVersion = data[0];
        String forgeVersion = data[1];
        String[] mcVersionSplit = mcVersion.split("\\.");
        if(Integer.parseInt(mcVersionSplit[1]) > 12){
            return "newforge";
        }else if(Integer.parseInt(mcVersionSplit[1]) < 12){
            if(Integer.parseInt(mcVersionSplit[1]) != 7){
                return "oldforge";
            }else{
                return "very_oldforge";
            }
        }else{
            String[] forgeVersionSplit = forgeVersion.split("\\.");
            if(Integer.parseInt(forgeVersionSplit[3]) >= 2851){
                return "newforge";
            }else{
                return "oldforge";
            }
        }
    }
}