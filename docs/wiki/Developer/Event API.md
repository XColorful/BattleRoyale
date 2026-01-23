# 事件API

事件机制简述：
- 编写一些**事件处理函数**，将他们集中添加至注册处
- 玩家登录时派发（玩家登录）**事件**；玩家被击杀时派发（玩家死亡）**事件**；……
- 派发事件即从注册处**按优先级顺序**逐个执行事件处理函数

### 事件类型

#### 自定义事件类型

> 本模组创建的自定义事件（版本`0.4.6-dev1`）
```java
package xiao.battleroyale.api.event;  
  
public enum CustomEventType {
	// finish
	GAME_COMPLETE_EVENT,
	GAME_COMPLETE_FINISH_EVENT,
	GAME_STOP_EVENT,
	GAME_STOP_FINISH_EVENT,
	SERVER_STOP_EVENT,
	SERVER_STOP_FINISH_EVENT,
	// game
	GAME_PLAYER_DEATH_EVENT,
	GAME_PLAYER_DEATH_FINISH_EVENT,
	GAME_PLAYER_DOWN_EVENT,
	GAME_PLAYER_DOWN_FINISH_EVENT,
	GAME_PLAYER_REVIVE_EVENT,
	GAME_PLAYER_REVIVE_FINISH_EVENT,
	GAME_SPECTATE_EVENT,
	// spawn
	GAME_LOBBY_TELEPORT_EVENT,
	GAME_LOBBY_TELEPORT_FINISH_EVENT,
	// starter
	GAME_INIT_EVENT,
	GAME_INIT_FINISH_EVENT,
	GAME_LOAD_EVENT,
	GAME_LOAD_FINISH_EVENT,
	GAME_START_EVENT,
	GAME_START_FINISH_EVENT,
	// team
	INVITE_PLAYER_EVENT,
	INVITE_PLAYER_COMPLETE_EVENT,
	REQUEST_PLAYER_EVENT,
	REQUEST_PLAYER_COMPLETE_EVENT,
	// tick
	GAME_LOOT_BFS_EVENT,
	GAME_LOOT_BFS_FINISH_EVENT,
	GAME_LOOT_EVENT,
	GAME_LOOT_FINISH_EVENT,
	GAME_TICK_EVENT,
	GAME_TICK_FINISH_EVENT,
	ZONE_TICK_EVENT,
	ZONE_TICK_FINISH_EVENT,
	// zone
	ZONE_CREATED_EVENT,
	ZONE_COMPLETE_EVENT,
	CUSTOM_ZONE_EVENT,
	AIRDROP_EVENT,
	ENTITY_EVENT,
	// generate
	CUSTOM_GENERATE_EVENT,
	// client
	SPECIAL_ZONE_RENDER_EVENT
}
```
> - 名称带 `"Finish"` 的事件类型不可取消
```java
package xiao.battleroyale.api.event;

public interface ICustomEvent extends IEvent {
	CustomEventType getEventType();
}
```
> - 通过查看`ICustomEvent`具体事件类的`getEventType`判断是否对应该事件

#### 模组事件类型

> Forge/NeoForge提供的事件
```java
package xiao.battleroyale.api.event;

public enum EventType {
	SERVER_TICK_EVENT,
	CLIENT_TICK_EVENT,
	LIVING_DAMAGE_EVENT,
	LIVING_DEATH_EVENT,
	PLAYER_LOGGED_IN_EVENT,
	PLAYER_LOGGED_OUT_EVENT
}
```
> - 仅包含本模组需要使用的事件类型

### 事件属性

#### 事件优先级

```java
package xiao.battleroyale.api.event;  
  
public enum EventPriority {  
    HIGHEST,  
    HIGH,  
    NORMAL,  
    LOW,  
    LOWEST;  
}
```
> - 自定义事件优先级与模组事件一致

#### 接收被取消的事件

```java
boolean receiveCanceled;
```
> - 通过本模组提供的API注册，对于相同优先级的**自定义事件**和**模组事件**，`receivedCanceled = true` 的事件一定在所有 `receivedCanceled = false` 的事件之后执行

### 注册事件处理器

> - 在Forge/NeoForge该操作为“监听事件”，而本模组已经封装相关接口，即只需要注册**事件处理器**即可自动监听
> - 建议保留注册的事件处理器的实例，以取消注册

#### 注册自定义事件处理器

```java
ICustomEventRegister customEventRegister = xiao.battleroyale.BattleRoyale.getEventRegister(); // 自定义事件注册API
ICustomEventHandler handler; // 自定义事件处理器
ICustomEventType customEventType; // 自定义事件类型
Priority priority; // 事件优先级
boolean receiveCanceled; // 是否接收被取消的事件
customEventRegister.register(handler, customEventType, priority, receiveCanceled);
```
> - `BattleRoyale.getEventRegister()`自模组加载时即可用

 #### 注册模组事件处理器
 
 ```java
 EventRegister eventRegister = xiao.battleroyale.event.EventRegister.get(); // 模组事件注册API
 EventHandler handler; // 模组事件处理器
 IEventType eventType; // 模组事件类型
 Priority priority; // 事件优先级
 boolean receiveCanceled; // 是否接收被取消的事件
 ```
