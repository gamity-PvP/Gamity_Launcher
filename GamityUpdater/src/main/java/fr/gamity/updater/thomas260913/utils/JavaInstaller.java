package fr.gamity.updater.thomas260913.utils;

import fr.flowarg.azuljavadownloader.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class JavaInstaller {
    private Path javaDir;
    public JavaInstaller(Path javaDir){
        this.javaDir = javaDir;
    }
    public Path installJava(String version) throws IOException {
        if(Objects.equals(version, "8")){
            final AzulJavaDownloader downloader = new AzulJavaDownloader(System.out::println);
            final AzulJavaBuildInfo buildInfoWindows = downloader.getBuildInfo(new RequestedJavaInfo("8", AzulJavaType.JRE, AzulJavaOS.WINDOWS, AzulJavaArch.X64).setJavaFxBundled(true));
            return downloader.downloadAndInstall(buildInfoWindows, javaDir);
        }else{
            final AzulJavaDownloader downloader = new AzulJavaDownloader(System.out::println);
            final AzulJavaBuildInfo buildInfoWindows = downloader.getBuildInfo(new RequestedJavaInfo(version, AzulJavaType.JRE, AzulJavaOS.WINDOWS, AzulJavaArch.X64).setJavaFxBundled(false));
            return downloader.downloadAndInstall(buildInfoWindows, javaDir);
        }
    }
}
