package com.duckpower.client;

import com.duckpower.registry.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

/**
 * Animação local de ativação: o boné sobe, dá uma volta completa e desce
 * suavemente até a cabeça. A animação é puramente visual; o servidor continua
 * responsável por ativar os poderes e controlar os 120 segundos.
 */
public final class DuckCapAnimation {
    private static final long DURATION_TICKS = 48L; // 2,4 segundos
    private static long startedAt = -1L;
    private static boolean playing = false;

    private DuckCapAnimation() {}

    public static void start(PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || player != client.player) return;
        startedAt = client.world == null ? 0L : client.world.getTime();
        playing = true;
    }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!playing || client.player == null || client.world == null) return;

            float tickDelta = context.tickCounter().getTickDelta(true);
            double elapsed = (client.world.getTime() - startedAt) + tickDelta;
            float progress = (float) (elapsed / DURATION_TICKS);
            if (progress >= 1.0F) {
                playing = false;
                return;
            }
            if (progress < 0.0F) return;

            PlayerEntity player = client.player;
            Vec3d playerPos = player.getLerpedPos(tickDelta);
            Vec3d cameraPos = context.camera().getPos();

            // Arco fofo: sobe primeiro, faz uma pequena pausa no alto e desce.
            float height;
            if (progress < 0.52F) {
                float up = progress / 0.52F;
                height = 0.25F + MathHelper.sin(up * (float)Math.PI / 2.0F) * 1.55F;
            } else {
                float down = (progress - 0.52F) / 0.48F;
                height = 1.80F - MathHelper.smoothstep(down) * 1.55F;
            }

            // O boné termina alinhado com o topo da cabeça.
            double x = playerPos.x - cameraPos.x;
            double y = playerPos.y + height - cameraPos.y;
            double z = playerPos.z - cameraPos.z;

            MatrixStack matrices = context.matrixStack();
            if (matrices == null) return;

            matrices.push();
            matrices.translate(x, y, z);
            float spin = progress * 360.0F;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spin));
            // Inclinação discreta durante o voo, endireitando ao pousar.
            float tilt = (1.0F - MathHelper.smoothstep(progress)) * 12.0F;
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(tilt));
            matrices.scale(0.78F, 0.78F, 0.78F);

            ItemStack cap = new ItemStack(ModItems.DUCK_CAP);
            ItemRenderer renderer = client.getItemRenderer();
            VertexConsumerProvider.Immediate consumers =
                    client.getBufferBuilders().getEntityVertexConsumers();
            renderer.renderItem(cap, ModelTransformationMode.GROUND,
                    0xF000F0, OverlayTexture.DEFAULT_UV, matrices,
                    consumers, client.world, 0);
            consumers.draw();
            matrices.pop();
        });
    }
}