> - 模组事件处理器需要在**游戏中**手动注册/取消注册，而不是在模组加载时就注册

# English

A brief description of the event mechanism:
- Write **event handler functions** and register them collectively to the registry.
- An **event** is dispatched (e.g., Player Logged In) when a player logs in; an **event** is dispatched (e.g., Player Death) when a player is killed; etc.
- Dispatching an event means executing the event handler functions **in priority order** from the registry one by one.

### Event types

#### Custom event type

> Custom events created in this mod (version `0.4.6-dev1`)
```java
package xiao.battleroyale.api.event;  
  
public enum CustomEventType {
	// finish
	GAME_COMPLETE_EVENT,
	GAME_COMPLETE_FINISH_EVENT,
	GAME_STOP_EVENT,
	GAME_STOP_FINISH_EVENT,
	SERVER_STOP_EVENT,
	SERVER_STOP_FINISH_EVENT,
	// game
	GAME_PLAYER_DEATH_EVENT,
	GAME_PLAYER_DEATH_FINISH_EVENT,
	GAME_PLAYER_DOWN_EVENT,
	GAME_PLAYER_DOWN_FINISH_EVENT,
	GAME_PLAYER_REVIVE_EVENT,
	GAME_PLAYER_REVIVE_FINISH_EVENT,
	GAME_SPECTATE_EVENT,
	// spawn
	GAME_LOBBY_TELEPORT_EVENT,
	GAME_LOBBY_TELEPORT_FINISH_EVENT,
	// starter
	GAME_INIT_EVENT,
	GAME_INIT_FINISH_EVENT,
	GAME_LOAD_EVENT,
	GAME_LOAD_FINISH_EVENT,
	GAME_START_EVENT,
	GAME_START_FINISH_EVENT,
	// team
	INVITE_PLAYER_EVENT,
	INVITE_PLAYER_COMPLETE_EVENT,
	REQUEST_PLAYER_EVENT,
	REQUEST_PLAYER_COMPLETE_EVENT,
	// tick
	GAME_LOOT_BFS_EVENT,
	GAME_LOOT_BFS_FINISH_EVENT,
	GAME_LOOT_EVENT,
	GAME_LOOT_FINISH_EVENT,
	GAME_TICK_EVENT,
	GAME_TICK_FINISH_EVENT,
	ZONE_TICK_EVENT,
	ZONE_TICK_FINISH_EVENT,
	// zone
	ZONE_CREATED_EVENT,
	ZONE_COMPLETE_EVENT,
	CUSTOM_ZONE_EVENT,
	AIRDROP_EVENT,
	ENTITY_EVENT,
	// generate
	CUSTOM_GENERATE_EVENT,
	// client
	SPECIAL_ZONE_RENDER_EVENT
}
```
- Event types with `"Finish"` in their name are non-cancellable.
```java
package xiao.battleroyale.api.event;

public interface ICustomEvent extends IEvent {
	CustomEventType getEventType();
}
```
> - Determine if it corresponds to the event by checking the `getEventType` of the specific event class of `ICustomEvent`.

#### Mod event type

> Events provided by Forge/NeoForge.
```java
package xiao.battleroyale.api.event;

public enum EventType {
	SERVER_TICK_EVENT,
	CLIENT_TICK_EVENT,
	LIVING_DAMAGE_EVENT,
	LIVING_DEATH_EVENT,
	PLAYER_LOGGED_IN_EVENT,
	PLAYER_LOGGED_OUT_EVENT
}
```
- Only includes event types required by this mod.

### Event property

#### Event priority

```java
package xiao.battleroyale.api.event;  
  
public enum EventPriority {  
    HIGHEST,  
    HIGH,  
    NORMAL,  
    LOW,  
    LOWEST;  
}
```
> - Custom event priorities are consistent with mod events.

#### Receive canceled event

```java
boolean receiveCanceled;
```
> - When registered via the API provided by this mod, for **Custom Events** and **Mod Events** of the same priority, events with `receivedCanceled = true` will always be executed **after** all events with `receivedCanceled = false`.

### Register event handler

> - In Forge/NeoForge, this operation is "listening for events." This mod has encapsulated the relevant interfaces, meaning you only need to register the **event handler** to automatically listen for events.
> - It is recommended to retain the instance of the registered event handler for unregistration purposes.

#### Register custom event handler

```java
ICustomEventRegister customEventRegister = xiao.battleroyale.BattleRoyale.getEventRegister(); // Custom event registration API
ICustomEventHandler handler; // Custom event handler
ICustomEventType customEventType; // Custom event type
Priority priority; // Event priority
boolean receiveCanceled; // Whether to receive canceled events
customEventRegister.register(handler, customEventType, priority, receiveCanceled);
```
> - `BattleRoyale.getEventRegister()` is available from the moment the mod is loaded.

#### Register mod event handler

 ```java
 EventRegister eventRegister = xiao.battleroyale.event.EventRegister.get(); // Mod event registration API
 EventHandler handler; // Mod event handler
 IEventType eventType; // Mod event type
 Priority priority; // Event priority
 boolean receiveCanceled; // Whether to receive canceled events
 ```
> - Mod event handlers need to be manually registered/unregistered **in-game**, not upon mod loading.