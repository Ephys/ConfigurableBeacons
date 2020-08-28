package be.ephys.configurablebeacons;

import be.ephys.cookiecore.config.ConfigSynchronizer;
import net.minecraftforge.fml.common.Mod;

@Mod(ConfigurableBeaconsMod.MODID)
public class ConfigurableBeaconsMod
{
    public static final String MODID = "configurablebeacons";

    public ConfigurableBeaconsMod() {
        ConfigSynchronizer.synchronizeConfig();
    }
}
