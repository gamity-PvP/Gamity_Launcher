package fr.gamity.installer;

import fr.flowarg.flowcompat.Platform;
import fr.gamity.installer.utils.JavaInstaller;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.Saver;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Comparator;


public class Main implements Runnable {
    private final Path launcherDir = GameDirGenerator.createGameDir("gamity", true);
    private Path updater;
    private Path storageFile;
    private Saver saver;
    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        try {
            saver = new Saver(this.launcherDir.resolve("config.properties"));
            saver.load();
            if(saver.get("oldFilesPath") != null){
                moveFiles(Paths.get(saver.get("oldFilesPath")),Paths.get(saver.get("filesPath")));
                saver.remove("oldFilesPath");
            }
            if (saver.get("weblink") == null) {
                saver.set("weblink", "https://gamity-pvp.fr");
                saver.save();
            }
            String updaterURL = saver.get("weblink") + "/apis/launcher/updater.jar";
            String filesPath = saver.get("filesPath");

            if (filesPath == null || filesPath.isEmpty()) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Sélectionnez un dossier");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setCurrentDirectory(this.launcherDir.toFile());

                int result = chooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    filesPath = chooser.getSelectedFile().getAbsolutePath();
                    if(!Paths.get(filesPath).toAbsolutePath().toString().equals(this.launcherDir.toAbsolutePath().toString())){
                        moveFiles(this.launcherDir,Paths.get(filesPath));
                    }
                    // Sauvegarder le chemin pour éviter de redemander plus tard
                    saver.set("filesPath", filesPath);
                    saver.save();
                } else {
                    // L'utilisateur a annulé
                    System.out.println("Aucun dossier sélectionné");
                    return;
                }
            }

            if(!Paths.get(filesPath).toAbsolutePath().toString().equals(this.launcherDir.toAbsolutePath().toString())){
                storageFile = Paths.get(filesPath).resolve("gamity");
            }else{
                storageFile = this.launcherDir.toAbsolutePath();
            }
            updater = storageFile.resolve("updater").resolve("updater.jar");
            JavaInstaller javaInstaller = new JavaInstaller(storageFile.resolve("java"));
            if (Files.notExists(this.updater)) {
                if (Files.notExists(this.updater.getParent()))
                    Files.createDirectories(this.updater.getParent());
                Files.copy(new URL(updaterURL).openStream(), this.updater, StandardCopyOption.REPLACE_EXISTING);
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
            processBuilder.command(javas, "-jar", this.updater.toAbsolutePath().toString(), "--installerVersion","4.2.1");
            processBuilder.start();
        }catch (Exception e) {
                e.printStackTrace();
        }
    }
    public static void moveFiles(Path oldDir, Path newDir) {
        try {
            Files.createDirectories(newDir);

            // 1. déplacer tout
            Files.walk(oldDir)
                    .forEach(source -> {
                        try {
                            if(source.toFile().isFile()){
                                if(!source.getFileName().toString().equals("config.properties")) {
                                    Path relative = oldDir.relativize(source);
                                    Path target = newDir.resolve(relative);

                                    Files.createDirectories(target.getParent());

                                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            // 2. supprimer l’ancien dossier (maintenant vide)
            Files.walk(oldDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            if(!path.getFileName().toString().equals("config.properties")) {
                                Files.deleteIfExists(path);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            System.out.println("Déplacement terminé");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}