package com.duckpower.registry;

import com.duckpower.DuckPower;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItemGroups {
    private ModItemGroups() {}

    /** Aba criativa "Duck Power" (nome vem do arquivo de idioma). */
    public static final ItemGroup DUCK_POWER_TAB = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(DuckPower.MOD_ID, "duck_power"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.DUCK_CAP))
                    .displayName(Text.translatable("itemGroup.duck_power"))
                    .entries((context, entries) -> {
                        entries.add(ModItems.DUCK_CAP);
                        // Futuros itens do mod entram aqui.
                    })
                    .build());

    public static void register() {}
}
