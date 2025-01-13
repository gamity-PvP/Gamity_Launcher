package fr.gamity.launcher.thomas260913.utils;

import fr.flowarg.azuljavadownloader.*;
import fr.flowarg.flowcompat.Platform;
import fr.gamity.launcher.thomas260913.Launcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;

public class JavaInstaller {
    private final Path javaDir;
    private final boolean setLauncherJavaVariable;
    private AzulJavaOS os;
    private AzulJavaArch arch;
    public JavaInstaller(Path javaDir, boolean setLauncherJavaVariable){
        this.javaDir = javaDir;
        this.setLauncherJavaVariable = setLauncherJavaVariable;
        switch(Platform.getCurrentPlatform()){
            case LINUX:
                this.os = AzulJavaOS.LINUX;
                break;
            case WINDOWS:
                this.os = AzulJavaOS.WINDOWS;
                break;
            default:
                Exception err = new IllegalStateException("unsupported os : " + Platform.getCurrentPlatform().name());
                Launcher.getInstance().getLogger().err("fatal error occured");
                Launcher.getInstance().getLogger().printStackTrace(err);
                Launcher.getInstance().getLogger().close();
                Launcher.getInstance().showErrorDialog(err);
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
                Launcher.getInstance().getLogger().err("fatal error occured");
                Launcher.getInstance().getLogger().printStackTrace(err);
                Launcher.getInstance().getLogger().close();
                Launcher.getInstance().showErrorDialog(err);
                System.exit(5);
        }
    }
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
                Launcher.getInstance().getLogger().err("fatal error occured");
                Launcher.getInstance().getLogger().printStackTrace(err);
                Launcher.getInstance().getLogger().close();
                Launcher.getInstance().showErrorDialog(err);
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
                Launcher.getInstance().getLogger().err("fatal error occured");
                Launcher.getInstance().getLogger().printStackTrace(err);
                Launcher.getInstance().getLogger().close();
                Launcher.getInstance().showErrorDialog(err);
                System.exit(5);
        }
        this.javaDir = javaDir;
        this.setLauncherJavaVariable = true;
    }
    public Path installJava(String version) throws IOException {
        Path javaPath;
        if(Objects.equals(version, "8")){
            final AzulJavaDownloader downloader = new AzulJavaDownloader(System.out::println);
            final AzulJavaBuildInfo buildInfoWindows = downloader.getBuildInfo(new RequestedJavaInfo("8", AzulJavaType.JRE, os, arch).setJavaFxBundled(true));
            javaPath = downloader.downloadAndInstall(buildInfoWindows, javaDir);
            if(setLauncherJavaVariable){
                Launcher.getInstance().setJava8(javaPath);
            }
        }else{
            final AzulJavaDownloader downloader = new AzulJavaDownloader(System.out::println);
            final AzulJavaBuildInfo buildInfoWindows = downloader.getBuildInfo(new RequestedJavaInfo(version, AzulJavaType.JRE, os, arch).setJavaFxBundled(false));
            javaPath = downloader.downloadAndInstall(buildInfoWindows, javaDir);
            if(setLauncherJavaVariable){
                switch (version) {
                    case "8":
                        Launcher.getInstance().setJava8(javaPath);
                        break;
                    case "17":
                        Launcher.getInstance().setJava17(javaPath);
                        break;
                    case "21":
                        Launcher.getInstance().setJava21(javaPath);
                        break;
                    default:
                        throw new NoSuchElementException("java " + version + " is unknow for this launcher or not configurate on this launcher\nplease contact admin to add if you have to get this version of java");
                }
            }
        }
        if(Platform.isOnLinux()){
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("chmod","+x",javaPath.resolve("bin").resolve("java").toAbsolutePath().toString());
            processBuilder.redirectErrorStream(true);
            processBuilder.start();
        }
        return javaPath;
    }
}
