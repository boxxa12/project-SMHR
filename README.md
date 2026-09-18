# Project SMHR — Custom Skyblock (Forge + Arclight)

Custom Skyblock server built on **Minecraft 1.20.1 / Forge 47.3.22**, running
[Arclight](https://github.com/IzzelAliz/Arclight) so we can develop our own
Bukkit/Spigot plugin (`/plugin`) alongside the Forge modpack.

## Repo layout

```
server/   -> server configs, scripts, and the built Arclight server jar
             (NO mod jars, NO world save, NO logs — see MODS.txt)
plugin/   -> our custom Spigot/Bukkit plugin (Gradle project)
```

## Prerequisites

- Java 17 (Temurin recommended)
- The Forge 1.20.1-47.3.22 **client** modpack installed for playing
- All 138 mods listed in `server/MODS.txt` (get these from whoever has the
  modpack export / CurseForge/Modrinth pack — jars are NOT committed here
  due to size + licensing)

## Setting up the server (first time on a new PC)

1. Clone this repo.
2. Put all mod jars listed in `server/MODS.txt` into `server/mods/`.
3. From `server/`, run once to accept the EULA:
   ```
   echo eula=true > eula.txt
   ```
4. Start the server:
   ```
   cd server
   .\start.bat
   ```
   (or `start.ps1`). First boot will download missing Forge libraries —
   this can take a few minutes.
5. Once you see `Done (X.Xs)! For help, type "help"`, stop it with `stop`
   in the console, then edit `ops.json` or just run `op <yourname>` in the
   console while it's running to get admin permissions.
6. Connect from the Forge 1.20.1-47.3.22 client to `localhost`.

> `server/arclight-forge-1.20.1-1.0.6.jar` is already built for you
> (Minecraft 1.20.1 / Forge 47.3.22, tag `Trials/1.0.6` from the Arclight
> repo). You don't need to rebuild it unless you want a newer version —
> if you do, clone https://github.com/IzzelAliz/Arclight, checkout the tag
> matching your Forge version, and run `./gradlew build` (build it twice;
> the first run just caches the remapped Spigot jar).

## Developing the plugin

```
cd plugin
.\gradlew.bat build
```

This produces `plugin/build/libs/skyblock-plugin-1.0.0.jar`. Copy it into
`server/plugins/` and restart the server to test your changes:

```
Copy-Item build\libs\skyblock-plugin-1.0.0.jar ..\server\plugins\ -Force
```

Edit code in `plugin/src/main/java/com/yourname/skyblock/`.

## Database (player/island/quest data)

The plugin stores all player, island, and quest data in a database via a
storage-agnostic layer (HikariCP + JDBC), configured in
`server/plugins/SkyblockPlugin/config.yml` (generated on first run).

- **Default: SQLite** — zero setup, stored as a single file
  (`plugins/SkyblockPlugin/database.db`). Great for solo/local dev and small
  testing sessions.
- **MySQL** — for a shared/production server. Fill in `storage.mysql.*` in
  `config.yml` (host, port, database, username, password), set
  `storage.type: MYSQL`, and restart.

Tables: `players`, `islands`, `island_members`, `quests`, `player_quests`.
All primary keys are UUID text (no vendor-specific `AUTO_INCREMENT`), so the
exact same schema and DAO code works unchanged on both backends.

### Migrating SQLite → MySQL later

You don't have to decide up front. When you're ready to move to MySQL:

1. Keep `storage.type: SQLITE` for now, but fill in `storage.mysql.*` with
   your MySQL server's connection details in `config.yml`.
2. Run `/skyblock migrate` in-game or console (op/admin only). This copies
   every row (players, islands, members, quests, progress) from your current
   SQLite database into the MySQL database, creating tables if needed.
3. Set `storage.type: MYSQL` in `config.yml` and restart the server.

No code changes or data loss — the migration command is built into the
plugin (`MigrationService`).

## Notes for collaborators

- Don't commit `server/mods/*.jar`, `server/world/`, `server/logs/`,
  `server/libraries/`, or anything in `.gitignore` — keep the repo small.
- If you add/remove mods, please update `server/MODS.txt` so everyone's
  mod list stays in sync.
- Each person keeps their own local `world/`, `ops.json`, `eula.txt` —
  these are player/machine-specific and gitignored.
