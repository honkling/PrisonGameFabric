package org.agmas.prisongamefabric.items.jobItems;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.agmas.prisongamefabric.util.Profile;
import org.agmas.prisongamefabric.util.StateSaverAndLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class LumberjackAxe extends AxeItem implements PolymerItem, MiningItem {
    public LumberjackAxe(Settings settings) {
        super(ToolMaterials.WOOD,settings);

    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        stack.set(DataComponentTypes.CAN_BREAK, new BlockPredicatesChecker(Collections.singletonList(BlockPredicate.Builder.create().blocks(Blocks.SPRUCE_LOG).build()),true));
        return stack;
    }

    @Override
    public void onMine(PlayerEntity p, BlockState b, World w, BlockPos pos) {

        Profile profile = Profile.getProfile(p);
        profile.addMoney(10 * MiningItem.jobModifiers(),true);
        MiningItem.super.onMine(p,b,w,pos);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.WOODEN_AXE;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, RegistryWrapper.WrapperLookup lookup, @Nullable ServerPlayerEntity player) {
        ItemStack stack = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, lookup, player);
        stack.set(DataComponentTypes.CAN_BREAK, new BlockPredicatesChecker(Collections.singletonList(BlockPredicate.Builder.create().blocks(Blocks.SPRUCE_LOG).build()),true));

        return stack;
    }
}
