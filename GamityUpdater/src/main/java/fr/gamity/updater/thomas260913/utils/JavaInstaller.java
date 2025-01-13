package fr.gamity.updater.thomas260913.utils;

import fr.flowarg.azuljavadownloader.*;
import fr.flowarg.flowcompat.Platform;
import fr.gamity.updater.thomas260913.Updater;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class JavaInstaller {
    private final Path javaDir;
    private AzulJavaOS os;
    private AzulJavaArch arch;

    public JavaInstaller(Path javaDir){
        switch(Platform.getCurrentPlatform()){
            case LINUX:
                this.os = AzulJavaOS.LINUX;
                break;
            case WINDOWS:
                this.os = AzulJavaOS.WINDOWS;
                break;
            default:
                Exception err = new IllegalStateException("unsupported os : " + Platform.getCurrentPlatform().name());
                Updater.getInstance().getLogger().err("fatal error occured");
                Updater.getInstance().getLogger().printStackTrace(err);
                Updater.getInstance().getLogger().close();
                Updater.getInstance().showErrorDialog(err);
                System.exit(5);

        }
        switch(Platform.getArch()){
            case "64":
                arch = AzulJavaArch.X64;
                break;
            case "32":
                arch = AzulJavaArch.X86;
                break;
            default:
                Exception err = new IllegalStateException("unsupported architecture : " + Platform.getArch());
                Updater.getInstance().getLogger().err("fatal error occured");
                Updater.getInstance().getLogger().printStackTrace(err);
                Updater.getInstance().getLogger().close();
                Updater.getInstance().showErrorDialog(err);
                System.exit(5);
        }
        this.javaDir = javaDir;
    }
    public Path installJava(String version) throws IOException {
        Path javaPath;
        final AzulJavaDownloader downloader = new AzulJavaDownloader(System.out::println);
        final AzulJavaBuildInfo buildInfo;
        if(Objects.equals(version, "8")){
            buildInfo = downloader.getBuildInfo(new RequestedJavaInfo("8", AzulJavaType.JRE, os, arch).setJavaFxBundled(true));
        }else{
            buildInfo = downloader.getBuildInfo(new RequestedJavaInfo(version, AzulJavaType.JRE, os, arch).setJavaFxBundled(false));
        }
        javaPath = downloader.downloadAndInstall(buildInfo, javaDir);
        if(Platform.isOnLinux()){
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("chmod","+x",javaPath.resolve("bin").resolve("java").toAbsolutePath().toString());
            processBuilder.redirectErrorStream(true);
            processBuilder.start();
        }
        return javaPath;
    }
}