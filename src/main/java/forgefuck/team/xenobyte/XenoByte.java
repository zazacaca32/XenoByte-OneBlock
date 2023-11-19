package forgefuck.team.xenobyte;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import forgefuck.team.xenobyte.api.Xeno;
import forgefuck.team.xenobyte.handlers.ModuleHandler;
import net.minecraft.client.Minecraft;

import static forgefuck.team.xenobyte.api.Xeno.logger;

@Mod(modid = Xeno.mod_id, name = Xeno.mod_name, version = Xeno.mod_version)

public class XenoByte {
    
    @EventHandler public void init(FMLInitializationEvent e) {
        if (e == null) {
            starter(null);
        }
    }
    
    @EventHandler public void starter(FMLLoadCompleteEvent e) {
        new ModuleHandler();
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {

        logger.info( Minecraft.getMinecraft().func_147104_D().toString());
        logger.info( event.toString());
    }
    
}