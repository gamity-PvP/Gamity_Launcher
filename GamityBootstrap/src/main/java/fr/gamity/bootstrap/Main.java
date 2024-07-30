package fr.gamity.bootstrap;

import fr.flowarg.azuljavadownloader.AzulJavaBuildInfo;
import fr.flowarg.azuljavadownloader.AzulJavaDownloader;
import fr.flowarg.azuljavadownloader.AzulJavaType;
import fr.flowarg.azuljavadownloader.RequestedJavaInfo;
import fr.flowarg.flowcompat.Platform;
import fr.flowarg.flowio.FileUtils;
import fr.theshark34.openlauncherlib.JavaUtil;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Main implements Runnable {
    private final Path launcher = Paths.get(Platform.isOnWindows() ? System.getenv("APPDATA") : (Platform.isOnMac() ? System.getProperty("user.home") + "/Library/Application Support/" : System.getProperty("user.home")), ".gamity/launcher.jar");

    private String launcherURL;
    private String launcherSha1;
    private String bootstrapVersion = "2.6";
    private final String latestVersionURL = "https://gamity-pvp.fr/apis/launcher/bootstrap/last_version";
    private String launcherInfoURL = "https://gamity-pvp.fr/apis/launcher/gamity_launcherinfo";
    private String bootstrapDownloadURL = "https://gamity-pvp.fr/apis/launcher/gamity.exe";
    private final Path launcherDir = GameDirGenerator.createGameDir("gamity", true);

    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        this.latestVersion();

        try {
        final AzulJavaDownloader downloader = new AzulJavaDownloader();
        final Path javas = Paths.get(this.launcherDir.toAbsolutePath() + "/java"); // The directory where the Java versions will be downloaded.
        final AzulJavaBuildInfo buildInfoWindows = downloader.getBuildInfo(new RequestedJavaInfo("8", AzulJavaType.JDK, "windows", "x64", true)); // jdk 17 with javafx for Windows 64 bits
        final Path javaHomeWindows = downloader.downloadAndInstall(buildInfoWindows, javas);
        JavaUtil.setJavaCommand(null);
        System.setProperty("java.home", javaHomeWindows.toAbsolutePath().toString());
        final Thread splash = new Thread(Splash::new);
        splash.setDaemon(true);
        splash.start();
        this.launcherInfo();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            if (Files.notExists(this.launcher) || !FileUtils.getSHA1(this.launcher).equalsIgnoreCase(this.launcherSha1)) {
                if (Files.notExists(this.launcher.getParent()))
                    Files.createDirectories(this.launcher.getParent());
                else if (Files.exists(this.launcher))
                    Files.deleteIfExists(this.launcher);
                verify();
            }else{
                ProcessBuilder processBuilder = new ProcessBuilder();
                final Path java = Paths.get(System.getProperty("java.home")).resolve("bin").resolve("java");
                processBuilder.command(java.toAbsolutePath().toString(), "-jar", this.launcher.toAbsolutePath().toString());
                processBuilder.start();
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }
    private void verify() throws IOException {
        System.out.println("choice = yes");
        Files.copy(new URL(this.launcherURL).openStream(), this.launcher, StandardCopyOption.REPLACE_EXISTING);
        ProcessBuilder processBuilder = new ProcessBuilder();
        final Path java = Paths.get(System.getProperty("java.home")).resolve("bin").resolve("java");
        processBuilder.command(java.toAbsolutePath().toString(), "-jar", this.launcher.toAbsolutePath().toString());
        processBuilder.start();
    }
    private void launcherInfo()
    {
        try {
            final URL url = new URL(launcherInfoURL);
            final String[] data = getContent(url).split("\\|");
            this.launcherURL = data[0];
            this.launcherSha1 = data[1];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void latestVersion()
    {
        try {
            final URL url = new URL(latestVersionURL);
            final String data = getContent(url);
            if(!data.equals(bootstrapVersion)){
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        "La version du bootstrap necessite une mise a jour\npour l'effectuer vous devez desinstaller le launcher\n(parametre>application>uninstall) vous devez ensuite\ntelecharger la derniere version du launcher\nvia https://gamity-pvp.fr/apis/launcher/gamity.exe\nvoulez vous telecharger la derniere version du launcher\net desinstaller automatiquement ?\nSi vous ne faites pas la màj vous ne pourrez plus lancé le launcher",
                        "Info",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    Path home = Paths.get(System.getProperty("user.home"));
                    Files.copy(new URL(bootstrapDownloadURL).openStream(), home.resolve("Desktop").resolve("gamity.exe"), StandardCopyOption.REPLACE_EXISTING);
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    final Path uninstall = Paths.get("C:", "Program Files", "Gamity Launcher", "unins000.exe");
                    processBuilder.command(uninstall.toAbsolutePath().toString());
                    processBuilder.start();
                    System.exit(0);
                } else {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static InputStream catchForbidden(URL url) throws Exception
    {
        final HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");
        connection.setInstanceFollowRedirects(true);
        return connection.getInputStream();
    }

    public static String getContent(URL url)
    {
        try
        {
            return getContent(catchForbidden(url));
        } catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    public static String getContent(InputStream remote)
    {
        final StringBuilder sb = new StringBuilder();

        try(InputStream stream = new BufferedInputStream(remote))
        {
            final ReadableByteChannel rbc = Channels.newChannel(stream);
            final Reader enclosedReader = Channels.newReader(rbc, StandardCharsets.UTF_8.newDecoder(), -1);
            final BufferedReader reader = new BufferedReader(enclosedReader);

            int character;
            while ((character = reader.read()) != -1) sb.append((char)character);

            reader.close();
            enclosedReader.close();
            rbc.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
