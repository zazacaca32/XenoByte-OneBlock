package forgefuck.team.xenobyte.modules;

import forgefuck.team.xenobyte.api.module.Category;
import forgefuck.team.xenobyte.api.module.CheatModule;
import forgefuck.team.xenobyte.api.module.PerformMode;
import forgefuck.team.xenobyte.api.module.PerformSource;

import forgefuck.team.xenobyte.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.multiplayer.WorldClient;


public class Menu extends CheatModule {

    public Menu() {
        super("Test", Category.MISC, PerformMode.SINGLE);
    }

    @Override  public void onPerform(PerformSource src) {
        Minecraft mc = utils.mc();
        utils.closeGuis();
        mc.theWorld.sendQuittingDisconnectingPacket();
        mc.loadWorld((WorldClient)null);
        utils.singlePlayerGui();
    }
    
    @Override public String moduleDesc() {
        return lang.get("t", "test");
    }
    


}