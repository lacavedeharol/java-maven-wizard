# Maven Project Generation wizard

Interactive Maven project generation CLI tool. Run it from any directory and it will generate a Maven project structure.

## Features

- Interactive prompts for standard configurations.
- Generates standard Maven directory structure.
- Produces a configured `pom.xml` with optional plugins.
- Generates a main class entry point.
- Optional Git integration with a pre-configured `.gitignore`.
- Optional Jar, Fat Jar (shade), and Javadoc plugin support.
- Persists your last configuration as defaults for future runs.

## Requirements

- Java 8 or later

## Installation

The wizard is distributed as a portable ZIP containing a Fat JAR and wrapper scripts for Linux/macOS and Windows.

1. Download the latest release ZIP and extract it to a permanent folder (e.g., ~/.local/share/java-maven-wizard or C:\tools\java-maven-wizard).
2. Add to PATH:

- Linux / macOS: Add export PATH="$HOME/.local/share/java-maven-wizard:$PATH" to your ~/.bashrc or ~/.zshrc, then run source ~/.bashrc.
- Windows: Add the folder path to your User Environment Variables under Path.

### Configuration Storage

The wizard automatically stores your last used values in a JSON descriptor to provide defaults for your next project:

- Linux / macOS: ~/.config/mvninit/descriptor.json
- Windows: %APPDATA%\mvninit\descriptor.json

## Usage

Navigate to the directory where you want your project generated, then run:

```bash
mvninit new
```

You will be prompted for the following:

| Field | Description |
| --- | --- |
| Artifact ID | The Maven Artifact ID (e.g. `my-app`) |
| Group ID | The Maven Group ID (e.g. `com.example.myapp`) |
| Developer ID | Your identifier, used in the POM |
| Main class | Name of the entry point class (e.g. `Main`) |
| Java version | Target JDK version (8–25) |
| Git integration | Adds a `.gitignore` tailored for Java/Maven projects |
| Jar plugin | Includes the `maven-jar-plugin` in the POM |
| Fat Jar plugin | Includes the `maven-shade-plugin` for an uber-jar |
| Javadoc plugin | Includes the `maven-javadoc-plugin` |

After reviewing the configuration summary, confirm to generate the project.

## Generated Structure

```text
my-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/myapp/
│   │   │       └── Main.java
│   │   └── resources/
│   └── test/
│       └── java/
├── pom.xml
└── .gitignore  (if Git integration enabled)
```

## Configuration Persistence

Your last set of inputs is saved automatically after each successful generation and used as default values the next time you run the wizard.

## Author

- **lacavedeharol**
