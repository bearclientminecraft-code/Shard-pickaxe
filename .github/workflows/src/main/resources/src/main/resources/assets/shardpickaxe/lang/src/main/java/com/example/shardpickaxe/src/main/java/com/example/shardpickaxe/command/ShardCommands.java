package com.example.shardpickaxe.command;

import com.example.shardpickaxe.item.ShardPickaxeItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class ShardCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("shard")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(literal("give")
                                .then(literal("pickaxe")
                                        .executes(ShardCommands::givePickaxe)
                                )
                        )
        );
    }

    private static int givePickaxe(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            source.sendError(Text.literal("Only players can use this command"));
            return 0;
        }

        ItemStack pickaxe = ShardPickaxeItem.createFresh();
        player.giveItemStack(pickaxe);
        source.sendFeedback(() -> Text.literal("Given Shard Pickaxe"), true);
        return 1;
    }
}
