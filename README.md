# 📦 VirtualChest 1.1 (Folia & Paper Ready)

A high-performance virtual chest plugin for Minecraft servers, specifically re-engineered for regional multithreading (Folia) and modern Paper environments.

## ✨ Version 1.1 Features
- **Folia Native Support**: Fully adapted to use Regional and Async Schedulers.
- **Smart Storage (I/O)**: Optimized YAML database; empty slots are no longer saved as `null`.
- **Dynamic Limits**: Chest limits are now dynamically calculated based on player permissions (e.g., `virtualchest.10`).
- **Async I/O**: Inventory operations are handled in background threads to prevent TPS drops.

## ⚠️ IMPORTANT: Migration from v1.0
**WARNING:** Version 1.1 uses a new data structure to save items (Slot-based Mapping). 
- **Compatibility**: Older data files from version 1.0 (List-based with nulls) may not be compatible.
- **Recommendation**: It is highly recommended to **backup and clear** the `plugins/VirtualChest/data` folder before upgrading, or manually test with a few files first.

## 🛠️ Commands and Permissions


| Command | Description | Permission |
| :--- | :--- | :--- |
| `/pv <id>` | Opens a specific virtual chest | `virtualchest.<id>` |
| `/pv admin <player> <id>` | Inspects another player's chest | `virtualchest.admin` |

### 🔑 Permission Logic
- The **default limit** is in `config.yml`.
- Grant `virtualchest.10` to allow access to chests 1 through 10.
- `virtualchest.admin` or **OP** bypasses all limits.

## ⚙️ Installation
1. Download the `VirtualChest-1.1.jar`.
2. **Back up your current `data` folder** if you are upgrading from 1.0.
3. Drop the JAR into your server's `plugins` folder.
4. Restart the server.

---
*Developed with focus on performance and reliability.*
