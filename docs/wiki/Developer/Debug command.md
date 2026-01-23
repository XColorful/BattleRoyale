# 调试指令

该指令与模组代码强相关，仅用于开发者以及熟悉模组机制的服主调试模组内部信息
> 本页Wiki默认读者已通过模组源代码了解相关机制

默认需要权限等级4，可由GameMaster修改

## 指令列表

- [min max]：选取范围[min, max] / 队列范围
- all：选取范围[Integer.MIN_VALUE, Integer.MAX_VALUE-1]
- [name]：遍历搜索选取范围 / 玩家名称
- [entity]：选取单个实体
- [player]：选择单个玩家
- [id]：获取详细信息 / 指定游戏玩家ID
- [xyz]：指定坐标

> 精确获取GamePlayer使用 _@p[name=]_，否则将遍历搜索GamePlayer.playerName且不保证唯一性

|游戏信息|详细指令|简化指令|
|-|-|-|
|获取游戏玩家|_/battleroyale debug get gameplayers_|_/cbr db get gps_|
||[min max / all]|
||_/battleroyale debug get gameplayer_|_/cbr db get gp_|
||[id/entity/name]|
|获取游戏队伍|_/battleroyale debug get gameteams_|_/cbr db get gts_|
||[min max / all]|
||_/battleroyale debug get gameteam_|_/cbr db get gt_|
||[id]|
|获取游戏区域|_/battleroyale debug get gamezones_|_/cbr db get gzs_|
||[min max / all]|
||_/battleroyale debug get gamezone_|_/cbr db get gz_|
||[id/name]|
|获取玩家模式备份|_/battleroyale debug get backupplayermodes_|_/cbr db get bpms_|
||[min max / all]|
||_/battleroyale debug get backupplayermode_|_/cbr db get bpm_|
||[id/name/entity]|
|获取原版规则备份|_/battleroyale debug get backupgamerule_|_/cbr db get bgr_|

|物资刷新|详细指令|简化指令|
|-|-|-|
|获取物资刷新状态|_/battleroyale debug get commonloot_|_/cbr db get cl_|
|获取游戏刷新状态|_/battleroyale debug get gameloot_|_/cbr db get gl_|
||[xyz]||

|消息|详细指令|简化指令|
|-|-|-|
|获取消息|_/battleroyale debug get messages_|_/cbr db get msgs_|
|获取区域消息|_/battleroyale debug get zonemessages_|_/cbr db get zmsgs_|
||[min max / all]|
||_/battleroyale debug get zonemessage_|_/cbr db get zmsg_|
||[id/name]|
|获取队伍消息|_/battleroyale debug get teammessages_|_/cbr db get tmsgs_|
||[min max / all]|
||_/battleroyale debug get teammessage_|_/cbr db get tmsg_|
||[id]|
|获取游戏消息|_/battleroyale debug get gamemessages_|_/cbr db get gmsgs_|
||[min max / all]|
||_/battleroyale debug get gamemessage_|_/cbr db get gmsg_|
||[id]|

|效果队列|详细指令|简化指令|
|-|-|-|
|获取粒子队列|_/battleroyale debug get particles_|_/cbr db get pts_|
||[min max / all]|
||_/battleroyale debug get particle_|_/cbr db get pt_|
||[channel / entity channel] [min max / all]|
|获取烟花队列|_/battleroyale debug get fireworks_|_/cbr db get fws_|
||[min max / all]|
||_/battleroyale debug get firework_|_/cbr db get fw_|
||[id / entity]|
|获取无敌队列|_/battleroyale debug get mutekis_|_/cbr db get mts_|
||[min max / all]|
||_/battleroyale debug get muteki_|_/cbr db get mt_|
||[id / entity]|
|获取玩家能量|_/battleroyale debug get boosts_|_/cbr db get bos_|
||[min max / all]|
||_/battleroyale debug get boost_|_/cbr db get bo_|
||[id / entity]|

