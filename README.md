# 📦 VirtualChest 1.2 (Folia & Paper Ready)

High-performance virtual chest plugin for Minecraft servers, re-engineered for regional multithreading (Folia) and modern Paper environments.

## ✨ Version 1.2 Features
- **External Translation Files**: Dedicated `messages_en.yml` and `messages_pt.yml` files for easier customization.
- **Folia Native Support**: Fully adapted to use Regional and Async Schedulers.
- **Smart Storage (I/O)**: Optimized YAML database; empty slots are no longer saved as `null`.
- **Dynamic Limits**: Chest limits are dynamically calculated based on permissions (e.g., `virtualchest.10`).

## ⚠️ IMPORTANT: Migration from v1.0/v1.1
**Version 1.2 introduces a new translation system and data mapping.**
1. **Delete old `config.yml`**: You must delete the old config file to allow the plugin to generate the new version with language file support.
2. **Clear Data**: Data files from versions prior to 1.1 are **not compatible**. Backup and clear the `plugins/VirtualChest/data` folder.

## 🛠️ Commands and Permissions


| Command | Description | Permission |
| :--- | :--- | :--- |
| `/pv <id>` | Opens a specific virtual chest | `virtualchest.<id>` |
| `/pv admin <player> <id>` | Inspects another player's chest | `virtualchest.admin` |

## ⚙️ Installation
1. Download `VirtualChest-1.2.jar`.
2. **Delete** your old `config.yml` and **clear** the `data` folder.
3. Drop the JAR into your `plugins` folder and restart.
