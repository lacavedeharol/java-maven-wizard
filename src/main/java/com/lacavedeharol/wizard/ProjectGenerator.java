package com.lacavedeharol.wizard;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

class ProjectGenerator {

    private final Descriptor descriptor;
    private final Path projectRoot;

    ProjectGenerator(Descriptor descriptor, Path projectRoot) {
        this.descriptor = descriptor;
        this.projectRoot = projectRoot;

    }

    boolean generateStructure() {
        try {
            Files.createDirectories(projectRoot);
            Path javaSourcePath = projectRoot.resolve("src/main/java/" + descriptor.groupId().replace(".", "/"));
            Files.createDirectories(javaSourcePath);
            Files.createDirectories(projectRoot.resolve("src/main/resources"));
            Files.createDirectories(projectRoot.resolve("src/test/java"));
            if (descriptor.integrateWithGit())
                copyGitIgnore();
            return true;
        } catch (IOException | NullPointerException e) {
            return false;
        }

    }

    private void copyGitIgnore() {
        try (InputStream is = ProjectGenerator.class.getResourceAsStream("/templates/.gitignore")) {
            if (is == null)
                return;
            Path target = projectRoot.resolve(".gitignore");
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
        }
    }

    private static final String MAIN_TEMPLATE = """
            package ${fullPackage};

            public class ${mainClass} {
                public static void main(String[] args) {
                }
            }
            """;

    boolean generateEntryPoint() {
        String rootPackage = Utils.getLastWord(descriptor.artifactId());
        String fullPackage = descriptor.groupId() + "." + rootPackage;

        Path mainClassDir = projectRoot
                .resolve("src/main/java")
                .resolve(descriptor.groupId().replace(".", "/"))
                .resolve(rootPackage);

        Path mainClassFile = mainClassDir.resolve(descriptor.mainClass() + ".java");

        String content = MAIN_TEMPLATE
                .replace("${fullPackage}", fullPackage)
                .replace("${mainClass}", descriptor.mainClass());

        try {
            Files.createDirectories(mainClassDir);
            Files.writeString(mainClassFile, content, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    boolean generateDescriptor() {
        String pomContent;
        Path pomFile = projectRoot.resolve("pom.xml");

        try (InputStream is = ProjectGenerator.class.getResourceAsStream("/templates/pom.xml")) {
            if (is == null)
                return false;
            pomContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return false;
        }

        pomContent = pomContent
                .replace("${groupId}", descriptor.groupId())
                .replace("${artifactId}", descriptor.artifactId())
                .replace("${developerId}", descriptor.developerId())
                .replace("${javaVersion}", String.valueOf(descriptor.javaVersion()))
                .replace("${mainClass}", descriptor.mainClass())
                .replace("${rootPackage}", Utils.getLastWord(descriptor.artifactId()));

        pomContent = handleBlock(pomContent, "integrateWithGit", descriptor.integrateWithGit());
        pomContent = handleBlock(pomContent, "includeJavadoc", descriptor.includeJavaDoc());
        pomContent = handleBlock(pomContent, "includeJar", descriptor.includeJar());
        pomContent = handleBlock(pomContent, "includeShade", descriptor.includeFatJar());

        try (BufferedWriter writer = Files.newBufferedWriter(pomFile, StandardCharsets.UTF_8)) {
            writer.write(pomContent);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String handleBlock(String content, String tag, boolean keep) {
        String openMarker = "<!-- " + tag + ":start -->";
        String closeMarker = "<!-- " + tag + ":end -->";

        if (keep) {
            return content
                    .replace(openMarker, "")
                    .replace(closeMarker, "");
        }

        while (true) {
            int start = content.indexOf(openMarker);
            if (start == -1)
                break;

            int end = content.indexOf(closeMarker, start + openMarker.length());
            if (end == -1)
                break;

            int finalEnd = end + closeMarker.length();

            String left = content.substring(0, start).stripTrailing();
            String right = content.substring(finalEnd);

            if (right.startsWith("\n"))
                right = right.substring(1);
            else if (right.startsWith("\r\n"))
                right = right.substring(2);

            content = right.isEmpty() ? left : left + "\n" + right;
        }

        return content;
    }
}
