package com.duckpower.registry;

import com.duckpower.DuckPower;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Dados guardados DENTRO do item (sobrevivem a salvar/carregar o mundo).
 */
public final class ModComponents {
    private ModComponents() {}

    /** true enquanto o boné está no modo Awakening (fúria). */
    public static final ComponentType<Boolean> AWAKENED = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(DuckPower.MOD_ID, "awakened"),
            ComponentType.<Boolean>builder()
                    .codec(Codec.BOOL)
                    .packetCodec(PacketCodecs.BOOL)
                    .build());

    /** Tick (do mundo) em que a fúria termina. Usado na Etapa 3. */
    public static final ComponentType<Long> AWAKEN_END_TICK = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(DuckPower.MOD_ID, "awaken_end_tick"),
            ComponentType.<Long>builder()
                    .codec(Codec.LONG)
                    .packetCodec(PacketCodecs.VAR_LONG)
                    .build());

    /** Configuração dos poderes do Awakening, salva dentro de cada boné. */
    public static final ComponentType<String> POWER_CONFIG = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(DuckPower.MOD_ID, "power_config"),
            ComponentType.<String>builder()
                    .codec(Codec.STRING)
                    .packetCodec(PacketCodecs.STRING)
                    .build());

    public static void register() {
        // Só carregar a classe já registra os campos estáticos acima.
    }
}
