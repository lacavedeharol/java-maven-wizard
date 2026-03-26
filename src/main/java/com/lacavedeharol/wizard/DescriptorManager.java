package com.lacavedeharol.wizard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

abstract class DescriptorManager {

    static Descriptor loadDescriptor() {
        File file = new File(DESCRIPTOR_PATH);
        Gson gson = new Gson();

        if (!file.exists())
            return new Descriptor();

        try (BufferedReader br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {

            Descriptor descriptor = gson.fromJson(br, Descriptor.class);

            return (descriptor != null) ? descriptor : new Descriptor();

        } catch (Exception e) {
            return new Descriptor();
        }
    }

    static void saveDescriptor(Descriptor descriptor) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File externalFile = new File(DESCRIPTOR_PATH);

        if (externalFile.getParentFile() != null)
            externalFile.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(externalFile),
                        StandardCharsets.UTF_8))) {

            gson.toJson(descriptor, writer);
        } catch (IOException e) {
            System.err.println("Error saving descriptor: " + e.getMessage());
        }
    }

    static void deleteDescriptor() {
        Path configDir = Paths.get(getDescriptorPath()).getParent();

        if (!Files.exists(configDir))
            return;

        try {
            Files.walk(configDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete: " + path);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Failed to delete config directory: " + e.getMessage());
        }
    }

    private static final String DESCRIPTOR_PATH = getDescriptorPath();

    private static String getDescriptorPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String base;

        if (os.contains("win"))
            base = System.getenv("APPDATA");
        else
            base = System.getProperty("user.home") + "/.config";

        return base + "/mvninit/descriptor.json";
    }

}
