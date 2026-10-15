# ChestESP v3.1 — Fabric Mod for Minecraft 26.3

[![Minecraft 26.3](https://img.shields.io/badge/Minecraft-26.3-brightgreen.svg)](https://minecraft.net/)
[![Fabric API](https://img.shields.io/badge/Fabric-0.163.0+26.3-blue.svg)](https://fabricmc.net/)
[![Java 21](https://img.shields.io/badge/Java-21+-orange.svg)](https://adoptium.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**ChestESP** is a modern, lightweight, client-side utility mod for **Minecraft 26.3** on the **Fabric** mod loader. It renders clear outlines and translucent fills around containers and container entities through walls and solid blocks within a 64-block radius, featuring distinct color coding for each container category.

**Developed by:** [LymXT](https://github.com/LymXT)

---

## ✨ Features

* **Through-Wall X-Ray Rendering:**
  Renders bounding boxes through solid blocks and terrain using Minecraft 26.3's native Gizmos API with `setAlwaysOnTop()`.
* **Dual Visualization (Outline + Translucent Fill):**
  * Sharp 2.0px stroke outlines for crisp edge visibility at any distance.
  * Soft translucent surface fills (20% opacity) that never obstruct the player's vision.
* **Distinct Color Differentiation by Category:**
  * 🟧 **Regular Chests** (`ChestBlockEntity`) — Vibrant Orange (`#FF8800` outline, `0x33FF8800` fill).
  * 🟥 **Trapped Chests** (`TrappedChestBlockEntity`) — Pure Crimson Red (`#FF1111` outline, `0x33FF1111` fill) for instant trap detection.
  * 🟦 **Ender Chests** (`EnderChestBlockEntity`) — Electric Cyan (`#00DDFF` outline, `0x3300DDFF` fill).
  * 🟪 **Shulker Boxes** (`ShulkerBoxBlockEntity`, all 16 colors) — Neon Purple (`#CC00FF` outline, `0x33CC00FF` fill).
  * 🟨 **Chest Minecarts** (`ChestMinecartEntity`) — Bright Yellow (`#FFFF00` outline, `0x33FFFF00` fill).
* **Double Chest Merging:**
  Automatically detects connected large chest halves and joins them into a single seamless bounding box.
* **Instant Toggle & Chat Feedback:**
  Toggle on/off with a single keypress. Displays an interactive colored status message in chat.

---

## 🎮 Controls

* **`V`** (Default) — Toggle ChestESP on / off.
* Key binding can be customized anytime in game settings:
  `Options -> Controls -> Key Binds -> Miscellaneous -> "Toggle Chest ESP"`

---

## 📁 Repository Structure

```text
ChestESP/
├── build.gradle                                    # Loom 1.18.4, Fabric API 0.163.0+26.3
├── gradle.properties                               # Version 3.1, archives_base_name = ChestESP
├── settings.gradle                                 # rootProject.name = 'ChestESP'
├── LICENSE                                         # MIT License
└── src/
    └── main/
        ├── java/
        │   └── com/example/mysecondmod/
        │       ├── ChestESPClient.java             # Client entrypoint, scanning & keybind handler
        │       ├── ESPCategory.java                # Enum of storage categories & ARGB palette
        │       └── RenderUtils.java                # Bounding box geometry & Gizmo renderer
        └── resources/
            ├── fabric.mod.json                     # Mod metadata (id: "chestesp", version: "3.1")
            └── assets/chestesp/
                ├── icon.png                        # 128x128 mod icon
                └── lang/
                    ├── en_us.json                  # English localization
                    ├── uk_ua.json                  # Ukrainian localization
                    └── ru_ru.json                  # Russian localization
```

---

## 🛠 Building from Source

### Prerequisites
* JDK 21 or higher installed.

### Build
Run Gradle Wrapper in the project directory:

```bash
# Linux / macOS
./gradlew build

# Windows (PowerShell)
.\gradlew build
```

The compiled mod JAR will be located at:
```text
build/libs/ChestESP-3.1.jar
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) — see the LICENSE file for details.
Copyright (c) 2026 [LymXT](https://github.com/LymXT).
