# 竞技模式
紧张刺激的对抗体验

## 大逃杀
经典吃鸡玩法，在不断缩小的安全区内生存下来，成为最后的幸存者或队伍

---

### 团队大逃杀
玩家队伍分布在地图各处，收集物资，淘汰其他队伍，并在不断缩小的安全区内生存下来，直到成为最后的幸存队伍。

#### 游戏前准备
**游戏地图**：
- 一张1024x1024至8192x8192的游戏地图，分布足够数量的含有物资刷新器的建筑

**游戏配置**：
- 物资刷新配置包含全部非稀有物品

#### 建议规范
**游戏规则配置**：
- 游戏时长15-30分钟
- 启用冒险模式
- 队伍规模为1-5人

**出生配置**：
- 按队伍随机出生在边界范围内

**区域配置**：
- 固定边界安全区：不透明颜色，秒杀伤害
- 缩圈安全区：半透明颜色

---

### 饥饿游戏
玩家分布在地图各处，收集物资，击败其他玩家，并在不断缩小的安全区内生存下来，直到成为最后的幸存者。

#### 游戏前准备
**游戏地图**：
- 一张1024x1024至8192x8192的游戏地图，分布足够数量的含有物资刷新器的建筑

**游戏配置**：
- 物资刷新配置文件包含全部非稀有物品

#### 建议规范
**游戏规则配置**：
- 游戏时长15-30分钟
- 启用冒险模式
- 队伍规模为1人

**出生配置**：
- 随机出生在边界范围内

**区域配置**：
- 固定边界安全区：不透明颜色，秒杀伤害
- 缩圈安全区：半透明颜色

---

### 纯净生存
玩家队伍分布在地图各处，收集物资，击败其他玩家，并在不断缩小的安全区内生存下来，直到成为最后的幸存队伍。

#### 游戏前准备
**游戏地图**：
- 新建原版地形存档或其他地形模组等

#### 建议规范
**游戏规则配置**：
- 游戏时长15-60分钟
- 禁用冒险模式
- 队伍规模为2-5人
> 原版玩法发育慢，队伍合作防止拖慢游戏时长

**出生配置**：
- 按队伍随机出生在边界范围内

**区域配置**：
- 固定边界安全区：不透明颜色，秒杀伤害
- 缩圈安全区：半透明颜色

## 区域争夺
长距离对决中队伍的战略部署和物资管理

---

### 部落争霸
玩家队伍分布在指定区域处，各有丰富的初始物资点位，相互距离较远。随着安全区不断缩小，边界处的队伍会先与其他队伍发起冲突。与此同时，其他队伍可能会对处于中间区域而暂时安全的队伍蠢蠢欲动。

#### 游戏前准备
**游戏地图**：
- 一张1024x1024至8192x8192的游戏地图，资源集中分布，以海洋间隔或移动时间较长

#### 建议规范
**游戏规则配置**：
- 游戏时长20-60分钟
- 使用原版地图则禁用冒险模式
- 队伍规模为1-5人

**出生配置**：
- 按队伍分配固定点位
- 随机偏移范围保证不超出领地范围

**区域配置**：
- 固定边界安全区：不透明颜色，秒杀伤害
- 缩圈安全区：**不透明颜色**，**秒杀伤害**

## 空间压缩
以压缩活动区域为核心的竞技玩法，通过特殊区域机制带来更多变数和策略选择。

---

### 风水轮转
玩家队伍分布在各处，地图中间的不安全区不断扩大的同时边界也朝不安全区的终点收缩。形成“回”字行区域后，四角生成矩形不安全区向内移动，分隔地图四方。四个矩形同时顺时针移动的同时，中间和边界保持圈中圈的方式向中心缩小。当四角不安全区均往各方向移动到终点后，立即在终点处开始继续移动，直至游戏结束。

#### 游戏前准备
**游戏地图**：
- 任意选择原版地形或1024x1024至8192x8192的游戏地图

#### 建议规范
**游戏规则配置**：
- 游戏时长20-40分钟
- 使用原版地图则禁用冒险模式
- 队伍规模为2-5人

**出生配置**：
- 按队伍随机出生在边界范围内

**区域配置**：
> 游戏区域可大可小，含有高生命恢复时应能强行反过四角移动不安全区，各分隔区域内无法透过中心透明区域与其他队伍发生战斗
> 四角区和边界缩圈区去的移动速度可根据需要调整
- 初始边界安全区：不透明颜色，秒杀伤害
- 缩圈不安全区：半透明颜色，秒杀伤害
- 四角移动不安全区：低透明颜色，高伤害

