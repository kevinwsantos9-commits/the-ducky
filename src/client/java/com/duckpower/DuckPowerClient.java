package com.duckpower;

import com.duckpower.client.DuckCapAnimation;
import com.duckpower.client.DuckPowerScreen;
import com.duckpower.item.DuckCapItem;
import com.duckpower.registry.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class DuckPowerClient implements ClientModInitializer {
    private static KeyBinding openPowerMenu;
    private static boolean wasAwakened = false;

    @Override
    public void onInitializeClient() {
        DuckCapAnimation.register();
        com.duckpower.client.DuckPowerHud.register();

        ModelPredicateProviderRegistry.register(
                ModItems.DUCK_CAP,
                Identifier.of(DuckPower.MOD_ID, "awakened"),
                (stack, world, entity, seed) -> DuckCapItem.isAwakened(stack) ? 1.0F : 0.0F);

        openPowerMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.duck_power.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.duck_power.controls"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // O estado do componente é sincronizado pelo servidor. Detectamos a transição
            // para awakened aqui para manter as classes client fora do source set principal.
            if (client.player != null) {
                boolean awakened = DuckCapItem.isAwakened(client.player.getMainHandStack())
                        || DuckCapItem.isAwakened(client.player.getOffHandStack());
                if (awakened && !wasAwakened) {
                    DuckCapAnimation.start(client.player);
                }
                wasAwakened = awakened;
            } else {
                wasAwakened = false;
            }

            while (openPowerMenu.wasPressed()) {
                if (client.player == null || client.currentScreen != null) continue;
                boolean hasDuckCap = client.player.getMainHandStack().isOf(ModItems.DUCK_CAP)
                        || client.player.getOffHandStack().isOf(ModItems.DUCK_CAP);
                if (hasDuckCap) {
                    var cap = client.player.getMainHandStack().isOf(ModItems.DUCK_CAP)
                            ? client.player.getMainHandStack() : client.player.getOffHandStack();
                    client.setScreen(new DuckPowerScreen(cap));
                }
            }
        });
    }
}
