package forgefuck.team.xenobyte.modules;

import forgefuck.team.xenobyte.api.Xeno;
import forgefuck.team.xenobyte.api.config.Cfg;
import forgefuck.team.xenobyte.api.gui.InputType;
import forgefuck.team.xenobyte.api.module.Category;
import forgefuck.team.xenobyte.api.module.CheatModule;
import forgefuck.team.xenobyte.api.module.PerformMode;
import forgefuck.team.xenobyte.api.module.PerformSource;
import forgefuck.team.xenobyte.gui.click.elements.Button;
import forgefuck.team.xenobyte.gui.click.elements.Panel;
import forgefuck.team.xenobyte.gui.click.elements.ScrollSlider;
import forgefuck.team.xenobyte.gui.swing.UserInput;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class NBTGive extends CheatModule {

    @Cfg("coords")
    private List<String> coords;

    @Cfg("slot")
    private int slot;
    @Cfg("count")
    private int count;

    public NBTGive() {
        super("NBTGive", Category.MODS, PerformMode.SINGLE);
        coords = new ArrayList<String>();
        coords.add("0");
        coords.add("0");
        coords.add("0");
        slot = 23;
        count = 1;
    }

    @Override
    public void onPerform(PerformSource src) {
        NBTTagCompound tag = new NBTTagCompound();
        try {

            Class<?> AbstractPacket = Class.forName("com.mcf.davidee.nbtedit.packets.AbstractPacket");

            Class<?> TileNBTUpdatePacket = Class.forName("com.mcf.davidee.nbtedit.packets.TileNBTUpdatePacket");


            Class<?> DISPATCHER = Class.forName("com.mcf.davidee.nbtedit.packets.PacketPipeline");

            DISPATCHER.getMethod("sendToDimension", AbstractPacket, int.class)
                    .invoke(DISPATCHER.getConstructor()
                            .newInstance(), TileNBTUpdatePacket.getConstructor(int.class, int.class, int.class, NBTTagCompound.class)
                                    .newInstance(Integer.parseInt(coords.get(0)), Integer.parseInt(coords.get(1)), Integer.parseInt(coords.get(2)), tag),
                            utils.player().dimension);

        } catch (Exception e) {
            Xeno.logger.error("error while : [" + e.getMessage() + "]");
            e.printStackTrace();
        }
    }

    @Override
    public boolean inGuiPerform() {
        return true;
    }

    @Override
    public boolean isWorking() {
        return true;
//        return Loader.isModLoaded("MetaNpc");
    }

    @Override
    public String moduleDesc() {
        return lang.get("", "Продать могу?");
    }

    public void setCoords(double[] c) {
        coords.clear();
        coords.add(String.valueOf(c[0]));
        coords.add(String.valueOf(c[1]));
        coords.add(String.valueOf(c[2]));
    }


    @Override
    public Panel settingPanel() {
        return new Panel(
                new Button("Coords") {
                    @Override
                    public void onLeftClick() {
                        new UserInput(lang.get("Coords", "Координаты"), coords, InputType.COORDS).showFrame();
                    }

                    @Override
                    public String elementDesc() {
                        return lang.get("Specified x y z", "Заданные x y z");
                    }
                },
                new ScrollSlider("slot", slot, 0, 23) {
                    @Override
                    public void onScroll(int dir, boolean withShift) {
                        slot = (int) processSlider(dir, withShift);
                    }

                    @Override
                    public String elementDesc() {
                        return lang.get("slot", "slot");
                    }
                },
                new ScrollSlider("count", count, 64) {
                    @Override
                    public void onScroll(int dir, boolean withShift) {
                        count = (int) processSlider(dir, withShift);
                    }

                    @Override
                    public String elementDesc() {
                        return lang.get("count", "count");
                    }
                }
        );
    }


}
