package com.example.shardpickaxe.item;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class ShardPickaxeItem extends Item {
    private static final long DURATION_MS = 24L * 60 * 60 * 1000;

    public ShardPickaxeItem(Settings settings) {
        super(settings);
    }

    public static ItemStack createFresh() {
        ItemStack stack = new ItemStack(com.example.shardpickaxe.ShardPickaxeMod.SHARD_PICKAXE);

        // Фиолетовое название
        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Shard Pickaxe").formatted(Formatting.LIGHT_PURPLE));

        // Зачарования через компоненты (новый способ)
        stack.applyComponentsFrom(ItemStack.CODEC);

        // Таймер самоуничтожения
        NbtCompound nbt = new NbtCompound();
        nbt.putLong("CreatedAt", System.currentTimeMillis());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        return stack;
    }

    public static boolean isExpired(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null) return true;

        NbtCompound nbt = customData.copyNbt();
        if (!nbt.contains("CreatedAt")) return true;

        long created = nbt.getLong("CreatedAt");
        return System.currentTimeMillis() - created > DURATION_MS;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient() || !(entity instanceof ServerPlayerEntity player)) return;

        if (isExpired(stack)) {
            player.getInventory().setStack(slot, ItemStack.EMPTY);
            player.sendMessage(Text.literal("Your Shard Pickaxe has self-destructed.").formatted(Formatting.RED), false);
        }
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (world.isClient() || !(miner instanceof ServerPlayerEntity player)) {
            return super.postMine(stack, world, state, pos, miner);
        }

        if (isExpired(stack)) {
            player.getInventory().removeOne(stack);
            return false;
        }

        breakFlat3x3(world, pos, player, stack);
        return super.postMine(stack, world, state, pos, miner);
    }

    private void breakFlat3x3(World world, BlockPos center, ServerPlayerEntity player, ItemStack stack) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Direction facing = player.getHorizontalFacing();
        Direction.Axis axis = facing.getAxis();

        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                if (a == 0 && b == 0) continue;

                BlockPos target = (axis == Direction.Axis.X)
                        ? center.add(0, a, b)
                        : center.add(a, b, 0);

                tryBreak(serverWorld, target, player, stack);
            }
        }
    }

    private void tryBreak(ServerWorld world, BlockPos pos, ServerPlayerEntity player, ItemStack stack) {
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) return;
        if (state.getHardness(world, pos) < 0) return;

        world.breakBlock(pos, true, player);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("Breaks 9 Blocks at Once").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Silk Touch I").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Efficiency V").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Unbreaking III").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Mending").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Self Destruct: 24h").formatted(Formatting.RED));
        tooltip.add(Text.literal("3K Shards").formatted(Formatting.GRAY));
        tooltip.add(Text.empty());
        tooltip.add(Text.literal("When in Main Hand:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal(" 6 Attack Damage").formatted(Formatting.DARK_GREEN));
        tooltip.add(Text.literal(" 1.2 Attack Speed").formatted(Formatting.DARK_GREEN));
    }
            }
