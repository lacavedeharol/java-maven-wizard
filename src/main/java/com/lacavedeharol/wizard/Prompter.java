package com.lacavedeharol.wizard;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;

class Prompter {

    private LineReader lineReader;

    Prompter(Terminal terminal) {
        this.lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .appName("Java Maven Wizard")
                .build();
    }

    public int promptForJavaVersion(int defaultVersion) {
        String currentPrompt = "Enter Java Version";

        while (true) {
            String input = promptForString(currentPrompt, String.valueOf(defaultVersion));

            try {
                int version = Integer.parseInt(input);

                if (version >= 8 && version <= 25)
                    return version;
                else
                    currentPrompt = "Java Version must be a valid JDK (8-25)";

            } catch (NumberFormatException nfe) {
                currentPrompt = "Invalid input. Please enter a numeric Java Version";
            }
        }
    }

    String promptForString(String prompt, String base) {
        String currentPrompt = prompt;

        while (true) {
            String input = lineReader
                    .readLine(RenderingUtils.stylizeString("─ ", null) + currentPrompt
                            + ((base != null && !base.isBlank())
                                    ? RenderingUtils.stylizeString(" [" + base + "]", null)
                                    : "")
                            + ": ");

            if (base != null)
                return !input.isBlank() ? input.replaceAll("\\s+", "") : base;
            else if (base == null && !input.isBlank())
                return input.replaceAll("\\s+", "");

            else
                currentPrompt = "Value must not be empty";
        }
    }

    String promptForGroupId(String prompt, String base) {
        String currentPrompt = prompt;

        while (true) {
            String value = promptForString(currentPrompt, base);

            if (base != null)
                return validateGroupId(value) ? value.trim().toLowerCase() : base;
            else if (base == null && validateGroupId(value))
                return value.trim().toLowerCase();

            else
                currentPrompt = "Invalid input. Please enter a valid Group ID";
        }
    }

    boolean promptForBoolean(String prompt, boolean base) {
        String value = lineReader
                .readLine(RenderingUtils.stylizeString("─ ", null) + prompt + " "
                        + RenderingUtils.stylizeString(base ? "[Y/n]" : "[y/N]", null)
                        + ": ");

        return value.isBlank() ? base : value.trim().matches("(?i)^y$");
    }

    private boolean validateGroupId(String input) {
        if (input == null || input.isBlank())
            return false;

        String[] parts = input.split("\\.");

        if (parts.length < 2)
            return false;

        for (String part : parts) {
            if (!part.matches("^[a-z][a-z0-9_]*$"))
                return false;
        }

        return true;
    }
}