---

### 边界驱逐
玩家随机分布到地图各处，地图中间的不安全区不断扩大的同时边界也朝不安全区的终点收缩。形成“回”字行区域后，四角生成矩形不安全区向内移动，分隔地图四方。四个矩形同时顺时针移动，四方区域被挤压的同时引发不同队伍开战，队伍可提前在终点等待埋伏被迫迁徙的队伍。在挤压完毕后，中间和边界不安全区保持圈中圈的方式向中心缩小，其一缩小至梅花桩后，边界外接圆随机缩小至梅花桩。

#### 游戏前准备
**游戏地图**：
- 任意选择原版地形或1024x1024至8192x8192的游戏地图

#### 建议规范
**游戏规则配置**：
- 游戏时长20-40分钟
- 使用原版地图则禁用冒险模式
- 队伍规模为2-5人

**出生配置**：
- 按队伍随机出生在边界范围内

**区域配置**：
> 设边界正方形边长为 _3a_ ，中心不安全区边长为 _a_ ，则建议四角矩形长 _≥1.5a_ ，宽为 _a_
- 初始边界安全区：不透明颜色，秒杀伤害
- 缩圈不安全区：不透明颜色，秒杀伤害
- 四角移动不安全区：低透明颜色，高伤害

---

### 四镜重圆
每个4人队伍被分散至地图四方，在各地区域内激烈搏斗的途中逐渐打开通往决战地点的道路，并压缩出生地点推动前行。地图四方的出生地被完全覆盖后，解锁决战地点，进行最后决斗。决战前队伍若有多位队员存活，将在最后取得优势。

#### 游戏前准备
**游戏地图**：
- 一张1024x1024至8192x8192的游戏地图，分布足够数量的含有物资刷新器的建筑
**游戏配置**：
- 物资刷新配置包含全部非稀有物品，使用时间刷新词条控制开拓通道阶段的高级物资刷新，取消最终决战期间的物资刷新

#### 建议规范
**游戏规则配置**：
- 游戏时长20-40分钟
- 启用冒险模式
- 队伍规模为4人

**出生配置**：
- 4个固定位置，随机半径
- 取消队伍共同出生

**区域配置**：
> 设边界正方形边长为 _3a_ ，四角正方形边长为 _a_ ，则四方初生点边长为 _a_
> 4个决战通道+决战区域的范围为 _a_ ，决战区域大小自定为 _b（b≤a/3）_ ，则单个通道长为 _a-2b_ ，宽 _b_
> 决战通道额外需要4个正方形不安全区来防止跨出生地，边长略大，保证使用末影珍珠后必承受一次秒杀伤害
- 初始边界安全区：不透明颜色，秒杀伤害，一定时间后开始缩小 _b/3a_ 倍（决战区域大小），持续至游戏结束
- 4个角落不安全区：**不透明颜色**，秒杀伤害，持续至初始边界缩圈结束
- 决战区域4个角落不安全区：**不透明颜色**，秒杀伤害，持续至初始边界缩圈结束
- 决战通道不安全区：半透明颜色，初始边长为 _a_ ，缩小至 _b_ （决战区域大小）后结束（此时初始边界缩小至 _a_ ）
- 决战区域安全区：半透明颜色，初始半径为决战区域 _1.414_ 倍，随机往决战区域缩小至梅花桩

---

### 螺旋围堵
玩家队伍分布在各处，可活动区域被分割为蚊香状，边界外的安全区不断向中间收缩成梅花桩，中心区域向四周扩展高伤害不安全区。环环间隔的毒伤非秒杀伤害，但没有足够的伤害吸收无法强行跨越。可提前向内圈移动进行埋伏，或是主动向外圈移动偷袭埋伏者。最内层的间隔厚度薄且强行通过的伤害不致命，提前到达中心区域的队伍将四面受敌。中心区域还将不断向外发射扩大的驱逐光柱干扰外圈队伍。

#### 游戏前准备
**游戏地图**：
- 任意选择原版地形或1024x1024至8192x8192的游戏地图

#### 建议规范
**游戏规则配置**：
- 游戏时长20-60分钟
- 使用原版地图则禁用冒险模式
- 队伍规模为2-5人

**出生配置**：
- 按队伍随机出生在边界范围内

