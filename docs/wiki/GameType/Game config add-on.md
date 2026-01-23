# 游戏补充配置
结合各游戏玩法使用

## 特殊功能圈
独立的圈功能，可选择混入各游戏机制中

---

### 圈中圈
提前生成缩圈终点位置的固定不安全区，随后生成即将缩至该圈位置及大小的缩圈安全区。

#### 建议规范
- 终点圈的id需靠前才能保证圈生成时确定到终点位置，否则将生成失败
- 在缩圈按犬奴开始缩圈或即将结束缩圈后结束本圈
- 建议使用与缩圈安全区不同的颜色以供玩家快速区分

---

### 封锁光柱
游戏地图内随机生成的不安全区，通常包含预备阶段，生效阶段。使用至少两个圈实现，分别用不同颜色以及不同伤害。

#### 建议规范
- 用于预备阶段的封锁光柱用不透明绿色，伤害为0
- 生效阶段的封锁光柱用低透明颜色，高伤害
- 建议使用独立的颜色以供玩家快速区分

---

### 驱逐光柱
从中心点往四周随机发射半径增长的不安全区。

#### 建议规范
- 初始半径为0，半径增长速度需保证玩家看到自身位置出现驱逐光柱后能及时躲避。
- 建议使用独立的颜色以供玩家快速区分

---

### 多功能区
用zoneId为x0的[无功能区](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#无功能区)确定起止状态，后续多个zoneId为x1、x2、x3的区域起止状态使用previous类型，randomRange为0，scale为1，颜色为#RRGGBB00（完全透明）

#### 建议规范
- 用于确定运动的zoneId个位为0
- 多个叠加的zongId个位从1开始叠加
- 若已叠加到zoneId为x9，则下一个用{x+1}1
- 多功能区之后的zoneId十位+1

# English
Combine with different game type.

## Special Functional Zones
Independent zone functions, optionally integrated into various game mechanics

---

### Circle in Circle
A fixed unsafe zone is generated in advance at the final position of the shrinking circle. Subsequently, a shrinking safe zone that will contract to that circle's position and size is generated.

#### Recommended Specifications
- The ID of the final circle needs to be prioritized to ensure its position is determined when the circle is generated, otherwise generation will fail.
- This circle should end after the shrinking circle starts contracting or is about to finish contracting.
- It is recommended to use a different color from the shrinking safe zone for players to quickly distinguish.

---

### Blockade Beams
Unsafe zones randomly generated within the game map, usually including a preparation phase and an active phase. These are implemented using at least two circles, with different colors and different damage.

#### Recommended Specifications
- Blockade Beams for the preparation phase should use an opaque green color with 0 damage.
- Blockade Beams for the active phase should use a low transparency color with high damage.
- It is recommended to use independent colors for players to quickly distinguish.

---

### Expulsion Beams
Unsafe zones that are emitted randomly outwards from the center point with increasing radius.

#### Recommended Specifications
- The initial radius is 0. The radius growth speed must ensure that players can promptly dodge when they see an Expulsion Beam appearing at their position.
- It is recommended to use independent colors for players to quickly distinguish.

---

### Multifunctional Zone

A Multifunctional Zone uses a [No function zone](https://github.com/XColorful/BattleRoyale/wiki/Zone-simple-function#No-function-zone) with zoneId as x0 to determine its start and end states. Subsequent zones with zoneId as x1, x2, x3 will use the "previous" type for their start and end states, with randomRange set to 0, scale set to 1, and color set to \#RRGGBB00 (fully transparent).

#### Recommended Specifications

- The zoneId for determining movement should have a unit digit of 0.
- Multiple superimposed zoneIds should have unit digits starting from 1.
- If superimposed zones reach a zoneId of x9, the next should be {x+1}1.
- For zones after a Multifunctional Zone, increment the tens digit of the zoneId by 1.