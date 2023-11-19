package forgefuck.team.xenobyte.modules;

import forgefuck.team.xenobyte.api.config.Cfg;
import forgefuck.team.xenobyte.api.gui.ColorPicker;
import forgefuck.team.xenobyte.api.integration.NEI;
import forgefuck.team.xenobyte.api.module.Category;
import forgefuck.team.xenobyte.api.module.CheatModule;
import forgefuck.team.xenobyte.api.module.PerformMode;
import forgefuck.team.xenobyte.api.module.PerformSource;
import forgefuck.team.xenobyte.gui.click.elements.Button;
import forgefuck.team.xenobyte.gui.click.elements.Panel;
import forgefuck.team.xenobyte.gui.swing.ColorPickerGui;
import forgefuck.team.xenobyte.render.Colors;
import forgefuck.team.xenobyte.render.EspDropHintRender;
import forgefuck.team.xenobyte.utils.Config;
import forgefuck.team.xenobyte.utils.Reflections;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EspDropSelect extends CheatModule {

    @Cfg("configBlocks") private List<String> configBlocks;
    @Cfg("guiHint") public boolean guiHint;
    private List<String> missingBlocks;
    public List<SelectedItem> blocks;
    private String neiSubset;

    public EspDropSelect() {
        super("EspDropSelect", Category.NEI, PerformMode.SINGLE);
        blocks = new CopyOnWriteArrayList<SelectedItem>();
        missingBlocks = new ArrayList<String>();
        configBlocks = new ArrayList<String>();
        neiSubset = "EspDrop";
        guiHint = true;
    }
    
    private void updateNEI() {
        NEI.addSubset(mod_name + "." + neiSubset, blocks.stream().map(b -> b.itemBlock).collect(Collectors.toList()));
    }
    
    public SelectedItem getItem(EntityItem e) {
        return getItem(b -> b.itemBlockEquals(e.getEntityItem()));
    }

    public SelectedItem getItem(ItemStack stack) {
        return getItem(b -> b.itemBlockEquals(stack));
    }
    
    public SelectedItem getItem(Block block, int meta) {
        return getItem(b -> b.blockEquals(block, meta));
    }
    
    private SelectedItem getItem(Predicate<SelectedItem> predicate) {
        return blocks.stream().filter(predicate).findFirst().orElse(null);
    }
    
    @Override public void onPostInit() {
        IdentityHashMap<Item, IItemRenderer> customRender = Reflections.getPrivateValue(MinecraftForgeClient.class, null, 0);
        for (Object obj : Item.itemRegistry) {
            if (obj instanceof ItemBlock && !customRender.containsKey(obj)) {
                MinecraftForgeClient.registerItemRenderer((Item) obj, new EspDropHintRender(this));
            }
        }
        for (String cBlock : configBlocks) {
            String[] data = cBlock.split(":");
            Block block = (Block) Block.blockRegistry.getObject(data[0] + ":" + data[1]);
            if (block instanceof BlockAir) {
                missingBlocks.add(cBlock);
            } else {
                int meta = Integer.parseInt(data[2]);
                int color = Integer.parseInt(data[3]);
                float scale = Float.parseFloat(data[4]);
                boolean hidden = data.length <= 5 ? false : Boolean.parseBoolean(data[5]);
                boolean tracer = data.length <= 5 ? false : Boolean.parseBoolean(data[6]);
                blocks.add(new SelectedItem(new ItemStack(block, 1, meta), color, scale, hidden, tracer));
            }
        }
        updateNEI();
    }
    
    @Override public void onPerform(PerformSource src) {
        switch (src) {
        case BUTTON:
               NEI.openGui("@" + neiSubset);
            break;
        case KEY:
            ItemStack stack = utils.isInGameGui() ? utils.getStackFromView() : NEI.getStackMouseOver();
            if (stack != null) {
                SelectedItem block = getItem(stack);
                new EspDropSettings(block == null ? new SelectedItem(stack, Colors.BLACK, 1, false, false) : block).showFrame();
            }
        }
    }
    
    @Override public boolean isWorking() {
        return NEI.isAvailable();
    }
    
    @Override public boolean inGuiPerform() {
        return true;
    }
    
    @Override public boolean allowStateMessages() {
        return false;
    }
    
    @Override public String moduleDesc() {
        return lang.get("Selecting by keybind a block for X-Ray in NEI, or by looking at block. Adds NEI @X-Ray tab", "Выбор по кейбинду блока для X-Ray в NEI, или по взгляду на блок. Добавляет вкладку @X-Ray");
    }
    
    @Override public Panel settingPanel() {
        return new Panel(
            new Button("GuiHint", guiHint) {
                @Override public void onLeftClick() {
                    buttonValue(guiHint = !guiHint);
                }
                @Override public String elementDesc() {
                    return lang.get("Highlighting selected items in inventory", "Подсветка выбранных предметов в инвентаре");
                }
            }
        );
    }
    
    class EspDropSettings extends ColorPickerGui {
        
        JCheckBox hidden, tracer;
        SelectedItem block;
        JSlider s;

        EspDropSettings(SelectedItem block) {
            super(block.itemBlock.getDisplayName(), block);
            this.block = block;
            s = new JSlider(0, 100);
            s.setPreferredSize(new Dimension(350, 50));
            s.setValue((int)(this.block.scale * 100));
            s.addChangeListener((e) -> {
                this.block.scale = (float) s.getValue() / 100;
            });
            hidden = new JCheckBox("Hidden", block.hidden);
            hidden.addActionListener((e) -> {
                this.block.hidden = hidden.isSelected();
            });
            tracer = new JCheckBox("Tracer", block.tracer);
            tracer.addActionListener((e) -> {
                this.block.tracer = tracer.isSelected();
            });
            buttonsBar.add(clear);
            buttonsBar.add(hidden);
            buttonsBar.add(tracer);
            sliders.add(s, GBC);
        }
        
        @Override public void localizeSet() {
            super.localizeSet();
            tracer.setToolTipText(lang.get("Draw tracer line to block", "Рисовать трасер линию к блоку"));
            hidden.setToolTipText(lang.get("Hide block from render", "Скрыть блок из отрисовки"));
            s.setBorder(customTitledBorder(lang.get("Size", "Размер")));
            clear.setText(lang.get("Delete", "Удалить"));
        }
        
        @Override public void actionPerformed(ActionEvent e) {
            if (e.getSource() == accept) {
                if (!blocks.contains(block)) {
                    blocks.add(block);
                }
            } else if (e.getSource() == clear) {
                blocks.remove(block);
            }
            configBlocks.clear();
            configBlocks.addAll(blocks.stream().map(SelectedItem::toString).collect(Collectors.toList()));
            configBlocks.addAll(missingBlocks);
            Config.save();
            updateNEI();
            dispose();
        }
    }
    
    public class SelectedItem extends ColorPicker {
        
        public boolean hidden, tracer;
        final ItemStack itemBlock;
        final Block block;
        final String id;
        final int meta;
        float scale;
        
        SelectedItem(ItemStack itemBlock, int color, float scale, boolean hidden, boolean tracer) {
            super(color);
            this.scale = scale;
            this.hidden = hidden;
            this.tracer = tracer;
            this.itemBlock = itemBlock;
            this.meta = itemBlock.getItemDamage();
            this.id = itemBlock.getItem().delegate.name();
            this.block = Block.getBlockFromItem(itemBlock.getItem());
        }
        
        boolean itemBlockEquals(ItemStack stack) {
            return itemBlock.isItemEqual(stack);
        }
        
        boolean blockEquals(Block block, int meta) {
            return Block.isEqualTo(this.block, block) && this.meta == meta;
        }
        
        @Override public boolean equals(Object o) {
            if (o instanceof SelectedItem) {
                SelectedItem sel = (SelectedItem) o;
                return itemBlockEquals(sel.itemBlock) && blockEquals(sel.block, sel.meta);
            }
            return false;
        }
        
        @Override public String toString() {
            return String.format("%s:%s:%s:%s:%s:%s", id, meta, rgba, scale, hidden, tracer);
        }
        
    }

}