### 0.5.x

#### 0.5.3
> Add 26.1neoforge

#### 0.5.2
- Add event-driven function execution with command source context
- Add function config in ./config/battleroyale/server/function
- Add game player related entity selector by '/cbr temp entitySelector'
- Remove & rebuild vanilla team via command

Wiki update:
- Recommend installing the Stylus extension for colorful titles

Gamerule config:
- Build vanilla team with configurable String.format
- Add freeToJoin to gamerule config

Add API commands:
- Add Algorithm, GameManager, FunctionManager api command
- Add TeleportScheduler to api command to teleport with AlgorithmApi

Other:
- Write gameId, configId to cloned EntitySpawner item
- Distinguish between items and entities in loot generator cleanup logs
- Add missing profile save translation key
- Reduce logo resolution to 256x256
- Never gonna give loot up (Add '/cbr utility lootconfig [id] toLootTable', but should use '/cbr loot [player]' instead)

For developer:
- Add ZoneManager to ZoneManager.ZoneContext
- Prefix '_' to isolate internal entropy via class fractaling

forge1.20.4 & forge1.21.1:
- Fix Forge crash issue

1.20.4neoforge:
- Fix loot spawner render issue

1.21.11:
- Correct debug permission check for non-player source

#### 0.5.1
- Fix 'clearPreviousContent' write 'clearCachedChunk' tag
- Fix duplicate death stats in DeathMatch
- Clear client data on quit single/multi player world
- Hide Component parser exception error log
- Improved configuration saving stability in extreme cases

forge1.20.1 & neoforge1.21.1
- Remove tacz bullet handler, use TaCZ Armor Scaling (new mod) instead

#### 0.5.0
- Fix '/cbr team build' creating non-bot game player for living entities
- Add '/cbr register manager' command, disabled during game
- Add mod metadata
- Re-online game players are now only eliminated when health drops below -0.1

Add DeathMatch mode:
- Use '/cbr register manager "battleroyale:DMGameProcessManager"' to switch to DeathMatch, and use '/cbr register manager "battleroyale:GameManager"' to switch back
- Reuse spawn points as DeathMatch respawn mechanism
- Re-tick zone function to game player who gained valid kill
- Re-tick zone function to respawned game player
- Add progress bar to show current max kill

Config:
- Add DeathMatch example configs
- Add doImmediateRespawn to gamerule config
- Rename clearInventory to clearInventoryAtStart in gamerule config
- Add extra rule to gamerule config
- Add loot spawner render config

For developer:
- Add custom event registration
- Isomorphic unification of client managers
- Add kill event hooks to DeathMatch
- Optimize event dispatching with lock-free retrieval
- Optimize event dispatching to eliminate GC pressure

forge1.21.1:
- Fix partial tick issue (affect special render)

### 0.4.x
> Stats & Multi-Sync: Implemented deep stats and decoupled architecture for multi-version/loader synchronization.

#### 0.4.9
- Correct spawn config translation in zh_cn
- Fix spawn config id not set via command
- Fix spawn, stats config not applied via command
- Downgrade game object removal log to debug
- Fix loot config file names not be recorded via '/cbr utility profile save'
- Correct badShape dimension calculation
- Draw thicker line on JourneyMap by default
- Fix coordinate truncation issues in JourneyMap overlay

Add scoreboard stats:
- Add stats config in ./config/battleroyale/game/stats
- Add many stats like hurt/knock/kill by player / non-player, win/lose, win rate……
- Add journey stats (travel distance in game)
- Sync game info to scoreboard

Config:
- Add example BattleRoyale, DeathMatch stats config
- Add restartAfterGame to gamerule config
- Add DeathMatch example gamerule, spawn, zone config
- Add cross, ring zone shape
- Add lineThickness to display config

For developer:
- Use BattleRoyale.getEventRegister() to register mod/custom events
- Put event handler to domain packages
- Unified stats tracking source via custom game events
- Add LivingEntity getter in custom game events
- Refactor StatsManager framework
- Decouple specific config manager from all IGameSubManager
- Move notifyWinner to GameProcessManager
- Adjust BattleRoyale stats framework

1.20.1&1.21.1:
- Use splash_potion as bandage in Tacz & Cbrg loot spawner config

#### 0.4.8
- Optimize loot spawner rendering with culling

