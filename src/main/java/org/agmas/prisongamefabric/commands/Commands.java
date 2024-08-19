package org.agmas.prisongamefabric.commands;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import org.agmas.prisongamefabric.PrisonGameFabric;
import org.agmas.prisongamefabric.commands.argumentTypes.RoleArgumentType;
import org.agmas.prisongamefabric.commands.suggestionProvider.RoleSuggestionProvider;
import org.agmas.prisongamefabric.util.Profile;
import org.agmas.prisongamefabric.util.Roles.Role;
import org.agmas.prisongamefabric.util.Tx;
import org.agmas.prisongamefabric.util.WardenProgress;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("warden").executes(context->{
            if (context.getSource().isExecutedByPlayer()) {
                if (PrisonGameFabric.wardens.isEmpty()) {
                    ServerPlayerEntity spe = context.getSource().getPlayer();
                    if (spe != null && spe.isPartOfGame()) {
                        PrisonGameFabric.progress = new WardenProgress(spe);
                        return  1;
                    }
                } else {
                    context.getSource().sendError(Text.of("There's already a warden!"));
                }
            } else {
                context.getSource().sendError(Text.of("You must be a player to execute this."));
            }
            return 0;
        }).then(literal("help").executes(context -> {
            context.getSource().sendMessage(Tx.tf(Formatting.GRAY, "------").append(Tx.tf(Formatting.RED, "\nWelcome, Warden!\nAs the warden, you have two main tasks: Upgrade the prison, and survive.\nSurvival is.. self explanatory. You do NOT keep your money from when you weren't a warden! Instead, you have warden funds. Warden funds are earned like normal money, but when *ANYONE* earns money, you get a 20% copy of their money.").append(Tx.tf(Formatting.GRAY, "\n------"))));
            return  1;
        }))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("plugins").executes(
                (context)->{
                    return Commands.pluginsMessage(context.getSource());
                }
        )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("pl").executes(
                (context)->{
                    return Commands.pluginsMessage(context.getSource());
                }
        )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("mods").executes(
                (context)->{
                    context.getSource().sendError(Text.of("No, fabric doesn't have /mods either."));
                    return 1;
                }
        )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("setrole")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("role", RoleArgumentType.role()).executes(context -> {

                    final Role value = RoleArgumentType.getRole("role", context);
                    context.getSource().sendFeedback(() -> net.minecraft.text.Text.literal(context.getSource().getName() + " set role to " + value.name), true);
                    if (context.getSource().isExecutedByPlayer()) {
                        Profile.getProfile(context.getSource().getPlayer()).setRole(value);
                    }
                    return 1;
                }))));
        ArgumentTypeRegistry.registerArgumentType(
                Identifier.of("minecraft", "role"),
                RoleArgumentType.class, ConstantArgumentSerializer.of(RoleArgumentType::role));
    }

    public static int pluginsMessage(ServerCommandSource source) {
        source.sendError(Text.of("Hehe, caught your ass snooping >:3\nWe don't use plugins on PrisonButFabric! because.. it's a fabric server.\n\nThe entire mod is available on https://github.com/agmass/prisongamefabric and contains most, if not all of the server's contents."));
        return 1;
    }
}
