package xiao.battleroyale.common.server.function;

import net.minecraft.resources.Identifier;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.api.server.function.IFunctionManager;
import xiao.battleroyale.common.server.AbstractServerManager;
import xiao.battleroyale.config.common.server.function.type.RegisterEntry;

public class FunctionManager extends AbstractServerManager implements IFunctionManager {

    private static class FunctionManagerHolder {
        private static final FunctionManager INSTANCE = new FunctionManager();
    }

    public static FunctionManager get() {
        return FunctionManagerHolder.INSTANCE;
    }

    protected FunctionManager() {
        ;
    }

    public static void init(McSide mcSide) {
        ;
    }

    // Config注册项常驻，存档内api调用在服务器关闭时自动清理
    protected final FunctionData configFunction = new FunctionData();
    protected final FunctionData apiFunction = new FunctionData();

    @Override
    public void clearConfigFunction() {
        this.configFunction.clear();
    }

    @Override
    public void clearApiFunction() {
        this.apiFunction.clear();
    }

    // --------IServerStopHandler--------

    @Override
    public void onServerStopping() {
        clearApiFunction();
    }

    // --------IConfigFunctionRegister--------

    @Override
    public void applyConfig(RegisterEntry registerEntry) {
        if (registerEntry.clearPreviousBeforeApply) {
            clearConfigFunction();
        }
        FunctionRegisterHelper.registerAll(this.configFunction, registerEntry);
    }

    // --------IFunctionRegisterApi--------

    @Override public boolean registerFunction(Identifier rl, boolean isTag, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        return this.apiFunction.registerFunction(BattleRoyale.getEventRegister(), rl, isTag, eventType, priority, receiveCanceled);
    }
    @Override public boolean registerFunction(Identifier rl, boolean isTag, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        return this.apiFunction.registerFunction(BattleRoyale.getEventRegister(), rl, isTag, customEventType, priority, receiveCanceled);
    }
    @Override public boolean registerFunction(Identifier rl, boolean isTag, Class<? extends ICustomEvent> eventClass, EventPriority priority, boolean receiveCanceled) {
        return this.apiFunction.registerFunction(BattleRoyale.getEventRegister(), rl, isTag, eventClass, priority, receiveCanceled);
    }
    @Override public boolean registerFunctionToEventClass(ResourceLocation rl, boolean isTag, String eventClassStr, String priorityStr, boolean receiveCanceled) {
        return FunctionRegisterHelper.registerEventClass(this.apiFunction, rl, isTag, eventClassStr, priorityStr, receiveCanceled);
    }

    @Override public boolean unregisterFunction(Identifier rl, boolean isTag, EventType eventType) {
        return this.apiFunction.unregisterFunction(BattleRoyale.getEventRegister(), rl, isTag, eventType);
    }
    @Override public boolean unregisterFunction(Identifier rl, boolean isTag, CustomEventType customEventType) {
        return this.apiFunction.unregisterFunction(BattleRoyale.getEventRegister(), rl, isTag, customEventType);
    }
    @Override public boolean unregisterFunction(Identifier rl, boolean isTag, Class<? extends ICustomEvent> eventClass) {
        return this.apiFunction.unregisterFunction(BattleRoyale.getEventRegister(), rl, isTag, eventClass);
    }
    @Override public boolean unregisterFunctionToEventClass(ResourceLocation rl, boolean isTag, String eventClassStr) {
        return FunctionRegisterHelper.unregisterEventClass(this.apiFunction, rl, isTag, eventClassStr);
    }
}