Config:
- Correct horse vehicle speed in entity spawner config
- Remove iron helmet in ElytraAddon
- Add Pubg1200x1200_random to zone config
- Add 30s Muteki, boost zone (used as countdown), ElytraAddon zone to PUBG zone config
- Add profile config
- Rename tntExplodes to tntExplostionDropDecay in gamerule config
- Adjust Cbrg loot config

For developer:
- Add ILivingAttackEvent, ILivingHurtEvent

1.20.1&1.21.1:
- Add TaCZ bullet damage reduction & durability calculation (like PUBG)
- netherite helmet, diamond chest plate: reduce 55% damage
- iron helmet & chest plate: reduce 40% damage
- leather helmet & chest plate: reduce 30% damage
- broken chest plate will turn to chainmail chest plate
- Add damage reduction equipment to Tacz & Cbrg loot spawner config

#### 0.4.7
Loot command:
- Add '/cbr utility lootconfig' to auto generate loot config
- Add '/cbr loot chunk/pos' to only loot chunk/block
- Add '/cbr loot stop' to stop '/cbr loot generate'
- Manual loot generation does not set a new GameManager gameId
- Add '/cbr loot [player] [lootId] reset/generate' to loot player's inventory (same as 'Inventory zone')

Other:
- Generalize game mechanics to support LivingEntity
- Add '/cbr team add/build' to add living entity to game team
- Elevate '/cbr temp clear' to permission level 3

Config:
- Adjust Cbrg loot config to CBRG 0.4.7
	- Add cbrg:suppressor_pistol, cbrg:suppressor_sr
	- Adjust existing loot
	- Remove several unused loot

For developer:
- Refactor API architecture
- Make stats event uncancellable
- Add switches to turn off ChatUtils globally
- Add remover to IGameIdWriteApi
- Promote getNameKey to config interfaces
- Replaced all new BlockPos() with BlockPos.containing()

#### 0.4.6
> Add 1.21.11neoforge
- Update creative tab translation
- Send game player down message
- Add zone special entry
- Add additional render to zone special entry
- Fix crash if server stop without a succeed '/cbr game load'
- Adjust Cbrg loot config to CBRG 0.4.6-1
- Add special render to example zone config
- Add special render to forecast zone in PUBG-style zone config #46

For developer:
- Refactor game & client framework, decouple certain manager from mod
- Let game & client manager replaceable

#### 0.4.5-1
- Replace wrong 'jsonTag' with 'tag' in example config

#### 0.4.5
- Add CFHC loot spawner & entity spawner example config
- Fix internal error when one-team-game ends

#### 0.4.4
Fix:
- Fix game tick running at double speed
- Fix 3D shape dimension not change
- Correct rectangle grid distribution
- Fix entity func not generate multiple entities
- Fix airdrop & entity zone crash

Config:
- Add canHurtNonGamePlayer, allowInterfererDamage to gamerule config
- Add entity loot zone
- Add example Custom Fast Hardcore zone config
- Replace 'tag' with 'jsonTag'

Other:
- Add description to mods.toml
- Add issue & display url

For developer:
- Add shape progress getter
- Add top/bottom center pos getter
- Refactor custom event API
- Provide distribution calculation API

1.21.6+:
- Fix enchantments components (require registry access) from nbt can't be read

#### 0.4.3
Command:
- Add /cbr temp initStackZoneConfig
- Add /cbr temp gameStep to quick fact zone config

Config:
- Add lootAnyBlockEntity to performance config
- Add golem loot, shape loot, event loot
- Add example UHC zone config

Fix:
- Backup & save command applied to all sub config
- Fix combined loot config written
- Correct loot config written directory

#### 0.4.2
> Add 1.21.6neoforge, 1.21.10neoforge
BattleRoyale related:
- Add distributed teleport type: Grid distribution (rectangle & square), Double center grid distribution (circle), Golden Spiral Distribution (circle)
- Use temp data to pre calculate double center grid distribution result
- Add inventory zone func
- Add example elytra zone config addon
- TeleportSpawner will calculate zone shape in advance (fix tick order issue)
- Fix empty entry not generate empty item/entity
- Correct unsafe zone damage type (affect death message)
- Save GameManager game id to temp data

Other:
- Add backup & save command
- Add config to render entity spawner
- Correct Zone controller particle
- Not render block offscreen

1.21.4+:
- Adjust horse vehicle entity spawner config

1.21.6+:
- Adjust horse vehicle entity spawner config

