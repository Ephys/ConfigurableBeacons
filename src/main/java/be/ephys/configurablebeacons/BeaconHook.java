package be.ephys.configurablebeacons;

import be.ephys.cookiecore.config.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class BeaconHook {

    @Config(description = "What is the base range?")
    public static int baseRange = 10;

    @Config(description = "How many blocks are added to the range per level?")
    public static int rangeStep = 10;

    @Config(description = "Spawn particles around the player when an effect is active")
    public static boolean showParticles = true;

    @Config(description = "How the range is altered vertically. Java = Vanilla Java Behavior. Bedrock = Vanilla Bedrock behavior. FullHeight = expand vertical range to maximum")
    public static VerticalRangeType verticalRangeType = VerticalRangeType.Java;

    public enum VerticalRangeType {
        Java("Vanilla Java Edition Behavior"),
        Bedrock("Vanilla Bedrock Edition Behavior"),
        FullHeight("Ignore Y axis when applying effect");

        public final String description;

        VerticalRangeType(String description) {
            this.description = description;
        }
    }

    public static int getBeaconLevel(TileEntityBeacon beacon) {
        return beacon.getField(0);
    }

    public static Potion getPrimaryEffect(TileEntityBeacon beacon) {
        return Potion.getPotionById(beacon.getField(1));
    }

    public static Potion getSecondaryEffect(TileEntityBeacon beacon) {
        return Potion.getPotionById(beacon.getField(2));
    }

    public static void addEffectsToPlayers(TileEntityBeacon beacon) {
        World world = beacon.getWorld();
        if (world.isRemote) {
            return;
        }

        int level = getBeaconLevel(beacon);
        if (level <= 0) {
            return;
        }

        Potion primaryEffect = getPrimaryEffect(beacon);
        if (primaryEffect == null) {
            return;
        }

        Potion secondaryEffect = getSecondaryEffect(beacon);

        int range = (level * rangeStep) + baseRange;
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

        switch (verticalRangeType) {
            case Java:
                axisalignedbb = axisalignedbb.expand(0.0D, world.getHeight(), 0.0D);
                break;
            case FullHeight:
                axisalignedbb = axisalignedbb.grow(0.0D, world.getHeight(), 0.0D);
            case Bedrock:
            default:
                break;
        }

        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);

        for (EntityPlayer player : players) {
            player.addPotionEffect(new PotionEffect(primaryEffect, effectDuration, i, true, showParticles));
        }

        if (level >= 4 && primaryEffect != secondaryEffect && secondaryEffect != null) {
            for (EntityPlayer player : players) {
                player.addPotionEffect(new PotionEffect(secondaryEffect, effectDuration, 0, true, showParticles));
            }
        }
    }
}
