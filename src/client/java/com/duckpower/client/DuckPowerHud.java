package com.duckpower.client;

import com.duckpower.item.DuckCapItem;
import com.duckpower.registry.ModComponents;
import com.duckpower.registry.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Mostra o tempo restante do Awakening enquanto o boné estiver no inventário. */
public final class DuckPowerHud {
    private DuckPowerHud() {}

    public static void register() {
        HudRenderCallback.EVENT.register((DrawContext context, float tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;

            ItemStack cap = findAwakenedCap(client);
            if (cap.isEmpty()) return;

            long end = cap.getOrDefault(ModComponents.AWAKEN_END_TICK, 0L);
            long remaining = Math.max(0L, end - client.world.getTime());
            int seconds = (int) Math.ceil(remaining / 20.0);
            int minutes = seconds / 60;
            seconds %= 60;
            String timer = String.format("%02d:%02d", minutes, seconds);

            int x = context.getScaledWindowWidth() / 2;
            int y = context.getScaledWindowHeight() - 54;
            context.fill(x - 76, y - 5, x + 76, y + 23, 0xA8200808);
            context.drawCenteredTextWithShadow(client.textRenderer,
                    Text.literal("DUCK AWAKENING").formatted(Formatting.RED, Formatting.BOLD),
                    x, y, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer,
                    Text.literal(timer).formatted(Formatting.GOLD, Formatting.BOLD),
                    x, y + 11, 0xFFFFFF);
        });
    }

    private static ItemStack findAwakenedCap(MinecraftClient client) {
        ItemStack main = client.player.getMainHandStack();
        if (main.isOf(ModItems.DUCK_CAP) && DuckCapItem.isAwakened(main)) return main;
        ItemStack off = client.player.getOffHandStack();
        if (off.isOf(ModItems.DUCK_CAP) && DuckCapItem.isAwakened(off)) return off;
        for (int i = 0; i < client.player.getInventory().size(); i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (stack.isOf(ModItems.DUCK_CAP) && DuckCapItem.isAwakened(stack)) return stack;
        }
        return ItemStack.EMPTY;
    }
}
