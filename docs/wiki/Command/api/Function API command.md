[English](#English)

## 函数API指令
> _/battleroyale api functionManager [...]_

### 函数管理器
> _IFunctionManager_

> _/battleroyale api functionManager [...]_

#### 清除配置函数注册
> _/battleroyale api functionManager clearConfigFunction_

清除[函数配置](https://github.com/XColorful/BattleRoyale/wiki/Function-config)的注册
- `返回值`：1

#### 清除API函数注册
> _/battleroyale api functionManager clearApiFunction_

清除[函数注册API](#函数注册API)的注册
- `返回值`：1

### 函数注册API
> _IFunctionRegisterApi_

> _/battleroyale api functionManager [...]_

#### 注册事件
> _/battleroyale api functionManager registerEvent [resourceLocation] [isTag] [...] [eventName] [eventPriority] [receiveCanceled]_

- resourceLocation：标识符
- isTag：_resourceLocation_ 是否为函数标签
- eventName：事件类型名称
- eventPriority：监听事件的[事件优先级](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/EventPriority.md)
- receiveCanceled：是否接收被取消的事件

##### 注册模组事件
> _/battleroyale api functionManager registerEvent [resourceLocation] [isTag] eventType [eventName] [eventPriority] [receiveCanceled]_

- eventName：[注册词条](https://github.com/XColorful/BattleRoyale/wiki/Function-config#注册词条)模组事件名称
- `返回值`：是否注册成功

##### 注册自定义事件
> _/battleroyale api functionManager registerEvent [resourceLocation] [isTag] customEventType [eventName] [eventPriority] [receiveCanceled]_

- eventName：[注册词条](https://github.com/XColorful/BattleRoyale/wiki/Function-config#注册词条)自定义事件名称
- `返回值`：是否注册成功

##### 注册自定义事件类
> _/battleroyale api functionManager registerEvent [resourceLocation] [isTag] eventClass [eventName] [eventPriority] [receiveCanceled]_

- eventName：[注册词条](https://github.com/XColorful/BattleRoyale/wiki/Function-config#注册词条)自定义事件类名
- `返回值`：是否注册成功

#### 取消注册事件
> _/battleroyale api functionManager unregisterEvent [resourceLocation] [isTag] [...] [eventName]_

- resourceLocation：标识符
- isTag：_resourceLocation_ 是否为函数标签
- eventName：事件类型名称

##### 取消注册模组事件
> _/battleroyale api functionManager unregisterEvent [resourceLocation] [isTag] eventType [eventName]_

- eventName：[注册词条](https://github.com/XColorful/BattleRoyale/wiki/Function-config#注册词条)模组事件类型名称
- `返回值`：是否取消注册成功

##### 取消注册自定义事件
> _/battleroyale api functionManager unregisterEvent [resourceLocation] [isTag] customEventType [eventName]_

- eventName：[注册词条](https://github.com/XColorful/BattleRoyale/wiki/Function-config#注册词条)自定义事件名称
- `返回值`：是否取消注册成功

##### 取消注册自定义事件类
> _/battleroyale api functionManager unregisterEvent [resourceLocation] [isTag] eventClass [eventName]_

- eventName：[注册词条](https://github.com/XColorful/BattleRoyale/wiki/Function-config#注册词条)自定义事件类名
- `返回值`：是否取消注册成功

# English

## Function API command
> _/battleroyale api functionManager [...]_

### Function manager
> _IFunctionManager_

> _/battleroyale api functionManager [...]_

#### Clear config function registration
> _/battleroyale api functionManager clearConfigFunction_

Clears the registration of [Function-config](https://github.com/XColorful/BattleRoyale/wiki/Function-config#English)
- `return value`: 1

#### Clear api function registration
> _/battleroyale api functionManager clearApiFunction_

Clears the registration of the [Function register API](#Function-register-API):
- `return value`: 1

### Function register API
> _IFunctionRegisterApi_

> _/battleroyale api functionManager [...]_

#### Register event
> _/battleroyale api functionManager registerEvent [resourceLocation] [isTag] [...] [eventName] [eventPriority] [receiveCanceled]_

- resourceLocation: Identifier
- isTag: whether _resourceLocation_ is a function tag
- eventName: event type name
- eventPriority: [Event priority](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/EventPriority.md) for the listener
- receiveCanceled: whether to receive canceled events

##### Register mod event
> _/battleroyale api functionManager registerEvent [resourceLocation] [isTag] eventType [eventName] [eventPriority] [receiveCanceled]_

- eventName: [Register entry](https://github.com/XColorful/BattleRoyale/wiki/Function-config#Register-entry) mod event type name
- `return value`: whether the registration was successful

##### Register custom event
> _/battleroyale api functionManager registerEvent [resourceLocation] [isTag] customEventType [eventName] [eventPriority] [receiveCanceled]_

- eventName: [Register entry](https://github.com/XColorful/BattleRoyale/wiki/Function-config#Register-entry) custom event type name
- `return value`: whether the registration was successful

##### Register custom event class
> _/battleroyale api functionManager registerEvent [resourceLocation] [isTag] eventClass [eventName] [eventPriority] [receiveCanceled]_

- eventName: [Register entry](https://github.com/XColorful/BattleRoyale/wiki/Function-config#Register-entry) custom event class name
- `return value`: whether the registration was successful

#### Unregister event
> _/battleroyale api functionManager unregisterEvent [resourceLocation] [isTag] [...] [eventName]_

- resourceLocation: Identifier
- isTag: whether _resourceLocation_ is a function tag
- eventName: event type name

##### Unregister mod event
> _/battleroyale api functionManager unregisterEvent [resourceLocation] [isTag] eventType [eventName]_

- eventName: [Register entry](https://github.com/XColorful/BattleRoyale/wiki/Function-config#Register-entry) mod event type name
- `return value`: whether the unregistration was successful

##### Unregister custom event
> _/battleroyale api functionManager unregisterEvent [resourceLocation] [isTag] customEventType [eventName]_

- eventName: [Register entry](https://github.com/XColorful/BattleRoyale/wiki/Function-config#Register-entry) custom event type name
- `return value`: whether the unregistration was successful

##### Unregister custom event class
> _/battleroyale api functionManager unregisterEvent [resourceLocation] [isTag] eventClass [eventName]_

- eventName: [Register entry](https://github.com/XColorful/BattleRoyale/wiki/Function-config#Register-entry) custom event class name
- `return value`: whether the unregistration was successful