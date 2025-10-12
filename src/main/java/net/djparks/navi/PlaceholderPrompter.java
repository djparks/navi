package net.djparks.navi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Prompts the user for placeholder values and substitutes them in the command string.
 */
public final class PlaceholderPrompter {
    private static final Pattern PLACEHOLDER = Pattern.compile("<([^<>]+)>");

    public String prompt(String command) throws IOException {
        if (command == null) return null;

        Matcher m = PLACEHOLDER.matcher(command);
        Set<String> names = new LinkedHashSet<>();
        while (m.find()) {
            String name = m.group(1).trim();
            if (!name.isEmpty()) names.add(name);
        }
        if (names.isEmpty()) return command;

        Map<String, String> values = new LinkedHashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        for (String name : names) {
            System.out.print("Enter value for " + name + ": ");
            System.out.flush();
            String val = br.readLine();
            if (val == null) val = "";
            values.put(name, val);
        }
        // Replace all placeholders using collected values
        m = PLACEHOLDER.matcher(command);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String name = m.group(1).trim();
            String val = values.getOrDefault(name, "");
            m.appendReplacement(sb, Matcher.quoteReplacement(val));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
