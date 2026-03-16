# 📦 VirtualChest 1.3 (Folia & Paper Ready)

High-performance virtual chest plugin for Minecraft servers, re-engineered for regional multithreading (Folia) and modern Paper environments.

## ✨ Version 1.3 Features
- **New Command & Alias**: Added `/pv reload` and `/vc` alias for easier management.
- **100% Translatable**: All messages, including console errors, are now moved to `messages_en.yml` and `messages_pt.yml`.
- **Folia Native Support**: Fully adapted to use Regional and Async Schedulers.
- **Smart Storage (I/O)**: Optimized YAML database; empty slots are no longer saved as `null`.
- **Dynamic Limits**: Chest limits are dynamically calculated based on permissions (e.g., `virtualchest.10`).

## 🛠️ Commands and Permissions


| Command | Alias | Description | Permission |
| :--- | :--- | :--- | :--- |
| `/pv <id>` | `/vc <id>` | Opens a specific virtual chest | `virtualchest.<id>` |
| `/pv admin <player> <id>` | `/vc admin` | Inspects another player's chest | `virtualchest.admin` |
| `/pv reload` | `/vc reload` | Reloads config and messages | `virtualchest.admin` |

## ⚠️ IMPORTANT: Migration Instructions
1. **Delete old `config.yml`**: You must delete the old config file to allow the plugin to generate the new version with language file support.
2. **Clear Data**: Data files from versions prior to 1.1 are **not compatible**. Backup and clear the `plugins/VirtualChest/data` folder.

## ⚙️ Installation
1. Download `VirtualChest-1.3.jar`.
2. **Delete** your old `config.yml` and **clear** the `data` folder if upgrading from v1.0.
3. Drop the JAR into your `plugins` folder and restart.

---
*Developed with focus on performance and reliability.*
