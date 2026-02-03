package xiao.battleroyale.compat.tacz;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IBulletHurtEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.minecraft.IMcRegistry;

import java.util.UUID;

/**
 * 处理盔甲对 TaCZ 子弹的比例减伤和比例破损
 */
public class TaczBulletHandler {

    private static class TaczBulletHandlerHolder {
        private static final TaczBulletHandler INSTANCE = new TaczBulletHandler();
    }

    public static TaczBulletHandler get() {
        return TaczBulletHandlerHolder.INSTANCE;
    }

    private TaczBulletHandler() {}

    private static boolean isRegistered = false;

    public void register() {
        if (isRegistered) return;
        isRegistered = BattleRoyale.getCompatApi().taczEventRegister().registerBulletHandler();
    }

    public void unregister() {
        if (!isRegistered) return;
        BattleRoyale.getCompatApi().taczEventRegister().unregisterBulletHandler();
        isRegistered = false;
    }

    public void onBulletHurt(IBulletHurtEvent event) {
        if (event.getMcSide() == McSide.CLIENT) return;
        if (!(event.getHurtEntity() instanceof LivingEntity victim)) return;

        IMcRegistry mcRegistry = BattleRoyale.getMcRegistry();
        boolean isHeadShot = event.isHeadShot();
        float baseDamage = event.getBaseDamage();

        if (baseDamage <= 0) return;

        EquipmentSlot slot = isHeadShot ? EquipmentSlot.HEAD : EquipmentSlot.CHEST;
        ItemStack armor = victim.getItemBySlot(slot);
        if (armor.isEmpty()) return;

        ResourceLocation itemRl = mcRegistry.getItemRl(armor.getItem());
        String path = itemRl.getPath();
        float reduction = 0f;
        boolean isBrokenArmor = false;

        if (isHeadShot) {
            // 三级头：减55% | 耐久230
            if (path.equals("netherite_helmet")) reduction = 0.55f;
                // 二级头：减40% | 耐久150
            else if (path.equals("iron_helmet")) reduction = 0.40f;
                // 一级头：减30% | 耐久80
            else if (path.equals("leather_helmet")) reduction = 0.30f;
        } else {
            // 碎甲：固定减20%
            if (path.equals("chainmail_chestplate")) {
                reduction = 0.20f;
                isBrokenArmor = true;
            }
            // 三级甲：减55% | 耐久250
            else if (path.equals("diamond_chestplate")) reduction = 0.55f;
                // 二级甲：减40% | 耐久220
            else if (path.equals("iron_chestplate")) reduction = 0.40f;
                // 一级甲：减30% | 耐久200
            else if (path.equals("leather_chestplate")) reduction = 0.30f;
        }

        if (reduction > 0) {
            float totalIncoming = baseDamage;
            if (isHeadShot) {
                totalIncoming *= event.getHeadShotMultiplier();
            }
            float absorbedDamage = totalIncoming * reduction;

            event.setBaseDamage(baseDamage * (1 - reduction));

            if (!isBrokenArmor) {
                float pubgLoss = absorbedDamage * 5.0f;
                float pubgMax = getPubgMaxDurability(slot, path);
                int mcMax = armor.getMaxDamage();

                int finalMcLoss = (int) ((pubgLoss / pubgMax) * mcMax);

                if (finalMcLoss > 0) {
                    processDurability(victim, armor, slot, finalMcLoss);
                }
            }
        }
    }

    private void processDurability(LivingEntity victim, ItemStack armor, EquipmentSlot slot, int loss) {
        int currentDamage = armor.getDamageValue();
        int maxDamage = armor.getMaxDamage();

        if (currentDamage + loss >= maxDamage) {
            if (slot == EquipmentSlot.HEAD) {
                armor.shrink(1);
                victim.broadcastBreakEvent(slot);
            } else {
                ItemStack brokenState = new ItemStack(Items.CHAINMAIL_CHESTPLATE);
                IGameManager gameManager = BattleRoyale.getGameManager();
                UUID gameId = gameManager.getGameIdReadApi().getGameId(armor);
                if (gameId != null) {
                    gameManager.getGameIdWriteApi().addGameId(brokenState, gameId);
                }
                brokenState.setDamageValue(brokenState.getMaxDamage());
                victim.setItemSlot(slot, brokenState);
            }
        } else {
            armor.setDamageValue(currentDamage + loss);
        }
    }

    private float getPubgMaxDurability(EquipmentSlot slot, String path) {
        if (slot == EquipmentSlot.HEAD) {
            if (path.contains("netherite")) return 230f;
            if (path.contains("iron")) return 150f;
            return 80f;
        } else {
            if (path.contains("diamond")) return 250f;
            if (path.contains("iron")) return 220f;
            return 200f;
        }
    }
}