|世界|详细指令|简化指令|
|-|-|-|
|获取区块方块实体NBT|_/battleroyale debug get blockentitiesnbt_|_/cbr db get besnbt_|
||[xyz]|
|获取方块实体NBT|_/battleroyale debug get blockentitynbt_|_/cbr db get benbt_|
||[xyz]|
|获取玩家背包物品NBT|_/battleroyale debug get itemstacksnbt_|_/cbr db get isnbt_|
||[player] [min max / all]||
|获取玩家手持物品NBT|_/battleroyale debug get itemstacknbt_|_/cbr db get inbt_|
||[player]||
|获取群系|_/battleroyale debug get biome_|_/cbr db get bi_|
||[xyz]|
|获取建筑|_/battleroyale debug get structures_|_/cbr db get ss_|
||[xyz]|
|获取维度|_/battleroyale debug get serverlevel_|_/cbr db get sl_|
||[name]||
|获取当前维度|_/battleroyale debug get levelkey_|_/cbr db get lk_|

|本地调试|详细指令|简化指令|
|-|-|-|
|显示消息|_/battleroyale localdebug get messages_|_/cbr ldb get msgs_|
|显示区域消息|_/battleroyale localdebug get zonemessages_|_/cbr ldb get zmsgs_|
||[min max / all]|
||_/battleroyale localdebug get zonemessage_|_/cbr ldb get zmsg_|
||[id/name]|
|显示队伍消息|_/battleroyale localdebug get teammessages_|_/cbr ldb get tmsgs_|
||[min max / all]|
||_/battleroyale localdebug get teammessage_|_/cbr ldb get tmsg_|
||[id]|
|显示游戏消息|_/battleroyale localdebug get gamemessages_|_/cbr ldb get gmsgs_|
||[min max / all]|
||_/battleroyale localdebug get gamemessage_|_/cbr ldb get gmsg_|
||[id]|

# English

This command is strongly related to the mod code and is only for developers and server owners familiar with the mod's mechanics to debug internal mod information.
> This Wiki page assumes the reader is already familiar with the relevant mechanics through the mod's source code.

Default permission level required is 4, modifiable by GameMaster.

## Command List

- [min max]: Select range [min, max] / queue range
- all: Select range [Integer.MIN_VALUE, Integer.MAX_VALUE-1]
- [name]: Iterate search selection range / player name
- [entity]: Select single entity
- [player]: Select single player
- [id]: Get detailed information / specified game player ID
- [xyz]: Specify coordinates

> To precisely get a GamePlayer, use _@p[name=]_, otherwise, it will iterate search GamePlayer.playerName and uniqueness is not guaranteed.

|Game Information|Detailed Command|Simplified Command|
|-|-|-|
|Get GamePlayers|_/battleroyale debug get gameplayers_|_/cbr db get gps_|
||[min max / all]|
||_/battleroyale debug get gameplayer_|_/cbr db get gp_|
||[id/entity/name]|
|Get GameTeams|_/battleroyale debug get gameteams_|_/cbr db get gts_|
||[min max / all]|
||_/battleroyale debug get gameteam_|_/cbr db get gt_|
||[id]|
|Get GameZones|_/battleroyale debug get gamezones_|_/cbr db get gzs_|
||[min max / all]|
||_/battleroyale debug get gamezone_|_/cbr db get gz_|
||[id/name]|
|Get Player Mode Backups|_/battleroyale debug get backupplayermodes_|_/cbr db get bpms_|
||[min max / all]|
||_/battleroyale debug get backupplayermode_|_/cbr db get bpm_|
||[id/name/entity]|
|Get Vanilla Rule Backups|_/battleroyale debug get backupgamerule_|_/cbr db get bgr_|

