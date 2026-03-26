package com.lacavedeharol.wizard;

import java.util.Random;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

abstract class RenderingUtils {

    static void boxFit(Terminal terminal, String[] content) {
        terminal.writer().print("\n");
        int width = terminal.getWidth();
        for (String line : content) {
            if (line.equals(content[0]))
                terminal.writer().println(RenderingUtils.stylizeString("┌ ", null) + line + " " +
                        RenderingUtils.stylizeString("─".repeat(
                                width - AttributedString.stripAnsi(line).length() - 4)
                                + "┐", null));
            else
                terminal.writer().println(RenderingUtils.stylizeString("├ ", null) + line
                        + RenderingUtils.stylizeString(" ".repeat(
                                width - AttributedString.stripAnsi(line).length() - 3)
                                + "│",
                                null));
        }

        terminal.writer().println(RenderingUtils.stylizeString("└" + "─".repeat(width - 2) + "┘", null));
        terminal.flush();
    }

    private static Integer cachedColor = null;

    static String stylizeString(String input, Integer style) {
        int[] colors = {
                AttributedStyle.BLUE, AttributedStyle.CYAN, AttributedStyle.MAGENTA,
                AttributedStyle.RED, AttributedStyle.YELLOW
        };

        if (cachedColor == null)
            cachedColor = colors[new Random().nextInt(colors.length)];

        return new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(style == null ? cachedColor : style).bold())
                .append(input)
                .toAnsi();
    }
}
