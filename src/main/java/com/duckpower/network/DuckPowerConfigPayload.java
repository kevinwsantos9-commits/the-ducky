package com.duckpower.network;

import com.duckpower.DuckPower;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Envia ao servidor a configuração escolhida no menu. */
public record DuckPowerConfigPayload(String config) implements CustomPayload {
    public static final Id<DuckPowerConfigPayload> ID =
            new Id<>(Identifier.of(DuckPower.MOD_ID, "power_config"));
    public static final PacketCodec<RegistryByteBuf, DuckPowerConfigPayload> CODEC =
            PacketCodec.tuple(net.minecraft.network.codec.PacketCodecs.STRING,
                    DuckPowerConfigPayload::config, DuckPowerConfigPayload::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
