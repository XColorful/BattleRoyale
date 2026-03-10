package xiao.battleroyale.compat.fabric.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import xiao.battleroyale.init.CommandRegistry;

public class FabricCommandRegistry {
    private static final CommandRegistry COMMAND_REGISTRY = CommandRegistry.get();

    public static void init(EnvType envType) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            COMMAND_REGISTRY.registerServerCommands(dispatcher);
        });

        if (envType == EnvType.CLIENT) {
            ClientCommandRegistration.registerClientOnly();
        }
    }

    /**
     * 内部类：用于隔离客户端 API 调用。
     * 只有在确定是客户端环境时才加载此类，防止服务端崩溃。
     */
    private static class ClientCommandRegistration {
        private static void registerClientOnly() {
            // to be continued =>
        }
    }
}