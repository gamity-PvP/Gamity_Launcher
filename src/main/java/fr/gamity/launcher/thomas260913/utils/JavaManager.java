package fr.gamity.launcher.thomas260913.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class JavaManager {
    public List<Java> javas = new ArrayList<>();
    private final Path javaDir;
    private JavaInstaller javaInstaller;
    public JavaManager(Path javaDir){
        this.javaDir = javaDir;
        this.javaInstaller = new JavaInstaller(javaDir);
    }
    public Path getJava(String version) throws IOException {
        Optional<Java> existing = javas.stream()
                .filter(j -> j.version.equals(version))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get().path;
        }else{
            javaInstaller = new JavaInstaller(javaDir);
            Path newJavaPath = javaInstaller.installJava(version);
            Java newJava = new Java();
            newJava.path = newJavaPath;
            newJava.version = version;
            javas.add(newJava);
            return newJavaPath;
        }
    }

    public Set<String> getAvailableVersions() throws IOException {
        Set<String> versions = javaInstaller.getAvailableVersions();
        return versions;
    }

    public static class Java{
        public Path path;
        public String version;
    }
}