package fr.gamity.launcher.thomas260913;

import fr.flowarg.azuljavadownloader.AzulJavaBuildInfo;
import fr.flowarg.azuljavadownloader.AzulJavaDownloader;
import fr.flowarg.azuljavadownloader.AzulJavaType;
import fr.flowarg.azuljavadownloader.RequestedJavaInfo;
import fr.gamity.launcher.thomas260913.ui.panels.pages.Splash;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static fr.flowarg.flowzipper.ZipUtils.unzip;

public class JavasDownloader {
    public JavasDownloader(String Version){
        try {
            switch (Version) {
                case "21":
                    Launcher.getInstance().getLogger().info("Starting download java 21 ...");
                    if(Files.notExists(Launcher.getInstance().getLauncherDir().resolve("java").resolve("java21"))) {
                        if (Files.notExists(Launcher.getInstance().getLauncherDir().resolve("tmp"))) {
                            Files.createDirectory(Launcher.getInstance().getLauncherDir().resolve("tmp"));
                        }
                        Splash.downloadFile("https://gamity-pvp.fr/download/java/JDK_FX/javafx21.zip", Launcher.getInstance().getLauncherDir().resolve("tmp").resolve("jdkfx21.zip").toAbsolutePath().toString());
                        unzip(Launcher.getInstance().getLauncherDir().resolve("jdkfx21.zip"), Launcher.getInstance().getLauncherDir().resolve("java").resolve("java21"));
                    }
                    Launcher.getInstance().setJava21(Launcher.getInstance().getLauncherDir().resolve("java").resolve("java21"));
                    Launcher.getInstance().getLogger().info("Finish download java 21");
                    break;
                case "17":
                    Launcher.getInstance().getLogger().info("Starting download java 17 ...");
                    if(Files.notExists(Launcher.getInstance().getLauncherDir().resolve("java").resolve("java17"))) {
                        if (Files.notExists(Launcher.getInstance().getLauncherDir().resolve("tmp"))) {
                            Files.createDirectory(Launcher.getInstance().getLauncherDir().resolve("tmp"));
                        }
                        Splash.downloadFile("https://gamity-pvp.fr/download/java/JDK_FX/javafx17.zip", Launcher.getInstance().getLauncherDir().resolve("tmp").resolve("jdkfx17.zip").toAbsolutePath().toString());
                        unzip(Launcher.getInstance().getLauncherDir().resolve("tmp").resolve("jdkfx17.zip"), Launcher.getInstance().getLauncherDir().resolve("java").resolve("java21"));
                    }
                    Launcher.getInstance().setJava17(Launcher.getInstance().getLauncherDir().resolve("java").resolve("java17"));
                    Launcher.getInstance().getLogger().info("Finish download java 17");
                    break;
                case "8":
                    Launcher.getInstance().getLogger().info("Starting download java 8 ...");
                    if(Files.notExists(Launcher.getInstance().getLauncherDir().resolve("java").resolve("java8"))) {
                        if (Files.notExists(Launcher.getInstance().getLauncherDir().resolve("tmp"))) {
                            Files.createDirectory(Launcher.getInstance().getLauncherDir().resolve("tmp"));
                        }
                        Splash.downloadFile("https://gamity-pvp.fr/download/java/JDK_FX/javafx17.zip", Launcher.getInstance().getLauncherDir().resolve("tmp").resolve("jdkfx17.zip").toAbsolutePath().toString());
                        unzip(Launcher.getInstance().getLauncherDir().resolve("tmp").resolve("jdkfx17.zip"), Launcher.getInstance().getLauncherDir().resolve("java").resolve("java21"));
                    }
                    Launcher.getInstance().setJava8(Launcher.getInstance().getLauncherDir().resolve("java").resolve("java17"));
                    Launcher.getInstance().getLogger().info("Finish download java 8");
                    break;
            }
        }catch(Exception ex){
            Launcher.getInstance().showErrorDialog(ex);
            Launcher.getInstance().getLogger().printStackTrace(ex);
        }
    }
}
