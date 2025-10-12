# navi

An interactive terminal helper that lets you search your own command snippets and run them with optional placeholders.

This project uses JLine for a cross-platform TTY UI and supports macOS, Linux, and Windows.

## Prerequisites
- Java 17 or newer (matching the version in the Maven pom)
- Maven 3.8+ (or use the provided mvnw/mvnw.cmd wrapper)

## Build
Using Maven (wrapper recommended):

- Unix/macOS:
  - `./mvnw clean package`
- Windows (CMD or PowerShell):
  - `mvnw.cmd clean package`

Artifacts are produced in `target/`:
- `target/navi.jar` — runnable fat jar (with dependencies)

## Run
After building, you can run the application using Java directly or the helper scripts provided below.

- Unix/macOS:
  - `java -jar target/navi.jar`
- Windows (CMD or PowerShell):
  - `java -jar target\navi.jar`

### Helper scripts
- `./run-navi.sh` (Unix/macOS): builds (if needed) and runs the app
- `run-navi.cmd` (Windows): builds (if needed) and runs the app

## Using the application
1. Create a directory named `navi` under your home directory.
   - Unix/macOS: `~/navi`
   - Windows: `%USERPROFILE%\navi`
2. Inside that directory, create one or more files with the `.navi` extension. Each file can contain multiple entries.
3. For each entry, use a comment line for the title followed by the command on the next non-empty line. Example:

```
# Git: Show recent commits
git log --oneline --graph --decorate -n 20

# Search in files
rg "<pattern>" <path>
```

Placeholders are indicated with angle brackets like `<pattern>` or `<path>`. When you press Enter on a selection that contains placeholders, the app will prompt you to input values which will be substituted into the command before execution.

### Command-line options
- -p: Print-only mode. When you press Enter on a command, navi will resolve any placeholders, print the final command to stdout, and exit without executing it.
  - Unix/macOS: `java -jar target/navi.jar -p` or `./run-navi.sh -p`
  - Windows: `java -jar target\navi.jar -p` or `run-navi.cmd -p`

### Navigation and keys
- Type to filter the list.
- Up/Down arrows to move selection.
- Enter to run the selected command (or print it when using -p).
- Esc to exit without running.

## Cross-platform notes
- Command execution shell:
  - On Windows, commands are run via `cmd.exe /c`.
  - On Unix-like systems (macOS, Linux), commands are run via `/bin/sh -c`.
- Terminal rendering uses ANSI escape sequences via JLine. On modern Windows 10+ consoles, ANSI support is enabled; if using older environments, consider Windows Terminal or enabling VT processing.

## Troubleshooting
- If you see "navi error" with a message, ensure your Java version matches the project requirements.
- Ensure your `~/navi` (or `%USERPROFILE%\navi`) directory exists and contains `.navi` files as described above.
- Some commands in your snippets may be shell-specific. When sharing snippets between platforms, prefer cross-platform tools or guard with appropriate shell syntax.

## License
MIT (or project’s chosen license).
