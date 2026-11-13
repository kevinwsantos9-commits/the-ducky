package com.duckpower.item;

import com.duckpower.power.FuryPowers;
import com.duckpower.registry.ModComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * Boné de pato. Na forma normal não faz nada.
 * Botão direito = Awakening: fúria por 2 minutos, depois volta ao normal.
 */
public class DuckCapItem extends Item {
    public static final int AWAKEN_DURATION_TICKS = 20 * 60 * 2;
    /** Recarga a partir da ativação: 2 min de fúria + 1 min de descanso. */
    public static final int COOLDOWN_TICKS = AWAKEN_DURATION_TICKS + 20 * 60;

    public DuckCapItem(Settings settings) {
        super(settings);
    }

    public static boolean isAwakened(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(ModComponents.AWAKENED));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (isAwakened(stack)) {
            return TypedActionResult.fail(stack);
        }
        if (!world.isClient()) {
            // A animação visual é iniciada no cliente quando o estado awakened sincronizar.
            startAwakening(world, user, stack);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    /** Só no servidor. Público para a animação da Etapa 4 poder chamar. */
    public static void startAwakening(World world, PlayerEntity player, ItemStack stack) {
        stack.set(ModComponents.AWAKENED, true);
        stack.set(ModComponents.AWAKEN_END_TICK, world.getTime() + AWAKEN_DURATION_TICKS);
        FuryPowers.apply(player, stack, AWAKEN_DURATION_TICKS);
        player.getItemCooldownManager().set(stack.getItem(), COOLDOWN_TICKS);
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_RAVAGER_ROAR, SoundCategory.PLAYERS, 1.0F, 1.4F);
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                    player.getX(), player.getY() + 1.8, player.getZ(), 12, 0.4, 0.3, 0.4, 0.02);
        }
        player.sendMessage(Text.translatable("message.duck_power.awakened").formatted(Formatting.RED), true);
    }

    /** Só no servidor. Volta o boné ao normal. */
    public static void endAwakening(World world, Entity holder, ItemStack stack) {
        stack.set(ModComponents.AWAKENED, false);
        stack.remove(ModComponents.AWAKEN_END_TICK);
        if (holder instanceof PlayerEntity player) FuryPowers.clear(player);
        world.playSound(null, holder.getX(), holder.getY(), holder.getZ(),
                SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 1.0F, 1.2F);
        if (holder instanceof PlayerEntity player) {
            player.sendMessage(Text.translatable("message.duck_power.calmed").formatted(Formatting.YELLOW), true);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient() || !isAwakened(stack)) {
            return;
        }
        long end = stack.getOrDefault(ModComponents.AWAKEN_END_TICK, 0L);
        if (world.getTime() >= end) {
            endAwakening(world, entity, stack);
            return;
        }

        // Aura leve: partículas em pequenos pulsos, sem criar entidades extras.
        if (entity instanceof PlayerEntity player && world instanceof ServerWorld serverWorld
                && world.getTime() % 5L == 0L) {
            double angle = (world.getTime() % 40L) * (Math.PI / 20.0);
            double radius = 0.62;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    px, player.getY() + 0.15 + (world.getTime() % 10L) * 0.035, pz,
                    1, 0.025, 0.08, 0.025, 0.005);
            serverWorld.spawnParticles(ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    1, 0.28, 0.45, 0.28, 0.005);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        String key = isAwakened(stack) ? "tooltip.duck_power.duck_cap.active" : "tooltip.duck_power.duck_cap.idle";
        tooltip.add(Text.translatable(key).formatted(Formatting.GRAY));
    }

    /** Brilho de encantado enquanto está em awakening. */
    @Override
    public boolean hasGlint(ItemStack stack) {
        return isAwakened(stack);
    }
}
