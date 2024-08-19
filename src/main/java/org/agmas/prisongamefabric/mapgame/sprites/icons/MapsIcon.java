package org.agmas.prisongamefabric.mapgame.sprites.icons;

import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import org.agmas.prisongamefabric.mapgame.Scene;
import org.agmas.prisongamefabric.mapgame.Sprite;
import org.agmas.prisongamefabric.mapgame.microUis.MapsMicroUI;

public class MapsIcon extends Icon {
    public MapsIcon(Scene parent) {
        super(parent);
        width = 32;
        height = 32;

        byte n = -1;
        byte b = Blocks.BLACK_CONCRETE.getDefaultMapColor().getRenderColorByte(MapColor.Brightness.NORMAL);
        byte w = Blocks.WHITE_CONCRETE.getDefaultMapColor().getRenderColorByte(MapColor.Brightness.NORMAL);
        byte s = Blocks.BLUE_CONCRETE.getDefaultMapColor().getRenderColorByte(MapColor.Brightness.NORMAL);
        byte d = Blocks.BROWN_CONCRETE.getDefaultMapColor().getRenderColorByte(MapColor.Brightness.NORMAL);
        byte g = Blocks.GRASS_BLOCK.getDefaultMapColor().getRenderColorByte(MapColor.Brightness.HIGH);
        bitmap = new Byte[][]{
                {n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n},
                {n,n,n,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,n,n,n},
                {n,n,n,b,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,b,n,n,n},
                {n,n,n,b,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,b,n,n,n},
                {n,n,n,b,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,b,n,n,n},
                {n,n,n,b,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,b,n,n,n},
                {n,n,n,b,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,b,n,n,n},
                {n,n,n,b,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,b,n,n,n},
                {n,n,n,b,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,b,n,n,n},
                {n,n,n,b,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,b,n,n,n},
                {n,n,n,b,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,b,n,n,n},
                {n,n,n,b,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,b,n,n,n},
                {n,n,n,b,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,s,b,n,n,n},
                {n,n,n,b,g,g,g,g,g,g,g,g,g,g,g,g,g,g,g,g,g,g,g,g,g,g,g,g,b,n,n,n},
                {n,n,n,b,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,b,n,n,n},
                {n,n,n,b,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,b,n,n,n},
                {n,n,n,b,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,b,n,n,n},
                {n,n,n,b,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,b,n,n,n},
                {n,n,n,b,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,b,n,n,n},
                {n,n,n,b,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,d,b,n,n,n},
                {n,n,n,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,n,n,n},
                {n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n},
                {n,n,n,w,w,w,n,n,n,w,w,w,w,w,w,w,w,w,w,w,w,w,w,n,w,w,w,w,w,n,n,n},
                {n,n,n,w,b,w,w,n,w,w,b,w,w,b,b,b,w,w,b,b,b,b,w,w,w,b,b,b,w,n,n,n},
                {n,n,n,w,b,b,w,w,w,b,b,w,b,b,w,b,b,w,b,b,w,b,b,w,b,b,w,w,w,n,n,n},
                {n,n,n,w,b,b,b,w,b,b,b,w,b,b,w,b,b,w,b,b,w,b,b,w,b,b,w,w,n,n,n,n},
                {n,n,n,w,b,b,b,b,b,b,b,w,b,b,w,b,b,w,b,b,w,b,b,w,w,b,b,w,w,n,n,n},
                {n,n,n,w,b,b,w,b,w,b,b,w,b,b,b,b,b,w,b,b,b,b,w,w,w,w,b,b,w,n,n,n},
                {n,n,n,w,b,b,w,w,w,b,b,w,b,b,w,b,b,w,b,b,w,w,w,w,w,w,b,b,w,n,n,n},
                {n,n,n,w,b,b,w,n,w,b,b,w,b,b,w,b,b,w,b,b,w,n,n,w,b,b,b,w,w,n,n,n},
                {n,n,n,w,w,w,w,n,w,w,w,w,w,w,w,w,w,w,w,w,w,n,n,w,w,w,w,w,w,n,n,n}
        };
    }

    @Override
    public void update() {
        if (selected) {
            if (parent.justAttacked) {
                new MapsMicroUI(parent.player);
            }
        }
        super.update();
    }
}