#### 0.4.1-1
> Add 1.21.4neoforge
- Make item lootType nbt usage same as 1.20.1 (consider 1.21.1components as a special nbt tag)
- Adjust example loot spawner config
- Adjust example Tacz & Cbrg loot config
- Adjust example horse vehicle entity loot config

#### 0.4.1
> Add 1.21.1forge, 1.21.1neoforge
- Only reload all configs once on mod setup
- Add Items & GameId to '/cbr db get benbt'
- Add '/cbr db get pi' to get ItemStack NBT

1.21.1:
- Fix loot spawner block issue (appearance, items lost)
- Fix message zone config manual reload requirement (proceed additional reload on server starting)

#### 0.4.0-1
> Add 1.20.4forge, 1.20.4neoforge
- Fix '/cbr reload' not reload loot configs

#### 0.4.0
> Add 1.20.2forge, 1.20.2neoforge
- Decouple mod from Forge API
- Fix entity loot amount

### 0.3.x
> Mechanics & QoL: Added knockdown/revive systems, spectator mode, and JourneyMap support.

#### 0.3.9-2
- Fix crash on server stopping
- Set default game level when not in game

#### 0.3.8
- Add GameApi, game events, refactor GameZone api for developers
- Add message zone, event zone function
- Add message zone to example zone config
- Update CBRG loot config

#### 0.3.7-2
Update lobby teleport related config in gamerule & utility config to enable/disable:
- Teleport to game lobby clear inventory
- Drop inventory before teleport to game lobby
- Leave team (force elimination) do lobby teleport
- Teleport to survival lobby drop inventory
- Only drop loot item

#### 0.3.7-1
- Fix TaCZ gamerule config not enabled

#### 0.3.7
- Add Cbrg pack loot config
- Not generate TaCZ loot config when TaCZ is not loaded
- Add gamerule config to disable downed player using TaCZ gun
- Add config to not draw zone on JourneyMap

#### 0.3.6-1
- Fix loot item & entity use sharing NBT data
- Add grappling hook to example TaCZ loot config

#### 0.3.6
Config:
- Gamerule config add many configs
- Add config to not teleport in initGame
- Switch config auto apply first config
- Add spectateEntry to client render config

Game:
- Adjust network protocol to let game spectator render all game teams
- Join game team also build vanilla team
- Adjust default game team color
- Client team info teamId render shader

Other:
- Lobby muteki keeps effect after game and takes effect immediately after reloading config
- Delayed game result, teleport chat message

#### 0.3.5
- Fix utility config not loaded at MC start
- Add debug command to get ServerLevel and get level key used in config
- Add defaultLevelKey, requiredTeamToStart, winnerTeamTotal to gamerule config

#### 0.3.4-2
- Fix TeleportSpawner crash issue
- Add server utility config
- Add survivalLobby to utility config

#### 0.3.4-1
- Add white & black list regex to filter block in loot generation
- Add config to not clear content before generate loot

#### 0.3.4
- Add doTimeSet to gamerule config
- Not reload client config on dedicated server
- Lobby add dimension check
- /cbr game toLobby will teleport to lobby dimension
- lobby muteki only effective in lobby dimension

#### 0.3.3-1
Fix crash on server

#### 0.3.3
- Add horse vehicle to entity spawner config
- Bleeding teammate display red health bar
- Zone render compatible with shader
- Send game player down, revive, elimation messages
- Add config to heal game player at game start

#### 0.3.2
- PlayerRevive: Add bleeding damage and knock out time limit
- PlayerRevive: Kill alive members after team elimination
- Send winner team with team command after game finished
- Send teleport message to all game player after game
- Send game info and spectate command to newly logged in player
- Spectate command teleport to random standing game player and notify all players
- Add config to teleport non-game player to lobby when attacking with game player
- Make zone damage not affect armor durability

#### 0.3.1
- PlayerRevive knockdown mechanism
- Render zone in JourneyMap
- Teleport to lobby will change gamemode
- Allow non-game player to spectate game
- Teleport newly logged in eliminated game player to lobby
- Add new entity spawner config
- Adjust other config

#### 0.3.0
- Fix zone delay stacking calculation
- Correct team size limit after switch config
- Render config add teamEntry
- Adjust default TaCZ loot config
- Cancel game players regroup at game init

### 0.2.x
> Geometry & Loot: Expanded zone shapes and refined loot generation with critical fixes.

#### 0.2.9
- Fix crash at server stopping

#### 0.2.8
- Attempt to fix loot generation issue
- Render teammate with team color
- Add command to spectate game if team is eliminated
- Adjust default performance config

