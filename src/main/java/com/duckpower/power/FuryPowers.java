package com.duckpower.power;

import com.duckpower.registry.ModComponents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.LinkedHashMap;
import java.util.Map;

/** Poderes configuráveis: nível 0 desliga; níveis 1-3 aumentam a intensidade. */
public final class FuryPowers {
    private FuryPowers() {}

    public record Power(String id, String name, RegistryEntry<StatusEffect> effect,
                        int baseAmplifier, int defaultLevel) {}

    private static final Map<String, Power> POWERS = new LinkedHashMap<>();

    static {
        register(new Power("strength", "Força", StatusEffects.STRENGTH, 0, 2));
        register(new Power("speed", "Velocidade", StatusEffects.SPEED, 0, 2));
        register(new Power("resistance", "Resistência", StatusEffects.RESISTANCE, 0, 1));
        register(new Power("regeneration", "Regeneração", StatusEffects.REGENERATION, 0, 1));
        register(new Power("jump", "Super salto", StatusEffects.JUMP_BOOST, 0, 0));
        register(new Power("fire", "Resistência ao fogo", StatusEffects.FIRE_RESISTANCE, 0, 0));
        register(new Power("night_vision", "Visão noturna", StatusEffects.NIGHT_VISION, 0, 0));
        register(new Power("haste", "Pressa", StatusEffects.HASTE, 0, 0));
    }

    private static void register(Power power) { POWERS.put(power.id(), power); }
    public static Map<String, Power> all() { return Map.copyOf(POWERS); }

    public static int getLevel(ItemStack stack, String id) {
        String config = stack.getOrDefault(ModComponents.POWER_CONFIG, "");
        for (String entry : config.split(";")) {
            String[] pair = entry.split("=", 2);
            if (pair.length == 2 && pair[0].equals(id)) {
                try { return Math.max(0, Math.min(3, Integer.parseInt(pair[1]))); }
                catch (NumberFormatException ignored) { return 0; }
            }
        }
        Power power = POWERS.get(id);
        return power == null ? 0 : power.defaultLevel();
    }

    public static String encode(ItemStack stack) {
        StringBuilder out = new StringBuilder();
        for (Power power : POWERS.values()) {
            if (out.length() > 0) out.append(';');
            out.append(power.id()).append('=').append(getLevel(stack, power.id()));
        }
        return out.toString();
    }

    public static void apply(PlayerEntity player, ItemStack cap, int durationTicks) {
        for (Power power : POWERS.values()) {
            int level = getLevel(cap, power.id());
            if (level <= 0) continue;
            int amplifier = Math.min(4, power.baseAmplifier() + level - 1);
            player.addStatusEffect(new StatusEffectInstance(
                    power.effect(), durationTicks, amplifier, false, true));
        }
    }

    public static void clear(PlayerEntity player) {
        for (Power power : POWERS.values()) player.removeStatusEffect(power.effect());
    }
}
