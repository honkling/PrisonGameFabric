package org.agmas.prisongamefabric.prisons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.agmas.prisongamefabric.prisons.upgrades.UpgradeWithMapSpecifics;

import java.util.List;

public class Prison {
    public final String name;
    public final Identifier itemIcon;
    public final Item itemIconAsItem;
    public final List<PrisonLocation> cellLocations;
    public final List<UpgradeWithMapSpecifics> upgrades;
    public final PrisonLocation wardenSpawn;
    public final PrisonLocation guardSpawn;
    public final PrisonLocation computerTeleport;

    public static final Codec<Prison> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(Prison::getName),
            Identifier.CODEC.fieldOf("itemIcon").forGetter(Prison::getItemIcon),
            PrisonLocation.CODEC.listOf().fieldOf("cellLocations").forGetter(Prison::getCellLocations),
            UpgradeWithMapSpecifics.CODEC.listOf().fieldOf("upgrades").forGetter(Prison::getUpgrades),
            PrisonLocation.CODEC.fieldOf("wardenSpawn").forGetter(Prison::getWardenSpawn),
            PrisonLocation.CODEC.fieldOf("guardSpawn").forGetter(Prison::getGuardSpawn),
            PrisonLocation.CODEC.fieldOf("computerTeleport").forGetter(Prison::getComputerTeleport)
            // Up to 16 fields can be declared here
    ).apply(instance, Prison::new));

    public Prison(String name, Identifier itemIcon, List<PrisonLocation> spawnLocation, List<UpgradeWithMapSpecifics> upgrades, PrisonLocation wardenSpawn, PrisonLocation guardSpawn, PrisonLocation computerTeleport) {
        this.name = name;
        this.itemIcon = itemIcon;
        this.itemIconAsItem = Registries.ITEM.get(itemIcon);
        this.cellLocations = spawnLocation;
        this.upgrades = upgrades;
        this.wardenSpawn = wardenSpawn;
        this.guardSpawn = guardSpawn;
        this.computerTeleport = computerTeleport;
    }

    public String getName() {
        return this.name;
    }

    public List<PrisonLocation> getCellLocations() {
        return cellLocations;
    }

    public PrisonLocation getWardenSpawn() {
        return wardenSpawn;
    }

    public Identifier getItemIcon() {
        return itemIcon;
    }

    public PrisonLocation getComputerTeleport() {
        return computerTeleport;
    }

    public PrisonLocation getGuardSpawn() {
        return guardSpawn;
    }

    public List<UpgradeWithMapSpecifics> getUpgrades() {
        return upgrades;
    }
}