**区域配置**：
- 初始边界安全区：不透明颜色，秒杀伤害，缩小后持续至游戏结束
- 缩圈安全区：半透明颜色
- 外圈分隔环不安全区：低透明颜色，高伤害，从边界缩小中心移动至目标位置后持续至游戏结束
- 内圈分割环不安全区：半透明颜色，厚度较薄，从边界缩小中心移动至目标位置后持续至游戏结束
- 驱逐光柱：低透明颜色，高伤害，从边界缩小中心向地图内随机位置移动，距离越远速度越快。

## 团战
团队协作和正面冲突。

---

### 歼灭模式
两队玩家从固定出生点开始，歼灭敌方队伍取得胜利。

#### 游戏前准备
**游戏地图**：
- 任意选择原版地形或不大于1024x1024的游戏地图
> 规模超过1024x1024可考虑大战场

**游戏配置**：
- 物资刷新配置包含全职业初始装备

#### 建议规范
**游戏规则配置**：
- 游戏时长3-25分钟
- 使用原版地图则禁用冒险模式
- 队伍规模为5-10人，共2队

**出生配置**：
- 为2个队伍各自分配固定出生点

**区域配置**：
- 固定边界安全区：不透明颜色，秒杀伤害
- 缩圈安全区：低透明颜色，高伤害，缩圈延迟较久

---

### 大战场
两军决战，地图内随机生成封锁光柱增加活动限制，在混乱的大战场上互帮互助，不断杀敌，取得最后的胜利。

#### 游戏前准备
**游戏地图**：
- 任意选择原版地形或512x512至2048x2048的游戏地图
> 规模超过2048x2048时应为更复杂且时长更久的游戏模式

**游戏配置**：
- 物资刷新配置包含全兵种初始装备

#### 建议规范
**游戏规则配置**：
- 游戏时长20-60分钟
- 使用原版地图则禁用冒险模式
- 队伍规模为10-50人，共2队

**出生配置**：
- 为2个队伍各自分配固定出生点，随机偏移以平均分布在初始区域

**区域配置**：
- 固定边界安全区：不透明颜色，秒杀伤害
- 两侧不安全区：低透明颜色，高伤害
- 封锁光柱预备：特殊半透明颜色，无伤害
- 封锁光柱：低透明颜色，高伤害

# English
Intense and thrilling combat experiences.

## Battle Royale
Classic last-man-standing gameplay, surviving in an ever-shrinking safe zone to become the last survivor or team.

---

### Team Battle Royale
Player teams are spread across the map, gathering resources, eliminating other teams, and surviving within a constantly shrinking safe zone until they are the last team standing.

#### Pre-Game Setup
**Game Map**:
- A game map from 1024x1024 to 8192x8192, with a sufficient number of structures containing loot spawners.

**Game Configuration**:
- Loot spawner config includes all non-rare items.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 15-30 minutes.
- Adventure mode enabled.
- Team size: 1-5 players.

**Spawn Configuration**:
- Random team spawns within the boundary.

**Zone Configuration**:
- Fixed Boundary Safe Zone: Opaque color, instant kill damage.
- Shrinking Safe Zone: Semi-transparent color.

---

### Hunger Games
Players are spread across the map, gathering resources, defeating other players, and surviving within a constantly shrinking safe zone until they are the last survivor.

#### Pre-Game Setup
**Game Map**:
- A game map from 1024x1024 to 8192x8192, with a sufficient number of structures containing loot spawners.

**Game Configuration**:
- Loot spawner config includes all non-rare items.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 15-30 minutes.
- Adventure mode enabled.
- Team size: 1 player.

**Spawn Configuration**:
- Random spawns within the boundary.

**Zone Configuration**:
- Fixed Boundary Safe Zone: Opaque color, instant kill damage.
- Shrinking Safe Zone: Semi-transparent color.

---

### Vanilla Survival
Player teams are spread across the map, gathering resources, defeating other players, and surviving within a constantly shrinking safe zone until they are the last team standing.

#### Pre-Game Setup
**Game Map**:
- New vanilla terrain save or other terrain mods, etc.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 15-60 minutes.
- Adventure mode disabled.
- Team size: 2-5 players.
> Vanilla gameplay development is slow, team cooperation prevents delaying game duration.

**Spawn Configuration**:
- Random team spawns within the boundary.

**Zone Configuration**:
- Fixed Boundary Safe Zone: Opaque color, instant kill damage.
- Shrinking Safe Zone: Semi-transparent color.

## Territorial Conquest
Team strategy and resource management in long-range engagements.

---

### Tribal Warfare
Player teams are distributed in designated areas, each with abundant initial resource points, located far from each other. As the safe zone continuously shrinks, teams at the boundaries will engage in conflict with other teams first. Meanwhile, other teams may eye the temporarily safe teams in the central area.

