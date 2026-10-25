package com.example.shardpickaxe;

import com.example.shardpickaxe.command.ShardCommands;
import com.example.shardpickaxe.item.ShardPickaxeItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShardPickaxeMod implements ModInitializer {
    public static final String MOD_ID = "shardpickaxe";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ShardPickaxeItem SHARD_PICKAXE = new ShardPickaxeItem(
            new net.minecraft.item.Item.Settings().maxCount(1)
    );

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "shard_pickaxe"), SHARD_PICKAXE);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SHARD_PICKAXE);
        });

        CommandRegistrationCallback.EVENT.register(ShardCommands::register);

        LOGGER.info("Shard Pickaxe mod loaded!");
    }
}
