package xiao.battleroyale.compat.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
//import xiao.battleroyale.compat.fabric.init.registry.FabricRegistrarFactory;
//import xiao.battleroyale.compat.fabric.minecraft.FabricRegistry;
//import xiao.battleroyale.compat.fabric.network.FabricNetworkAdapter;
//import xiao.battleroyale.compat.fabric.network.FabricNetworkHook;
//import xiao.battleroyale.compat.fabric.event.FabricEventRegister;
//import xiao.battleroyale.compat.fabric.client.renderer.FabricBlockModelRenderer;
import xiao.battleroyale.init.registry.*;

public class BattleRoyaleFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        McSide mcSide = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;

//        BattleRoyale.init(
//                mcSide,
//                new FabricRegistrarFactory(),
//                new FabricRegistry(),
//                new FabricNetworkAdapter(),
//                new FabricNetworkHook(),
//                new FabricEventRegister(),
//                new FabricBlockModelRenderer(),
//                new BattleRoyale.CompatApi(null, null, null)
//        );
    }
}