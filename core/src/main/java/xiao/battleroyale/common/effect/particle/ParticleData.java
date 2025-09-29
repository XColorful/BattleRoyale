package xiao.battleroyale.common.effect.particle;

import net.minecraft.core.particles.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.config.common.effect.particle.ParticleDetailEntry;
import xiao.battleroyale.config.common.effect.particle.ParticleParameterEntry;
import xiao.battleroyale.util.ColorUtils;
import xiao.battleroyale.util.NBTUtils;
import xiao.battleroyale.util.Vec3Utils;

import java.awt.*;

/**
 * 单个数据
 * 描述粒子如何生成，不包括生成位置和追踪玩家的信息
 * 一个通道里可以包含多个数据
 */
public class ParticleData {

    public final ServerLevel serverLevel;
    public final long worldTime;

    public final ParticleDetailEntry particle;

    public int delayRemain = 0;
    public int finishedRepeat = 0;

    public ParticleData(ServerLevel serverLevel, ParticleDetailEntry detailEntry) {
        this.serverLevel = serverLevel;
        this.worldTime = serverLevel.getGameTime();
        this.particle = detailEntry;
        this.delayRemain = detailEntry.initDelay();
    }

    public void spawnParticle(Vec3 spawnPos) {
        if (this.serverLevel == null || this.serverLevel.isClientSide()) {
            return;
        }

        ResourceLocation particleRL = this.particle.particleType();
        ParticleType<?> typeObj = BattleRoyale.getMcRegistry().getParticleType(particleRL);
        if (typeObj == null) {
            BattleRoyale.LOGGER.warn("Unknown particle ResourceLocation: {}", particleRL);
            return;
        }

        ParticleParameterEntry parameter = this.particle.parameter();
        ParticleOptions options = null;

        CompoundTag parsedNbt = (parameter != null) ? parameter.nbt() : new CompoundTag();

        if (typeObj == ParticleTypes.DUST) {
            if (parameter != null && parameter.color() != null && !parameter.color().isEmpty()) {
                Color color = ColorUtils.parseColorFromString(parameter.color());
                options = new DustParticleOptions(new Vector3f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F), parameter.scale());
            } else {
                BattleRoyale.LOGGER.warn("Invalid or missing color for dust particle: {}", particleRL);
            }
        } else if (typeObj == ParticleTypes.NOTE) {
            options = (SimpleParticleType) typeObj;
        }
        // 对BlockParticleOption类型粒子进行处理
        else if ((typeObj == ParticleTypes.BLOCK || typeObj == ParticleTypes.FALLING_DUST || typeObj == ParticleTypes.BLOCK_MARKER)
                && parsedNbt.contains("block_state")) {
            @SuppressWarnings("unchecked")
            ParticleType<BlockParticleOption> blockParticleType = (ParticleType<BlockParticleOption>) typeObj;
            BlockState blockState = NBTUtils.readBlockState(parsedNbt.getCompound("block_state"));
            options = new BlockParticleOption(blockParticleType, blockState);
        } else if (typeObj == ParticleTypes.ITEM && parsedNbt.contains("item")) {
            ItemStack itemStack = NBTUtils.readItemStack(parsedNbt.getCompound("item"));
            options = new ItemParticleOption(ParticleTypes.ITEM, itemStack);
        } else if (typeObj instanceof SimpleParticleType) {
            options = (SimpleParticleType) typeObj;
        }

        if (options == null) {
            BattleRoyale.LOGGER.debug("Failed to create ParticleOptions for {}, skipped", particleRL);
            return;
        }

        float speed = 0;
        if (parameter != null) {
            speed = parameter.speed();
        }

        Vec3 offset = particle.offset();
        Vec3 offsetRange = particle.offsetRange();
        boolean exactOffset = particle.exactOffset();
        if (exactOffset) {
            for (int i = 0; i < this.particle.count(); i++) {
                Vec3 offsetVec = Vec3Utils.randomAdjustXYZ(offset, offsetRange, BattleRoyale.COMMON_RANDOM::nextFloat);
                this.serverLevel.sendParticles(options,
                        spawnPos.x() + offsetVec.x(),
                        spawnPos.y() + offsetVec.y(),
                        spawnPos.z() + offsetVec.z(),
                        1,
                        0.0D, 0.0D, 0.0D,
                        speed);
            }
        } else {
            for (int i = 0; i < this.particle.count(); i++) {
                this.serverLevel.sendParticles(options,
                        spawnPos.x() + offset.x(),
                        spawnPos.y() + offset.y(),
                        spawnPos.z() + offset.z(),
                        this.particle.count(),
                        offsetRange.x, offsetRange.y, offsetRange.z,
                        speed);
            }
        }
    }
}
