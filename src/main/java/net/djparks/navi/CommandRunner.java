package net.djparks.navi;

import java.io.IOException;

/**
 * Executes shell commands in a subshell and returns the exit code.
 */
public final class CommandRunner {
    public int run(String command) {
        if (command == null) return 0;
        try {
            Process p = new ProcessBuilder("/bin/sh", "-c", command)
                    .inheritIO()
                    .start();
            return p.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to run command: " + e.getMessage());
            return 1;
        }
    }
}
