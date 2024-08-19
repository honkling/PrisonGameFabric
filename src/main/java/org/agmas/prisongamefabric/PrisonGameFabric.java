package org.agmas.prisongamefabric;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.agmas.prisongamefabric.commands.Commands;
import org.agmas.prisongamefabric.items.Keycard;
import org.agmas.prisongamefabric.prisons.Prison;
import org.agmas.prisongamefabric.prisons.ResourceListener;
import org.agmas.prisongamefabric.prisons.upgrades.PrisonUpgrade;
import org.agmas.prisongamefabric.prisons.upgrades.UpgradeWithMapSpecifics;
import org.agmas.prisongamefabric.prisons.upgrades.UpgradesListener;
import org.agmas.prisongamefabric.util.Profile;
import org.agmas.prisongamefabric.util.Roles.Role;
import org.agmas.prisongamefabric.util.Schedule;
import org.agmas.prisongamefabric.util.Tx;
import org.agmas.prisongamefabric.util.WardenProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class PrisonGameFabric implements ModInitializer {
    public static HashMap<UUID, Profile> PlayerProfiles= new HashMap<>();
    public static ArrayList<Prison> availablePrisons = new ArrayList<>();
    public static HashMap<Identifier, PrisonUpgrade> availablePrisonUpgrades = new HashMap<>();
    public static Prison active = null;
    public static ArrayList<PlayerEntity> wardens = new ArrayList<>();
    public static String humanReadableWardenList;
    public static WardenProgress progress = new WardenProgress();
    public static MinecraftServer serverInstance=null;
    public static ServerCommandSource commandSource;

    public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of("prisonbutfabric", "prisonfabric"));
    public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(Items.IRON_BARS::getDefaultStack)
            .displayName(Text.literal("PrisonButFabric Items"))
            .build();

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ResourceListener());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new UpgradesListener());

        PrisonGameBlocks.initalize();
        PrisonGameBlocks.itemsInitalize();
        PrisonGameItems.initalize();

        Commands.init();
        Schedule.createSchedule();

        ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY).register((me)->{
            me.add(PrisonGameItems.ONE_CARD);
            me.add(PrisonGameItems.TWO_CARD);
            me.add(PrisonGameItems.THREE_CARD);
            me.add(PrisonGameBlocks.ONE_DOOR);
            me.add(PrisonGameBlocks.TWO_DOOR);
            me.add(PrisonGameBlocks.THREE_DOOR);
            me.add(PrisonGameBlocks.REFILLBLOCK);
        });

        ServerLifecycleEvents.SERVER_STARTED.register((a)->
        {
            serverInstance = a;
            commandSource = pbfCommandSource();
            Schedule.scheduleBar = a.getBossBarManager().add(Identifier.of("prisongamefabric", "schedulebar"), Text.of("Schedule Bar"));
            Schedule.scheduleBar.setColor(BossBar.Color.WHITE);
            setActive(availablePrisons.getFirst(), a.getOverworld().getServer());
        });

        ServerPlayConnectionEvents.JOIN.register((a,b,c)->{
            if (a.player != null) {
                Profile p = new Profile(a.player);
                PlayerProfiles.put(a.player.getUuid(), p);
            }
        });

    }


    public static void handleDisconnectedWarden() {
        if (!wardens.isEmpty()) {
            progress = new WardenProgress(wardens.getFirst());
        } else {
            serverInstance.getPlayerManager().getPlayerList().forEach((serverPlayerEntity -> {
                serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(Tx.tf(Formatting.RED, progress.wardenName)));
                serverPlayerEntity.networkHandler.sendPacket(new SubtitleS2CPacket(Tx.tf(Formatting.GREEN, "has died!")));
                serverPlayerEntity.playSoundToPlayer(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.MASTER, 1, 1);
            }));
            progress = new WardenProgress();
        }
    }

    public static void handleDeadWarden(PlayerEntity p) {
        wardens.remove(p);
        handleDisconnectedWarden();
    }

    public static void runUnlock(UpgradeWithMapSpecifics upgrade, MinecraftServer s) {
        PrisonUpgrade pupgrade = PrisonGameFabric.availablePrisonUpgrades.get(upgrade.upgrade);
        CommandFunction<ServerCommandSource> function = s.getCommandFunctionManager().getFunction(upgrade.functionOnUnlock.orElse(Identifier.of("prisongamefabric","nothing"))).orElse(null);
        s.getCommandFunctionManager().execute(function, commandSource);
        function = s.getCommandFunctionManager().getFunction(pupgrade.globalUnlock.orElse(Identifier.of("prisongamefabric","nothing"))).orElse(null);
        s.getCommandFunctionManager().execute(function, commandSource);
    }

    private ServerCommandSource pbfCommandSource() {
        return new ServerCommandSource(CommandOutput.DUMMY, serverInstance.getCommandSource().getPosition(), serverInstance.getCommandSource().getRotation(), serverInstance.getCommandSource().getWorld(), 2, "PBF Command Source", Text.of("PrisonButFabric"), serverInstance, serverInstance.getCommandSource().getEntity());
    }

    public static void setActive(Prison active, MinecraftServer s) {
        PrisonGameFabric.active = active;
        active.upgrades.forEach((u)->{
            if (!progress.upgrades.contains(availablePrisonUpgrades.get(u.upgrade))) {
                CommandFunction<ServerCommandSource> function = s.getCommandFunctionManager().getFunction(u.functionOnLock.orElse(Identifier.of("prisongamefabric", "nothing"))).orElse(null);
                s.getCommandFunctionManager().execute(function, commandSource);
            } else {
                CommandFunction<ServerCommandSource> function = s.getCommandFunctionManager().getFunction(u.functionOnUnlock.orElse(Identifier.of("prisongamefabric", "nothing"))).orElse(null);
                s.getCommandFunctionManager().execute(function, commandSource);
            }
        });
    }


}
