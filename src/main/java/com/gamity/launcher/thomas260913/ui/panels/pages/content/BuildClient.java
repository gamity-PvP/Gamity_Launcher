package com.gamity.launcher.thomas260913.ui.panels.pages.content;

import com.gamity.launcher.thomas260913.Launcher;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.json.ExternalFile;
import fr.flowarg.flowupdater.download.json.Mod;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.AbstractForgeVersion;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.JavaUtil;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;

import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;

public class BuildClient {
    public BuildClient(Config config, Path gameDir, IProgressCallback callback) throws Exception {
        final VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                .withName(config.mcinfo.mc.version)
                .build();
        switch (config.mcinfo.type){
            case "newforge":
                ForgeVersionType forgeVersionType = ForgeVersionType.NEW;
                List<Mod> mods = Mod.getModsFromJson(config.mcinfo.forge.mods);
                final AbstractForgeVersion forge = new ForgeVersionBuilder(forgeVersionType)
                        .withForgeVersion(config.mcinfo.forge.version)
                        .withMods(mods)
                        .withFileDeleter(new ModFileDeleter(true))
                        .build();
                final FlowUpdater updater;
                if(config.mcinfo.mc.extfiles != null && !config.mcinfo.mc.extfiles.startsWith("http")){

                    updater = new FlowUpdater.FlowUpdaterBuilder()
                            .withExternalFiles(ExternalFile.getExternalFilesFromJson(config.mcinfo.mc.extfiles))
                            .withVanillaVersion(vanillaVersion)
                            .withModLoaderVersion(forge)
                            .withLogger(Launcher.getInstance().getLogger())
                            .withProgressCallback(callback)
                            .build();

                }else {

                    updater = new FlowUpdater.FlowUpdaterBuilder()
                            .withVanillaVersion(vanillaVersion)
                            .withModLoaderVersion(forge)
                            .withLogger(Launcher.getInstance().getLogger())
                            .withProgressCallback(callback)
                            .build();

                }
                updater.update(gameDir);
                break;
            case "oldforge":
            case "very_oldforge":
                ForgeVersionType forgeVersionType2 = ForgeVersionType.OLD;
                List<Mod> mods2 = Mod.getModsFromJson(config.mcinfo.forge.mods);
                final AbstractForgeVersion forge2 = new ForgeVersionBuilder(forgeVersionType2)
                        .withForgeVersion(config.mcinfo.forge.version)
                        .withMods(mods2)
                        .withFileDeleter(new ModFileDeleter(true))
                        .build();
                final FlowUpdater updater2;
                if(config.mcinfo.mc.extfiles != null && !config.mcinfo.mc.extfiles.startsWith("http")){

                    updater2 = new FlowUpdater.FlowUpdaterBuilder()
                            .withExternalFiles(ExternalFile.getExternalFilesFromJson(config.mcinfo.mc.extfiles))
                            .withVanillaVersion(vanillaVersion)
                            .withModLoaderVersion(forge2)
                            .withLogger(Launcher.getInstance().getLogger())
                            .withProgressCallback(callback)
                            .build();

                }else {

                    updater2 = new FlowUpdater.FlowUpdaterBuilder()
                            .withVanillaVersion(vanillaVersion)
                            .withModLoaderVersion(forge2)
                            .withLogger(Launcher.getInstance().getLogger())
                            .withProgressCallback(callback)
                            .build();

                }
                updater2.update(gameDir);
                break;
            case "vanilla":
                final FlowUpdater updater3;
                if(config.mcinfo.mc.extfiles != null && config.mcinfo.mc.extfiles.startsWith("http")){
                    updater3 = new FlowUpdater.FlowUpdaterBuilder()
                            .withExternalFiles(ExternalFile.getExternalFilesFromJson(config.mcinfo.mc.extfiles))
                            .withVanillaVersion(vanillaVersion)
                            .withLogger(Launcher.getInstance().getLogger())
                            .withProgressCallback(callback)
                            .build();

                }else {
                    updater3 = new FlowUpdater.FlowUpdaterBuilder()
                            .withVanillaVersion(vanillaVersion)
                            .withLogger(Launcher.getInstance().getLogger())
                            .withProgressCallback(callback)
                            .build();

                }
                updater3.update(gameDir);
        }
    }
    public void startGame(Config config,String ram,Path gameDir) throws Exception {
        NoFramework noFramework = new NoFramework(
                gameDir,
                Launcher.getInstance().getAuthInfos(),
                GameFolder.FLOW_UPDATER
        );
        JavaUtil.setJavaCommand(null);
        switch(config.mcinfo.mc.java){
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
        switch (config.mcinfo.type){
            case "vanilla":
                noFramework.getAdditionalVmArgs().add(ram);
                if(config.mcinfo.autoconnect) {
                    noFramework.getAdditionalArgs().add("--server");
                    noFramework.getAdditionalArgs().add(config.mcinfo.server.ip);
                    if(config.mcinfo.server.port != null) {
                        noFramework.getAdditionalArgs().add("--port");
                        noFramework.getAdditionalArgs().add(config.mcinfo.server.port);
                    }
                }

                noFramework.setLastCallback(externalLauncher -> {
                    for (int i = 0; i < externalLauncher.getProfile().getArgs().size(); i++) {
                        final String arg = externalLauncher.getProfile().getArgs().get(i);
                        if(arg.contains("userProperties"))
                        {
                            externalLauncher.getProfile().getArgs().set(i + 1, "{}");
                            break;
                        }
                    }
                });
                noFramework.launch(config.mcinfo.mc.version, null, NoFramework.ModLoader.VANILLA);
                break;
            case "newforge":
                noFramework.getAdditionalVmArgs().add(ram);
                if(config.mcinfo.autoconnect) {
                    noFramework.getAdditionalArgs().add("--server");
                    noFramework.getAdditionalArgs().add(config.mcinfo.server.ip);
                    if(config.mcinfo.server.port != null) {
                        noFramework.getAdditionalArgs().add("--port");
                        noFramework.getAdditionalArgs().add(config.mcinfo.server.port);
                    }
                }
                NoFramework.ModLoader.FORGE.setJsonFileNameProvider((version, modLoaderVersion) -> modLoaderVersion.replaceAll("-","-forge-") + ".json");

                noFramework.setLastCallback(externalLauncher -> {
                    for (int i = 0; i < externalLauncher.getProfile().getArgs().size(); i++) {
                        final String arg = externalLauncher.getProfile().getArgs().get(i);
                        if(arg.contains("userProperties"))
                        {
                            externalLauncher.getProfile().getArgs().set(i + 1, "{}");
                            break;
                        }
                    }
                });

                noFramework.launch(config.mcinfo.mc.version, config.mcinfo.forge.version, NoFramework.ModLoader.FORGE);
                break;
            case "oldforge":
                noFramework.getAdditionalVmArgs().add(ram);
                if(config.mcinfo.autoconnect) {
                    noFramework.getAdditionalArgs().add("--server");
                    noFramework.getAdditionalArgs().add(config.mcinfo.server.ip);
                    if(config.mcinfo.server.port != null) {
                        noFramework.getAdditionalArgs().add("--port");
                        noFramework.getAdditionalArgs().add(config.mcinfo.server.port);
                    }
                }
                NoFramework.ModLoader.OLD_FORGE.setJsonFileNameProvider((version, modLoaderVersion) -> version + "-Forge" + modLoaderVersion + "-" + version + ".json");

                noFramework.setLastCallback(externalLauncher -> {
                    for (int i = 0; i < externalLauncher.getProfile().getArgs().size(); i++) {
                        final String arg = externalLauncher.getProfile().getArgs().get(i);
                        if(arg.contains("userProperties"))
                        {
                            externalLauncher.getProfile().getArgs().set(i + 1, "{}");
                            break;
                        }
                    }
                });
                noFramework.launch(config.mcinfo.mc.version, config.mcinfo.forge.version.split("-")[1], NoFramework.ModLoader.OLD_FORGE);
                break;
            case "very_oldforge":
                noFramework.getAdditionalVmArgs().add(ram);
                if(config.mcinfo.autoconnect) {
                    noFramework.getAdditionalArgs().add("--server");
                    noFramework.getAdditionalArgs().add(config.mcinfo.server.ip);
                    if(config.mcinfo.server.port != null) {
                        noFramework.getAdditionalArgs().add("--port");
                        noFramework.getAdditionalArgs().add(config.mcinfo.server.port);
                    }
                }
                NoFramework.ModLoader.VERY_OLD_FORGE.setJsonFileNameProvider((version, modLoaderVersion) -> version + "-Forge" + modLoaderVersion + "-" + version + ".json");

                noFramework.setLastCallback(externalLauncher -> {
                    for (int i = 0; i < externalLauncher.getProfile().getArgs().size(); i++) {
                        final String arg = externalLauncher.getProfile().getArgs().get(i);
                        if(arg.contains("userProperties"))
                        {
                            externalLauncher.getProfile().getArgs().set(i + 1, "{}");
                            break;
                        }
                    }
                });
                noFramework.launch(config.mcinfo.mc.version, config.mcinfo.forge.version.split("-")[1], NoFramework.ModLoader.VERY_OLD_FORGE);
                break;
        }
    }
}
