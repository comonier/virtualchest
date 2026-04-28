# VirtualChest

VirtualChest is a high-performance, secure, and reliable virtual storage system for Minecraft servers. It allows players to have multiple private inventories that they can access from anywhere.

---

### ✨ Key Features
*   **Multiple Chests:** Players can own several virtual chests, not just one.
*   **Smart Storage:** Supports both **SQLite** (local file) and **MySQL** (external database).
*   **Automated Maintenance:** The plugin creates a safety backup of your database every time the server restarts and automatically cleans up old backups to save disk space.
*   **Data Migration:** Effortlessly moves all player items from old YAML files to the new SQL database upon first run.
*   **Anti-Conflict & Security:** Uses a custom InventoryHolder system to prevent other plugins from interfering with or overwriting player data.
*   **Multi-Language:** Native support for **English**, **Portuguese**, **Spanish**, and **Russian**.

---

### 🛠 Technical Specifications
*   **Platform:** Paper, Spigot, or Folia.
*   **Game Version:** Optimized for **1.21.1**.
*   **Java Version:** Compiled using **Java 21**.

---

### 🛡️ How to set Chest Limits
Control how many chests a player or a group can have using permissions in the format: `virtualchest.X` (where **X** is the number of chests).

*   **Default Limit:** Configured in `config.yml` (e.g., 5 chests).
*   **VIP Example:** Grant `virtualchest.20` to allow access to chests 1 through 20.
*   **Admin Access:** Players with `virtualchest.admin` can open any chest number and inspect other players' inventories.

---

### ⌨️ Commands
*   `/pv <number>` - Opens a specific virtual chest (e.g., `/pv 1`).
*   `/pv admin <player> <number>` - Allows admins to inspect someone else's chest.
*   `/pv reload` - Reloads all configurations and translations.

---

### 🌍 Configuration & Translation
In the `config.yml`, you can manage:
*   **Language:** Choose between `en`, `pt`, `es`, or `ru`.
*   **Storage:** Toggle between `SQLITE` or `MYSQL`.
*   **Backups:** Set `max_backups_to_keep` to control how many history files are stored.

---

### ❤️ Special Thanks
A very special thanks to the developer **Wackenzie (Mackenzie)**. After a misunderstanding regarding a review on another plugin, Mackenzie took the time to reach out privately and guide me on how to improve my own code. Thanks to that advice, this plugin was rewritten with professional security standards (InventoryHolder) to ensure player data is never corrupted by third-party menu conflicts again. This version is a result of that learning experience.
