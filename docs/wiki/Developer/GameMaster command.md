[English](#English)

# 游戏管理员指令

游戏管理员需要手动操作本地文件来启用，且游戏中新增的游戏管理员会在重启游戏后失去权限
> 本页Wiki默认读者已经`非常熟悉`模组关键代码的运行原理及机制设计

无法通过提升权限等级执行该指令

## 指令列表

|GM激活|详细指令|简化指令|
|-|-|-|
|本地生成|_/battleroyale ecc_|_/cbr ecc_|
|申请GM|_/battleroyale request_|_/cbr rq_|
|开关GM|_/battleroyale gamemaster_|_/cbr gm_|

### 通用权限
任何游戏管理员都能执行的命令

- [level]：整数等级
- [entity]：选中玩家实体
- [id]：获取详细信息 / 指定游戏玩家ID / 指定队伍ID
- [amount]：整数数值 / 浮点数值

|调试管理|详细指令|简化指令|
|-|-|-|
|调整调试权限|_/battleroyale gamemaster debugpermission_|_/cbr gm dbp_|
||[level]||
|增减调试玩家|_/battleroyale gamemaster debug_|_/cbr gm db_|
||[entity] [bool]||

|游戏管理|详细指令|简化指令|
|-|-|-|
|解除游戏玩家|_/battleroyale gamemaster delete gameplayer_|_/cbr gm del gp_|
||[id / entity]||
|淘汰游戏玩家|_/battleroyale gamemaster forceeliminate gameplayer_|_/cbr gm fe gp_|
||[id / entity]||
|解除游戏队伍|_/battleroyale gamemaster delete gameteam_|_/cbr gm del gt_|
||[id]||
|淘汰游戏队伍|_/battleroyale gamemaster forceeliminate gameteam_|_/cbr gm fe gt_|
||[id / entity]||
|结束游戏区域|_/battleroyale gamemaster delete gamezone_|_/cbr gm del gz_|
||[id / name]||
|添加人机生物|_/battleroyale gamemaster add bot_|_/cbr gm ad bt_|
||[entity]||
|更改人机生物|_/battleroyale gamemaster change bot_|_/cbr gm cg bt_|
||[id / entity] [entity]||
|更改最后位置|_/battleroyale gamemaster change lastpos_|_/cbr gm cg lp_|
||[id / entity] [xyz]||
|更改离线时长|_/battleroyale gamemaster change invalidtime_|_/cbr gm cg it_|
||[id / entity] [xyz]||
|更改最后血量|_/battleroyale gamemaster change lasthealth_|_/cbr gm cg lh_|
||[id / entity] [amount]||
|更改队伍队长|_/battleroyale gamemaster change teamleader_|_/cbr gm cg tl_|
||[id] [id / entity]||

|物资刷新|详细指令|简化指令|
|-|-|-|
|中止物资刷新|_/battleroyale gamemaster delete commonloot_|_/cbr gm del cl_|
|清理游戏刷新队列|_/battleroyale gamemaster delete queuedchunk_|_/cbr gm del qc_|
||[amount / all]||
|清理区块缓存|_/battleroyale gamemaster delete processedchunk_|_/cbr gm del pc_|
||[amount / all]||
|清理中心缓存|_/battleroyale gamemaster delete cachedcenter_|_/cbr gm del cc_|
||[amount / all]||

|消息|详细指令|简化指令|
|-|-|-|
|删除所有消息|_/battleroyale gamemaster delete messages_|_/cbr gm del msgs_|
|删除区域消息|_/battleroyale gamemaster delete zonemessage_|_/cbr gm del zmsg_|
||[min max] / [all]||
|删除队伍消息|_/battleroyale gamemaster delete teammessage_|_/cbr gm del tmsg_|
||[min max] / [all]||
|删除游戏消息|_/battleroyale gamemaster delete gamemessage_|_/cbr gm del gmsg_|
||[min max] / [all]||

|效果队列|详细指令|简化指令|
|-|-|-|
|删除粒子队列|_/battleroyale gamemaster delete particle_|_/cbr gm del pt_|
||[entity / all]||
|删除烟花队列|_/battleroyale gamemaster delete firework_|_/cbr gm del fw_|
||[entity / min max / all]||
|修改无敌时间上限|_/battleroyale gamemaster change mutekitime_|_/cbr gm cg mtt_|
||[amount]||
|修改玩家能量|_/battleroyale gamemaster change boost_|_/cbr gm cg bo_|
||[id / entity] [amount]||
|修改能量恢复频率|_/battleroyale gamemaster change boostheal_|_/cbr gm cg boh_|
||[amount]||
|修改能量效果频率|_/battleroyale gamemaster change boosteffect_|_/cbr gm cg boe_|
||[amount]||

### 原始权限
仅在服务器启动时注册的游戏管理员能够执行以下命令

|GM管理|详细指令|简化指令|
|-|-|-|
|授权GM|_/battleroyale original accept gm_|_/cbr og ac gm_|
||[entity]||
|移除GM|_/battleroyale original delete gm_|_/cbr og del gm_|
||[entity]||

该类指令名称跟功能实现方式相关

|GM保护|详细指令|简化指令|
|-|-|-|
|二次无敌|_/battleroyale original muteki2_|_/cbr og mt2_|
|无摔传送|_/battleroyale original saveteleport_|_/cbr og stp_|
||[id / entity / xyz]||

使用该指令则默认已**充分了解**MC相关代码

|原版操作|详细指令|简化指令|
|-|-|-|
|修改血量|_/battleroyale original sethealth_|_/cbr og sh_|
||[id / entity]||
|修改掉落高度|_/battleroyale original falldistance_|_/cbr og fd_|
||[id / entity]||

|GM能力|详细指令|简化指令|
|-|-|-|
|事件修改伤害|_/battleroyale original eventdamage_|_/cbr og ed_|
||[amount]||
|最后位置引导|_/battleroyale original lastpos_|_/cbr og lp_|
||[id / entity]||

|GM客户端|详细指令|简化指令|
|-|-|-|
|本地实体光柱|_/battleroyale original localentity_|_/cbr og le_|
||[amount]||

# English

GameMasters need to manually edit local files to enable these commands. Additionally, any new GameMasters added in-game will lose their permissions after a game restart.
> This Wiki page assumes the reader is `very familiar` with the core principles and mechanism design of the mod's critical code.

These commands cannot be executed by simply raising permission levels.

## Command List

|GM Activation|Detailed Command|Simplified Command|
|-|-|-|
|Local Generation|_/battleroyale ecc_|_/cbr ecc_|
|Request GM|_/battleroyale request_|_/cbr rq_|
|Toggle GM|_/battleroyale gamemaster_|_/cbr gm_|

### General Permissions

Commands that any Game Master can execute.
- [level]: Integer level
- [entity]: Selected player entity
- [id]: Get detailed information / Specified game player ID / Specified team ID
- [amount]: Integer value / Floating-point value

|Debug Management|Detailed Command|Simplified Command|
|-|-|-|
|Adjust Debug Permission|_/battleroyale gamemaster debugpermission_|_/cbr gm dbp_|
||[level]||
|Add/Remove Debug Player|_/battleroyale gamemaster debug_|_/cbr gm db_|
||[entity] [bool]||

|Game Management|Detailed Command|Simplified Command|
|-|-|-|
|Remove GamePlayer|_/battleroyale gamemaster delete gameplayer_|_/cbr gm del gp_|
||[id / entity]||
|Force Eliminate GamePlayer|_/battleroyale gamemaster forceeliminate gameplayer_|_/cbr gm fe gp_|
||[id / entity]||
|Remove GameTeam|_/battleroyale gamemaster delete gameteam_|_/cbr gm del gt_|
||[id]||
|Force Eliminate GameTeam|_/battleroyale gamemaster forceeliminate gameteam_|_/cbr gm fe gt_|
||[id / entity]||
|End GameZone|_/battleroyale gamemaster delete gamezone_|_/cbr gm del gz_|
||[id / name]||
|Add Bot Entity|_/battleroyale gamemaster add bot_|_/cbr gm ad bt_|
||[entity]||
|Change Bot Entity|_/battleroyale gamemaster change bot_|_/cbr gm cg bt_|
||[id / entity] [entity]||
|Change Last Position|_/battleroyale gamemaster change lastpos_|_/cbr gm cg lp_|
||[id / entity] [xyz]||
|Change Offline Duration|_/battleroyale gamemaster change invalidtime_|_/cbr gm cg it_|
||[id / entity] [xyz]||
|Change Last Health|_/battleroyale gamemaster change lasthealth_|_/cbr gm cg lh_|
||[id / entity] [amount]||
|Change Team Leader|_/battleroyale gamemaster change teamleader_|_/cbr gm cg tl_|
||[id] [id / entity]||

|Loot Generation|Detailed Command|Simplified Command|
|-|-|-|
|Stop Loot Generation|_/battleroyale gamemaster delete commonloot_|_/cbr gm del cl_|
|Clear Game Queue|_/battleroyale gamemaster delete queuedchunk_|_/cbr gm del qc_|
||[amount / all]||
|Clear Chunk Cache|_/battleroyale gamemaster delete processedchunk_|_/cbr gm del pc_|
||[amount / all]||
|Clear Center Cache|_/battleroyale gamemaster delete cachedcenter_|_/cbr gm del cc_|
||[amount / all]||

|Messages|Detailed Command|Simplified Command|
|-|-|-|
|Delete All Messages|_/battleroyale gamemaster delete messages_|_/cbr gm del msgs_|
|Delete ZoneMessages|_/battleroyale gamemaster delete zonemessage_|_/cbr gm del zmsg_|
||[min max] / [all]||
|Delete TeamMessages|_/battleroyale gamemaster delete teammessage_|_/cbr gm del tmsg_|
||[min max] / [all]||
|Delete GameMessages|_/battleroyale gamemaster delete gamemessage_|_/cbr gm del gmsg_|
||[min max] / [all]||

|Effect Queues|Detailed Command|Simplified Command|
|-|-|-|
|Delete Particle Queue|_/battleroyale gamemaster delete particle_|_/cbr gm del pt_|
||[entity / all]||
|Delete Firework Queue|_/battleroyale gamemaster delete firework_|_/cbr gm del fw_|
||[entity / min max / all]||
|Modify Muteki Duration Limit|_/battleroyale gamemaster change mutekitime_|_/cbr gm cg mtt_|
||[amount]||
|Modify Player Boost|_/battleroyale gamemaster change boost_|_/cbr gm cg bo_|
||[id / entity] [amount]||
|Modify Boost Recovery Frequency|_/battleroyale gamemaster change boostheal_|_/cbr gm cg boh_|
||[amount]||
|Modify Boost Effect Frequency|_/battleroyale gamemaster change boosteffect_|_/cbr gm cg boe_|
||[amount]||

### Original Permissions

Commands that can only be executed by GameMasters registered when the server starts.

|GM Management|Detailed Command|Simplified Command|
|-|-|-|
|Authorize GM|_/battleroyale original accept gm_|_/cbr og ac gm_|
||[entity]||
|Remove GM|_/battleroyale original delete gm_|_/cbr og del gm_|
||[entity]||

The names of these commands are related to their functional implementation.

|GM Protection|Detailed Command|Simplified Command|
|-|-|-|
|Double muteki|_/battleroyale original muteki2_|_/cbr og mt2_|
|Fall Damage-Free Teleport|_/battleroyale original saveteleport_|_/cbr og stp_|
||[id / entity / xyz]||

Using these commands implies **full understanding** of relevant MC code.

|Vanilla Operations|Detailed Command|Simplified Command|
|-|-|-|
|Modify Health|_/battleroyale original sethealth_|_/cbr og sh_|
||[id / entity]||
|Modify Fall Distance|_/battleroyale original falldistance_|_/cbr og fd_|
||[id / entity]||

|GM Abilities|Detailed Command|Simplified Command|
|-|-|-|
|Event Modify Damage|_/battleroyale original eventdamage_|_/cbr og ed_|
||[amount]||
|Last Position Guidance|_/battleroyale original lastpos_|_/cbr og lp_|
||[id / entity]||

|GM Client|Detailed Command|Simplified Command|
|-|-|-|
|Local Entity Beacon|_/battleroyale original localentity_|_/cbr og le_|
||[amount]||