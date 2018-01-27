package be.ephys.configurablebeacons;

import net.minecraft.tileentity.TileEntityBeacon;

public class BeaconHook {

    public static void addEffectsToPlayers(TileEntityBeacon beacon) {
        System.out.println("HOOK CALLED");
        System.out.println(beacon);
    }
}
