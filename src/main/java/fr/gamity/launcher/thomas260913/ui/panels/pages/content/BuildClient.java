package fr.gamity.launcher.thomas260913.ui.panels.pages.content;

import fr.flowarg.flowupdater.download.json.*;
import fr.flowarg.flowupdater.versions.forge.ForgeVersion;
import fr.flowarg.flowupdater.versions.forge.ForgeVersionBuilder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class BuildClient {
    public BuildClient(Config.CustomServer config, Path gameDir, IProgressCallback callback, boolean optifineEnable) {
        try {
            final VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                    .withName(config.mcinfo.mc.version)
                    .withSnapshot(config.mcinfo.type == "snapshot")
                    .build();

            FlowUpdater updater;
            List<Mod> mods = new ArrayList<>();
            List<CurseFileInfo> CurseMods = new ArrayList<>();
            List<ModrinthVersionInfo> ModrinthMods = new ArrayList<>();
            CurseModPackInfo CurseModPack = null;
            ModrinthModPackInfo ModrinthModPack = null;
            if (Objects.equals(config.mcinfo.type, "forge")) {
                if(config.mcinfo.forge.mods.custom.json != null) {
                    if (config.mcinfo.forge.mods.custom.json.startsWith("http")) {
                        mods = new Parser.ModsJsonParser.ModsParser().ModsParserUrl(new URL(config.mcinfo.forge.mods.custom.json));
                    } else if (config.mcinfo.forge.mods.custom.json.startsWith("{")) {
                        mods = new Parser.ModsJsonParser.ModsParser().ModsParserJson(config.mcinfo.forge.mods.custom.json);
                    }
                }
                if(config.mcinfo.forge.mods.curseForge.json != null) {
                    if (config.mcinfo.forge.mods.curseForge.json.startsWith("http")) {
                        CurseMods = new Parser.ModsJsonParser.CurseParser().CurseParserUrl(new URL(config.mcinfo.forge.mods.curseForge.json));
                    } else if (config.mcinfo.forge.mods.curseForge.json.startsWith("{")) {
                        CurseMods = new Parser.ModsJsonParser.CurseParser().CurseParserJson(config.mcinfo.forge.mods.curseForge.json);
                    }
                }
                if(config.mcinfo.forge.mods.modrinthModpack.json != null) {
                    if (config.mcinfo.forge.mods.modrinthModpack.json.startsWith("http")) {
                        ModrinthModPack = new Parser.ModsJsonParser.ModrinthModpackParser().ModrinthModpackParserUrl(new URL(config.mcinfo.forge.mods.modrinthModpack.json));
                    } else if (config.mcinfo.forge.mods.modrinthModpack.json.startsWith("{")) {
                        ModrinthModPack = new Parser.ModsJsonParser.ModrinthModpackParser().ModrinthModpackParserJson(config.mcinfo.forge.mods.modrinthModpack.json);
                    }
                }
                if(config.mcinfo.forge.mods.modrinth.json != null) {
                    if (config.mcinfo.forge.mods.modrinth.json.startsWith("http")) {
                        ModrinthMods = new Parser.ModsJsonParser.ModrinthParser().ModrinthParserUrl(new URL(config.mcinfo.forge.mods.modrinth.json));
                    } else if (config.mcinfo.forge.mods.modrinth.json.startsWith("{")) {
                        ModrinthMods = new Parser.ModsJsonParser.ModrinthParser().ModrinthParserJson(config.mcinfo.forge.mods.modrinth.json);
                    }
                }
                if(config.mcinfo.forge.mods.curseForgeModpack.json != null) {
                    if (config.mcinfo.forge.mods.curseForgeModpack.json.startsWith("http")) {
                        CurseModPack = new Parser.ModsJsonParser.CurseModPackParser().CurseModPackParserUrl(new URL(config.mcinfo.forge.mods.curseForgeModpack.json));
                    } else if (config.mcinfo.forge.mods.curseForgeModpack.json.startsWith("{")) {
                        CurseModPack = new Parser.ModsJsonParser.CurseModPackParser().CurseModPackParserJson(config.mcinfo.forge.mods.curseForgeModpack.json);
                    }
                }
                if (optifineEnable && config.mcinfo.forge.allowOptifine) {
                    if (config.mcinfo.forge.optifine.optifineVersion != null) {
                        mods.add(new Parser.OptifineParser().OptifineRequestMod(config.mcinfo.forge.optifine.optifineVersion));
                    } else {
                        mods.add(new Parser.OptifineParser().OptifineRequestMod(config.mcinfo.mc.version));
                    }
                }
            }
            switch (config.mcinfo.type) {
                case "forge":
                    ForgeVersion forge = new ForgeVersionBuilder()
                            .withForgeVersion(config.mcinfo.forge.version)
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
                    Launcher.getInstance().getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );
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
                        noFramework.getAdditionalArgs().add("--server");
                        noFramework.getAdditionalArgs().add(config.mcinfo.server.ip);
                        if (config.mcinfo.server.port != null) {
                            noFramework.getAdditionalArgs().add("--port");
                            noFramework.getAdditionalArgs().add(config.mcinfo.server.port);
                        }
                    }

                    noFramework.setLastCallback(externalLauncher -> {
                        for (int i = 0; i < externalLauncher.getProfile().getArgs().size(); i++) {
                            final String arg = externalLauncher.getProfile().getArgs().get(i);
                            if (arg.contains("userProperties")) {
                                externalLauncher.getProfile().getArgs().set(i + 1, "{}");
                                break;
                            }
                        }
                    });
                    noFramework.launch(config.mcinfo.mc.version, null, NoFramework.ModLoader.VANILLA);
                    break;
                case "forge":
                    String forgeType = getForgeType(config.mcinfo.forge.version);
                    switch (forgeType) {
                        case "newforge":
                            noFramework.getAdditionalVmArgs().add(ram);
                            if (config.mcinfo.autoconnect) {
                                noFramework.getAdditionalArgs().add("--server");
                                noFramework.getAdditionalArgs().add(config.mcinfo.server.ip);
                                if (config.mcinfo.server.port != null) {
                                    noFramework.getAdditionalArgs().add("--port");
                                    noFramework.getAdditionalArgs().add(config.mcinfo.server.port);
                                }
                            }
                            NoFramework.ModLoader.FORGE.setJsonFileNameProvider((version, modLoaderVersion) -> modLoaderVersion.replaceAll("-", "-forge-") + ".json");

                            noFramework.setLastCallback(externalLauncher -> {
                                for (int i = 0; i < externalLauncher.getProfile().getArgs().size(); i++) {
                                    final String arg = externalLauncher.getProfile().getArgs().get(i);
                                    if (arg.contains("userProperties")) {
                                        externalLauncher.getProfile().getArgs().set(i + 1, "{}");
                                        break;
                                    }
                                }
                            });
                            noFramework.launch(config.mcinfo.mc.version, config.mcinfo.forge.version, NoFramework.ModLoader.FORGE);
                            break;
                        case "oldforge":
                            noFramework.getAdditionalVmArgs().add(ram);
                            if (config.mcinfo.autoconnect) {
                                noFramework.getAdditionalArgs().add("--server");
                                noFramework.getAdditionalArgs().add(config.mcinfo.server.ip);
                                if (config.mcinfo.server.port != null) {
                                    noFramework.getAdditionalArgs().add("--port");
                                    noFramework.getAdditionalArgs().add(config.mcinfo.server.port);
                                }
                            }
                            NoFramework.ModLoader.OLD_FORGE.setJsonFileNameProvider((version, modLoaderVersion) -> version + "-Forge" + modLoaderVersion + "-" + version + ".json");

                            noFramework.setLastCallback(externalLauncher -> {
                                for (int i = 0; i < externalLauncher.getProfile().getArgs().size(); i++) {
                                    final String arg = externalLauncher.getProfile().getArgs().get(i);
                                    if (arg.contains("userProperties")) {
                                        externalLauncher.getProfile().getArgs().set(i + 1, "{}");
                                        break;
                                    }
                                }
                            });
                            noFramework.launch(config.mcinfo.mc.version, config.mcinfo.forge.version.split("-")[1], NoFramework.ModLoader.OLD_FORGE);
                            break;
                        case "very_oldforge":
                            noFramework.getAdditionalVmArgs().add(ram);
                            if (config.mcinfo.autoconnect) {
                                noFramework.getAdditionalArgs().add("--server");
                                noFramework.getAdditionalArgs().add(config.mcinfo.server.ip);
                                if (config.mcinfo.server.port != null) {
                                    noFramework.getAdditionalArgs().add("--port");
                                    noFramework.getAdditionalArgs().add(config.mcinfo.server.port);
                                }
                            }
                            NoFramework.ModLoader.VERY_OLD_FORGE.setJsonFileNameProvider((version, modLoaderVersion) -> version + "-Forge" + modLoaderVersion + "-" + version + ".json");

                            noFramework.setLastCallback(externalLauncher -> {
                                for (int i = 0; i < externalLauncher.getProfile().getArgs().size(); i++) {
                                    final String arg = externalLauncher.getProfile().getArgs().get(i);
                                    if (arg.contains("userProperties")) {
                                        externalLauncher.getProfile().getArgs().set(i + 1, "{}");
                                        break;
                                    }
                                }
                            });
                            noFramework.launch(config.mcinfo.mc.version, config.mcinfo.forge.version.split("-")[1], NoFramework.ModLoader.VERY_OLD_FORGE);
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