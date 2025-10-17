package net.djparks.navi;

import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class App {


    public static void main(String[] args) {
        try {
            boolean printOnly = false;
            if (args != null) {
                for (String a : args) {
                    if ("-p".equals(a)) { printOnly = true; break; }
                }
            }
            new App().run(printOnly);
        } catch (Exception e) {
            // In case of any failure, do nothing special: exit non-zero with message to stderr
            System.err.println("navi error: " + e.getMessage());
            System.exit(1);
        }
    }

    private void run(boolean printOnly) throws IOException {
        List<NaviItem> items = ItemLoader.load(Paths.get(System.getProperty("user.home")));
        Renderer renderer = new Renderer();
        PlaceholderPrompter prompter = new PlaceholderPrompter();
        CommandRunner runner = new CommandRunner();
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
            renderer.render(terminal, query.toString(), filtered, selected);

            while (true) {
                int ch = reader.read();
                if (ch == -1) {
                    // EOF: exit silently
                    return;
                }
                if (ch == '\r' || ch == '\n') { // Enter
                    if (!filtered.isEmpty()) {
                        // Execute selected command and exit (with placeholder prompting if needed)
                        NaviItem it = filtered.get(selected);
                        String command = it.command();
                        // Check for placeholders like <name>
                        if (command != null && command.contains("<") && command.contains(">")) {
                            restoreTerminal(terminal, original);
                            try {
                                command = prompter.prompt(command, it.placeholderOptions());
                            } catch (IOException e) {
                                System.err.println("Failed to read parameters: " + e.getMessage());
                                return;
                            }
                            if (printOnly) {
                                System.out.println();
                                System.out.println(command);
                                System.exit(0);
                                return;
                            }
                            int code = runner.run(command);
                            System.exit(code);
                            return;
                        } else {
                            restoreTerminal(terminal, original);
                            if (printOnly) {
                                System.out.println(command);
                                System.exit(0);
                                return;
                            }
                            int code = runner.run(command);
                            System.exit(code);
                            return;
                        }
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
                        renderer.render(terminal, query.toString(), filtered, selected);
                        continue;
                    }
                }
                if (ch == 127 || ch == 8) { // Backspace/Delete
                    if (!query.isEmpty()) {
                        query.setLength(query.length() - 1);
                        selected = 0;
                    }
                } else if (ch == 0x1b) {
                    // already handled above; exit on bare ESC for completeness
                    return;
                } else if (ch == '[') {
                    // In raw mode, arrow keys generally come as ESC [ A/B/C/D. However, since we read char-by-char,
                    // we need to detect full sequence. We'll handle this more robustly below by peeking after ESC.
                } else if (isPrintable(ch)) {
                    query.append((char) ch);
                    selected = 0;
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
                renderer.render(terminal, query.toString(), filtered, selected);
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


    private List<NaviItem> filter(List<NaviItem> items, String q) {
        if (q == null || q.isEmpty()) return items;
        String needle = q.toLowerCase(Locale.ROOT);
        return items.stream()
                .filter(it -> it.title().toLowerCase(Locale.ROOT).contains(needle)
                        || it.command().toLowerCase(Locale.ROOT).contains(needle))
                .collect(Collectors.toList());
    }





        private boolean isPrintable(int ch) {
        // accept basic ASCII printable characters including space
        return ch >= 32 && ch <= 126;
    }
}