#### 0.2.7
- Offline player auto leave team
- Correct game finish logic
- Fix game player and non-game player mutual damage avoidance logic
- Add friendlyFire, clearInventory, keepInventory config
- Add lobby lobbyHeal config
- Raise loot speed gap
- Teleport add hangTime config

#### 0.2.6
- Add debug command
- Prevent crash when zone damage ends BR game
- Instant send ending messages at game end
- Team size limit will cause reset disband game teams
- Backup offline gameplayer player's gamemode at game start
- Fix team gamerule not recorded to stats

#### 0.2.5
- Team command compatible with command block
- Add short command prefix
- Fix Empty loot issue
- Add Bound loot, Extra loot, Shuffle loot, Clean loot, Biome loot, Structure loot, Regex loot, Message loot
- Add config reading for NBT loot, Range loot, Zone loot (Not implemented yet)
- Performance config add removeLootTable
- Add logo to in-game mod list

#### 0.2.4-hotfix2
Correct inverted config option (not severe)

#### 0.2.4-hotfix
Fix thread crash

#### 0.2.4
Config:
- Add server config, add config to control message
- Apply default config in reloading
- Add segments to zone render config

Game:
- Refactor game loot generation
- Add command to check selected game config
- Correct command permission level
- Instant zone ending message

#### 0.2.3
Configs:
- Add game entry to gamerule config
- Add client config to control render & display
- Add bot config placeholder
- Add equipment, heal potion to default TacZ loot config
- Default tag also switch game selected config id
- Update default PUBG spawn config
- Fix config loading issue, if apply default it also udpate game selected config id

Command:
- Add PUBGMC command compatibility, not enabled by default
- Add command to manually generate default configs
- Add command to control temporary data

Zone:
- Zone damage bypass time, effect, enchantments
- Add global zone offset and teleport spawn support using zone center as origin
- Apply useCircleRange for 3D shape

Other:
- Refactor message sending system
- Render alive game player count

#### 0.2.2
- Add GunFireMode for TacZ gun loot
- Add PUBG default zone, spawn, gamerule config
- Add zone config used in mod cover
- Fix zone dimension calculation
- Send lobby message to eliminated game player
- Add preZoneDelayId , rangeAsStartDimScale, useCircleRange tag
- Adjust auto teleport rule

#### 0.2.1
- Generate both example.json and example_tacz_1.1.6.json when there's no config loaded or missing config file
- Add default tag, one of the single config in a .json file set to true will use the file as initial selected file
- Note: The loot spawner has a high performance overhead in rendering a large number of TacZ gun models. If you have multiple loot spawners in the scene, please go to ./minecraft/config/tacz-client.toml and set "GunLodRenderDistance" to a very low value

#### 0.2.0
Zone shape update: 
- new 2D zone shape: ellipse, star
- new 3D zone shape: sphere, cube, cuboid, ellipsoid
- add zone rotation, lock player center, player center lerp
- add allowBadShape option for negative dimension and inverted space judgement
- scale, range allow negative

Zone function update:
- muteki zone
- boost zone
- particle zone
- effect zone

Command update:
- muteki command
- particle command
- boost command

Other:
- Fix config loading issue
- Fix zone shape issue
- Render dark grey team HUD when offline
- Client clear zone and team render when they're expired
- Additional particle generation after winning battleroyale game

### 0.1.x
> Foundation: Established core BattleRoyale mechanics like loot, teams, and zone damage.

#### 0.1.5
- Add firework command & zone, track player or fixed position
- Send winner message, firework celebration
- Save gamerule, spawn, zone stats
- Add lockPlayer zone center type
- Add zone progress tag

#### 0.1.4
- Load config by file, allow switch config file via command
- Add zone controller, allow switch without permission level
- Fix gamerule backup & restore issue
- Rename spawn config 'ground' to 'teleport'
- Render loot spawner model when 16blocks away or it's empty

#### 0.1.3
Polygon shape, chest loot generation, team HUD, saturation gamerule

- Auto saturation
- Render team info HUD
- Fill boost when game start
- Fix lobby teleport issue
- Cancel same team damage dealt stats
- Generate loot in vanilla chest

Shape update:
- add relative entry
- add regular hexagon shape
- add regular polygon shape

#### 0.1.1
- Item & entity loot generation
- Zone render, damage
- Game example configs
- Team management
- Lobby teleportation