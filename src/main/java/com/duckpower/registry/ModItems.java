package com.duckpower.registry;

import com.duckpower.DuckPower;
import com.duckpower.item.DuckCapItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class ModItems {
    private ModItems() {}

    public static final Item DUCK_CAP = Registry.register(
            Registries.ITEM,
            Identifier.of(DuckPower.MOD_ID, "duck_cap"),
            new DuckCapItem(new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
                    .component(ModComponents.AWAKENED, false)));

    public static void register() {}
}
