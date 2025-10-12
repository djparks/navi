package net.djparks.navi;

import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class App {
    private static final String CLEAR_SCREEN = "\u001b[2J";
    private static final String CURSOR_HOME = "\u001b[H";
    private static final String INVERSE_ON = "\u001b[7m";
    private static final String INVERSE_OFF = "\u001b[27m";

        private record NaviItem(String title, String command) {}

    public static void main(String[] args) {
        try {
            new App().run();
        } catch (Exception e) {
            // In case of any failure, do nothing special: exit non-zero with message to stderr
            System.err.println("navi error: " + e.getMessage());
            System.exit(1);
        }
    }

    private void run() throws IOException {
        List<NaviItem> items = loadItems();
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .encoding(StandardCharsets.UTF_8)
                .jna(true)
                .build();

        Attributes original = terminal.enterRawMode();
        NonBlockingReader reader = terminal.reader();
        try {
            int selected = 0;
            StringBuilder query = new StringBuilder();
            List<NaviItem> filtered = filter(items, query.toString());
            render(terminal, query.toString(), filtered, selected);

            while (true) {
                int ch = reader.read();
                if (ch == -1) {
                    // EOF: exit silently
                    return;
                }
                if (ch == '\r' || ch == '\n') { // Enter
                    if (!filtered.isEmpty()) {
                        // Execute selected command and exit
                        NaviItem it = filtered.get(selected);
                        restoreTerminal(terminal, original);
                        int code = runCommand(it.command());
                        System.exit(code);
                        return;
                    } else {
                        return;
                    }
                }
                // Handle ESC (escape) and arrow keys sequences early
                if (ch == 0x1b) {
                    int next1 = reader.read(50);
                    if (next1 == -2) {
                        // Bare ESC pressed -> exit without printing
                        return;
                    }
                    if (next1 == '[') {
                        int next2 = reader.read(50);
                        if (next2 == 'A') { // Up
                            if (!filtered.isEmpty()) {
                                selected = (selected - 1 + filtered.size()) % filtered.size();
                            }
                        } else if (next2 == 'B') { // Down
                            if (!filtered.isEmpty()) {
                                selected = (selected + 1) % filtered.size();
                            }
                        }
                        // re-render after handling arrows
                        filtered = filter(items, query.toString());
                        if (selected >= filtered.size()) selected = Math.max(0, filtered.size() - 1);
                        render(terminal, query.toString(), filtered, selected);
                        continue;
                    }
                }
                if (ch == 127 || ch == 8) { // Backspace/Delete
                    if (query.length() > 0) {
                        query.setLength(query.length() - 1);
                        selected = 0;
                    }
                } else if (ch == 0x1b) {
                    // already handled above, but keep for completeness
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == '[') {
                    // In raw mode, arrow keys generally come as ESC [ A/B/C/D. However, since we read char-by-char,
                    // we need to detect full sequence. We'll handle this more robustly below by peeking after ESC.
                } else if (isPrintable(ch)) {
                    query.append((char) ch);
                    selected = 0;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else if (ch == 0x1b) {
                    return;
                } else {
                    // Handle escape sequences for arrows
                    if (ch == 0x1b) {
                        int next1 = reader.read(10);
                        int next2 = reader.read(10);
                        if (next1 == '[') {
                            if (next2 == 'A') { // Up
                                if (!filtered.isEmpty()) {
                                    selected = (selected - 1 + filtered.size()) % filtered.size();
                                }
                            } else if (next2 == 'B') { // Down
                                if (!filtered.isEmpty()) {
                                    selected = (selected + 1) % filtered.size();
                                }
                            } else if (next2 == 'C' || next2 == 'D') {
                                // ignore left/right
                            } else if (next2 == -2) {
                                // timeout, ignore
                            }
                        } else if (next1 == -2) {
                            // Just ESC pressed
                            return;
                        }
                    }
                }

                filtered = filter(items, query.toString());
                if (selected >= filtered.size()) selected = Math.max(0, filtered.size() - 1);
                render(terminal, query.toString(), filtered, selected);
            }
        } finally {
            restoreTerminal(terminal, original);
        }
    }

    private void restoreTerminal(Terminal terminal, Attributes original) {
        if (terminal != null && original != null) {
            try {
                terminal.setAttributes(original);
            } catch (Exception ignored) { }
        }
    }

    private List<NaviItem> loadItems() throws IOException {
        Path dir = Paths.get(System.getProperty("user.home"), "navi");
        if (!Files.isDirectory(dir)) {
            return new ArrayList<>();
        }
        List<NaviItem> items = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.navi")) {
            for (Path p : stream) {
                List<String> lines = Files.readAllLines(p);
                String currentTitle = null;
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.matches("^#\\s+(.+)$")) {
                        currentTitle = line.replaceFirst("^#\\s+", "").trim();
                        // find first subsequent non-empty line as command
                        String command = "";
                        int j = i + 1;
                        while (j < lines.size()) {
                            String next = lines.get(j).trim();
                            if (!next.isEmpty()) {
                                command = next;
                                break;
                            }
                            j++;
                        }
                        items.add(new NaviItem(currentTitle, command));
                    }
                }
            }
        }
        // Sort by title for determinism
        items = items.stream().sorted(Comparator.comparing(NaviItem::title, Comparator.naturalOrder())).collect(Collectors.toList());
        return items;
    }

    private List<NaviItem> filter(List<NaviItem> items, String q) {
        if (q == null || q.isEmpty()) return items;
        String needle = q.toLowerCase(Locale.ROOT);
        return items.stream()
                .filter(it -> it.title().toLowerCase(Locale.ROOT).contains(needle)
                        || it.command().toLowerCase(Locale.ROOT).contains(needle))
                .collect(Collectors.toList());
    }

    private void render(Terminal terminal, String query, List<NaviItem> items, int selected) throws IOException {
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

        private int runCommand(String command) {
            try {
                if (command == null || command.isBlank()) return 0;
                ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-lc", command);
                pb.inheritIO();
                Process p = pb.start();
                return p.waitFor();
            } catch (Exception e) {
                System.err.println("Failed to run command: " + e.getMessage());
                return 1;
            }
        }

        private boolean isPrintable(int ch) {
        // accept basic ASCII printable characters including space
        return ch >= 32 && ch <= 126;
    }
}
