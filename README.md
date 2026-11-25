# MacroKeybindMod — Minecraft 1.21.11 Edition

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-4CAF50)
![Mod Loader](https://img.shields.io/badge/Mod%20Loader-Fabric-DBCFAB)
![Java](https://img.shields.io/badge/Java-21-orange)
![License](https://img.shields.io/badge/License-MIT-blue)

A client-side Minecraft mod for creating, editing and running scriptable macros through custom keybinds and an integrated in-game editor.

This is a fork of [jackulau/MacroKeybindMod](https://github.com/jackulau/MacroKeybindMod), focused on a more convenient macro-management and scripting experience.

> [!IMPORTANT]
> **This fork officially supports Minecraft Java Edition 1.21.11 with Fabric only.**
> The upstream project supports multiple Minecraft versions, but the custom features in this fork are developed and tested exclusively on 1.21.11. Other versions are unsupported, even if their build configurations or original JAR files remain in the repository.

## Features

### Custom macro system

- Load custom macros from individual `.txt` files.
- Assign macros to letter keys (A–Z), number keys (0–9), or function keys (F1–F12, except reserved keys).
- Automatically reload added, edited or deleted macro files approximately every two seconds, without restarting Minecraft.
- Run multiple macros concurrently.
- Use the original scripting engine for commands, variables, conditions, loops and timed actions.

### In-game macro manager

Press **F10** to open the macro manager.

- View registered custom macros in a paginated list.
- Create a new macro and assign its activation key.
- Edit and save scripts without leaving Minecraft.
- Delete macros with a confirmation prompt.
- Prevent duplicate filenames and assignments to reserved keys.
- Warn when a script file has changed externally since opening the editor, rather than silently overwriting it.

### Integrated script editor

- Multiline text editing with line numbers and scrolling.
- Syntax highlighting for common commands, control-flow keywords, strings, numeric literals and script delimiters.
- Command and keyword autocomplete based on the current word before the cursor.
- Use the Up/Down arrow keys to select a suggestion, Tab to accept it, or Escape to dismiss the suggestion panel.
- Preserve native Minecraft text-editing controls, including cursor movement and selection.

Autocomplete currently uses an initial built-in list of commands and keywords. It does not yet provide parameter hints, variable suggestions or complete syntax validation, and may suggest words inside strings or arguments.



## Compatibility and requirements

| Requirement | Supported configuration |
| --- | --- |
| Minecraft | Java Edition 1.21.11 only |
| Mod loader | Fabric Loader 0.16 or newer, compatible with 1.21.11 |
| Java | 21 |
| Fabric API | Version compatible with Minecraft 1.21.11 |
| Fabric Language Kotlin | 1.13.0 or newer, compatible with the installation |
| Environment | Client-side |

The scripting engine is included in the mod JAR; it does not require a separate installation. Forge, NeoForge, Bedrock Edition and other Minecraft versions are not supported by this fork.

## Installation

1. Create a Minecraft 1.21.11 Fabric instance in your launcher (for example, Prism Launcher).
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) and [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin) in compatible versions.
3. Build the modified JAR using the instructions below, or obtain a release of this fork if one has been published.
4. Copy `macromod-0.1.0+1.21.11.jar` into the instance's `mods` directory. Remove the previous MacroKeybindMod JAR to avoid duplicates. Do not install the `-sources.jar` file.
5. Launch the game and press **F10** to open the macro manager.

Do not assume that pre-existing JARs in the upstream `dist/` directory include the custom features described here.

## Keyboard shortcuts

| Key | Action |
| --- | --- |
| F10 | Open the macro manager |
| F12 | Emergency stop |
| Up / Down | Select an autocomplete suggestion when the panel is active |
| Tab | Accept an autocomplete suggestion when the panel is active |
| Esc | Dismiss autocomplete when the panel is active |

F10 and F12 are reserved and cannot be assigned to custom macros.

## Creating a macro

### In-game

Open the manager with F10, create a macro, choose an available activation key, edit its script and save. Close the manager and press the assigned key to run it.

### Using text files

Custom macros are stored relative to the Minecraft instance directory:

```text
config/macromod/macros/
```

The filename identifies the activation key. For example:

```text
config/macromod/macros/
├── K.txt
├── L.txt
└── F8.txt
```

Use one file per key. Files can also be created and edited with an external text editor.

### Example: display a local message

Create `K.txt` with the following content:

```text
$${
    log("Hello from my macro!");
}$$
```

Press K in-game to display the message locally in the Minecraft chat interface.

### Example: toggle a repeating message

Create `L.txt` with the following content:

```text
$${
    if(active);
        unset(active);
    else;
        set(active);
        do;
            log("Macro running");
            wait(1000ms);
        while(active);
    endif;
}$$
```

Press L to start the macro and press it again to turn off its repeating behavior. F12 provides an emergency stop.

## Build from source

Use JDK 21 and open a terminal in the repository root.

### Windows (PowerShell)

```powershell
.\gradlew.bat :fabric:1.21.11:build --max-workers=2 --no-parallel
```

### Linux / macOS

```bash
./gradlew :fabric:1.21.11:build --max-workers=2 --no-parallel
```

The resulting JAR is expected at:

```text
fabric/versions/1.21.11/build/libs/macromod-0.1.0+1.21.11.jar
```

For development in IntelliJ IDEA, set the active Stonecutter project to 1.21.11 and reload Gradle if necessary:

```powershell
.\gradlew.bat "Set active project to 1.21.11" --max-workers=2 --no-parallel
```

The inherited multi-version project structure is retained, but building or distributing other versions is outside the supported scope of this fork.

## Documentation

The upstream documentation remains the reference for the macro language, supported commands, expressions, variables, events, navigation and engine architecture:

- [Upstream documentation](https://github.com/jackulau/MacroKeybindMod/tree/main/docs)
- [Project documentation directory](docs/)

Some upstream instructions concern other Minecraft versions or the original keybinding workflow and may not reflect this fork's custom macro manager.

## Credits and license

- [jackulau/MacroKeybindMod](https://github.com/jackulau/MacroKeybindMod) — upstream Fabric rewrite and scripting engine.
- Mumfrey — creator of the original Macro / Keybind Mod.

This fork retains the upstream MIT license. See [LICENSE](LICENSE) and [NOTICE.md](NOTICE.md) for license information and attribution.

Use macros responsibly and follow the rules of any multiplayer server you join.
