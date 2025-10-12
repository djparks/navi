package net.djparks.navi;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
                        items.add(new NaviItem(currentTitle, command));
                    }
                }
            }
        }
        // Sort by title for determinism
        return items.stream()
                .sorted(Comparator.comparing(NaviItem::title, Comparator.naturalOrder()))
                .collect(Collectors.toList());
    }
}
