[English](#English)

# 创建自定义事件处理器

1. 查看[自定义事件类型](https://github.com/XColorful/BattleRoyale/wiki/Event-API#自定义事件类型)
2. [注册自定义事件处理器](https://github.com/XColorful/BattleRoyale/wiki/Event-API#注册自定义事件处理器)
3. 创建事件处理器

> 实际代码编写顺序为3→2，然而2→3则是提醒不要忘记**注册**事件处理器

在模组主类：
```java
package xiao.battleroyale;

public class BattleRoyale {
	public static void init(McSide mcSide) {
		ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister(); // 自定义事件注册API
		CustomEventHandler.registerAll(customEventRegister);
	}
}
```
集中注册所有事件处理器，使模组主类更简洁：
```java
package xiao.battleroyale.event.custom;

public class CustomEventHandler {
	public static void registerAll(ICustomEventRegister customEventRegister) {
		ICustomEventHandler handler; // 事件处理器
		CustomEventType customEventType; // 自定义事件类型
		EventPriority priority; // 事件优先级
		customEventRegister.register(handler, customEventType, priority);
		if (BattleRoyale.getMcSide().isClientSide()) {
			// 客户端检查，防止在专用服务器上执行客户端专属代码
			ICustomEventHandler clientEventHandler; // 客户端事件处理器
			// ...注册方式同上
		}
	}
}
```
在 _xiao.battleroyale.api.event_ 下寻找`CustomEventType`对应的`ICustomEvent`具体事件类，如 _SpecialZoneRenderEvent_：
```java
package xiao.battleroyale.api.event.client.render;

public class SpecialZoneRenderEvent extends AbstractSpecialRenderEvent {
	// 对应事件类型
	@Override public CustomEventType getEventType() {
		return CustomEventType.SPECIAL_ZONE_RENDER_EVENT;
	}
	// 其余方法为事件提供的上下文
	public IRenderLevelStageEvent getRenderEvent() {}
	public Matrix4f getBaseModelView() {}
	public Vec3 getCameraPos() {}
	public float getPartialTick() {}
	public IClientSimpleZoneRenderer getClientZoneRenderer() {}
	public ClientSingleZoneData getZoneData() {}
}
```
实现自定义事件处理器：
> - 本模组的自定义事件机制已经保证`ICustomEvent`的具体事件类型与`CustomEventType`对应，`handleEvent`的`CustomEventType`已经是从`ICustomEvent`提取的
```java
package xiao.battleroyale.event.custom.client;
  
public class SpecialRenderHandler implements ICustomEventHandler {

// 自拟事件处理器名称，建议为"namespace:className"格式
@Override public String getEventHandlerName() {
    return String.format("%s:SpecialRenderHandler", BattleRoyale.MOD_ID);  
}

@Override public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
	// 对于只处理1个事件类型，直接使用if即可；对于处理超过1个事件类型，请使用switch case
	if (customEventType == CustomEventType.SPECIAL_ZONE_RENDER_EVENT) {
		SpecialZoneRenderEvent renderEvent = (SpecialZoneRenderEvent) event; // 只需判断事件类型枚举，无需类型检查
		// 部分带可扩展功能的自定义事件带有额外信息，应先检查是否指向该事件处理器
		SpecialRenderProtocol renderProtocol = SpecialRenderProtocol.getConfigFromProtocol(renderEvent.getProtocol(), renderEvent.getJsonTag());
		if (renderProtocol == null) return;
		
		// 1. 从当前事件获取API / 从BattleRoyale(模组主类)获取全局静态API
		// 2. 处理事件
	} else {
		onReceiveWrongEvent(customEventType); // 统一日志记录，可自行替换
	}
}

```

# English

1. Check [Custom event type](https://github.com/XColorful/BattleRoyale/wiki/Event-API#Custom-event-type)
2. [Register custom event handler](https://github.com/XColorful/BattleRoyale/wiki/Event-API#Register-custom-event-handler)
3. Create the event handler

> The actual code writing order is 3→2, but 2→3 is a reminder not to forget to **register** the event handler.

In the mod's main class:
```java
package xiao.battleroyale;

public class BattleRoyale {
	public static void init(McSide mcSide) {
		ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister(); // Custom event registration API
		CustomEventHandler.registerAll(customEventRegister);
	}
}
```
Centralize the registration of all event handlers to keep the mod's main class concise:
```java
package xiao.battleroyale.event.custom;

public class CustomEventHandler {
	public static void registerAll(ICustomEventRegister customEventRegister) {
		ICustomEventHandler handler; // Event handler
		CustomEventType customEventType; // Custom event type
		EventPriority priority; // Event priority
		customEventRegister.register(handler, customEventType, priority);
		if (BattleRoyale.getMcSide().isClientSide()) {
			// Client-side check to prevent client-exclusive code from running on a dedicated server
			ICustomEventHandler clientEventHandler; // Client event handler
			// ...registration method is the same as above
		}
	}
}
```
Find the specific event class `ICustomEvent` corresponding to `CustomEventType` under _xiao.battleroyale.api.event_, such as _SpecialZoneRenderEvent_:
```java
package xiao.battleroyale.api.event.client.render;

public class SpecialZoneRenderEvent extends AbstractSpecialRenderEvent {
	// Corresponding event type
	@Override public CustomEventType getEventType() {
		return CustomEventType.SPECIAL_ZONE_RENDER_EVENT;
	}
	// The other methods provide context for the event.
	public IRenderLevelStageEvent getRenderEvent() {}
	public Matrix4f getBaseModelView() {}
	public Vec3 getCameraPos() {}
	public float getPartialTick() {}
	public IClientSimpleZoneRenderer getClientZoneRenderer() {}
	public ClientSingleZoneData getZoneData() {}
}
```
Custom event handler implementation:
> - The custom event mechanism in this mod ensures that the specific event type of `ICustomEvent` corresponds to `CustomEventType`, and the `CustomEventType` of `handleEvent` is extracted from `ICustomEvent`.
```java
package xiao.battleroyale.event.custom.client;
  
public class SpecialRenderHandler implements ICustomEventHandler {

// Self-defined event handler name, recommended format is "namespace:className"
@Override public String getEventHandlerName() {
    return String.format("%s:SpecialRenderHandler", BattleRoyale.MOD_ID);  
}

@Override public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
	// Use 'if' for handling only 1 event type; use 'switch case' for handling more than 1 event type
	if (customEventType == CustomEventType.SPECIAL_ZONE_RENDER_EVENT) {
		SpecialZoneRenderEvent renderEvent = (SpecialZoneRenderEvent) event; // Only check the event type enum, no need for type checking
		// Some custom events with extensible functionality come with additional information; you should first check if it targets this handler
		SpecialRenderProtocol renderProtocol = SpecialRenderProtocol.getConfigFromProtocol(renderEvent.getProtocol(), renderEvent.getJsonTag());
		if (renderProtocol == null) return;
		
		// 1. Obtain API from the current event or static API via BattleRoyale (Mod Main Class)
		// 2. Handle the event
	} else {
		onReceiveWrongEvent(customEventType); // Unified logging, can be replaced as needed
	}
}

```