package net.djparks.navi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
        return prompt(command, null);
    }

    public String prompt(String command, Map<String, List<String>> options) throws IOException {
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
            List<String> opts = options == null ? null : options.get(name);
            String val;
            if (opts != null && !opts.isEmpty()) {
                // Present a numbered list for selection
                System.out.println();
                System.out.println("Select value for " + name + ":");
                for (int i = 0; i < opts.size(); i++) {
                    System.out.println("  " + (i + 1) + ") " + opts.get(i));
                }
                System.out.print("Enter number or value: ");
                System.out.flush();
                String input = br.readLine();
                if (input == null) input = "";
                input = input.trim();
                String chosen = null;
                if (!input.isEmpty()) {
                    try {
                        int idx = Integer.parseInt(input);
                        if (idx >= 1 && idx <= opts.size()) {
                            chosen = opts.get(idx - 1);
                        }
                    } catch (NumberFormatException ignore) {
                        // not a number, treat as direct value
                        chosen = input;
                    }
                }
                if (chosen == null) {
                    // default to first option if nothing valid entered
                    chosen = opts.get(0);
                }
                val = chosen;
            } else {
                System.out.print("Enter value for " + name + ": ");
                System.out.flush();
                String in = br.readLine();
                if (in == null) in = "";
                val = in;
            }
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
