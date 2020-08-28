package be.ephys.configurablebeacons;

import be.ephys.cookiecore.config.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class BeaconHook {

    @Config(name = "beacon.base_range", description = "What is the base range?")
    @Config.IntDefault(value = 10)
    public static ForgeConfigSpec.IntValue baseRange;

    @Config(name = "beacon.range_step", description = "How many blocks are added to the range per level?")
    @Config.IntDefault(value = 10)
    public static ForgeConfigSpec.IntValue rangeStep;

    @Config(name = "beacon.show_particles", description = "Spawn particles around the player when an effect is active")
    @Config.BooleanDefault(value = true)
    public static ForgeConfigSpec.BooleanValue showParticles;

    @Config(name = "beacon.vertical_range", description = "How the range is altered vertically. Java = Vanilla Java Behavior. Bedrock = Vanilla Bedrock behavior. FullHeight = expand vertical range to maximum")
    @Config.EnumDefault(value = "FullHeight", enumType = VerticalRangeType.class)
    public static ForgeConfigSpec.EnumValue<VerticalRangeType> verticalRangeType;

    public enum VerticalRangeType {
        Java("Vanilla Java Edition Behavior"),
        Bedrock("Vanilla Bedrock Edition Behavior"),
        FullHeight("Ignore Y axis when applying effect");

        public final String description;

        VerticalRangeType(String description) {
            this.description = description;
        }
    }

    public static int getBeaconLevel(BeaconTileEntity beacon) {
        return beacon.getLevels();
    }

    public static Effect getPrimaryEffect(BeaconTileEntity beacon) {
        return beacon.primaryEffect;
    }

    public static Effect getSecondaryEffect(BeaconTileEntity beacon) {
        return beacon.secondaryEffect;
    }

    public static void addEffectsToPlayers(BeaconTileEntity beacon) {
        World world = beacon.getWorld();
        if (world.isRemote) {
            return;
        }

        int level = getBeaconLevel(beacon);
        if (level <= 0) {
            return;
        }

        Effect primaryEffect = getPrimaryEffect(beacon);
        if (primaryEffect == null) {
            return;
        }

        Effect secondaryEffect = getSecondaryEffect(beacon);

        int range = (level * rangeStep.get()) + baseRange.get();
        int i = 0;

        if (level >= 4 && primaryEffect == secondaryEffect) {
            i = 1;
        }

        BlockPos beaconPos = beacon.getPos();

        int effectDuration = (9 + level * 2) * 20;
        int x = beaconPos.getX();
        int y = beaconPos.getY();
        int z = beaconPos.getZ();

        AxisAlignedBB axisalignedbb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1)
                .grow(range);

        switch (verticalRangeType.get()) {
            case Java:
                axisalignedbb = axisalignedbb.expand(0.0D, world.getHeight(), 0.0D);
                break;
            case FullHeight:
                axisalignedbb = axisalignedbb.grow(0.0D, world.getHeight(), 0.0D);
            case Bedrock:
            default:
                break;
        }

        List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, axisalignedbb);

        for (PlayerEntity player : players) {
            player.addPotionEffect(new EffectInstance(primaryEffect, effectDuration, i, true, showParticles.get()));
        }

        if (level >= 4 && primaryEffect != secondaryEffect && secondaryEffect != null) {
            for (PlayerEntity player : players) {
                player.addPotionEffect(new EffectInstance(secondaryEffect, effectDuration, 0, true, showParticles.get()));
            }
        }
    }
}
