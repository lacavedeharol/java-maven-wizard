package com.lacavedeharol.wizard;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;

public class Main {

    public static void main(String[] args) {
        switch (args.length == 0 ? "new" : args[0].toLowerCase()) {
            case "new" -> handleNew();
            case "clean" -> DescriptorManager.deleteDescriptor();
            default -> handleNew();
        }
    }

    private static void handleNew() {
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
            terminal.puts(Capability.clear_screen);
            terminal.flush();

            RenderingUtils.boxFit(terminal, new String[] { RenderingUtils
                    .stylizeString("Interactive Maven Project Generation wizard"
                            .toUpperCase(),
                            null) });

            Prompter prompter = new Prompter(terminal);
            final Descriptor descriptor = prepareDescriptor(prompter);
            RenderingUtils.boxFit(terminal, new String[] {
                    RenderingUtils.stylizeString(
                            "Project configuration summary".toUpperCase(),
                            null),
                    "Artifact ID: " + RenderingUtils.stylizeString(descriptor.artifactId(),
                            null),
                    "Group ID: " + RenderingUtils.stylizeString(descriptor.groupId(), null),
                    "Developer ID: "
                            + RenderingUtils.stylizeString(descriptor.developerId(),
                                    null),
                    "Main class: " + RenderingUtils.stylizeString(descriptor.mainClass(),
                            null),
                    "Java Version: " + RenderingUtils
                            .stylizeString(String.valueOf(descriptor.javaVersion()),
                                    null),
                    "Integrate with Git: " + RenderingUtils.stylizeString(
                            String.valueOf(descriptor.integrateWithGit()), null),
                    "Include JAR plugin: " + RenderingUtils
                            .stylizeString(String.valueOf(descriptor.includeJar()),
                                    null),
                    "Include Fat JAR plugin: " + RenderingUtils.stylizeString(
                            String.valueOf(descriptor.includeFatJar()), null),
                    "Include Javadoc plugin: " + RenderingUtils.stylizeString(
                            String.valueOf(descriptor.includeJavaDoc()), null)
            });

            if (prompter.promptForBoolean("Do you wish to proceed? ", true)) {

                Path targetPath = Paths.get(System.getProperty("user.dir"))
                        .resolve(descriptor.artifactId());
                ProjectGenerator generator = new ProjectGenerator(descriptor, targetPath);

                boolean success = generator.generateStructure()
                        && generator.generateDescriptor()
                        && generator.generateEntryPoint();

                if (success) {
                    terminal.writer()
                            .println("Project generated successfully at: " + RenderingUtils
                                    .stylizeString(targetPath.toString(), null));
                    DescriptorManager.saveDescriptor(descriptor);
                } else
                    terminal.writer().println("Project generation failed.");

            } else
                terminal.writer().println("Project generation aborted.");
            terminal.close();

        } catch (IOException e) {
        }
    }

    private static Descriptor prepareDescriptor(Prompter prompter) {
        Descriptor descriptor = DescriptorManager.loadDescriptor();

        String artifactId, groupId;
        artifactId = prompter.promptForString("Enter Artifact ID", null);
        groupId = prompter.promptForGroupId("Enter Group ID", descriptor.groupId());

        return new Descriptor(
                artifactId, groupId,
                prompter.promptForString("Enter Developer ID", Utils.getLastWord(groupId)),
                Utils.setToUppercase(prompter.promptForString("Enter Main class",
                        descriptor.mainClass() != null ? descriptor.mainClass()
                                : Utils.setToUppercase(Utils.getLastWord(artifactId)))),
                prompter.promptForJavaVersion(descriptor.javaVersion()),
                prompter.promptForBoolean("Integrate with Git?", descriptor.integrateWithGit()),
                prompter.promptForBoolean("Include JAR plugin?", descriptor.includeJar()),
                prompter.promptForBoolean("Include Fat JAR plugin?", descriptor.includeFatJar()),
                prompter.promptForBoolean("Include Javadoc plugin?", descriptor.includeJavaDoc()));
    }

}
