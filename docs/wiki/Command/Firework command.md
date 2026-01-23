[English](#English)

# 烟花指令
_/battleroyale firework [player/~ ~ ~/clear] [amount] [interval] [vRange] [hRange]_

需要权限等级2

- **游戏初始化后会清空剩余待生成烟花**

## 添加烟花任务
_/battleroyale firework [player/~ ~ ~] [amount] [interval] [vRange] [hRange]_

- amount：总烟花数量
- interval：生成的间隔
- vRange：垂直方向随机偏移[0, vRange]
- hRange：水平方向在半径为hRange的圆内随机偏移

### 添加玩家烟花
_/battleroyale firework [player] [amount] [interval] [vRange] [hRange]_

- 每次生成烟花时取玩家最新位置，玩家离开当前维度则取消烟花任务
- 当生成位置与玩家水平距离小于1，则保证生成位置不低于在玩家2格高处
> 文明烟花，安全燃放

### 添加固定烟花
_/battleroyale firework [~ ~ ~] [amount] [interval] [vRange] [hRange]_

- 以该点为中心持续生成烟花
- 当前维度失效则取消烟花任务

## 清除烟花任务
_/battleroyale firework clear_

### 清除所有烟花
_/battleroyale firework clear_

需要权限等级3

- 清除剩余待生成烟花，不包含已经发射但未爆炸的烟花

# English
_/battleroyale firework [player/~ ~ ~] [amount] [interval] [vRange] [hRange]_

Require permission level 2
- **The remaining fireworks will be cleared after the game initialization**

## Add fireworks task
_/battleroyale firework [player/~ ~ ~] [amount] [interval] [vRange] [hRange]_

- amount: total number of fireworks
- interval: interval of generation
- vRange: vertical random offset [0, vRange]
- hRange: horizontal random offset within a circle with a radius of hRange

### Add player fireworks
_/battleroyale firework [player] [amount] [interval] [vRange] [hRange]_

- Take the player's latest position every time a firework is generated. If the player leaves the current level, the fireworks task will be canceled
- When the horizontal distance between the generated position and the player is less than 1, ensure that the generated position is not lower than 2 blocks above the player
> Civilized fireworks, set off safely

### Add fixed fireworks
_/battleroyale firework [~ ~ ~] [amount] [interval] [vRange] [hRange]_

- Continuously generate fireworks with this point as the center
- Cancel the fireworks task if the current level is invalid

## Clear Fireworks Task
_/battleroyale firework clear_

### Clear all fireworks
_/battleroyale firework clear_

Require permission level 3

- Clears the remaining fireworks to be generated, excluding fireworks that have been launched but not exploded