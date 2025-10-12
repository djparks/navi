package net.djparks.navi;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ExampleStringHandling {
    public static void main(String[] args) {
        // Create a snippet for VSCode of the following:
        final List<String> list = List.of("apple", "banana", "", "cherry"); // Add blank for

        // Concatenation of Strings
        String result = String.join(",", list);
        System.out.println(result.substring(0, 5));

        // Compare Strings with null safety? Uses Apache Commons Lang
        String name = "admin";
         if (StringUtils.equals(name, "admin")) {
             System.out.println("name is equal to admin");
         } else {
             System.out.println("name is not equal to admin: " + name);
         }

        // In Java 11+ instead of trim use String.strip()
         String trimmed = " strip me! ".strip();
        System.out.println("-" + trimmed + "-");

        // In Java 15+ instead of trim use String.stripLeading() and String.stripTrailing()
        String leading = " strip leading! ".stripLeading();
        String trailing = " strip trailing! ".stripTrailing();
        System.out.println("-" + leading + "-" + trailing + "-");

        // In Java 15+ instead of trim use String.isBlank()
        if (leading.isBlank() || trailing.isBlank()) {
            System.out.println("Both leading and trailing spaces are blank");
        }

        // In Java 15+ instead of trim use String.lines()
        list.stream().map(String::strip).forEach(System.out::println);

        // In Java 15+ instead of trim use String.repeat()
        String repeated = "x".repeat(5);
        System.out.println(repeated);

        // In Java 15+ formatting strings with {}
        String formatted = "Hello, %s!".formatted( "world");
        System.out.println(formatted);

        // Using Apache Commons checking null safety.  !! Check difference
        if (StringUtils.isNotEmpty(trimmed)) {
            System.out.println("trimmed is not empty");
        }
        if (StringUtils.isNotBlank(trimmed)) { System.out.println("trimmed is not blank"); }

        // Set default value
        String defaultValue = StringUtils.defaultIfBlank(trimmed, "default");
        System.out.println(defaultValue);

    }
}
