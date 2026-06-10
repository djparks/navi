package net.djparks.navi;

import java.io.IOException;

/**
 * Executes shell commands in a subshell and returns the exit code.
 */
public final class CommandRunner {
    public int run(String command) {
        if (command == null) return 0;
        try {
            String os = System.getProperty("os.name", "").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                // Use Windows cmd.exe
                pb = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                // Use POSIX shell on Unix-like systems (macOS, Linux, etc.)
                pb = new ProcessBuilder("/bin/sh", "-c", command);
            }
            Process p = pb.inheritIO().start();
            return p.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to run command: " + e.getMessage());
            return 1;
        }
    }
}
