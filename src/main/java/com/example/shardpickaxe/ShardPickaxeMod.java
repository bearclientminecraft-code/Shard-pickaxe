package com.example.shardpickaxe;

import com.example.shardpickaxe.command.ShardCommands;
import com.example.shardpickaxe.item.ShardPickaxeItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShardPickaxeMod implements ModInitializer {
    public static final String MOD_ID = "shardpickaxe";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Item SHARD_PICKAXE;

    @Override
    public void onInitialize() {
        SHARD_PICKAXE = Registry.register(
                Registries.ITEM,
                Identifier.of(MOD_ID, "shard_pickaxe"),
                new ShardPickaxeItem(new Item.Settings().maxCount(1))
        );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SHARD_PICKAXE);
        });

        CommandRegistrationCallback.EVENT.register(ShardCommands::register);

        LOGGER.info("Shard Pickaxe mod loaded!");
    }
}
