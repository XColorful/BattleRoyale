[English](#English)

# 休闲模式
轻松有趣的游戏体验，减少竞技压力，增加互动性。

## 冲刺
冲冲冲

---

### 最速传说
场地内仅刷新辅助类物品，不包含高杀伤性的武器。落后的玩家将被身后追赶的大圈吞噬。

#### 游戏前准备
**游戏地图**：
- 新建原版存档或任意规模地图

**游戏配置**：
- 可选物资刷新器刷新加速物品（迅捷药水与跳跃药水），阻碍行动的物品（雪球），末影珍珠

#### 建议规范
- 游戏时长3-20分钟
- 队伍规模为1-50人
- 禁用自然恢复，启用自动饱和度
- 根据自定特殊规则选择启用冒险模式
> 例如不允许接触水而需要破坏原版地形获取方块来渡海，则禁用冒险模式

**出生配置**：
- 按队伍并排分配固定点位，或使用单个固定点位并增加随机偏移

**区域配置**：
> 根据赛道复杂程度搭建足够数量的赛道边界不安全区
> 可添加一些半透明赛道边界不安全区作为捷径，消耗一定血量为代价
- 固定边界安全区：不透明颜色，秒杀伤害
- 赛道边界不安全区：低透明颜色，高伤害
- 追赶不安全区：特殊半透明颜色，高伤害

## 追与逃
合作抓住他们，或拼尽全力逃脱追捕

---

### 生死时速
玩家分为警察与小偷阵营，初始点分别刷新强力近战武器和加速物品。地图内刷新加速物品可供两阵营拾取，并额外刷新道路阻拦物品，拦截追捕或封锁逃亡路线。游戏期间额外刷新封锁光柱和驱逐光柱干扰小偷走位或削减状态。直至游戏结束仍有小偷存活则胜利。

#### 游戏前准备
**游戏地图**：
- 一张不大于512x512的跑酷地图或规模更小但建筑复杂的游戏地图，可选择分布足够数量的物资刷新器

**游戏配置**：
- 可选物资刷新器刷新加速物品（迅捷药水与跳跃药水等），阻碍行动的物品（雪球）
- 警察出生点刷新拥有强大附魔的木棍或控制杆杆，一定数量的末影珍珠（提供强力追击次数或防止难以抵达跑酷点位），足够强大的防具使光柱伤害不具威胁
> 根据期望的攻击次数确定武器伤害，非一刀斩则额外增加高击退效果
> 若期望两次击杀，则伤害可设置为15，以增加小偷摔落和未躲避光柱的惩罚

#### 建议规范
**游戏规则配置**：
- 游戏时长3-15分钟
- 启用冒险模式
- 队伍规模不限，其中警察成员数小于小偷
- 禁用自然恢复，启用自动饱和度

**出生配置**：
- 两阵营各自分配固定出生位置，或按队伍随机出生

**区域配置**：
- 固定边界安全区：不透明颜色，秒杀伤害
- 封锁光柱预备：特殊半透明颜色，无伤害
- 封锁光柱：低透明颜色，高伤害
- 驱逐光柱：低透明颜色，高伤害，逐渐扩大半径并往随机方向移动

---

### 无形忙碌
非物化躲猫猫。躲藏方通过牵制抓捕方或利用掩体周旋等方式，争取时间并拾取物品材料抵御抓捕方。游戏期间额外刷新封锁光柱和驱逐光柱干扰躲藏方走位或削减状态，并周期性生成烟花区以提示行踪。限定时间内仍有躲藏方存活即胜利。

#### 游戏前准备
**游戏地图**：
- 一张不大于256x256的游戏地图，包含足够数量的物资刷新器

**游戏配置**：
- 躲藏方出生点刷新若干隐身药水和迅捷药水
- 抓捕方出生点刷新拥有一刀斩效果的木棍或控制杆杆，一定数量的末影珍珠（提供强力追击次数或防止难以抵达跑酷点位），足够强大的防具使光柱伤害不具威胁
> 若抓捕方武器伤害足以秒杀满血躲藏方，则影响躲藏方消耗血量跑酷躲避追捕的决策，并使追捕更紧张刺激

#### 建议规范
**游戏规则配置**：
- 游戏时长3-15分钟
- 启用冒险模式
- 队伍规模不限，其中抓捕方成员数小于躲藏方
- 禁用自然恢复，启用自动饱和度

**出生配置**：
- 按队伍分配两方固定初始点位

**区域配置**：
- 初始边界安全区：不透明颜色，秒杀伤害
- 初始阵营分隔不安全区：不透明颜色，秒杀伤害，用于为躲藏方提供准备时间
- 封锁光柱预备：特殊半透明颜色，无伤害
- 封锁光柱：低透明颜色，高伤害
- 驱逐光柱：低透明颜色，高伤害，逐渐扩大半径并往随机方向移动
- 周期烟花区：不透明颜色，略小于边界安全区（提供颜色提示并消除z-fighting）

---

### 迷城探险
玩家阵营分为探险者和监守者，依次进入迷宫区域。游戏期间随机刷新你封锁光柱和驱逐光柱干扰行动，并周期性生成烟花区以提示行踪。限定时间内探险者需抵达宝藏点取得强力武器并返回入口，到指定时间将有不安全区从宝藏点往入口方向驱逐两方玩家。若探险者未取得强力武器，则最终必将在入口处与监守者展开实力悬殊的搏斗。

#### 游戏前准备
**游戏地图**：
- 一张256x256至1024x1024的游戏地图，包含足够数量的物资刷新器

**游戏配置**：
- 游戏时长10-30分钟
- 启用冒险模式
- 队伍规模不限，其中监守者成员数小于探险者
- 禁用自然恢复，启用自动饱和度

#### 建议规范
**游戏规则配置**：
- 游戏时长10-20分钟
- 启用冒险模式
- 队伍规模不限，其中监守者成员数小于探险者
- 禁用自然恢复，启用自动饱和度

**出生配置**：
- 按队伍分配两方固定初始点位，并使探险者出生点距离迷宫区域入口更近

**区域配置**：
> 若地图不含迷宫围墙，则需要手动利用区域模拟围墙
- 初始边界安全区：不透明颜色，秒杀伤害
- 初始阵营分隔不安全区：不透明颜色，秒杀伤害，用于为探险者方提供准备时间
- 封锁光柱预备：特殊半透明颜色，无伤害
- 封锁光柱：低透明颜色，高伤害
- 驱逐光柱：低透明颜色，高伤害，逐渐扩大半径并往随机方向移动
- 周期烟花区：不透明颜色，略小于边界安全区（提供颜色提示并消除z-fighting）

# English
Relaxed and enjoyable game experience, reducing competitive pressure and increasing interactivity.

## Dash
Go, Go, Go!

---

### The Fastest Legend
Only auxiliary items spawn in the arena, excluding high-damage weapons. Lagging players will be swallowed by the large circle chasing from behind.

#### Pre-Game Setup
**Game Map**:
- New vanilla save or any size map.

**Game Configuration**:
- Optional loot spawners generate acceleration items (Swiftness Potions and Leaping Potions), movement-hindering items (Snowballs), and Ender Pearls.

#### Recommended Specifications
- Game duration: 3-20 minutes.
- Team size: 1-50 players.
- Disable natural regeneration, enable auto saturation.
- Choose to enable adventure mode based on custom special rules.
> For example, if contact with water is not allowed and players need to break vanilla terrain blocks to cross the sea, then adventure mode is disabled.

**Spawn Configuration**:
- Teams are assigned fixed side-by-side spawn points, or a single fixed spawn point with random offset is used.

**Zone Configuration**:
> Set up a sufficient number of track boundary unsafe zones based on track complexity.
> Semi-transparent track boundary unsafe zones can be added as shortcuts, at the cost of some health.
- Fixed Boundary Safe Zone: Opaque color, instant kill damage.
- Track Boundary Unsafe Zone: Low transparency color, high damage.
- Chasing Unsafe Zone: Special semi-transparent color, high damage.

## Chase & Escape
Cooperate to catch them, or give it your all to escape pursuit.

---

### Race Against Time
Players are divided into Police and Thief factions. Initial spawn points refresh powerful melee weapons for Police and acceleration items for Thieves. Acceleration items can be picked up by both factions on the map, and additional road-blocking items spawn to intercept pursuit or block escape routes. During the game, additional blockade beams and expulsion beams refresh to interfere with Thief movement or deplete their status. Victory is achieved if any Thief remains alive until the game ends.

#### Pre-Game Setup
**Game Map**:
- A parkour map no larger than 512x512 or a smaller but architecturally complex game map, optionally with a sufficient number of loot spawners.

**Game Configuration**:
- Optional loot spawners refresh acceleration items (Swiftness Potions and Leaping Potions, etc.) and movement-hindering items (Snowballs).
- Police spawn points refresh wooden sticks or levers with powerful enchantments, a certain number of Ender Pearls (providing powerful pursuit opportunities or preventing difficult-to-reach parkour spots), and sufficiently strong armor to render beam damage non-threatening.
> Determine weapon damage based on expected number of hits; if not a one-hit kill, additionally add high knockback effect.
> If two kills are expected, damage can be set to 15, to increase the penalty for Thieves falling or failing to avoid beams.

#### Recommended Specifications

**Game Rules Configuration**:
- Game duration: 3-15 minutes.
- Adventure mode enabled.
- Team size is unlimited, with fewer Police members than Thieves.
- Disable natural regeneration, enable auto saturation.

**Spawn Configuration**:
- Both factions are assigned fixed spawn locations, or random team spawns.

**Zone Configuration**:
- Fixed Boundary Safe Zone: Opaque color, instant kill damage.
- Blockade Beam Preparation: Special semi-transparent color, no damage.
- Blockade Beams: Low transparency color, high damage.
- Expulsion Beams: Low transparency color, high damage, gradually expands radius and moves in random directions.

---

### Invisible Hustle
Non-materialized hide-and-seek. Hiders gain time and pick up items and materials to defend against Catchers by distracting them or utilizing cover for maneuvering. During the game, additional blockade beams and expulsion beams refresh to interfere with Hider movement or deplete their status, and periodic fireworks zones to indicate whereabouts. Victory is achieved if any Hider remains alive within the time limit.

#### Pre-Game Setup

**Game Map**:
- A game map no larger than 256x256, containing a sufficient number of loot spawners.

**Game Configuration**:
- Hider spawn points refresh several Invisibility Potions and Swiftness Potions.
- Catcher spawn points refresh wooden sticks or levers with one-hit kill effect, a certain number of Ender Pearls (providing powerful pursuit opportunities or preventing difficult-to-reach parkour spots), and sufficiently strong armor to render beam damage non-threatening.
> If Catcher weapon damage is sufficient to one-shot a full-health Hider, it influences Hiders' decisions to spend health for parkour evasion and makes the pursuit more intense and thrilling.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 3-15 minutes.
- Adventure mode enabled.
- Team size is unlimited, with fewer Catcher members than Hiders.
- Disable natural regeneration, enable auto saturation.

**Spawn Configuration**:
- Teams are assigned fixed initial spawn points for both sides.

**Zone Configuration**:
- Initial Boundary Safe Zone: Opaque color, instant kill damage.
- Initial Faction Separation Unsafe Zone: Opaque color, instant kill damage, used to provide preparation time for Hiders.
- Blockade Beam Preparation: Special semi-transparent color, no damage.
- Blockade Beams: Low transparency color, high damage.
- Expulsion Beams: Low transparency color, high damage, gradually expands radius and moves in random directions.
- Periodic firework zone: Opaque color, slightly smaller than boundary safe zone (provides color hint and eliminates z-fighting)

---

### Maze Expedition

Player factions are divided into Explorers and Overseers, entering the maze area sequentially. During the game, random blockade beams and expulsion beams refresh to interfere with movement, and periodic fireworks zones to indicate whereabouts. Within the time limit, Explorers must reach a treasure point to acquire powerful weapons and return to the entrance. At a designated time, an unsafe zone will expel both factions from the treasure point towards the entrance. If Explorers fail to acquire powerful weapons, they will ultimately face a highly unbalanced struggle with the Overseers at the entrance.

#### Pre-Game Setup
**Game Map**:
- A game map from 256x256 to 1024x1024, containing a sufficient number of loot spawners.

**Game Configuration**:
- Game duration: 10-30 minutes.
- Adventure mode enabled.
- Team size is unlimited, with fewer Overseer members than Explorer members.
- Disable natural regeneration, enable auto saturation.

#### Recommended Specifications
**Game Rules Configuration**：
- Game duration: 10-20 minutes.
- Adventure mode enabled.
- Team size is unlimited, with fewer Overseers members than Explorers.
- Disable natural regeneration, enable auto saturation.

**Spawn Configuration**:
- Both factions are assigned fixed initial spawn points, with Explorer spawn points closer to the maze area entrance.

**Zone Configuration**:
> If the map does not include maze walls, walls need to be manually simulated using zones.
- Initial Boundary Safe Zone: Opaque color, instant kill damage.
- Initial Faction Separation Unsafe Zone: Opaque color, instant kill damage, used to provide preparation time for Explorers.
- Blockade Beam Preparation: Special semi-transparent color, no damage.
- Blockade Beams: Low transparency color, high damage.
- Expulsion Beams: Low transparency color, high damage, gradually expands radius and moves in random directions.
- Periodic firework zone: Opaque color, slightly smaller than boundary safe zone (provides color hint and eliminates z-fighting)