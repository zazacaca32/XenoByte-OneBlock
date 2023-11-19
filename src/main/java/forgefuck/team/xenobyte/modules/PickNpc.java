package forgefuck.team.xenobyte.modules;

import forgefuck.team.xenobyte.api.Xeno;
import forgefuck.team.xenobyte.api.gui.WidgetMode;
import forgefuck.team.xenobyte.api.module.Category;
import forgefuck.team.xenobyte.api.module.CheatModule;
import forgefuck.team.xenobyte.api.module.PerformMode;
import forgefuck.team.xenobyte.api.module.PerformSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class PickNpc extends CheatModule {

    public PickNpc() {
        super("PickNpc", Category.MISC, PerformMode.SINGLE);
    }
    

    @Override public void onPerform(PerformSource src) {
        utils.nearEntityes(3)
                .filter(e -> e instanceof EntityLivingBase && !e.isDead)
                .filter(utils::isCustom)
                .forEach(e -> {
                    Xeno.logger.info(e.toString());
                    double[] pl = utils.dcoords(e);
                    getMeteSell().setCoords(pl);
                    widgetMessage(e.getCommandSenderName() + " #  " + e.posX + "  " + e.posY + "  " + e.posZ,  WidgetMode.INFO);

                });

    }


    
    @Override public String moduleDesc() {
        return lang.get("Displays the nearest players and the distance to them on the info panel", "Выбрать Npc для прожажи предметов");
    }

}