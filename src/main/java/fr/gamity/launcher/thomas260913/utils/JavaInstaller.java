package fr.gamity.launcher.thomas260913.utils;

import fr.flowarg.azuljavadownloader.*;
import fr.gamity.launcher.thomas260913.Launcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;

public class JavaInstaller {
    private final Path javaDir;
    private final boolean setLauncherJavaVariable;
    public JavaInstaller(Path javaDir, boolean setLauncherJavaVariable){
        this.javaDir = javaDir;
        this.setLauncherJavaVariable = setLauncherJavaVariable;
    }
    public JavaInstaller(Path javaDir){
        this.javaDir = javaDir;
        this.setLauncherJavaVariable = true;
    }
    public Path installJava(String version) throws IOException {
        if(Objects.equals(version, "8")){
            final AzulJavaDownloader downloader = new AzulJavaDownloader(System.out::println);
            final AzulJavaBuildInfo buildInfoWindows = downloader.getBuildInfo(new RequestedJavaInfo("8", AzulJavaType.JRE, AzulJavaOS.WINDOWS, AzulJavaArch.X64).setJavaFxBundled(true));
            Path javaPath = downloader.downloadAndInstall(buildInfoWindows, javaDir);
            if(setLauncherJavaVariable){
                Launcher.getInstance().setJava8(javaPath);
            }
            return javaPath;
        }else{
            final AzulJavaDownloader downloader = new AzulJavaDownloader(System.out::println);
            final AzulJavaBuildInfo buildInfoWindows = downloader.getBuildInfo(new RequestedJavaInfo(version, AzulJavaType.JRE, AzulJavaOS.WINDOWS, AzulJavaArch.X64).setJavaFxBundled(false));
            Path javaPath = downloader.downloadAndInstall(buildInfoWindows, javaDir);
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
            return javaPath;
        }
    }
}
