package com.example.shardpickaxe.item;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class ShardPickaxeItem extends Item {
    private static final long DURATION_MS = 24L * 60 * 60 * 1000;

    public ShardPickaxeItem(Settings settings) {
        super(settings);
    }

    public static ItemStack createFresh() {
        ItemStack stack = new ItemStack(
                com.example.shardpickaxe.ShardPickaxeMod.SHARD_PICKAXE
        );

        stack.set(
                DataComponentTypes.CUSTOM_NAME,
                Text.literal("Shard Pickaxe")
                        .formatted(Formatting.LIGHT_PURPLE)
        );

        NbtCompound nbt = new NbtCompound();
        nbt.putLong("CreatedAt", System.currentTimeMillis());

        stack.set(
                DataComponentTypes.CUSTOM_DATA,
                NbtComponent.of(nbt)
        );

        return stack;
    }

    public static boolean isExpired(ItemStack stack) {
        NbtComponent customData =
                stack.get(DataComponentTypes.CUSTOM_DATA);

        if (customData == null) {
            return true;
        }

        NbtCompound nbt = customData.copyNbt();

        if (!nbt.contains("CreatedAt")) {
            return true;
        }

        long created = nbt.getLong("CreatedAt").orElse(0L);

        return System.currentTimeMillis() - created > DURATION_MS;
    }

    @Override
    public void inventoryTick(
            ItemStack stack,
            ServerWorld world,
            Entity entity,
            EquipmentSlot slot
    ) {
        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }

        if (isExpired(stack)) {
            player.getInventory().removeOne(stack);

            player.sendMessage(
                    Text.literal(
                            "Your Shard Pickaxe has expired."
                    ).formatted(Formatting.RED),
                    false
            );
        }
    }

    @Override
    public boolean postMine(
            ItemStack stack,
            World world,
            BlockState state,
            BlockPos pos,
            LivingEntity miner
    ) {
        if (world.isClient()
                || !(miner instanceof ServerPlayerEntity player)) {
            return super.postMine(stack, world, state, pos, miner);
        }

        if (isExpired(stack)) {
            player.getInventory().removeOne(stack);
            return false;
        }

        if (world instanceof ServerWorld serverWorld) {
            breakFlat3x3(serverWorld, pos, player);
        }

        return super.postMine(stack, world, state, pos, miner);
    }

    private void breakFlat3x3(
            ServerWorld world,
            BlockPos center,
            ServerPlayerEntity player
    ) {
        Direction facing = player.getHorizontalFacing();
        Direction.Axis axis = facing.getAxis();

        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                if (a == 0 && b == 0) {
                    continue;
                }

                BlockPos target = (axis == Direction.Axis.X)
                        ? center.add(0, a, b)
                        : center.add(a, b, 0);

                BlockState targetState =
                        world.getBlockState(target);

                if (!targetState.isAir()
                        && targetState.getHardness(world, target) >= 0) {
                    world.breakBlock(target, true, player);
                }
            }
        }
    }

    @Override
    public void appendTooltip(
            ItemStack stack,
            Item.TooltipContext context,
            TooltipDisplayComponent displayComponent,
            Consumer<Text> textConsumer,
            TooltipType type
    ) {
        textConsumer.accept(
                Text.literal("Breaks 9 Blocks at Once")
                        .formatted(Formatting.GRAY)
        );

        textConsumer.accept(
                Text.literal("Silk Touch I")
                        .formatted(Formatting.GRAY)
        );

        textConsumer.accept(
                Text.literal("Efficiency V")
                        .formatted(Formatting.GRAY)
        );

        textConsumer.accept(
                Text.literal("Unbreaking III")
                        .formatted(Formatting.GRAY)
        );

        textConsumer.accept(
                Text.literal("Mending")
                        .formatted(Formatting.GRAY)
        );

        textConsumer.accept(
                Text.literal("Self Destruct: 24h")
                        .formatted(Formatting.RED)
        );

        textConsumer.accept(
                Text.literal("3K Shards")
                        .formatted(Formatting.GRAY)
        );

        textConsumer.accept(Text.empty());

        textConsumer.accept(
                Text.literal("When in Main Hand:")
                        .formatted(Formatting.GRAY)
        );

        textConsumer.accept(
                Text.literal(" 6 Attack Damage")
                        .formatted(Formatting.DARK_GREEN)
        );

        textConsumer.accept(
                Text.literal(" 1.2 Attack Speed")
                        .formatted(Formatting.DARK_GREEN)
        );
    }
}

