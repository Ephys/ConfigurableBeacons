package be.ephys.configurablebeacons;

import be.ephys.cookiecore.config.ConfigSynchronizer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

@Mod(
    modid = ConfigurableBeacons.MODID,
    version = ConfigurableBeacons.VERSION,
    certificateFingerprint = "@FINGERPRINT@",
    dependencies="required-after:cookiecore@[2.0.0,);"
)
public class ConfigurableBeacons
{
    public static final String MODID = "configurablebeacons";
    public static final String VERSION = "@VERSION@";

    @NetworkCheckHandler
    public boolean acceptConnection(Map<String, String> modList, Side side) {

        // Mod can be used on both client & server even if the other party doesn't have it installed
        return true;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigSynchronizer.synchronizeConfig(event);
    }
}
