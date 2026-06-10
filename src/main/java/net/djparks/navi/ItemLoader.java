package net.djparks.navi;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Loads .navi files from a directory (typically ~/navi) into NaviItem entries.
 */
public final class ItemLoader {
    private ItemLoader() {}

    public static List<NaviItem> load(Path homeDir) throws IOException {
        Path dir = homeDir.resolve("navi");
        if (!Files.isDirectory(dir)) {
            return new ArrayList<>();
        }
        List<NaviItem> items = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.navi")) {
            for (Path p : stream) {
                List<String> lines = Files.readAllLines(p);
                // First, collect any $$ option lines in this file
                Map<String, List<String>> fileOptions = parseOptions(lines);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.matches("^#\\s+(.+)$")) {
                        String currentTitle = line.replaceFirst("^#\\s+", "").trim();
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
                        items.add(new NaviItem(currentTitle, command, fileOptions));
                    }
                }
            }
        }
        // Sort by title for determinism
        return items.stream()
                .sorted(Comparator.comparing(NaviItem::title, Comparator.naturalOrder()))
                .collect(Collectors.toList());
    }

    private static Map<String, List<String>> parseOptions(List<String> lines) {
        Map<String, List<String>> options = new HashMap<>();
        for (String raw : lines) {
            String line = raw.trim();
            if (line.startsWith("$$")) {
                String rest = line.substring(2).trim();
                int colon = rest.indexOf(':');
                if (colon > 0) {
                    String name = rest.substring(0, colon).trim();
                    String valuesPart = rest.substring(colon + 1).trim();
                    // split by | and trim
                    String[] parts = valuesPart.split("\\|");
                    List<String> vals = new ArrayList<>();
                    for (String part : parts) {
                        String v = part.trim();
                        if (!v.isEmpty()) vals.add(v);
                    }
                    if (!name.isEmpty() && !vals.isEmpty()) {
                        options.put(name, vals);
                    }
                }
            }
        }
        return options;
    }
}
