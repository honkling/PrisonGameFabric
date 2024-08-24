package org.agmas.prisongamefabric.mapgame.microUis;

import eu.pb4.polymer.core.impl.ui.MicroUi;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.agmas.prisongamefabric.PrisonGameFabric;
import org.agmas.prisongamefabric.util.Profile;
import org.agmas.prisongamefabric.util.Tx;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MapsMicroUI extends MicroUi {

    public static int cooldown = 0;

    public MapsMicroUI(ServerPlayerEntity spe) {
        super(2);
        this.title(Tx.tf(Formatting.GRAY, "Maps"));
        this.open(spe);
        AtomicInteger loop = new AtomicInteger();
        PrisonGameFabric.availablePrisons.forEach((a)->{
            ItemStack mapRepresenter = a.itemIconAsItem.getDefaultStack();
            mapRepresenter.set(DataComponentTypes.ITEM_NAME, Tx.tf(Formatting.GOLD, a.name));
            this.slot(loop.get(), mapRepresenter, (aa,b,c,d)->{
                if (cooldown <= 0) {
                    cooldown = 20*128;

                    aa.getInventory().armor.set(3, Profile.getProfile(aa).helmetItem);
                    PrisonGameFabric.setActive(a, aa.getServer());
                } else {
                    aa.sendMessage(Tx.ttf(Formatting.RED, Text.translatable("map.cooldown", cooldown)));
                }
            });
            loop.getAndIncrement();
        });
    }
}