|Loot Generation|Detailed Command|Simplified Command|
|-|-|-|
|Get Loot Generation Status|_/battleroyale debug get commonloot_|_/cbr db get cl_|
|Get Game Loot Status|_/battleroyale debug get gameloot_|_/cbr db get gl_|
||[xyz]||

|Messages|Detailed Command|Simplified Command|
|-|-|-|
|Get Messages|_/battleroyale debug get messages_|_/cbr db get msgs_|
|Get ZoneMessages|_/battleroyale debug get zonemessages_|_/cbr db get zmsgs_|
||[min max / all]|
||_/battleroyale debug get zonemessage_|_/cbr db get zmsg_|
||[id/name]|
|Get TeamMessages|_/battleroyale debug get teammessages_|_/cbr db get tmsgs_|
||[min max / all]|
||_/battleroyale debug get teammessage_|_/cbr db get tmsg_|
||[id]|
|Get GameMessages|_/battleroyale debug get gamemessages_|_/cbr db get gmsgs_|
||[min max / all]|
||_/battleroyale debug get gamemessage_|_/cbr db get gmsg_|
||[id]|

|Effect Queues|Detailed Command|Simplified Command|
|-|-|-|
|Get Particle Queues|_/battleroyale debug get particles_|_/cbr db get pts_|
||[min max / all]|
||_/battleroyale debug get particle_|_/cbr db get pt_|
||[channel / entity channel] [min max / all]|
|Get Firework Queues|_/battleroyale debug get fireworks_|_/cbr db get fws_|
||[min max / all]|
||_/battleroyale debug get firework_|_/cbr db get fw_|
||[id / entity]|
|Get Muteki Queues|_/battleroyale debug get mutekis_|_/cbr db get mts_|
||[min max / all]|
||_/battleroyale debug get muteki_|_/cbr db get mt_|
||[id / entity]|
|Get Player Boosts|_/battleroyale debug get boosts_|_/cbr db get bos_|
||[min max / all]|
||_/battleroyale debug get boost_|_/cbr db get bo_|
||[id / entity]|

|World|Detailed Command|Simplified Command|
|-|-|-|
|Get Chunk Block Entity NBT|_/battleroyale debug get blockentitiesnbt_|_/cbr db get besnbt_|
||[xyz]|
|Get Block Entity NBT|_/battleroyale debug get blockentitynbt_|_/cbr db get benbt_|
||[xyz]|
|Get Player Inventory Items NBT|_/battleroyale debug get itemstacksnbt_|_/cbr db get isnbt_|
||[player] [min max / all]||
|Get Player Handheld Item NBT|_/battleroyale debug get itemstacknbt_|_/cbr db get inbt_|
||[player]||
|Get Biome|_/battleroyale debug get biome_|_/cbr db get bi_|
||[xyz]|
|Get Structures|_/battleroyale debug get structures_|_/cbr db get ss_|
||[xyz]|
|Get Level|_/battleroyale debug get serverlevel_|_/cbr db get sl_|
||[name]||
|Get Current Level|_/battleroyale debug get levelkey_|_/cbr db get lk_|

|Local Debugging|Detailed Command|Simplified Command|
|-|-|-|
|Display Messages|_/battleroyale localdebug get messages_|_/cbr ldb get msgs_|
|Display ZoneMessages|_/battleroyale localdebug get zonemessages_|_/cbr ldb get zmsgs_|
||[min max / all]|
||_/battleroyale localdebug get zonemessage_|_/cbr ldb get zmsg_|
||[id/name]|
|Display TeamMessages|_/battleroyale localdebug get teammessages_|_/cbr ldb get tmsgs_|
||[min max / all]|
||_/battleroyale localdebug get teammessage_|_/cbr ldb get tmsg_|
||[id]|
|Display GameMessages|_/battleroyale localdebug get gamemessages_|_/cbr ldb get gmsgs_|
||[min max / all]|
||_/battleroyale localdebug get gamemessage_|_/cbr ldb get gmsg_|
||[id]|