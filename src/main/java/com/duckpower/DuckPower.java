package com.duckpower;

import com.duckpower.registry.ModItemGroups;
import com.duckpower.registry.ModItems;
import com.duckpower.network.DuckPowerConfigPayload;
import com.duckpower.registry.ModComponents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuckPower implements ModInitializer {
    public static final String MOD_ID = "duck_power";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // A ordem importa: componentes -> itens -> aba criativa
        ModComponents.register();
        ModItems.register();
        ModItemGroups.register();
        PayloadTypeRegistry.playC2S().register(DuckPowerConfigPayload.ID, DuckPowerConfigPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(DuckPowerConfigPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                var player = context.player();
                String config = payload.config();
                if (config.length() > 256) return;
                var main = player.getMainHandStack();
                var off = player.getOffHandStack();
                if (main.isOf(ModItems.DUCK_CAP)) main.set(ModComponents.POWER_CONFIG, config);
                else if (off.isOf(ModItems.DUCK_CAP)) off.set(ModComponents.POWER_CONFIG, config);
            });
        });
        LOGGER.info("Duck Power carregado! Quack.");
    }
}
