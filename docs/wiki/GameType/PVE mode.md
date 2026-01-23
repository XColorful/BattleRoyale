[English](#English)

# PVE模式
玩家合作对抗出现的敌人

## 存活至上
活下去

---

### 末日求生
安全区不断缩小，并且地图上刷新的怪物数量不断增多且难以对付。玩家必须在各个资源富集区间穿梭以获取足够物资抵御怪物。游戏期间将随机生成大面积的封锁光柱及驱逐光柱，干扰玩家的行动策略。成功坚持到最后即可返回安全地带取得胜利。

#### 游戏前准备
**游戏地图**：
- 一张1024x1024至8192x8192的游戏地图，分布足够数量的含有物资刷新器的建筑

**游戏配置**：
- 物资刷新配置分阶段包含不同稀有度的物品，使用时间刷新词条控制各阶段刷新的物资，敌对生物数量及种类
> 如果需要刷新中立/友好生物，建议单词条只刷新敌对或非敌对生物

#### 建议规范
**游戏规则配置**：
- 游戏时长20-60分钟
- 根据敌对生物是否能应对“挖三填一”策略选用冒险模式
- 队伍规模不限，共1队+人机队占位

**出生配置**：
- 随机出生在边界范围内或固定出生点
- 可用较大的随机偏移来使队伍成员分散出生但又控制最大距离

**区域配置**：
- 固定边界安全区：不透明颜色，秒杀伤害
- 缩圈安全区：**低透明颜色**，**高伤害**
- 封锁光柱预备：特殊半透明颜色，无伤害
- 封锁光柱：低透明颜色，高伤害
- 驱逐光柱：低透明颜色，高伤害，从边界缩小中心向地图内随机位置移动，距离越远速度越快

## 持续抵抗
我本无意杀戮，直到你们源源不断地出现在我家客厅

---

### 挑战模式
玩家在固定区域内抵御持续刷新的怪物，直至消灭最终的幕后黑手。游戏期间将随机生成封锁光柱及驱逐光柱，阻碍玩家行动。

#### 游戏前准备
**游戏地图**：
- 一张不大于512x512游戏地图，分布足够数量的含有物资刷新器的建筑

**游戏配置**：
- 物资刷新配置可分阶段刷新不同数量及稀有度的物品，可定点刷新弓箭及弹药等消耗品
- 使用时间刷新词条控制各阶段刷新的敌对生物数量及种类
> 如果需要刷新中立/友好生物，建议单词条只刷新敌对或非敌对生物

#### 建议规范
**游戏规则配置**：
- 游戏时长20-40分钟
- 启用冒险模式
- 队伍规模不限，共1队+人机队占位
- 禁用自然恢复，启用自动饱和度

**出生配置**：
- 玩家队伍出生在地图固定出生点，可增加一些随机偏移

**区域配置**：
- 固定边界安全区：不透明颜色，秒杀伤害
- 封锁光柱预备：特殊半透明颜色，无伤害
- 封锁光柱：低透明颜色，高伤害
- 驱逐光柱：低透明颜色，高伤害，从边界中心向地图内随机位置移动，距离越远速度越快

---

### 突袭任务
玩家前往各区域内抵御持续刷新的怪物，直至消灭最终的幕后黑手。

#### 游戏前准备
**游戏地图**：
- 一张不大于512x512游戏地图，分布足够数量的含有物资刷新器的建筑

**游戏配置**：
- 使用时间刷新词条控制物资刷新配置在不同阶段刷新不同数量及稀有度的物品
- 使用时间刷新词条控制各阶段刷新的敌对生物数量及种类

#### 建议规范
**游戏规则配置**：
- 游戏时长20-40分钟
- 启用冒险模式
- 队伍规模不限，共1队+人机队占位
- 禁用自然恢复，启用自动饱和度

**出生配置**：
- 玩家队伍出生在地图固定出生点，可增加一些随机偏移

**区域配置**：
> 每阶段开始时仅有边界安全区，移动阶段时边界安全区改为不安全区围绕实现，并有充当移动通道的安全区和强迫玩家移动的不安全区
- 边界安全区：不透明颜色，秒杀伤害
- 通道安全区：不透明颜色，秒杀伤害
- 移动不安全区：不透明颜色，秒杀伤害

# English
Players cooperate to fight against emerging enemies.

## Survival Focus
Just survive.

---

### Doomsday Survival
The safe zone constantly shrinks, and the number of monsters spawning on the map continuously increases and becomes harder to deal with. Players must traverse various resource-rich areas to acquire enough supplies to defend against monsters. During the game, large-scale blockade beams and expulsion beams will randomly generate, interfering with players' action strategies. Successfully persisting until the end allows players to return to safety and achieve victory.

#### Pre-Game Setup
**Game Map**:
- A game map from 1024x1024 to 8192x8192, with a sufficient number of buildings containing loot spawners.

**Game Configuration**:
- Loot spawner config includes items of different rarities in stages, using time entries to control the generation, hostile creature quantity, and types spawned in each stage.
> If neutral/friendly creatures need to be spawned, it is recommended that a single entry only spawns hostile or non-hostile creatures.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 20-60 minutes.
- Use adventure mode based on whether ground creatures can counter the "Dig Three Fill One" strategy.
- Team size is unlimited, with 1 team + AI team filling slots.
- Disable natural regeneration, enable auto saturation.

**Spawn Configuration**:
- Random spawn within boundaries or at fixed spawn points.
- Larger random offsets can be used to scatter team members while controlling maximum distance.

**Zone Configuration**:
- Fixed Boundary Safe Zone: Opaque color, instant kill damage.
- Shrinking Safe Zone: **Low transparency color**, **high damage**.
- Blockade Beam Preparation: Special semi-transparent color, no damage.
- Blockade Beam: Low transparency color, high damage.
- Expulsion Beam: Low transparency color, high damage, moves from the shrinking boundary center towards a random position within the map, increasing speed with distance.

## Endless Resistance
I had no intention of killing... until you kept spawning in my living room.

---

### Challenge Mode
Players defend a fixed area against continuously spawning monsters until they eliminate the final mastermind. During the game, blockade beams and expulsion beam will randomly generate, hindering player movement.

#### Pre-Game Setup
**Game Map**:
- A game map no larger than 512x512, with a sufficient number of buildings containing loot spawners.

**Game Configuration**:
- Loot spawner config can generate items of different quantities and rarities in stages, and can generate consumables like bows and arrows at fixed points.
- Use time entries to control the quantity and types of hostile creatures spawned in each stage.
> If neutral/friendly creatures need to be spawned, it is recommended that a single entry only spawns hostile or non-hostile creatures.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 20-40 minutes.
- Adventure mode enabled.
- Team size is unlimited, with 1 team + AI team filling slots.
- Disable natural regeneration, enable auto saturation.

**Spawn Configuration**:
- Player team spawns at fixed spawn points on the map, with some random offsets.

**Zone Configuration**:
- Fixed Boundary Safe Zone: Opaque color, instant kill damage.
- Blockade Beams Preparation: Special semi-transparent color, no damage.
- Blockade Beams: Low transparency color, high damage.
- Expulsion Beams: Low transparency color, high damage, moves from the boundary center towards a random position within the map, increasing speed with distance.

---

### Raid Mission
Players proceed to various areas to defend against continuously spawning monsters until they eliminate the final mastermind.

#### Pre-Game Setup
**Game Map**:
- A game map no larger than 512x512, with a sufficient number of buildings containing loot spawners.

**Game Configuration**:
- Use time entries to control loot spawner config to generate items of different quantities and rarities in different stages.
- Use time entries to control the quantity and types of hostile creatures spawned in each stage.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 20-40 minutes.
- Adventure mode enabled.
- Team size is unlimited, with 1 team + AI team filling slots.

**Spawn Configuration**:
- Player team spawns at fixed spawn points on the map, with some random offsets.

**Zone Configuration**:
> At the start of each stage, there is only a boundary safe zone. During movement stages, the boundary safe zone becomes an unsafe zone, designed to encompass movement, with safe zones acting as moving corridors and unsafe zones forcing player movement.
- Boundary Safe Zone: Opaque color, instant kill damage.
- Corridor Safe Zone: Opaque color, instant kill damage.
- Moving Unsafe Zone: Opaque color, instant kill damage.