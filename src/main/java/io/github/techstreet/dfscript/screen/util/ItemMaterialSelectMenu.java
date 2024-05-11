package io.github.techstreet.dfscript.screen.util;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CReloadableScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.screen.widget.CTexturedButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.awt.*;
import java.util.function.Consumer;

public class ItemMaterialSelectMenu extends CReloadableScreen {

    Item material;
    Consumer<Item> onClose;

    CItem icon;
    CScrollPanel panel;
    CTextField searchField;

    public ItemMaterialSelectMenu(Item currentMaterial, Consumer<Item> onClose) {
        super(162, 105);
        material = currentMaterial;
        this.onClose = onClose;

        icon = new CItem(5, 3, new ItemStack(currentMaterial));

        searchField = new CTextField("", 15, 2, 162-15-10-5, 8, true);
        searchField.setMultiline(false);
        searchField.setChangedListener(this::reload);

        CTexturedButton finishButton = new CTexturedButton(162-8-5, 2, 8, 8, DFScript.MOD_ID + ":on_button.png", DFScript.MOD_ID + ":on_button_highlight.png", this::close);

        panel = new CScrollPanel(2, 12, 160, 100-8);

        widgets.add(icon);
        widgets.add(searchField);
        widgets.add(finishButton);
        widgets.add(panel);

        reload();
    }

    @Override
    public void close() {
        onClose.accept(material);
    }

    @Override
    public void reload() {
        panel.clear();

        int x = 0;
        int y = 0;

        int index = 0;

        for (Item item : Registries.ITEM) {
            if(item == Items.AIR) {
                continue;
            }

            if(!searchField.getText().isBlank() && !item.getName().toString().contains(searchField.getText())) {
                continue;
            }

            ItemStack itemStack = new ItemStack(item);
            CItem citem;
            if(item == material) {
                citem = new CItem(x, y, itemStack) {
                    @Override
                    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
                        super.render(context, mouseX, mouseY, tickDelta);
                        Rectangle b = getBounds();
                        context.fill(b.x, b.y, b.x + b.width, b.y + b.height, 0x3300ff00);
                    }
                };
            }
            else {
                citem = new CItem(x, y, itemStack);
            }
            citem.setClickListener((button) -> {
                material = item;
                icon.setItemStack(itemStack);
                reload();
            });
            panel.add(citem);

            index++;
            if(index == 16) {
                x = 0;
                y += 10;
                index = 0;
            }
            else {
                x += 10;
            }
        }
    }
}
