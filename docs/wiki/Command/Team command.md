[English](#English)

# 队伍管理
_/battleroyale team [join/leave/kick/invite/request/accept/decline/id/add/build] [player/senderName/requesterName/teamId] [teamId/size] [forceRebuild]_

非[权限指令](#权限指令)均不需要权限

## 加入队伍
_/battleroyale team join [teamId]_

**游戏进行时无法执行**

### 加入新队
_/battleroyale team join_

- 自动寻找下一个可用的队伍id并创建加入新队伍
- 如果没有可用的队伍id，则改为向随机未满员队伍发送入队申请

### 加入指定队伍
_/battleroyale team join [teamId]_

- 创建指定队伍id并加入
- 若队伍id已存在，则改为发送入队申请

## 申请入队
_/battleroyale team request [player]_
_/battleroyale team [accept/decline] [requesterName]_

**游戏进行时无法执行**

### 申请队伍
_/battleroyale team request [player]_

- 向指定玩家的队伍发送申请
- 若对方不是队长，则申请无效

### 同意申请
_/battleroyale team accept [requesterName]_

- 同意指定玩家的入队申请
- 若自己已不是队长，则同意无效

### 拒绝申请
_/battleroyale team decline [requesterName]_

- 拒绝指定玩家的入队申请
- 若自己已不是队长，则拒绝无效

## 邀请入队
_/battleroyale team invite [player]_
_/battleroyale team [accept/decline] [senderName]_

**游戏进行时无法执行**

### 邀请玩家
_/battleroyale team invite [player]_

- 邀请指定玩家加入队伍
- 若自己不是队长，则邀请无效

### 同意邀请
_/battleroyale team accept [senderName]_

- 同意指定玩家的入队邀请
- 若已经向其他玩家发送邀请，对方已不是队长或已在队伍中，则同意无效

### 拒绝邀请
_/battleroyale team decline [senderName]_

- 拒绝指定玩家的入队邀请

## 队内指令
_/battleroyale team [kick/leave/id] [player]_

### 踢出队伍
_/battleroyale team kick [player]_

**游戏进行时无法执行**

- 若自己不为队长，则指令无效

### 离开队伍
_/battleroyale team leave_

- 若游戏正在进行中，将被强制淘汰

### 队伍信息
_/battleroyale team id_

- 查看队伍id

## 权限指令
_/battleroyale team [add/build] [player] [teamId/size] [forceRebuild]_

需要权限等级2

### 加进队伍
_/battleroyale team add [player] [teamId]_

将生物实体加入指定队伍
- 若该生物已在队伍中则先退队再尝试加入

### 组建队伍
_/battleroyale team build [player] [size] [forceRebuild]_

将所选实体随机打乱后按规模组建队伍
- 自动过滤非生物实体
- size：组建的队伍规模，不超过实际限制
- forceRebuild：强制重新组建队伍

# English
_/battleroyale team [join/leave/kick/invite/request/accept/decline/id/add/build] [player/senderName/requesterName/teamId] [teamId/size] [forceRebuild]_

All commands other than [Permission Command](#Permission-command) do not require permissions

## Join a team
_/battleroyale team join [teamId]_

**Cannot be executed while the game is in progress**

### Join a new team
_/battleroyale team join_

- Automatically find the next available team id and create a new team
- If there is no available team id, send a team joining request to a random team that is not full

### Join a specified team
_/battleroyale team join [teamId]_

- Create a specified team id and join
- If the team id already exists, send a team joining request instead

## Team request
_/battleroyale team request [player]_
_/battleroyale team [accept/decline] [requesterName]_

**Cannot be executed while the game is in progress**

### Request to join team
_/battleroyale team request [player]_

- Send a request to the specified player's team
- If the target player is not the team leader, the request is invalid

### Accept the request
_/battleroyale team accept [requesterName]_

- Accept the requester player to join the team
- If you are no longer the team leader, the approval is invalid

### Decline the request
_/battleroyale team decline [requesterName]_

- Reject the requester player to join the team
- If you are no longer the team leader, the decline is invalid

## Team invitation
_/battleroyale team invite [player]_
_/battleroyale team [accept/decline] [senderName]_

**Cannot be executed while the game is in progress**

### Invite a player
_/battleroyale team invite [player]_

- Invite a specified player to join your team
- If you are not the team leader, the invitation is invalid

### Accept the invitation
_/battleroyale team accept [senderName]_

- Accept to join the sender player's team
- If an invitation has been sent to another player, the sender player is no longer the team leader or you are already in the team, the approval is invalid

### Decline invitation
_/battleroyale team decline [senderName]_

- Decline to join the sender player's team

## Team command
_/battleroyale team [kick/leave/id] [player]_

### Kick out of the team
_/battleroyale team kick [player]_

**Cannot be executed while the game is in progress**

- If you are not the team leader, the command will be invalid

### Leave the team
_/battleroyale team leave_

- If the game is in progress, you will encounter force elimination

### Team information
_/battleroyale team id_

- Check the team id

## Permission command
_/battleroyale team [add/build] [player] [teamId/size] [forceRebuild]_

Require permission level 2

### 加进队伍
_/battleroyale team add [player] [teamId]_

Add a living entity to the specified team
- If the entity is already in a team, it will leave the current team before attempting to join

### 组建队伍
_/battleroyale team build [player] [size] [forceRebuild]_

Build teams based on size after shuffling selected entities randomly
- Automatically filter out non-living entities
- size: The size of the teams to be built, not exceeding the actual limit
- forceRebuild: Forcibly rebuild teams