#### Pre-Game Setup
**Game Map**:
- A game map from 1024x1024 to 8192x8192, with concentrated resources, separated by oceans or requiring long travel times.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 20-60 minutes.
- If using vanilla map, adventure mode is disabled.
- Team size: 1-5 players.

**Spawn Configuration**:
- Teams are assigned fixed spawn points.
- Random offset range ensures not exceeding territory boundaries.

**Zone Configuration**:
- Fixed Boundary Safe Zone: Opaque color, instant kill damage.
- Shrinking Safe Zone: **Opaque color**, **instant kill damage**.

## Area Compression
Competitive gameplay centered on compressing the active area, introducing more variables and strategic choices through special zone mechanics.

---

### Shifting Fates
Player teams are spread out. The unsafe zone in the middle of the map continuously expands while the boundaries also contract towards the unsafe zone's endpoint. After forming a "回" (return) shaped area, rectangular unsafe zones generate at the four corners, moving inwards to divide the map into four sections. As the four rectangles move clockwise simultaneously, the middle and boundaries continue to shrink towards the center in a circle-within-a-circle manner. Once the four corner unsafe zones have moved to their respective endpoints, they immediately continue moving from those endpoints until the game ends.

#### Pre-Game Setup
**Game Map**:
- Any vanilla terrain or a game map from 1024x1024 to 8192x8192.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 20-40 minutes.
- If using vanilla map, adventure mode is disabled.
- Team size: 2-5 players.

**Spawn Configuration**:
- Random team spawns within the boundary.

**Zone Configuration**:
> The game area can be large or small; with high health regeneration, it should be possible to force through the moving corner unsafe zones. Combat between teams across the central transparent area is not possible within separated zones.
> The movement speed of corner zones and shrinking boundary zones can be adjusted as needed.
- Initial Boundary Safe Zone: Opaque color, instant kill damage.
- Shrinking Unsafe Zone: Semi-transparent color, instant kill damage.
- Moving Corner Unsafe Zone: Low transparency color, high damage.

---

### Zone Purge
Players are randomly distributed across the map. The unsafe zone in the middle of the map continuously expands while the boundaries also contract towards the unsafe zone's endpoint. After forming a "回" (return) shaped area, rectangular unsafe zones generate at the four corners, moving inwards to divide the map into four sections. The four rectangles move clockwise simultaneously, squeezing the four areas and triggering conflicts between different teams. Teams can wait at the endpoint to ambush forced-migration teams. After the squeeze is complete, the middle and boundary unsafe zones continue to shrink towards the center in a circle-within-a-circle manner. Once one shrinks to a plum blossom shape, the outer circle boundary randomly shrinks to a plum blossom shape.

#### Pre-Game Setup
**Game Map**:
- Any vanilla terrain or a game map from 1024x1024 to 8192x8192.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 20-40 minutes.
- If using vanilla map, adventure mode is disabled.
- Team size: 2-5 players.

**Spawn Configuration**:
- Random team spawns within the boundary.

**Zone Configuration**:
> Assuming the boundary square side length is _3a_ , and the central unsafe zone side length is _a_ , it is recommended that the rectangular corners have a length of _≥1.5a_ and a width of _a_.
- Initial Boundary Safe Zone: Opaque color, instant kill damage.
- Shrinking Unsafe Zone: Opaque color, instant kill damage.
- Moving Corner Unsafe Zone: Low transparency color, high damage.

---

### Quadrant Reunion
Each 4-player team is dispersed into four quadrants of the map. During intense battles within their respective areas, paths to the final battleground gradually open, and spawn locations are compressed, pushing players forward. Once the four quadrant spawn points are fully covered, the final battleground is unlocked for the decisive clash. Teams with multiple surviving members before the final battle will gain an advantage.

#### Pre-Game Setup
**Game Map**:
- A game map from 1024x1024 to 8192x8192, with a sufficient number of structures containing loot spawners

**Game Configuration**:
- Loot spawner config includes all non-rare items, using time entry to control high-tier loot generations during the channel-opening phase, and disabling loot generations during the final battle.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 20-40 minutes.
- Adventure mode enabled.
- Team size: 4 players.

**Spawn Configuration**:
- 4 fixed locations, random radius.
- Disable joint team spawns.

