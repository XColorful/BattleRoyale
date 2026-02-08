[English](#English)

# 临时数据
_/battleroyale temp [pubgmc/initStackZoneConfig/gameStep/clear] [boolean]_

需要权限等级2

## 功能类
_/battleroyale temp pubgmc [boolean]_

### 启用PUBGMC兼容
_/battleroyale temp pubgmc [boolean]_

使[PUBGMC兼容](https://github.com/XColorful/BattleRoyale/wiki/PUBGMC-compatibility)生效

### 启用区域配置叠加
_/battleroyale temp initStackZoneConfig [boolean]_

使[读取配置](https://github.com/XColorful/BattleRoyale/wiki/Game-command#读取配置)不清除已加载的[区域配置](https://github.com/XColorful/BattleRoyale/wiki/Zone-config)，若区域ID相同则使用新读取的进行覆盖
- 该设置不会保存至临时数据，关闭游戏后仍需要手动启用
- 大逃杀游戏结束时仍然会进行清理，该功能设计为仅用于临时测试配置
> 该功能等价于将新读取的配置添加至原配置文件（按顺序读取时会覆盖已有同id配置）

### 设置大逃杀游戏步长
_/battleroyale temp gameStep [inverval]_

使大逃杀游戏时长每游戏刻增加 _inverval_
- 该设置不会保存至临时数据，关闭游戏后仍需要手动启用
> 该功能设计为快速预览区域配置是否符合预期，而不会处理步长范围内的每游戏时刻对应的大逃杀游戏tick

### 启用TaCZ子弹处理器
_/battleroyale temp tacz bullerHandler [boolean]_

机制与 _PUBG_ 类似，仅对[永恒枪械工坊：零（TaCZ）](https://github.com/MCModderAnchor/TACZ)子弹生效：
- _Minecraft_ 20生命值对应100%耐久度

最终伤害计算：
$$\text{最终伤害} = \text{基础伤害} \times (1 - \text{减伤比例})$$

盔甲耐久损耗计算：
$$\text{耐久损耗} = \left( \frac{\text{吸收伤害} \times 5.0}{\text{耐久度比例}} \right) \times \text{物品最大耐久度}$$
> 参数说明：
> - $\text{吸收伤害}$：被盔甲吸收的原始伤害（含爆头倍率）
> - $5.0$：生命值转换系数（$100 \div 20$）
> - $\text{耐久度比例}$：下表中的“耐久度比例”参数
> - $\text{物品最大耐久度}$：该物品在 _Minecraft_ 中的最大耐久值

盔甲数值对应表：

|级别|对应 _Minecraft_ 物品|减伤比例|耐久度比例|特性|
|---|---|---|---|---|
|三级头|下届合金头盔|55%|230%|损坏后消失|
|三级甲|钻石胸甲|55%|250%|损坏后转化为碎甲|
|二级头|铁头盔|40%|150%|损坏后消失|
|二级甲|铁胸甲|40%|220%|损坏后转化为碎甲|
|一级头|皮革头盔|30%|80%|损坏后消失|
|一级甲|皮革胸甲|30%|200%|损坏后转化为碎甲|
|碎甲|锁链胸甲|20%|∞|无法被子弹破坏|

## 清理数据
_/battleroyale temp clear_

需要权限等级3

### 删除所有临时数据
_/battleroyale temp clear_

删除所有[临时数据](https://github.com/XColorful/BattleRoyale/wiki/Temp-data)文件

# English
_/battleroyale temp [pubgmc/clear] [boolean]_

Require permission level 2

## Function category
_/battleroyale temp pubgmc [boolean]_

### Enable PUBGMC compatibility
_/battleroyale temp pubgmc [boolean]_

To put [PUBGMC compatibility](https://github.com/XColorful/BattleRoyale/wiki/PUBGMC-compatibility#English) into effect

### Enable zone configuration stacking
_/battleroyale temp initStackZoneConfig [boolean]_

Enables [Read configuration](https://github.com/XColorful/BattleRoyale/wiki/Game-command#Read-configuration) to not clear the loaded [Zone config](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#English). If the zone ID is the same, the newly loaded one will be overwritten.
- This setting is not saved to temporary data and must be manually enabled after closing the game.
- Cleanup will still occur after the BattleRoyale game ends. This feature is intended for temporary testing purposes only.
> This function is equivalent to appending the newly loaded configuration to the original configuration (reading sequentially will overwrite existing configurations with the same ID).

### Set the BattleRoyale game step
_/battleroyale temp gameStep [inverval]_

Increases the BattleRoyale game time by _inverval_ per game tick.
- This setting is not saved to temporary data and must be manually enabled after closing the game.
> This feature is designed to quickly preview whether the zone configuration is as expected, and does not process the BattleRoyale game ticks corresponding to each game time within the step range.

### Enable TaCZ bullet handler
_/battleroyale temp tacz bullerHandler [boolean]_

The mechanism is similar to _PUBG_, only effective for [TaCZ](https://github.com/MCModderAnchor/TACZ) bullets:
- 20 HP in _Minecraft_ corresponds to 100% durability

Final damage calculation:
$$\text{Final damage} = \text{Base damage} \times (1 - \text{Reduction rate})$$

Armor durability loss calculation:
$$\text{Durability loss} = \left( \frac{\text{Absorbed damage} \times 5.0}{\text{Durability ratio}} \right) \times \text{Item max durability}$$
> Parameter Description:
> - $\text{Absorbed damage}$: Original damage absorbed by armor (including headshot multiplier)
> - $5.0$: HP conversion coefficient ($100 \div 20$)
> - $\text{Durability ratio}$: The "Durability Ratio" parameter in the table below
> - $\text{Item max durability}$: The maximum durability of the item in _Minecraft_

Armor values table:

|Level|Corresponds to _Minecraft_ item|Reduction rate|Durability ratio|Features|
|---|---|---|---|---|
|Level 3 Helmet|Netherite Helmet|55%|230%|Disappears after damage|
|Level 3 Vest|Diamond Chestplate|55%|250%|Converts to Broken Armor|
|Level 2 Helmet|Iron Helmet|40%|150%|Disappears after damage|
|Level 2 Vest|Iron Chestplate|40%|220%|Converts to Broken Armor|
|Level 1 Helmet|Leather Helmet|30%|80%|Disappears after damage|
|Level 1 Vest|Leather Chestplate|30%|200%|Converts to Broken Armor|
|Broken Armor|Chainmail Chestplate|20%|∞|Cannot be destroyed by bullet|

## Clear data
_/battleroyale temp clear_

Requires permission level 3

### Delete all temporary data
_/battleroyale temp clear_

Delete all [Temporary data](https://github.com/XColorful/BattleRoyale/wiki/Temp-data#English) files