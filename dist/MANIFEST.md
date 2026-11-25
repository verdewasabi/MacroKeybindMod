# MacroKeybindMod — Minecraft 1.21.11 release

MacroKeybindMod `0.1.0` — client-side Fabric edition for **Minecraft Java Edition 1.21.11 only**.
The upstream project supports multiple Minecraft versions, but this fork distributes and supports only 1.21.11. The scripting engine is bundled in the JAR (Jar-in-Jar).

## Install

Download the [release JAR](macromod-0.1.0+1.21.11.jar) and place it in your Minecraft instance's `mods/` directory, together with a compatible **Fabric Loader**, **Fabric API**, and **Fabric Language Kotlin**. Use Java 21 and remove any previous MacroKeybindMod JAR to avoid duplicates.

See the [README](../README.md#installation) for details.

## Release JAR

| Minecraft | JAR | Size | Java | Fabric API |
| --- | --- | ---: | :---: | --- |
| 1.21.11 | [`macromod-0.1.0+1.21.11.jar`](macromod-0.1.0+1.21.11.jar) | 492.1 KiB | 21 | `0.141.4+1.21.11` |

Fabric Loader: `>=0.16` (compatible with 1.21.11). Fabric Language Kotlin: `>=1.13.0` (compatible with the installation). Other Minecraft versions and mod loaders are unsupported by this fork.

## Verifying downloads

The SHA-256 checksum is available in [`checksums.sha256`](checksums.sha256).

Linux / macOS (from the repository root):

```bash
cd dist && shasum -a 256 -c checksums.sha256
```

Windows (PowerShell, from the repository root):

```powershell
Get-FileHash "dist/macromod-0.1.0+1.21.11.jar" -Algorithm SHA256
```

Compare the result with the value in `checksums.sha256`.

## Building a new release

Build only Minecraft 1.21.11 using the instructions in the [README](../README.md#build-from-source). Replace the release JAR in `dist/`, recalculate its SHA-256 checksum and update the size in this manifest. Do not use the inherited multi-version distribution script without adapting it to this fork.