**Zone Configuration**:
> Assuming the boundary square side length is _3a_ , and the four corner square side length is _a_ , then the side length of the four initial spawn points is _a_ .
> The range of the 4 battle channels + battle zone is _a_ , and the battle zone size is custom set to _b (b≤a/3)_ , so the length of a single channel is _a-2b_ , and the width is _b_ .
> The battle channels additionally require 4 square unsafe zones to prevent crossing spawn points, with slightly larger side lengths, ensuring that using an ender pearl will result in one instance of instant kill damage.
- Initial Boundary Safe Zone: Opaque color, instant kill damage, starts shrinking by _b/3a_ (battle zone size) after a certain time, continues until game end.
- 4 Corner Unsafe Zones: **Opaque color**, instant kill damage, persist until the initial boundary shrinking ends.
- 4 Corner Unsafe Zones of Battle Zone: **Opaque color**, instant kill damage, persist until the initial boundary shrinking ends.
- Battle Channel Unsafe Zone: Semi-transparent color, initial side length _a_ , shrinks to _b_ (battle zone size) then ends (at which point the initial boundary has shrunk to _a_ ).
- Battle Zone Safe Zone: Semi-transparent color, initial radius _1.414_ times the battle zone, randomly shrinks towards the battle zone to a plum blossom shape.

---

### Spiral Containment
Player teams are spread out. The active area is divided into a spiral shape, with the safe zone outside the boundary continuously shrinking towards the center into a plum blossom shape, while the central area expands outward with high-damage unsafe zones. The spaced toxic damage is not instant kill, but cannot be forcefully crossed without sufficient damage absorption. Teams can move into the inner circle early to ambush, or actively move into the outer circle to surprise ambushers. The innermost gaps are thin and the damage from forced passage is not lethal, but teams reaching the central area early will be vulnerable from all sides. The central area will also continuously emit expanding expulsion beams outward to interfere with outer circle teams.

#### Pre-Game Setup
**Game Map**:
- Any vanilla terrain or a game map from 1024x1024 to 8192x8192.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 20-60 minutes.
- If using vanilla map, adventure mode is disabled.
- Team size: 2-5 players.

**Spawn Configuration**:
- Random team spawns within the boundary.

**Zone Configuration**:
- Initial Boundary Safe Zone: Opaque color, instant kill damage, persists until game end after shrinking.
- Shrinking Safe Zone: Semi-transparent color.
- Outer Dividing Ring Unsafe Zone: Low transparency color, high damage, persists until game end after moving from boundary shrink center to target position.
- Inner Dividing Ring Unsafe Zone: Semi-transparent color, thinner, persists until game end after moving from boundary shrink center to target position.
- Expulsion Beam: Low transparency color, high damage, moves from boundary shrink center to a random location within the map, faster speed at greater distances.

## Team Battle
Team cooperation and direct conflict.

---

### Annihilation
Two teams of players start from fixed spawn points, aiming to eliminate the enemy team to achieve victory.

#### Pre-Game Setup
**Game Map**:
- Any vanilla terrain or a game map no larger than 1024x1024.
> For maps larger than 1024x1024, consider Grand Battlefield.

**Game Configuration**:
- Loot spawner config includes initial equipment for all classes.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 3-25 minutes.
- If using vanilla map, adventure mode is disabled.
- Team size: 5-10 players, 2 teams in total.

**Spawn Configuration**:
- Assign fixed spawn points for each of the 2 teams.

**Zone Configuration**:
- Fixed Boundary Safe Zone: Opaque color, instant kill damage.
- Shrinking Safe Zone: Low transparency color, high damage, longer shrinking delay.

---

### Grand Battlefield
Two armies clash. Random blockade beams generate within the map to restrict activity. Players must cooperate and eliminate enemies in the chaotic grand battlefield to achieve final victory.

#### Pre-Game Setup
**Game Map**:
- Any vanilla terrain or a game map from 512x512 to 2048x2048.
> For maps larger than 2048x2048, a more complex and longer game mode should be considered.

**Game Configuration**:
- Loot spawner config includes initial equipment for all classes.

#### Recommended Specifications
**Game Rules Configuration**:
- Game duration: 20-60 minutes.
- If using vanilla map, adventure mode is disabled.
- Team size: 10-50 players, 2 teams in total.

**Spawn Configuration**:
- Assign fixed spawn points for each of the 2 teams, with random offsets to ensure even distribution within the initial area.

**Zone Configuration**:
- Fixed Boundary Safe Zone: Opaque color, instant kill damage.
- Two Side Unsafe Zones: Low transparency color, high damage.
- Blockade Beam Preparation: Special semi-transparent color, no damage.
- Blockade Beam: Low transparency color, high damage.