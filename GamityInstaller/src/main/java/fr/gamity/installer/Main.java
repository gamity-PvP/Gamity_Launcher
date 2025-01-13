package fr.gamity.installer;

import fr.flowarg.flowcompat.Platform;
import fr.gamity.installer.utils.JavaInstaller;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;

import java.net.URL;
import java.nio.file.*;


public class Main implements Runnable {
    private final Path launcherDir = GameDirGenerator.createGameDir("gamity", true);
    private final Path updater = launcherDir.resolve("updater").resolve("updater.jar");
    private final String updaterURL = "https://gamity-pvp.fr/apis/launcher/updater.jar";

    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        try {
            JavaInstaller javaInstaller = new JavaInstaller(this.launcherDir.resolve("java"));
            if (Files.notExists(this.updater)) {
                if (Files.notExists(this.updater.getParent()))
                    Files.createDirectories(this.updater.getParent());
                Files.copy(new URL(this.updaterURL).openStream(), this.updater, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("download java 8 fx Jre");
            ProcessBuilder processBuilder = new ProcessBuilder();
            String javas;
            if(Platform.getCurrentPlatform() == Platform.EnumOS.WINDOWS) {
                javas = javaInstaller.installJava("8").resolve("bin").resolve("java.exe").toAbsolutePath().toString();
            }else if(Platform.getCurrentPlatform() == Platform.EnumOS.LINUX){
                javas = javaInstaller.installJava("8").resolve("bin").resolve("java").toAbsolutePath().toString();;
            }else{
                javas = "";
                Exception err = new IllegalStateException("unsupported os : " + Platform.getCurrentPlatform().name());
                err.printStackTrace();
                System.exit(5);
            }
            processBuilder.command(javas, "-jar", this.updater.toAbsolutePath().toString());
            processBuilder.start();
        }catch (Exception e) {
                e.printStackTrace();
        }
    }

}