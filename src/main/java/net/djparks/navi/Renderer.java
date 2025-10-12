package net.djparks.navi;

import org.jline.terminal.Terminal;

import java.io.IOException;

/**
 * Renders the interactive list UI to the terminal.
 */
public final class Renderer {
    private static final String CLEAR_SCREEN = "\u001b[2J";
    private static final String CURSOR_HOME = "\u001b[H";
    private static final String INVERSE_ON = "\u001b[7m";
    private static final String INVERSE_OFF = "\u001b[27m";

    public void render(Terminal terminal, String query, java.util.List<NaviItem> items, int selected) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(CLEAR_SCREEN).append(CURSOR_HOME);
        sb.append("Filter: ").append(query).append('\n');
        int width = Math.max(20, terminal.getWidth());
        String sep = " | ";
        int sepLen = sep.length();
        int titleWidth = Math.max(10, Math.min(50, (width - sepLen) / 2));
        int cmdWidth = Math.max(10, width - sepLen - titleWidth);
        if (items.isEmpty()) {
            sb.append("(no matches)\n");
        } else {
            for (int i = 0; i < items.size(); i++) {
                NaviItem it = items.get(i);
                String line = fit(it.title(), titleWidth) + sep + fit(it.command(), cmdWidth);
                if (i == selected) {
                    sb.append(INVERSE_ON).append(line).append(INVERSE_OFF);
                } else {
                    sb.append(line);
                }
                sb.append('\n');
            }
        }
        terminal.writer().print(sb.toString());
        terminal.writer().flush();
    }

    private String fit(String s, int width) {
        if (s == null) s = "";
        if (width <= 0) return "";
        if (s.length() == width) return s;
        if (s.length() < width) {
            // pad right with spaces
            StringBuilder sb = new StringBuilder(width);
            sb.append(s);
            while (sb.length() < width) sb.append(' ');
            return sb.toString();
        }
        // truncate with ellipsis if room
        if (width <= 1) return s.substring(0, width);
        return s.substring(0, width - 1) + "…";
    }
}
