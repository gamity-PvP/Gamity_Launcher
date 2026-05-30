package fr.gamity.launcher.thomas260913.utils;

import fr.flowarg.azuljavadownloader.*;
import fr.flowarg.flowcompat.Platform;
import fr.gamity.launcher.thomas260913.Launcher;
import sun.net.www.http.HttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;


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
    }
    public Path installJava(String version) throws IOException {
        Path javaPath;
        if(Objects.equals(version, "8")){
            final AzulJavaDownloader downloader = new AzulJavaDownloader(System.out::println);
            final AzulJavaBuildInfo buildInfoWindows = downloader.getBuildInfo(new RequestedJavaInfo("8", AzulJavaType.JRE, os, arch).setJavaFxBundled(true));
            javaPath = downloader.downloadAndInstall(buildInfoWindows, javaDir);
        }else{
            final AzulJavaDownloader downloader = new AzulJavaDownloader(System.out::println);
            final AzulJavaBuildInfo buildInfoWindows = downloader.getBuildInfo(new RequestedJavaInfo(version, AzulJavaType.JRE, os, arch).setJavaFxBundled(false));
            javaPath = downloader.downloadAndInstall(buildInfoWindows, javaDir);
        }
        if(Platform.isOnLinux()){
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("chmod","+x",javaPath.resolve("bin").resolve("java").toAbsolutePath().toString());
            processBuilder.redirectErrorStream(true);
            processBuilder.start();
        }
        return javaPath;
    }
    public Set<String> getAvailableVersions() throws IOException {

        String urlStr =
                "https://api.azul.com/metadata/v1/zulu/packages/?" +
                        "os=" + this.os.name().toLowerCase() +
                        "&arch=" + getArchName(this.arch) +
                        "&java_package_type=" + AzulJavaType.JRE.name().toLowerCase() +
                        "&archive_type=zip";

        URL url = new URL(urlStr);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Java");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );

        StringBuilder response = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        JSONArray array = new JSONArray(response.toString());

        Set<String> versions = new TreeSet<String>(
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return Integer.compare(
                                Integer.parseInt(o2),
                                Integer.parseInt(o1)
                        );
                    }
                }
        );

        for (int i = 0; i < array.length(); i++) {

            JSONObject obj = array.getJSONObject(i);

            if (!obj.has("java_version")) {
                continue;
            }

            JSONObject javaVersion =
                    obj.getJSONObject("java_version");

            if (!javaVersion.has("major")) {
                continue;
            }

            int major = javaVersion.getInt("major");

            versions.add(String.valueOf(major));
        }

        return versions;
    }

    private static String getArchName(AzulJavaArch arch) {

        switch (arch) {
            case X64:
                return "x86_64";

            case X86:
                return "i686";

            case ARM:
                return "arm";

            default:
                return arch.name().toLowerCase();
        }
    }
}
