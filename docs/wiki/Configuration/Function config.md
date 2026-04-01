[English](#English)

# 函数配置

## 单个配置

- id：函数配置唯一id
- name：为该配置命名，可重复
- color：暂时没有功能
- register：事件注册词条
```json
{
	"id": 0,
	"name": "Default function",
	"color": "#FFFFFF",
	"register": {
		事件注册词条
	}
}
```

## 事件注册

- details：注册词条列表
```json
"register": {
	"details": [
		{
			"function": "battleroyale:test_function",
			"event": "GAME_START_FINISH_EVENT",
			"priority": "NORMAL",
			"receiveCanceled": false
		},
		{
			注册词条
		},
		{
			// ...
		}
	]
}
```

### 注册词条
> [事件API](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/architecture/api/event-api.md)

标识符（`function`或`tag`）：
- function：注册的单个函数，优先于`tag`
- tag：注册的函数标签

注册参数：
- event：事件类型名称，优先识别[自定义事件](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/CustomEventType.md)，其次识别[模组事件](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/EventType.md)
- priority：监听事件的[事件优先级](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/EventPriority.md)
- receiveCanceled：是否接收被取消的事件
> 函数返回值 ≤ -1 即可取消事件（如 _/return -1_），对于不可取消的事件类型则无效

（可选）自定义事件类：
- event：任意（注册时会被覆盖为 _CUSTOM\_EVENT_）
- eventClass：自定义事件类名称，优先于`event`
> 当包含`eventClass`时，若`eventClass`无效，则跳过该注册词条

#### 注册单个函数监听自定义事件

```json
{
	"function": "battleroyale:test_function",
	"event": "GAME_START_FINISH_EVENT",
	"priority": "NORMAL",
	"receiveCanceled": false
}
```

#### 注册函数标签监听模组事件

```json
{
	"function": "battleroyale:test_function",
	"event": "PLAYER_LOGGED_OUT_EVENT",
	"priority": "NORMAL",
	"receiveCanceled": false
}
```

#### 注册单个函数监听自定义事件类

该类别用于支持扩展自定义事件类
```json
{
	"function": "battleroyale:test_function",
	"event": "CUSTOM_EVENT",
	"priority": "NORMAL",
	"receiveCanceled": false,
	"eventClass": "xiao.battleroyale.api.event.custom.deathmatch.AddKillEvent$AddTeamKillEvent"
}
```

`eventClass`为 Java 类名（格式为：_{文件路径名}.{文件名}_ 或 _{文件路径名}.{文件名}${内部类名}_）：
- 文件路径名：第一行`package`到末尾`;`间的内容
- 文件名：加上`.`和`文件名`
- 如果是内部类，则再加上`$`和`内部类名`
> 如 _AddPlayerKillEvent_ 对应的`eventClass`为 _"xiao.battleroyale.api.event.custom.deathmatch.AddKillEvent$AddTeamKillEvent"_
```java
package xiao.battleroyale.api.event.custom.deathmatch; // 👈 1.文件路径名

// ...

// 类名（文件名）      👇2.文件名
public abstract class AddKillEvent extends CustomEvent {
	// ...
	
	// 内部类           👇3.内部类名
	public static class AddPlayerKillEvent extends AddKillEvent {
		// ...
	}
}
```

# English

```json
{
	"id": 0,
	"name": "Default function",
	"color": "#FFFFFF",
	"register": {
		EVENT REGISTER ENTRY
	}
}
```

## Single function config

- id: unique function id
- name: name the config, can be repeated
- color: no function for now
- register: event register entry
```json
{
	"id": 0,
	"name": "Default function",
	"color": "#FFFFFF",
	"register": {
		EVENT REGISTER ENTRY
	}
}
```

## Event register

- details: list of register entries
```json
"register": {
	"details": [
		{
			"function": "battleroyale:test_function",
			"event": "GAME_START_FINISH_EVENT",
			"priority": "NORMAL",
			"receiveCanceled": false
		},
		{
			REGISTER ENTRY
		},
		{
			// ...
		}
	]
}
```

### Register entry
> [Event API](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/architecture/api/event-api.md#English)

Identifiers (`function` or `tag`):
- function: single function to register, prioritized over `tag`
- tag: function tag to register

Registration parameters:
- event: event type name, prioritizes [Custom event type](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/CustomEventType.md#English), followed by [Mod event type](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/EventType.md#English)
- priority: [Event priority](https://github.com/XColorful/BattleRoyale/blob/HEAD/docs/api/event/EventPriority.md) for the listener
- receiveCanceled: whether to receive canceled events
> Event is canceled if function return value is ≤ -1 (e.g., `/return -1`), ineffective for non-cancelable event types

(Optional) Custom event class:
- event: any (overwritten as _CUSTOM\_EVENT_ during registration)
- eventClass: custom event class name, prioritized over `event`
> When `eventClass` is included, if `eventClass` is invalid, the register entry is skipped

#### Register single function to subscribe custom event

```json
{
	"function": "battleroyale:test_function",
	"event": "GAME_START_FINISH_EVENT",
	"priority": "NORMAL",
	"receiveCanceled": false
}
```

#### Register function tag to subscribe mod event

```json
{
	"function": "battleroyale:test_function",
	"event": "PLAYER_LOGGED_OUT_EVENT",
	"priority": "NORMAL",
	"receiveCanceled": false
}
```

#### Register single function to subscribe custom event class

This category is used to support extended custom event classes.
```json
{
	"function": "battleroyale:test_function",
	"event": "CUSTOM_EVENT",
	"priority": "NORMAL",
	"receiveCanceled": false,
	"eventClass": "xiao.battleroyale.api.event.custom.deathmatch.AddKillEvent$AddTeamKillEvent"
}
```

`eventClass` is a Java class name (Format: `{file_path}.{file_name}` or `{file_path}.{file_name}${inner_class_name}`):
- file_path: content from the first line `package` to the ending `;`
- file_name: add `.` and the `file name`
- If it is an inner class, add `$` and the `inner class name`
> e.g., the `eventClass` for `AddPlayerKillEvent` is _"xiao.battleroyale.api.event.custom.deathmatch.AddKillEvent$AddTeamKillEvent"_
```java
package xiao.battleroyale.api.event.custom.deathmatch; // 👈 1.file_path

// ...

// class name（file_name) 👇2.file_name
public abstract class AddKillEvent extends CustomEvent {
	// ...
	
	// inner class      👇3.inner class name
	public static class AddPlayerKillEvent extends AddKillEvent {
		// ...
	}
}
```