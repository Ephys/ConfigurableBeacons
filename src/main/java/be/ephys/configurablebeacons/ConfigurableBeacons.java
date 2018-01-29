package be.ephys.configurablebeacons;

import be.ephys.cookiecore.config.ConfigSynchronizer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ConfigurableBeacons.MODID, version = ConfigurableBeacons.VERSION, serverSideOnly = true, clientSideOnly = true)
public class ConfigurableBeacons
{
    public static final String MODID = "configurablebeacons";
    public static final String VERSION = "@VERSION@";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigSynchronizer.synchronizeConfig(event);
    }
}
