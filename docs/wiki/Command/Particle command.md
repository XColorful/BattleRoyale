[English](#English)

# 粒子指令
_/battleroyale particle [~ ~ ~/id/clear] [id/all] [cooldown]_

需要权限等级2

## 生成粒子效果
_/battleroyale particle [~ ~ ~/id] [id] [cooldown]_

### 添加指令粒子
_/battleroyale particle [~ ~ ~] [id] [cooldown]_
_/battleroyale particle [id] [cooldown]_

- 从粒子配置中读取
- 玩家各自使用自己名称的指令通道名称
- 非玩家指令使用通道"command"，玩家若为此名称则将冲突
- _cooldown_ 参数需要权限等级3

## 清除粒子效果
_/battleroyale particle clear [all]_

需要权限等级3

### 清除当前通道粒子效果
_/battleroyale particle clear_

- 玩家各自使用自己名称的指令通道名称
- 非玩家指令使用通道"command"，玩家若为此名称则将冲突

### 清除全部粒子效果
_/battleroyale particle clear all_

# English
_/battleroyale particle [~ ~ ~/id/clear] [id/all] [cooldown]_

Require permission level 2

## Generate Particle Effect
_/battleroyale particle [~ ~ ~/id] [id] [cooldown]_

### Add command particle
_/battleroyale particle [~ ~ ~] [id] [cooldown]_
_/battleroyale particle [id] [cooldown]_

- Reads from particle configuration
- Players use command channels named after themselves
- Non-player commands use the "command" channel; if a player uses this name, it will conflict
- The _cooldown_ parameter requires permission level 3

## Clear Particle Effect
_/battleroyale particle clear [all]_

Requires permission level 3

### Clear current channel particle effect
_/battleroyale particle clear_

- Players use command channels named after themselves
- Non-player commands use the "command" channel; if a player uses this name, it will conflict

### Clear All Particle Effects
_/battleroyale particle clear all_