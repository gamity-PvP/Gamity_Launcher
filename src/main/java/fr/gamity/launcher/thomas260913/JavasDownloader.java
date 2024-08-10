package fr.gamity.launcher.thomas260913;

import fr.flowarg.azuljavadownloader.AzulJavaBuildInfo;
import fr.flowarg.azuljavadownloader.AzulJavaDownloader;
import fr.flowarg.azuljavadownloader.AzulJavaType;
import fr.flowarg.azuljavadownloader.RequestedJavaInfo;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JavasDownloader {
    public JavasDownloader(String Version){
        try {
            switch (Version) {
                case "21":
                    Launcher.getInstance().getLogger().info("Starting download java 21 ...");
                    final AzulJavaDownloader downloader1 = new AzulJavaDownloader();
                    final Path javas1 = Paths.get(Launcher.getInstance().getLauncherDir().toFile().getAbsolutePath() + "/java"); // The directory where the Java versions will be downloaded.
                    final AzulJavaBuildInfo buildInfoWindows1 = downloader1.getBuildInfo(new RequestedJavaInfo("21", AzulJavaType.JRE, "windows", "x64", false)); // jdk 21 with javafx for Windows 64 bits
                    final Path javaHomeWindows1 = downloader1.downloadAndInstall(buildInfoWindows1, javas1);
                    Launcher.getInstance().setJava21(javaHomeWindows1);
                    Launcher.getInstance().getLogger().info("Finish download java 21");
                    break;
                case "17":
                    Launcher.getInstance().getLogger().info("Starting download java 17 ...");
                    final AzulJavaDownloader downloader2 = new AzulJavaDownloader();
                    final Path javas2 = Paths.get(Launcher.getInstance().getLauncherDir().toFile().getAbsolutePath() + "/java"); // The directory where the Java versions will be downloaded.
                    final AzulJavaBuildInfo buildInfoWindows2 = downloader2.getBuildInfo(new RequestedJavaInfo("17", AzulJavaType.JRE, "windows", "x64", false)); // jdk 17 with javafx for Windows 64 bits
                    final Path javaHomeWindows2 = downloader2.downloadAndInstall(buildInfoWindows2, javas2);
                    Launcher.getInstance().setJava17(javaHomeWindows2);
                    Launcher.getInstance().getLogger().info("Finish download java 17");
                    break;
                case "8":
                    Launcher.getInstance().getLogger().info("Starting download java 8 ...");
                    final AzulJavaDownloader downloader3 = new AzulJavaDownloader();
                    final Path javas3 = Paths.get(Launcher.getInstance().getLauncherDir().toFile().getAbsolutePath() + "/java"); // The directory where the Java versions will be downloaded.
                    final AzulJavaBuildInfo buildInfoWindows3 = downloader3.getBuildInfo(new RequestedJavaInfo("8", AzulJavaType.JRE, "windows", "x64", false)); // jdk 8 with javafx for Windows 64 bits
                    final Path javaHomeWindows3 = downloader3.downloadAndInstall(buildInfoWindows3, javas3);
                    Launcher.getInstance().setJava8(javaHomeWindows3);
                    Launcher.getInstance().getLogger().info("Finish download java 8");
                    break;
            }
        }catch(Exception ex){
            Launcher.getInstance().showErrorDialog(ex);
            Launcher.getInstance().getLogger().printStackTrace(ex);
        }
    }
}
